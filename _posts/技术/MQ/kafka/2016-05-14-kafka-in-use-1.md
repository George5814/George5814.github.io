---
layout: post
title: Kafka初步使用
category: 技术	
tags:  Kafka
keywords: 
description: 
---
 
 
{:toc} 
部分操作在[kafka集群安装]({% post_url 2016-05-14-kafka-setup %}){:title="kafka集群安装"  :target="_blank"}中。


### 1.创建Topic

```bash
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test2
```



创建Topic：

![kafka 创建Topic](/public/pic/kafka/kafka-in-use-1.png "kafka 创建Topic")

查看Topic：

![kafka 查看Topic](/public/pic/kafka/kafka-in-use-2.png "kafka 查看Topic")

示例：

`kafka-topics.sh --create --topic test3 --replication-factor 2 --zookeeper h2m1:2181 --partitions 3`

示例结果说明：

```
Topic:test3     PartitionCount:3        ReplicationFactor:2     Configs:
        Topic: test3    Partition: 0    Leader: 1       Replicas: 1,2   Isr: 1,2
        Topic: test3    Partition: 1    Leader: 2       Replicas: 2,3   Isr: 2,3
        Topic: test3    Partition: 2    Leader: 3       Replicas: 3,1   Isr: 3,1
```

- Partition 三个分区，分别为"0","1","2"
- 每个分区一个leader，因三个分区存在三个节点上，所以每个分区的leader都是所在的机器。
- Replicas 备份节点，任选包含分区所在broker的两个(参数中指定的)
- Isr：存活的备份节点集合，不可能比Replicas中的集合多。

如果停掉分区3所在broker（3），则在Isr，即存活的备份节点，中就没有了id为3的broker。

### 2.发送和接收消息

```bash
kafka-console-producer.sh --broker-list localhost:9092 --topic test2
```
kafka 发送消息:

![kafka 发送消息](/public/pic/kafka/kafka-in-use-3.png "kafka 发送消息")

```bash
kafka-console-consumer.sh --zookeeper localhost:2181 --topic test2 --from-beginning
```

kafka 接收消息(集群中其他节点)：

![kafka 接收消息](/public/pic/kafka/kafka-in-use-4.png "kafka 接收消息")


### 3.查看topic的状态

查看所有topic

`kafka-topics.sh  --describe --zookeeper h2m1:2181`

查看指定的topic,可以是多个topic，用逗号分隔。

`kafka-topics.sh  --describe --zookeeper h2m1:2181,h2s1:2181 --topic test2,test`

结果内容说明：

- leader:该节点负责所有指定分区的读和写，每个节点的领导都是随机选择的。
- replicas:备份的节点，无论该节点是否是leader或者目前是否还活着，只是显示。
- isr:备份节点的集合，也就是活着的节点集合。


### 4.查看kafka服务器版本

通过查看kafka安装目录下的lib目录中kafka的jar名称中的版本。客户端和服务端的版本不相同可能会报错。

<http://stackoverflow.com/questions/27606065/how-to-find-the-kafka-version-in-Linux>
