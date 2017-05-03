---
layout: post
title: log4j2 日志发送到kafka配置实战
category: 技术
tags: Logger
keywords: 
description:  
---

{:toc}

## 1. 目的

为了方便对日志进行统计和分析，需要将业务系统日志通过kafka发送到日志处理平台，那本文就是为了解决使用log4j2将业务系统的日志发送到kafka的过程。


## 2. 实战

### 2.1 启动预备环境

需要启动zookeeper集群和kafka集群，zookeeper是为了协调kafka集群数据一致的。这两种集群都需要在每个节点上执行启动命令。

```bash
# /usr/local/zk/bin/zkServer.sh start
# /usr/local/kafka/bin/kafka-server-start.sh  -daemon /usr/local/kafka/config/server.properties 
```

检查ZK启动和kafka节点topic状态

```bash
# /usr/local/zk/bin/zkServer.sh status
# kafka-topics.sh  --zookeeper h2m1:2181 --list 
```

### 2.2 创建Topic

使用log4j2发送日志到kafka之前，需要现在kafka上创建对应的topic，否则会报错

![log4j2-kafka](http://omsz9j1wp.bkt.clouddn.com/image/log4j2/log4j2-kafka-1.png)

也就是要先创建topic才能进行日志发送操作：

![log4j2-kafka](http://omsz9j1wp.bkt.clouddn.com/image/log4j2/log4j2-kafka-3.png)

查看topic列表：

![log4j2-kafka](http://omsz9j1wp.bkt.clouddn.com/image/log4j2/log4j2-kafka-2.png)


既然kafka环境已经成功启动，并且topic已经创建成功，那么就需要配置log4j2来测试将日志发送到kafka。

### 2.3 配置log4j2

使用名称叫`log4j2-kafka.xml`的配置文件，其中只配置了console和kafka，仅作为测试用。


`log4j2-kafka.xml`内容

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration name="brief-logger" status="info" monitorInterval="5" strict="false">
  <appenders>
    <Console name="stdout" target="SYSTEM_OUT">
      <ThresholdFilter level="INFO" onMatch="ACCEPT" onMismatch="DENY"/>
      <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/>
    </Console>

    <!--kafka的配置-->
    <Kafka topic="test-esn-log-3" name="kafkaLog" >
      <PatternLayout pattern="%date %message"/>
      <!--kakfa集群的各个节点：host:port,以逗号分隔-->
      <Property name="bootstrap.servers">h2m1:9092,h2s1:9092,h2s2:9092</Property>
    </Kafka>

  </appenders>
  <loggers>

    <Logger name="org.apache.kafka" level="INFO" />
    <Root level="info">
      <AppenderRef ref="stdout"/>
      <AppenderRef ref="infoLog"/>
      <AppenderRef ref="kafkaLog"/>
    </Root>
  </loggers>
</configuration>
```

### 2.4 编写测试代码

```java
package cn.followtry.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 该类为了分别测试log4j2的不同功能，因此会指定log4j2的不同功能在不同的配置文件中 
   Created by followtry on 2017/5/2.
 */
public class LoggerTest {

  private static Logger LOGGER;

  private static String classPath;

  static {
    <!--获取到class路径-->
    classPath = Common.class.getClassLoader().getResource("").getPath();
  }

  public static void main(String[] args) {
    consoleAndKafkaLoggerTest();
  }

  public static void consoleAndKafkaLoggerTest() {
    setLogger("log4j2-kafka.xml");
    LOGGER.info("控制台和写kafka的日志信息:test kafka info");
  }
  private static void setLogger(String loggerConfigFileName) {
    //配置log4j.configurationFile属性，log4j2会优先使用该属性配置，然后才是默认配置文件名。
    System.setProperty("log4j.configurationFile",classPath + loggerConfigFileName);
    LOGGER = LoggerFactory.getLogger(LoggerTest.class);
  }
}
```

执行代码，发现报错了

![log4j2-kafka](http://omsz9j1wp.bkt.clouddn.com/image/log4j2/log4j2-kafka-4.png)

哦，想起来了，是因为没有配置kafka客户端依赖

```xml
<dependency>
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka-clients</artifactId>
  <version>0.10.2.1</version>
</dependency>
```

此处没有贴出log4j2和slf4j等日志模块的依赖包。请看[log4j2日志发送到kafka配置实战](http://followtry.cn/2017/04/26/log4j2-action.html) 
再次执行应该是不会报错了，那我怎么检查日志是否发送到了kafka呢。

### 2.5 从kafka消费日志消息

在一台kafka节点上执行命令：`kafka-console-consumer.sh --zookeeper h2m1:2181 --topic test-esn-log-3 --from-beginning` 会从头开始读取kafka中存储的对应topic的消息

![log4j2-kafka](http://omsz9j1wp.bkt.clouddn.com/image/log4j2/log4j2-kafka-5.png)

这样就将发送到kafka的日志消息消费了（仅仅打印在终端）。

## 3 总结

该文只是应用了log4j2将日志发送kafka功能的初级配置，而且封装的比较好，只需要简单的配置就可以将日志发送到kafka。以后有时间还会对其高级特性进行研究分析，欢迎各位看官的关注和交流!!




