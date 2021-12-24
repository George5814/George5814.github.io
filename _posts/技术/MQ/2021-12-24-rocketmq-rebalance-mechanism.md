---
layout: post
title:  深入理解RocketMQ Rebalance机制
category: 技术
tags: Java MQ RocketMQ rebalance
keywords: Java MQ RocketMQ rebalance
description: 深入理解RocketMQ Rebalance机制
date: 2021-12-24
modified_date: 2021-12-24
author: followtry
published: true
istop: false
---


本文深入的分析了RocketMQ的Rebalance机制，主要包括以下内容：

1. Rebalance必要的元数据信息的维护
1. Broker协调通知机制；
1. 消费者/启动/运行时/停止时Rebalance触发时机
1. 单个Topic的Rebalance流程
1. 分区分配策略
1. RocketMQ与Kafka Rebalance机制的区别，


## Rebalance简介

Rebalance(再均衡)机制指的是：将一个Topic下的多个队列(或称之为分区)，在同一个消费者组(consumer group)下的**多个消费者实例**(consumer instance)之间进行重新分配。 

**Rebalance机制本意是为了提升消息的并行处理能力。**例如，一个Topic下5个队列，在只有1个消费者的情况下，那么这个消费者将负责处理这5个队列的消息。如果此时我们增加一个消费者，那么可以给其中一个消费者分配2个队列，给另一个分配3个队列，从而提升消息的并行处理能力。如下图：

![rocketMq消费组分配]({{ site.baseurl }}/img/mq/rocketmq-client-1.jpg)

但是Rebalance机制也存在明显的**限制**与**危害**。

### Rebalance限制

由于一个队列最多分配给一个消费者，因此当某个消费者组下的消费者实例数量大于队列的数量时，多余的消费者实例将分配不到任何队列。

### Rebalance危害

1. **消费暂停** 

    考虑在只有Consumer 1的情况下，其负责消费所有5个队列；在新增Consumer 2，触发Rebalance时，需要分配2个队列给其消费。那么Consumer 1就需要停止这2个队列的消费，等到这两个队列分配给Consumer 2后，这两个队列才能继续被消费
1. **重复消费**

    Consumer 2 在消费分配给自己的2个队列时，必须接着从Consumer 1之前已经消费到的offset继续开始消费。然而默认情况下，offset是异步提交的，如consumer 1当前消费到offset为10，但是异步提交给broker的offset为8；那么如果consumer 2从8的offset开始消费，那么就会有2条消息重复。也就是说，Consumer 2 并不会等待Consumer1提交完offset后，再进行Rebalance，因此提交间隔越长，可能造成的重复消费就越多
1. **消费突刺**

    由于rebalance可能导致重复消费，如果需要重复消费的消息过多；或者因为rebalance暂停时间过长，导致积压了部分消息。那么都有可能导致在rebalance结束之后瞬间可能需要消费很多消息。

基于以上的影响，需要对其内部原理进行剖析，以便于问题排查。

## Rebalance原理分析

我们将从Broker和Consumer端两个角度进行分析

1. Broker，负责Rebalance元数据维护，通知机制。在Rebalance过程中扮演着协调者的作用。
1. Consumer, 聚焦在单个Consumer的Rebalance过程

### Broker端Rebalance协调机制

首选需要了解触发Rebalance的根本原因

1. 订阅的Topic下的Queue数量发生变化

    场景： broker宕机，升级等运维操作，queue扩缩容等。
1. 消费者组信息发生变化

    场景：日常发布过程中的停止，启动消费者；异常宕机；网络异常导致消费者与broker断开连接；主动消费者数量扩缩容；Topic订阅信息发生变化

将队列信息和消费者组信息称之为Rebalance元数据。Broker负责维护这些元数据，并在二者信息发生变化时以某种机制告诉消费组下的所有实例，需要进行Rebalance。可以看出，在Rebalance过程中，Broker是协调者的角色。

broker内部通过元数据管理器维护的Rebalance元数据信息,如下图

![broker元数据维护]({{ site.baseurl }}/img/mq/rocketmq-broker-1.jpg)

其具体实现其实都是个Map。

1. 队列信息

    由TopicConfigManager维护。Map 的key是Topic名称，Value是TopicConfig。Broker通过实时的或者周期性的上报自己的Topic配置信息给NameServer，在NameServer组装成Topic的完整路由信息。消费者定时向NameServer定时拉取最新路由信息，以实现间接通知，当发现队列信息变化，触发Rebalance。
1. 消费者组信息

    由**ConsumerManager**、**ConsumerOffsetManager**、**SubscriptionGroupManager**三者共同维护。
    
    ConsumerManager维护了消费者组订阅信息，以及消费者组下当前的消费者实例信息，当消费者组的订阅信息或者实例发生变化，Broker都会主动给所有消费者实例发送通知，触发Rebalance。而在Rebalance时，
    
    消费者需要从ConsumerOffsetManager查询应该从那个位置继续开始消费。
    
    SubscriptionGroupManager主要是维护消费者组的一些附加信息，方便运维。

#### 队列信息变化

通常情况下，一个Topic下的队列数量不会频繁的变化，但是如果遇到，Topic队列数量扩/缩容，、broker日常运维时的停止/启动或者broker异常宕机，也有可能导致队列数量发生变化。

这里我们重点讲一下为什么broker异常停止/宕机会导致数量变化。一些读者可能会认为创建Topic时，已经明确指定了队列的数量，那么之后不论怎样，队列的数量信息都不会发生变化，这是一种典型误解。

下图展示了一个RocketMQ集群双主部署模式下，某个broker宕机后，Topic路由信息的变化

![broker-a宕机]({{ site.baseurl }}/img/mq/rocketmq-broker-2.jpg)

可以看到，在宕机前，主题TopicX下队列分布在broker-a和broker-b两个broker上，每个broker上各有8个队列。当broker-a宕机后，其路由信息会被移除，此时我们就只能看到TopicX在broker-b上的路由信息。

因此，在RocketMQ中，Topic的路由信息实际上是动态变化的。不论是停止/启动/扩容导致的所有变化最终都会上报给NameServer。客户端可以给NameServer发送GET_ROUTEINTO_BY_TOPIC请求，来获得某个Topic的完整路由信息。如果发现队列信息发生变化，则触发Reabalance。

#### 消费者组信息变化

Rebalance的另外一个条件：消费者组信息。

Broker端通过以下三个组件共同维护：

1. ConsumerManager：维护消费者实例信息和订阅信息
1. ConsumerOffsetManager：维护offset进度信息
1. SubscriptionGroupManager：运维相关操作信息维护

##### ConsumerManager

ConsumerManager是最重要的一个消费者组元数据管理器，其维护了某个消费者组的订阅信息，以及所有消费者实例的详细信息，并在发生变化时提供通知机制。

- 数据添加

    客户端通过发送HEART_BEAT请求给Broker，将自己添加到ConsumerManager中维护的某个消费者组中。需要注意的是，每个Consumer都会向所有的Broker进行心跳，因此每个Broker都维护了所有消费者的信息。
- 数据删除

    客户端正常停止时，发送UNREGISTER_CLIENT请求，将自己从ConsumerManager移除；此外在发生网络异常时，Broker也会主动将消费者从ConsumerManager中移除。
- 数据查询

    消费者可以向任意一个Broker发送GET_CONSUMER_LIST_BY_GROUP请求，来获得一个消费者组下的所有消费者实例信息。

不论是数据添加还是删除，Broker都会主动通知这个消费者组下的所有实例进行Rebalance。在ConsumerManager的registerConsumer方法中，我们可以看到这个通知机制。如以下源码片段红色框中所示：

![通知变化]({{ site.baseurl }}/img/mq/rocketmq-broker-3.jpg)

consumerIdsChangeListener在处理消费者组信息变更事件时，会给每个消费者实例都发送一个通知，各个消费者实例在收到通知后触发Rebalance，如下图所示：

![通知客户端]({{ site.baseurl }}/img/mq/rocketmq-broker-4.jpg)

敏锐读者注意到了，Broker是通知每个消费者各自Rebalance，即每个消费者自己给自己重新分配队列，而不是Broker将分配好的结果告知Consumer。从这个角度，RocketMq和Kafka的Rebalance机制类似，**都是在客户端进行Rebalance分配**

不同的是
- kafka： 在消费组的多个消费者里选择一个作为Group Leader，由该leader进行分区分配，分配结果通过Cordinator同步给其消费者。相当于Kafka的分区分配只有一个大脑。
- RocketMq: 每个消费者，自己负责给自己分配队列，相当于每个消费者都是一个大脑。

##### ConsumerOffsetManager

通过ConsumerManager已经可以获得Rebalance时需要的消费者所有必要信息。但是还有一点，Rebalance时，如果某个队列重新分配给了某个消费者，那么必须接着从上一个消费者的位置继续开始消费，这就是ConsumerOffsetManager的作用。

消费者发送UPDATE_CONSUMER_OFFSET请求给Broker，来更新消费者组对于某个Topic的消费进度。
发送QUERY_CONSUMER_OFFSET请求，来查询消费进度。

##### SubscriptionGroupManager

订阅组配置管理器，内部针对每个消费者组维护一个SubscriptionGroupConfig。主要是为了针对消费者组进行一些运维操作，这里不做过多介绍，感兴趣的读者自行查阅源码。

### 每个Consumer的Rebalance的触发时机

前面分析Broker在Rebalance过程中起的是协调通知的作用，可以帮忙我们从整体对Rebalance有个初步的认知。但是Rebalance的细节，却是在Consumer端完成的。在本节中，我们将着重讨论单个consumer的Rebalance流程。

需要说明的是，RocketMQ的consumer分配pull和push两种模式，二者的工作逻辑并不相同。这里主要以push模式的默认实现类DefaultMQPushConsumer为例进行讲解。

在前文，我们提到Broker会主动通知消费者进行Rebalance，但是从消费者的角度来看，整个生命过程的各个阶段，都有可能触发Rebalance，而不仅仅是收到通知后才进行Rebalance。

具体来说，Consumer在启动/运行时/停止时，都有可能触发Rebalance，如下图所示：

![客户端再平衡]({{ site.baseurl }}/img/mq/rocketmq-client-2.jpg)

Rebalance时机

1. **启动时**

    消费者立即向所有的Broker发送一次心跳请求，Broker会将消费者添加到由ConsumerManager维护的某个消费者组中。
    然后这个Consumer自己会立即触发一次Rebalance。
1. **运行时**

    ConsumerManager内维护的消费组内的消费者有变化时，Broker会通知该消费组下的所有的消费者实例，消费者接收到Broker通知后会立即触发Rebalance。
    同时为了避免通知丢失，会周期性的触发Rebalance。
1. **停止时**

    消费者向所有的Broker发送取消注册的命令时，Broker将消费者从ConsuemrManager中移除，并通知其他Consumer进行Rebalance。

在启动和停止时，被操作的实例都是可以自主知道自己应该Rebalance和将自己下线的。**最需要关注的变化是在运行时，因为在运行时，每个在线的Consumer，需要接收到其他Consumer上下线的通知来Rebalance自身的订阅关系的。**

#### 启动时触发

DefaultMQPushConsumerImpl的start方法显示了一个消费者的启动流程，如下图所示：

![客户端再平衡]({{ site.baseurl }}/img/mq/rocketmq-client-3.jpg)

分为五个步骤

1. 启动准备工作
1. 从NameServer更新Topic路由信息，收集到Rebalance需要的队列信息
1. 检查Consumer配置
1. 想每个Broker发送心跳信息，将自己加入消费组
1. 立即触发一次Rebalance，在步骤2和步骤4的基础上立即触发一次Rebalance

##### 更新订阅的Topic路由信息

调用updateTopicSubscribeInfoWhenSubscriptionChanged()方法，从NameServer更新topic路由信息，由于一个消费者可以订阅多个topic，因此这个Topic都需要更新，如下：

![更新订阅的Topic路由信息]({{ site.baseurl }}/img/mq/rocketmq-client-4.jpg)

通过这一步，当前Consumer就拿到了Topic下所有队列信息，具备了Rebalance的第一个条件。

##### 向Broker发送心跳信息

调用sendHeartbeatToAllBrokerWithLock方法，给每个Broker都发送一个心跳请求。

当Broker收到心跳请求后，将这个消费者注册到ConsumerManager中，前文提到，当Consumer数量变化时，Broker会主动通知其他消费者进行Rebalance。

##### 立即触发一次Rebalance

消费者启动流程的最后一步是调用以下方法立即触发一次rebalance，`this.mQClientFactory.rebalanceImmediately();`. 该方法内部实际是通过`this.rebalanceService.wakeup();`唤醒RebalanceService来触发Rebalance.

这里我们并不着急分析RebalanceService的内部具体实现，因为所有的Rebalance触发都是以这个类为入口，我们将在讲解完运行时/停止时的Rebalance触发时机后，统一进行说明。

#### 停止时触发

消费者在正常停止时，需要调用shutdown方法，这个方法的工作逻辑如下所示：

![停止时触发]({{ site.baseurl }}/img/mq/rocketmq-client-8.jpg)

主要关注第二和第三步

- 第二步：持久化offset

    offset是异步提交的，为了避免重复消费，因此在关闭时，必须要对尚未提交的offset进行持久化。其实就是发送更新offset请求(UPDATE_CONSUMER_OFFSET)给Broker，Broker对应更新ConsumerOffsetManager中的记录。这样当队列分配给其他消费者时，就可以从这个位置继续开始消费。
- 第三步：取消注册Consumer
    
    向所有broker发送UNREGISTER_CLIENT命令，取消注册Consumer。broker接收到这个命令后，将consumer从ConsumerManager中移除，然后通知这个消费者下的其他Consumer进行Rebalance。

#### 运行时触发

消费者在运行时，通过两种机制触发Rebalance

1. 监听Broker消费者数量变化通知，触发Rebalance。
1. 周期性触发Rebalance，避免Broker的Rebalance通知丢失

##### 监听Broker消费者数量变化通知，触发Rebalance

RocketMQ支持双向通信机制，在客户端通过ClientRemotingProcessor的processRequest方法来处理Broker发起的通知请求，如下：

![监听Broker消费者数量变化通知，触发Rebalance]({{ site.baseurl }}/img/mq/rocketmq-client-5.jpg)

目前，我们关注的是，消费者数量变化时，Broker给客户端的通知，也就是上图中红色框的内容。在收到通知后，其调用notifyConsumerIdsChanged进行处理，这个方法内部会立即触发Rebalance。

![监听Broker消费者数量变化通知，触发Rebalance]({{ site.baseurl }}/img/mq/rocketmq-client-6.jpg)

可以看到这里是调用mqClientFactory的rebalanceImmediately方法触发Rebalance，而前面讲解消费者启动时是通过RebalanceService触发，事实上，后者RebalanceService内部也是通过mqClientFactory进行触发Rebalance。

##### 周期性触发Rebalance，避免Rebalance通知丢失

为了避免Broker的Rebalance通知丢失问题，客户端还会通过RebalanceService定时的触发Rebalance，默认间隔是20秒，如下图：

![周期性触发Rebalance，避免Rebalance通知丢失]({{ site.baseurl }}/img/mq/rocketmq-client-7.jpg)

### Consumer的Rebalance流程

前面花了大量的篇幅，讲解了Rebalance元数据维护，Broker通知机制，以及Consumer的Rebalance触发时机，目的是让读者有一个更高层面的认知，而不是直接分析单个Consumer Rebalance的具体步骤，避免一叶障目不见泰山。先总后分的方式进行讲解。

#### Rebalance流程整体介绍

不同的触发机制最终底层都调用了MQClientInstance的doRebalance方法，而在这个方法的源码中，并没有区分哪个消费者组需要进行Rebalance，只要任意一个消费者组需要Rebalance，这台机器上启动的所有其他消费者，也都要进行Rebalance。相关源码如下所示：

MQClientInstance#doRebalance

![Rebalance流程整体介绍]({{ site.baseurl }}/img/mq/rocketmq-client-9.jpg)

述代码逐一迭代当前机器启动的所有消费者(MQConsumerInner)，并调用其doRebalance方法进行触发Rebalance。

MQConsumerInner有push模式和pull模式两种实现，分别是：

- DefaultMQPushConsumerImpl

    其会根据消费者指定的消息监听器是有序还是无序进行判定Rebalance过程中是否需要对有序消费进行特殊处理。参见`DefaultMQPushConsumerImpl#doRebalance(this.isConsumeOrderly)`。
- DefaultMQPullConsumerImpl

    总是认为是无需的，固定的为false。参见`DefaultMQPushConsumerImpl#doRebalance(false)`。


我们看到，不管是push还是pull模式的Consumer实现，内部都是调用RebalanceImpl的doRebalance方法进行触发，将是否有序作为一个参数传入。

在这个方法内部，如果一个消费者订阅了多个Topic，会迭代每个Topic维度逐一触发Rebalance。相关源码如下所示：

RebalanceImpl#doRebalance

![Rebalance流程整体介绍]({{ site.baseurl }}/img/mq/rocketmq-client-10.jpg)

**RocketMQ按照Topic维度进行Rebalance，会导致一个很严重的结果：如果一个消费者组订阅多个Topic，可能会出现分配不均，部分处于排序前列的分配更多的队列，部分消费者处于空闲状态。**

**由于订阅多个Topic时可能会出现分配不均，这是在RocketMQ中我们为什么不建议同一个消费者组订阅多个Topic的重要原因。在这一点上，Kafka与不RocketMQ同，其是将所有Topic下的所有队列合并在一起，进行Rebalance，因此相对会更加平均。**

##### 单个Topic的Rebalance流程

可以直接看源码`RebalanceImpl.rebalanceByTopic`，整体分为三个步骤

1. 获取Rebalance的元数据信息
1. 进行队列的再分配
1. 对分配结果进行处理，执行remove或者add的操作

![Rebalance流程整体介绍]({{ site.baseurl }}/img/mq/rocketmq-client-11.jpg)

1. 获得Rebalance元数据

    消费者在Rebalance时需要获得Topic的queue信息和消费者组的实例信息
1. 进行队列分配
    RocketMQ提供了多种分配策略，默认使用的是`AllocateMessageQueueAveragely`.其实现的接口都是`AllocateMessageQueueStrategy`

    1. AllocateMessageQueueAveragely：平均分配，默认
    1. AllocateMessageQueueAveragelyByCircle：循环分配
    1. AllocateMessageQueueConsistentHash：一致性哈希
    1. AllocateMessageQueueByConfig：根据配置进行分配
    1. AllocateMessageQueueByMachineRoom：根据机房
    1. AllocateMachineRoomNearby：就近分配

    这里举例来进行说明。假设某个Topic有10个队列，消费者组有3个实例c1、c2、c3，使用AllocateMessageQueueAveragely分配结果如下图所示：

    ![Rebalance流程整体介绍]({{ site.baseurl }}/img/mq/rocketmq-client-12.jpg)

    因为这是一个平均分配策略，在分配时，每个消费者(c1、c2、c3)平均分配3个，此时还多出1个，多出来的队列按顺序分配给消费者队列的头部元素，因此c1多分配1个，最终c1分配了4个队列。

    需要注意的是，每个消费者是自己给自己分配，相当于存在多个大脑。那么**如何保证分配结果的一致呢？** 通过以下两个手段来保证：

    - 对Topic队列，以及消费者各自进行排序
    - 每个消费者需要使用相同的分配策略。

1. 队列分配结果处理

    消费者计算出分配给自己的队列结果后，需要与之前进行比较，判断添加了新的队列，或者移除了之前分配的队列，也可能没有变化。

    - 对于新增的队列，需要先计算从哪个位置开始消费，接着从这个位置开始拉取消息进行消费；
    - 对于移除的队列，要移除缓存的消息，并停止拉取消息，并持久化offset。

### RocketMq的分配方案的问题

#### 每个消费者自己给自己分配，如何避免脑裂的问题？

RocketMq的分配方案是拿到此刻的所有queue和cid的列表（排序后），然后所有的都按照相同的规则平均分配或循环分配。这样可以保证按规则计算完后，一个queue只有一个消费者。

#### 如果某个消费者没有收到Rebalance通知怎么办？

RocketMQ的每个消费者实例都会启动单独的线程周期性的进行Rebalance，避免Rebalance通知丢失的情况。

#### Rebalance过程中，如何保证分配前的消费者和分配后的消费者避免重复消费？

client会通过将queue进行dropped操作，然后通过broker将该信息同步给其他已订阅该queue的消费者，消费者在处理消息时，判断queue的状态为dropped时不进行消费和offset更新。

### broker和client的通知模型是什么样？

每个客户端注册到broker后都与broker的长连接，封装了netty的多路复用的io能力，通过长连接的方式实现双向通信。可参考源码`org.apache.rocketmq.broker.processor.ClientManageProcessor#processRequest`.

**broker只会通知一次，不保证client一定会收到变更事件通知。**

## 参考

> <https://cloud.tencent.com/developer/article/1554950>