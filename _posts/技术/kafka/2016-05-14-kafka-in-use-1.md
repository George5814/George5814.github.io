---
layout: post
title: Kafka初步使用
category: 技术	
tags:  Kafka
keywords: 
description: 
---
 
 
{:toc} 

### 1.创建Topic

```bash
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test2

```

创建Topic：

![kafka 创建Topic](/public/pic/kafka/kafka-in-use-1.png "kafka 创建Topic")

查看Topic：

![kafka 查看Topic](/public/pic/kafka/kafka-in-use-2.png "kafka 查看Topic")

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
