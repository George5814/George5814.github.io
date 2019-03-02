---
layout: post
title: Kafka集群安装
category: 技术	
tags:  Kafka
keywords: 
description: 
---
 
{:toc} 

### 1.下载kafka

对应[scala-2.10.x](http://www.scala-lang.org/download/all.html){:target="_blank"}版本：
[ kafka_2.10-0.9.0.1.tgz](https://www.apache.org/dyn/closer.cgi?path=/kafka/0.9.0.1/kafka_2.10-0.9.0.1.tgz){:target="_blank"}


对应[scala-2.11.x](http://www.scala-lang.org/download/all.html){:target="_blank"}版本：
[ kafka_2.11-0.9.0.1.tgz](https://www.apache.org/dyn/closer.cgi?path=/kafka/0.9.0.1/kafka_2.11-0.9.0.1.tgz){:target="_blank"}

### 2.通过winscp将软件复制到VM的centos系统中

### 3.在CentOS中解压kafka到指定目录并更名

将kafka解压到`/usr/local`

```BASH
#tar -xzvf kafka_2.11-0.9.0.1.tgz  -C /usr/local/
```

重命名为简单的目录名`kafka`

```BASH
#mv /usr/local/kafka_2.11-0.9.0.1/ /usr/local/kafka
```


![kafka所在目录](//raw.githubusercontent.com/George5814/blog-pic/master/image/kafka/kafka-setup-1.png)

### 4.添加kafka环境变量并生效

添加全局环境变量

```bash
#vim /etc/profile


export KAFKA_HOME=/usr/local/kafka

export PATH=:$PATH:$JAVA_HOME/bin:$SCALA_HOME/bin:$KAFKA_HOME/bin:$ZOOKEEPER_HOME/bin
```


![kafka环境变量](//raw.githubusercontent.com/George5814/blog-pic/master/image/kafka/kafka-setup-2.png) 

使得修改后的环境变量生效

```bash
#source /etc/profile
```

### 4.安装ZooKeeper集群并启动

 本教程认为已经成功安装Zookeeper集群并成功启动。
 
 本教程启动的ZK集群为:`h2m1:2181`,`h2s1:2181`,`h2s2:2181`。

### 5.配置kafka

修改server.properties


设置broker的id，集群中每个broker的id都不能相同

```bash
broker.id=1  #集群中每个broker的id，每个节点都不能相同，类似于zookeeper的myid，必须为集群内唯一的正整数作为id

```

![kafka server 配置](//raw.githubusercontent.com/George5814/blog-pic/master/image/kafka/kafka-setup-3.png "kafka server 配置")

日志存储位置配置

```sh
log.dirs=/usr/local/kafka/kafka-logs
```

使用外部ZK集群

```sh
zookeeper.connect=h2m1:2181,h2s1:2181,h2s2:2181
```

### 6.将kafka及配置复制到其他机器组成集群

将`kafka`整个目录复制到其他机器,将`/etc/profile`复制到其他机器并使得环境变量生效。

```bash
scp -r ./kafka/ host:/usr/local/
scp /etc/profile host:/etc/
source /etc/profile ## 每台kafka节点机器上执行一遍
```

修改broker的id

```bash
broker.id=2  #修改为当前集群没使用过的整数
```

### 7.启动和停止kafka集群

集群的每个节点机器都要启动

```
kafka-server-start.sh  -daemon /usr/local/kafka/config/server.properties 
```

![kafka server 启动](//raw.githubusercontent.com/George5814/blog-pic/master/image/kafka/kafka-setup-4.png "kafka server 启动")


停止kafka的一个节点机器

```bash
 kafka-server-stop.sh 
```

被停止节点从kafka集群中移除
 
![kafka server 被停止节点](//raw.githubusercontent.com/George5814/blog-pic/master/image/kafka/kafka-setup-5.png "kafka server 被停止节点")

kafka集群未被停止节点重新选择leader

![kafka server 未被停止节点](//raw.githubusercontent.com/George5814/blog-pic/master/image/kafka/kafka-setup-6.png "kafka server 未被停止节点")



