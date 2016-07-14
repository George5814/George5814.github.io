---
layout: post
title: 9.hadoop-2.7.2官网文档翻译-Hadoop命令行微型集群
category: 技术
tags:  Hadoop
keywords: 
description: Hadoop文件系统的规范。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/CLIMiniCluster.html
---

{:toc}

### 目标

使用CLI微型集群，用户可以使用一条命令简单的启动或停止单节点的Hadoop集群，不需要设置任何环境变量或者管理配置文件。CLI的微型集群同时启动YARN/MapReduce和HDFS集群。

对于用户想要快速体验真是的Hadoop集群或者依靠显著的Hadoop功能测试非java程序都是很有用的。

#### Hadoop TarBall

你应该可以从发行版本中获取Hadoop的Tar包。你也可以从源码直接常见一个tar包。

```bash
$ mvn clean install -DskipTests

$ mvn package -Pdist -Dtar -DskipTests -Dmaven.javadoc.skip

```

注意：你需要安装[protoc 2.5.0](http://code.google.com/p/protobuf/){:target="_blank"}

tar包可以在`hadoop-dist/target/`目录中获得。


### 运行微型集群

从root目录提取出来tar包，你可以使用以下命令启动CLI微型集群：

```
$ bin/hadoop jar ./share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.2-tests.jar minicluster -rmport RM_PORT -jhsport JHS_PORT
```

以上是举例的一个命令。`RM_PORT`和`JHS_PORT`应该被用户选择的端口替换。如果没有指定，将会随机使用空闲的端口。

这里有一系列用户会用来控制服务启动的命令。通过其他的配置属性。可获得的命令行参数如下：

```
$ -D <property=value>    Options to pass into configuration object
$ -datanodes <arg>       How many datanodes to start (default 1)
$ -format                Format the DFS (default false)
$ -help                  Prints option help.
$ -jhsport <arg>         JobHistoryServer port (default 0--we choose)
$ -namenode <arg>        URL of the namenode (default is either the DFS
                         cluster or a temporary dir)
$ -nnport <arg>          NameNode port (default 0--we choose)
$ -nodemanagers <arg>    How many nodemanagers to start (default 1)
$ -nodfs                 Don't start a mini DFS cluster
$ -nomr                  Don't start a mini MR cluster
$ -rmport <arg>          ResourceManager port (default 0--we choose)
$ -writeConfig <path>    Save configuration to this XML file.
$ -writeDetails <path>   Write basic information to this JSON file.
```

为了显示完整的可获得参数，用户可以通过`-help`参数显示上面的命令。