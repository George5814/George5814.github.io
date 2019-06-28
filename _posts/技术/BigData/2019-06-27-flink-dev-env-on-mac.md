---
layout: post
title: 在 Mac 上搭建 Flink 的开发环境
category: 技术
tags: BigData
keywords: 
description: 
---
 
{:toc}

## 构建读写数据的kafka 环境

### 搭建 Zookeeper 集群

参考文章: <http://followtry.cn/2015-04-04/ZooKeeper-setup.html>

### 搭建 Kafka 集群

参考文章：<http://followtry.cn/2016-05-14/kafka-setup.html>

### 搭建 kafka manager环境

Keep manager 主要用来管理 kafka，可以用来管理topic，分区和消费组等

参考文章: <https://www.cnblogs.com/dadonggg/p/8205302.html>

#### 先下载源码

源码地址：<https://github.com/yahoo/kafka-manager> ，雅虎开源的管理 kafka 集群的工具

#### 源码编译

执行命令:`./sbt clean dist` 编译源码生成安装包

#### 解压安装包

将编译出的`kafka-manager-xxx/target/universal/kafka-manager-xxx.zip`解压出`kafka-manager-xxx`

#### 设置配置文件

打开配置文件`conf/application.conf`

配置 zk 的集群信息
kafka-manager.zkhosts=“host:port,host2:port2,host3:port3”

#### 启动 kafak manager

执行命令： `kafkamanager/bin/kafka-manager -Dconfig.file=./kafkamanager/conf/application.conf -Dhttp.port=8080` ,
其中指定了配置文件`./kafkamanager/conf/application.conf`和 web 端口号 `8080`

kafka manager 地址: <http://localhost:8080/clusters>


#### 增加 kafka 集群信息

在http://localhost:8080/addCluster输入配置信息

```
Cluster name : xxx 
Cluster zookeeper Hosts(kafka 集群配置的 zk): host:port,host2:port2,host3:port
kafka  version： 查看 kafka 安装目录下的 libs 内的kafka 包版本
勾选项： 勾选上除了`JMX with SSL`的其他选项
其他选项： 可以默认或自定义配置
```

点击 save 保存即可

![编辑页面](//raw.githubusercontent.com/George5814/blog-pic/master/image/kafka/kafka-manager.png)

## 本地安装 flink

### 下载安装包并解压

`wget https://flink.apache.org/downloads.html` 下载 flink 的独立包，可直接在本地启动的。

`tar xzf flink-*.tgz`

### 调整配置

在`conf/application.yaml`中设置 web 监控页面

```yaml
rest.port: 8081
web.submit.enable: true
```


### flink 启动

执行命令`./bin/start-cluster.sh`，启动 flink


### 检查是否启动成功

web 监控页面正常打开则说明启动正常

<http://localhost:8081>

![flink-web](//raw.githubusercontent.com/George5814/blog-pic/master/image/flink/flink-web.png)


### 页面上传 jar 包执行

![flink-web-submit](//raw.githubusercontent.com/George5814/blog-pic/master/image/flink/flink-web-submit.png)

![flink-web-submit-2](//raw.githubusercontent.com/George5814/blog-pic/master/image/flink/flink-web-submit-2.png)








