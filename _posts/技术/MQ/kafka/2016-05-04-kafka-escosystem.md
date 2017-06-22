---
layout: post
title: LinkedIn的Kafka生态系统
category: 业闻	
tags:  Kafka
keywords: 
description: 原文地址:https://engineering.linkedin.com/blog/2016/04/kafka-ecosystem-at-linkedin,<br>转载译文：http://geek.csdn.net/news/detail/71406
---
 
{:toc} 

 

[Apache Kafka](http://kafka.apache.org/){:target="blank"} 是一个高度可扩展的消息系统，它在 LinkedIn 的中央数据管道中扮演着十分重要的角色。Kafka 是 LinkedIn 于2010年开发的消息系统，目前每天通过1400个 broker （消息中间件处理结点）处理逾1.4万亿条消息。鉴于其高耐久与低延迟两项优点，我们在 LinkedIn 使用 Kafka 为大量新的关键用例提供支持，包括使用基于 Kafka 的 replication 机制来取代 Espresso 集群间的 MySQL 主从复制（replication），支持 Venice 系统以及正在开发中的第二代 Databus 实时低延迟数据抓取系统等。

随着我们对 Kafka 的依赖迅速增长，为了支持这些用例，必须先解决掉一些重大的问题，因此我们围绕着 Kafka 开发出了一套完整的生态系统。在本文中，我会总结及列出部分解决方案，可能对 Kafka 的使用者有所帮助；另外我会列出一些即将举行的大会动向，以饕读者。

**LinkedIn 的 Kafka 生态环境**


![Kafka 生态环境](/public/pic/kafka/kafka-cosystem.jpg)


上图并未将 LinkedIn 所有各类的数据管道与拓扑结构全部列出，仅作为 LinkedIn Kafka 关键功能部署及其互动方式的图示说明。

## 核心的 Kafka 服务

### Kafka 的 Broker

每个数据中心都有各种用途的 Kafka broker 集群运行，在 LinkedIn 我们目前部署了将近1400个 broker，这些 broker 每周接收2个多 PB 的数据。我们一般使用 Apache Kafka 的 trunk 版本，大约每个季度发布一个新的内部版。

### Kafka 的 Mirror-Maker

我们使用Kafka内置的 Mirrormaker 工具来创建集群的镜像——读取源集群，生成目标集群。有多个可在同一个数据中心上或跨数据中心运行的镜像管道，Todd Palino曾撰文总结过在 LinkedIn 我们使用 Mirrormaker 来执行多管道复制的方式。

### Schema 注册中心

我们设定 [Avro](https://avro.apache.org/){:target="_blank"} 为 LinkedIn 数据管道的标准编码通用语，这样一来每个生产者（producer）都会编译 Avro 数据，在 schema 注册中心注册 Avro schema，每个序列化信息中也都要嵌入 schema-ID；而消费者（consumer）则会根据 ID 从 schema 注册中心获取相应的 schema ，以便反序列化 Avro 信息。尽管各数据中心有多个 schema 注册实例，但都受同一个包含 schema 的单个（复制）数据库支持。

### Kafka REST 代理

Kafka REST 是我们提供给非 Java 客户端的 HTTP 代理，大多 Kafka 集群都有相关的 REST 代理， Kafka REST 也是负责 topic 管理的正式网关。

### Nuage

Kafka 所提供的大多都是自助服务：用户定义自己的事件 schema ，然后开始生产 topic ；Kakfa 的 broker 通过默认设置与分区数自动生成 topic ；最后，任何消费者都能消费该 topic，这使得 Kafka 完全开放。

随着使用 Kafka 的地方越来越多，新的用例不断出现，上述方法的诸多限制也愈发明显。首先，一些对 Kafka SRE （网站可靠性工程师）有着特殊要求的 topic 需要用户自定义配置；其次对大多用户来说，发现和检查 topic 相关的元数据（比如字节率、审计完整性、schema 历史等）都十分困难；再次，Kafka 整合了各种安全功能，特定 topic 的拥有者可能想要限制自己的访问权，并自行管理 ACL。

[Nuage](https://www.linkedin.com/pulse/invisible-infrastructure-alex-vauthey){:target="_blank"}  是 LinkedIn 的在线数据架构自助服务门户，我们最近曾与 Nuage 团队合作过，在 Nuage 中添加了对 Kafka 的支持。这种做法为用户管理自己的 topic 和相关元数据提供了方便，Nuage 将 topic 的增删 （CRUD）操作指派给了负责总结 Kafka 管理工具之间细微差异的 Kafka REST。

## 各种库

### LiKafka 客户端库

LiKafka 的生产者对开源生产者进行包装，并负责 schema 注册、 Avro 编码、审计等工作，还对大型消息提供支持。审计事件会计算在10分钟内通过窗口发送给每个 topic 的事件数量。同样，消费者也负责包装开源消费者、查找 schema、Avro编码及审计任务等工作。

### Kafka 推送任务

Kafka 推送任务负责将各类数据从 Hadoop 运送到 Kafka 供在线服务消费，它们运行在公司内部（CORP）环境的 Hadoop 集群上，为 CORP 的数据部署 Kafka 集群生产数据，另有 Mirrormaker 将这些数据复制到生产（PROD）数据部署的集群上。

### Gobblin

[Gobblin](https://github.com/linkedin/gobblin){:target="_blank"} 是 LinkedIn 新数据摄取框架，负责沟通 Kafka 与 Hadoop，它取代了我们之前使用的 Camus。基本上它就是一个大型 Hadoop 任务，将 Kafka 中的所有数据复制到 Hadoop 上以供离线操作。

## 监控服务

### Kafka 监控

这是一个连续运行的 Kafka 部署验证测试套件，我们用它来验证新版发布，并监控现有的部署情况。目前我们用它来监控基础却十分关键的指标，比如端对端延迟以及数据丢失。我们预想未来在测试集群中，使用这一框架来连续测试管理操作的正确性（比如分区再分配），甚至利用诸如 Simoorg 之类的故障注入框架来确保即便有各种故障存在的情况下，我们也能满足可用的 SLA 要求。

### Kafka 审计

在我们的跟踪审计框架中有两个关键组件：

- 一个 Kafka 审计服务负责消费并重新计算 Kafka 集群的所有数据，并发出与被追踪生产者相似的审计事件数量。我们可以按照生产者计数来调整 Kafka 集群的计数，及时检测到数据丢失的情况。

- Kafka 的审计验证服务会持续监控数据的完整度，并为审计追踪的可视化提供相应 UI。这个服务是审计事件的消费者，它会插入审计数据库中，并在数据出现延迟或丢失时发出警告。我们使用审计数据库来调查警告出现的情况，并确切定义数据丢失的位置。

![Kafka 审计](/public/pic/kafka/kafka-cosystem-2.jpg)

## Kafka 生态环境

### Burrow

[Burrow](https://github.com/linkedin/Burrow){:target="_blank"}  是监控 Kafka 消费者健康情况这一棘手问题的恰当方式，它为我们提供了消费者状态的综合概览，并提供了无需指定阈值的消费者延迟验证服务。Burrow 会按照 topic 分区来监控所有消费者所提交数据的偏差值，并根据需求计算相应消费者的状态。

### 在 LinkedIn 的流处理

[Samza](http://samza.apache.org/){:target="_blank"} 是 LinkedIn 的流处理平台，它授权用户获取自己流处理任务，并第一时间运行在生产环境中。在流处理领域，各种讨论一直很积极，有很多开源系统也负责类似的工作。但与其他的流处理系统不同，我们之前并未涉及较大范围的功能集，而是着重于让 Samza 在可用性、性能与规模运行方面获得提高。不过现在，鉴于我们有很多生产工作负载已经启动运行，也能将注意力转向功能集的扩展了。之前曾有文章描述过我们在相关生产用例上所实现的功能细节，包括分析、网站监控、安全性等许多新功能。