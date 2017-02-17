---
layout: post
title: HDFS基于QJM的HA之路
category: 技术
tags:  Hadoop
keywords: 
description: HDFS基于QJM的HA之路
---

{:toc}

## hdfs haadmin 命令

用法：

```
Usage:hdfs haadmin
    [-transitionToActive [--forceactive] <serviceId>]
    [-transitionToStandby <serviceId>]
    [-failover [--forcefence] [--forceactive] <serviceId> <serviceId>]
    [-getServiceState <serviceId>]
    [-checkHealth <serviceId>]
    [-help <command>]

Generic options supported are
-conf <configuration file>     specify an application configuration file
-D <property=value>            use value for given property
-fs <local|namenode:port>      specify a namenode
-jt <local|resourcemanager:port>    specify a ResourceManager
-files <comma separated list of files>    specify comma separated files to be copied to the map reduce cluster
-libjars <comma separated list of jars>    specify comma separated jar files to include in the classpath.
-archives <comma separated list of archives>    specify comma separated archives to be unarchived on the compute machines.

The general command line syntax is
bin/hadoop command [genericOptions] [commandOptions]
```

## 启动JobHistory进程

`../../sbin/mr-jobhistory-daemon.sh --config ../ start historyserver`

启动成功后的日志：

```
starting historyserver, logging to /usr/local/hadoop271/logs/mapred-root-historyserver-h2m1.out
2016-11-12 09:18:53,028 INFO  [main] hs.JobHistoryServer (LogAdapter.java:info(45)) - STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting JobHistoryServer
STARTUP_MSG:   host = h2m1/192.168.31.101
STARTUP_MSG:   args = []
STARTUP_MSG:   version = 2.7.1
STARTUP_MSG:   classpath = ../:/usr/local/hadoop271/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:{太多，省略}/usr/local/hadoop271/modules/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r 15ecc87ccf4a0228f35af08fc56de536e6ce657a; compiled by 'jenkins' on 2015-06-29T06:04Z
STARTUP_MSG:   java = 1.7.0_75
************************************************************/
```

## Hadoop集群基于QJM的HA

### 硬件资源

- Namenode机器 ：两个完全相同的NN机器。

- JournalNode机器：运行JournalNode的机器。JournalNode进程相对轻量，因此可以	与NN，JobTracker和YARN RM放在一台机器上。

**注意**：JournalNode最少要有三个节点，与ZooKeeper集群一样，需要多数节点存在保证有效。

在HA中，因为备用NN会执行namespaces状态的检查点，因此在HA集群中可以不需要启动SNN，CpN或者BN。但事实上这样做是错的。


### 按官方要求修改完配置


修改`hdfs-site.xml`文件

```xml
<property>
  <name>dfs.nameservices</name>
  <value>mycluster</value>
</property>

<!-- value标签中填写的是任意的 nameservices id-->

<property>
  <name>dfs.ha.namenodes.mycluster</name>
  <value>nn1,nn2</value>
</property>

<!-- value标签中填写的是需要指定为NN的机器的namenode id -->

```

**注意：**当前情况下，每个nameservices最多只能配置2个NN，在Hadoop3.0中可以配置多个（多于两个）NN。


```xml
<property>
  <name>dfs.namenode.rpc-address.mycluster.nn1</name>
  <value>h2m1:8020</value>
  <description>namenode id为nn1的提供RPC服务的主机及端口号</description>
</property>
<property>
  <name>dfs.namenode.rpc-address.mycluster.nn2</name>
  <value>h2s1:8020</value>
  <description>namenode id为nn2的提供RPC服务的主机及端口号</description>
</property>
```

`value`标签中填写的是`host:port`

**注意：**你也可相似的配置`servicerpc-address`,只要你愿意。

```xml
<property>
  <name>dfs.namenode.http-address.mycluster.nn1</name>
  <value>h2m1:50070</value>
  <description>namenode id为nn1的提供HTTP web服务的主机及端口号，可自定义</description>
</property>
<property>
  <name>dfs.namenode.http-address.mycluster.nn2</name>
  <value>h2s1:50070</value>
  <description>namenode id为nn2的提供HTTP web服务的主机及端口号，可自定义</description>
</property>
```

**注意：**如果你的Hadoop启用了安全功能，你应该在每个NN上也设置`https-address`

```xml
<property>
  <name>dfs.namenode.shared.edits.dir</name>
  <value>qjournal://h2m1:8485;h2m1:8485;h2m1:8485/mycluster</value>
</property>
```
qjournal://*host1:port1*;*host2:port2*;*host3:port3*/*journalId*

**其中qjournal为协议，h2m1等为指定的主机名，8485位提供服务的端口，mycluster为nameservices id**

```xml
<property>
  <name>dfs.client.failover.proxy.provider.mycluster</name>
  <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
</property>
```

```xml
 <property>
      <name>dfs.ha.fencing.methods</name>
      <value>sshfence</value>
    </property>

    <property>
      <name>dfs.ha.fencing.ssh.private-key-files</name>
      <value>/root/.ssh/id_rsa</value>
    </property>
```
**配置指定用户的私钥**

**很重要的一点是在使用QJM时，只有一个NameNode会被允许写到JournalNode中，因此不会有分离的方案搞乱文件系统的元数据**

在`core-site.xml`中，

```xml
<property>
  <name>fs.defaultFS</name>
  <value>hdfs://mycluster</value>
</property>

<property>
  <name>dfs.journalnode.edits.dir</name>
  <value>/path/to/journal/node/local/data</value>
</property>
```

### 详细部署

在每台机器上执行`hadoop-daemon.sh start journalnode`,以便journalnode进程都已经启动。

1. 如果你建立新的HDFS集群， 你应该在每个NN上运行格式化命令`hdfs namenode -format  -clusterId hadoop-cluster-jingzz`

```
16/11/13 01:37:31 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = h2m1/192.168.31.101
STARTUP_MSG:   args = [-format, -clusterId, hadoop-cluster-jingzz]
STARTUP_MSG:   version = 2.7.1
STARTUP_MSG:   classpath = /usr/local/hadoop271/etc/hadoop:/usr/local/hadoop271/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/usr/local/hadoop271/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-compress-1.4.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/usr/local/hadoop271/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/usr/local/hadoop271/share/hadoop/common/lib/asm-3.2.jar:/usr/local/hadoop271/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/usr/local/hadoop271/share/hadoop/common/lib/mockito-all-1.8.5.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-logging-1.1.3.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-math3-3.1.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/servlet-api-2.5.jar:/usr/local/hadoop271/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/usr/local/hadoop271/share/hadoop/common/lib/jetty-6.1.26.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-httpclient-3.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-io-2.4.jar:/usr/local/hadoop271/share/hadoop/common/lib/gson-2.2.4.jar:/usr/local/hadoop271/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop271/share/hadoop/common/lib/hadoop-auth-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-digester-1.8.jar:/usr/local/hadoop271/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/usr/local/hadoop271/share/hadoop/common/lib/jsp-api-2.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/xmlenc-0.52.jar:/usr/local/hadoop271/share/hadoop/common/lib/activation-1.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/stax-api-1.0-2.jar:/usr/local/hadoop271/share/hadoop/common/lib/jersey-server-1.9.jar:/usr/local/hadoop271/share/hadoop/common/lib/hadoop-annotations-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-configuration-1.6.jar:/usr/local/hadoop271/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/common/lib/log4j-1.2.17.jar:/usr/local/hadoop271/share/hadoop/common/lib/hamcrest-core-1.3.jar:/usr/local/hadoop271/share/hadoop/common/lib/httpcore-4.2.5.jar:/usr/local/hadoop271/share/hadoop/common/lib/zookeeper-3.4.6.jar:/usr/local/hadoop271/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/usr/local/hadoop271/share/hadoop/common/lib/jersey-json-1.9.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-codec-1.4.jar:/usr/local/hadoop271/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/usr/local/hadoop271/share/hadoop/common/lib/netty-3.6.2.Final.jar:/usr/local/hadoop271/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/usr/local/hadoop271/share/hadoop/common/lib/jettison-1.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/curator-framework-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/jets3t-0.9.0.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-net-3.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/avro-1.7.4.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-lang-2.6.jar:/usr/local/hadoop271/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/usr/local/hadoop271/share/hadoop/common/lib/guava-11.0.2.jar:/usr/local/hadoop271/share/hadoop/common/lib/junit-4.11.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-cli-1.2.jar:/usr/local/hadoop271/share/hadoop/common/lib/httpclient-4.2.5.jar:/usr/local/hadoop271/share/hadoop/common/lib/jsr305-3.0.0.jar:/usr/local/hadoop271/share/hadoop/common/lib/paranamer-2.3.jar:/usr/local/hadoop271/share/hadoop/common/lib/jetty-util-6.1.26.jar:/usr/local/hadoop271/share/hadoop/common/lib/xz-1.0.jar:/usr/local/hadoop271/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-collections-3.2.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/curator-client-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/jsch-0.1.42.jar:/usr/local/hadoop271/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/usr/local/hadoop271/share/hadoop/common/lib/jersey-core-1.9.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/usr/local/hadoop271/share/hadoop/common/hadoop-nfs-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/hadoop-common-2.7.1-tests.jar:/usr/local/hadoop271/share/hadoop/common/hadoop-common-2.7.1.jar:/usr/local/hadoop271/share/hadoop/hdfs:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/asm-3.2.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-io-2.4.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/guava-11.0.2.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/usr/local/hadoop271/share/hadoop/hdfs/hadoop-hdfs-2.7.1-tests.jar:/usr/local/hadoop271/share/hadoop/hdfs/hadoop-hdfs-2.7.1.jar:/usr/local/hadoop271/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jersey-client-1.9.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/javax.inject-1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/asm-3.2.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/servlet-api-2.5.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jetty-6.1.26.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-io-2.4.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/activation-1.1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jersey-server-1.9.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/aopalliance-1.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/log4j-1.2.17.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/guice-3.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jersey-json-1.9.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-codec-1.4.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jettison-1.1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-lang-2.6.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/guava-11.0.2.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-cli-1.2.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/xz-1.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-collections-3.2.1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jersey-core-1.9.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-registry-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-common-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-client-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-api-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-common-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/javax.inject-1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/asm-3.2.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/guice-3.0.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/junit-4.11.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/xz-1.0.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.1-tests.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.1.jar:/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r 15ecc87ccf4a0228f35af08fc56de536e6ce657a; compiled by 'jenkins' on 2015-06-29T06:04Z
STARTUP_MSG:   java = 1.7.0_75
************************************************************/
16/11/13 01:37:31 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
16/11/13 01:37:31 INFO namenode.NameNode: createNameNode [-format, -clusterId, hadoop-cluster-jingzz]
16/11/13 01:37:32 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Formatting using clusterid: hadoop-cluster-jingzz
16/11/13 01:37:33 INFO namenode.FSNamesystem: No KeyProvider found.
16/11/13 01:37:33 INFO namenode.FSNamesystem: fsLock is fair:true
16/11/13 01:37:33 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
16/11/13 01:37:33 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
16/11/13 01:37:33 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
16/11/13 01:37:33 INFO blockmanagement.BlockManager: The block deletion will start around 2016 Nov 13 01:37:33
16/11/13 01:37:33 INFO util.GSet: Computing capacity for map BlocksMap
16/11/13 01:37:33 INFO util.GSet: VM type       = 64-bit
16/11/13 01:37:33 INFO util.GSet: 2.0% max memory 966.7 MB = 19.3 MB
16/11/13 01:37:33 INFO util.GSet: capacity      = 2^21 = 2097152 entries
16/11/13 01:37:33 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
16/11/13 01:37:33 INFO blockmanagement.BlockManager: defaultReplication         = 3
16/11/13 01:37:33 INFO blockmanagement.BlockManager: maxReplication             = 512
16/11/13 01:37:33 INFO blockmanagement.BlockManager: minReplication             = 1
16/11/13 01:37:33 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
16/11/13 01:37:33 INFO blockmanagement.BlockManager: shouldCheckForEnoughRacks  = false
16/11/13 01:37:33 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
16/11/13 01:37:33 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
16/11/13 01:37:33 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
16/11/13 01:37:33 INFO namenode.FSNamesystem: fsOwner             = root (auth:SIMPLE)
16/11/13 01:37:33 INFO namenode.FSNamesystem: supergroup          = supergroup
16/11/13 01:37:33 INFO namenode.FSNamesystem: isPermissionEnabled = false
16/11/13 01:37:33 INFO namenode.FSNamesystem: Determined nameservice ID: c
16/11/13 01:37:33 INFO namenode.FSNamesystem: HA Enabled: true
16/11/13 01:37:33 INFO namenode.FSNamesystem: Append Enabled: true
16/11/13 01:37:34 INFO util.GSet: Computing capacity for map INodeMap
16/11/13 01:37:34 INFO util.GSet: VM type       = 64-bit
16/11/13 01:37:34 INFO util.GSet: 1.0% max memory 966.7 MB = 9.7 MB
16/11/13 01:37:34 INFO util.GSet: capacity      = 2^20 = 1048576 entries
16/11/13 01:37:34 INFO namenode.FSDirectory: ACLs enabled? false
16/11/13 01:37:34 INFO namenode.FSDirectory: XAttrs enabled? true
16/11/13 01:37:34 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
16/11/13 01:37:34 INFO namenode.NameNode: Caching file names occuring more than 10 times
16/11/13 01:37:34 INFO util.GSet: Computing capacity for map cachedBlocks
16/11/13 01:37:34 INFO util.GSet: VM type       = 64-bit
16/11/13 01:37:34 INFO util.GSet: 0.25% max memory 966.7 MB = 2.4 MB
16/11/13 01:37:34 INFO util.GSet: capacity      = 2^18 = 262144 entries
16/11/13 01:37:34 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
16/11/13 01:37:34 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
16/11/13 01:37:34 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
16/11/13 01:37:34 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
16/11/13 01:37:34 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
16/11/13 01:37:34 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
16/11/13 01:37:34 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
16/11/13 01:37:34 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
16/11/13 01:37:34 INFO util.GSet: Computing capacity for map NameNodeRetryCache
16/11/13 01:37:34 INFO util.GSet: VM type       = 64-bit
16/11/13 01:37:34 INFO util.GSet: 0.029999999329447746% max memory 966.7 MB = 297.0 KB
16/11/13 01:37:34 INFO util.GSet: capacity      = 2^15 = 32768 entries
Re-format filesystem in Storage Directory /usr/local/hadoop271/data/dfs/nn/name ? (Y or N) y
Re-format filesystem in Storage Directory /usr/local/hadoop271/data/dfs/nn/edits ? (Y or N) y
Re-format filesystem in QJM to [192.168.31.101:8485, 192.168.31.102:8485, 192.168.31.103:8485] ? (Y or N) y
16/11/13 01:37:39 INFO namenode.FSImage: Allocated new BlockPoolId: BP-1190435511-192.168.31.101-1479029859389
16/11/13 01:37:39 INFO common.Storage: Storage directory /usr/local/hadoop271/data/dfs/nn/name has been successfully formatted.
16/11/13 01:37:39 INFO common.Storage: Storage directory /usr/local/hadoop271/data/dfs/nn/edits has been successfully formatted.
16/11/13 01:37:39 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
16/11/13 01:37:39 INFO util.ExitUtil: Exiting with status 0
16/11/13 01:37:39 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at h2m1/192.168.31.101
************************************************************/
```

2. 如果你已经格式化过NN，或者如果从HA到非HA，你需要将你的NN的元数据目录复制到另一个，

在未格式化的NN上执行`hdfs namenode -bootstrapStandby`命令。
运行该命令会确保通过`dfs.namenode.shared.edits.dir`配置的JournalNode包含足够的edits协定以便启动两个NN。

**注意：**在新的HA集群中，第一个NN使用format命令格式化后，第二个NN不能再格式化了，而是需要使用`scp`命令将第一个NN节点上的data目录复制到第二个NN节点的相同位置。否则会报错`Initialization failed for Block pool`，因为格式化了两次。会导致第二个NN启动不起来。DN无法连接到NN上并一直在重试，直到最终失败。

3. 如果要将一个非HA的NN转到HA，要执行`hdfs namenode -initializeSharedEdits`,会使用本地NN编辑目录的编辑数据初始化JournalNodes

执行成功的结果:

```
[root@h2m1 hadoop]# 
16/11/12 09:06:38 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = h2m1/192.168.31.101
STARTUP_MSG:   args = [-initializeSharedEdits]
STARTUP_MSG:   version = 2.7.1
STARTUP_MSG:   classpath = /usr/local/hadoop271/etc/hadoop:/usr/local/hadoop271/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:{太多，中间的jar省略}/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r 15ecc87ccf4a0228f35af08fc56de536e6ce657a; compiled by 'jenkins' on 2015-06-29T06:04Z
STARTUP_MSG:   java = 1.7.0_75
************************************************************/
16/11/12 09:06:38 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
16/11/12 09:06:38 INFO namenode.NameNode: createNameNode [-initializeSharedEdits]
16/11/12 09:06:39 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
16/11/12 09:06:40 WARN namenode.FSNamesystem: Only one image storage directory (dfs.namenode.name.dir) configured. Beware of data loss due to lack of redundant storage directories!
16/11/12 09:06:40 WARN namenode.FSNamesystem: Only one namespace edits storage directory (dfs.namenode.edits.dir) configured. Beware of data loss due to lack of redundant storage directories!
16/11/12 09:06:40 INFO namenode.FSNamesystem: No KeyProvider found.
16/11/12 09:06:40 INFO namenode.FSNamesystem: fsLock is fair:true
16/11/12 09:06:40 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
16/11/12 09:06:40 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
16/11/12 09:06:40 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
16/11/12 09:06:40 INFO blockmanagement.BlockManager: The block deletion will start around 2016 Nov 12 09:06:40
16/11/12 09:06:40 INFO util.GSet: Computing capacity for map BlocksMap
16/11/12 09:06:40 INFO util.GSet: VM type       = 64-bit
16/11/12 09:06:40 INFO util.GSet: 2.0% max memory 966.7 MB = 19.3 MB
16/11/12 09:06:40 INFO util.GSet: capacity      = 2^21 = 2097152 entries
16/11/12 09:06:40 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
16/11/12 09:06:40 INFO blockmanagement.BlockManager: defaultReplication         = 3
16/11/12 09:06:40 INFO blockmanagement.BlockManager: maxReplication             = 512
16/11/12 09:06:40 INFO blockmanagement.BlockManager: minReplication             = 1
16/11/12 09:06:40 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
16/11/12 09:06:40 INFO blockmanagement.BlockManager: shouldCheckForEnoughRacks  = false
16/11/12 09:06:40 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
16/11/12 09:06:40 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
16/11/12 09:06:40 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
16/11/12 09:06:40 INFO namenode.FSNamesystem: fsOwner             = root (auth:SIMPLE)
16/11/12 09:06:40 INFO namenode.FSNamesystem: supergroup          = supergroup
16/11/12 09:06:40 INFO namenode.FSNamesystem: isPermissionEnabled = false
16/11/12 09:06:40 INFO namenode.FSNamesystem: Determined nameservice ID: c
16/11/12 09:06:40 INFO namenode.FSNamesystem: HA Enabled: true
16/11/12 09:06:40 INFO namenode.FSNamesystem: Append Enabled: true
16/11/12 09:06:41 INFO util.GSet: Computing capacity for map INodeMap
16/11/12 09:06:41 INFO util.GSet: VM type       = 64-bit
16/11/12 09:06:41 INFO util.GSet: 1.0% max memory 966.7 MB = 9.7 MB
16/11/12 09:06:41 INFO util.GSet: capacity      = 2^20 = 1048576 entries
16/11/12 09:06:41 INFO namenode.FSDirectory: ACLs enabled? false
16/11/12 09:06:41 INFO namenode.FSDirectory: XAttrs enabled? true
16/11/12 09:06:41 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
16/11/12 09:06:41 INFO namenode.NameNode: Caching file names occuring more than 10 times
16/11/12 09:06:41 INFO util.GSet: Computing capacity for map cachedBlocks
16/11/12 09:06:41 INFO util.GSet: VM type       = 64-bit
16/11/12 09:06:41 INFO util.GSet: 0.25% max memory 966.7 MB = 2.4 MB
16/11/12 09:06:41 INFO util.GSet: capacity      = 2^18 = 262144 entries
16/11/12 09:06:41 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
16/11/12 09:06:41 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
16/11/12 09:06:41 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
16/11/12 09:06:41 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
16/11/12 09:06:41 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
16/11/12 09:06:41 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
16/11/12 09:06:41 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
16/11/12 09:06:41 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
16/11/12 09:06:41 INFO util.GSet: Computing capacity for map NameNodeRetryCache
16/11/12 09:06:41 INFO util.GSet: VM type       = 64-bit
16/11/12 09:06:41 INFO util.GSet: 0.029999999329447746% max memory 966.7 MB = 297.0 KB
16/11/12 09:06:41 INFO util.GSet: capacity      = 2^15 = 32768 entries
16/11/12 09:06:41 INFO common.Storage: Lock on /root/hadoop/dfs/nn/name/in_use.lock acquired by nodename 41461@h2m1
16/11/12 09:06:41 INFO common.Storage: Lock on /root/hadoop/dfs/nn/edits/in_use.lock acquired by nodename 41461@h2m1
16/11/12 09:06:41 INFO namenode.FSImage: No edit log streams selected.
16/11/12 09:06:41 INFO namenode.FSImageFormatPBINode: Loading 38 INodes.
16/11/12 09:06:41 INFO namenode.FSImageFormatProtobuf: Loaded FSImage in 0 seconds.
16/11/12 09:06:41 INFO namenode.FSImage: Loaded image for txid 271 from /root/hadoop/dfs/nn/name/current/fsimage_0000000000000000271
16/11/12 09:06:41 INFO namenode.FSNamesystem: Need to save fs image? false (staleImage=false, haEnabled=true, isRollingUpgrade=false)
16/11/12 09:06:41 INFO namenode.NameCache: initialized with 0 entries 0 lookups
16/11/12 09:06:41 INFO namenode.FSNamesystem: Finished loading FSImage in 456 msecs
16/11/12 09:06:42 INFO namenode.FileJournalManager: Recovering unfinalized segments in /root/hadoop/dfs/nn/edits/current
16/11/12 09:06:43 INFO namenode.FileJournalManager: Finalizing edits file /root/hadoop/dfs/nn/edits/current/edits_inprogress_0000000000000000273 -> /root/hadoop/dfs/nn/edits/current/edits_0000000000000000273-0000000000000000273
16/11/12 09:06:43 INFO client.QuorumJournalManager: Starting recovery process for unclosed journal segments...
16/11/12 09:06:43 INFO client.QuorumJournalManager: Successfully started new epoch 1
16/11/12 09:06:43 INFO namenode.EditLogInputStream: Fast-forwarding stream '/root/hadoop/dfs/nn/edits/current/edits_0000000000000000272-0000000000000000272' to transaction ID 272
16/11/12 09:06:43 INFO namenode.FSEditLog: Starting log segment at 272
16/11/12 09:06:43 INFO namenode.FSEditLog: Ending log segment 272
16/11/12 09:06:43 INFO namenode.FSEditLog: Number of transactions: 1 Total time for transactions(ms): 1 Number of transactions batched in Syncs: 0 Number of syncs: 1 SyncTimes(ms): 154 
16/11/12 09:06:43 INFO namenode.EditLogInputStream: Fast-forwarding stream '/root/hadoop/dfs/nn/edits/current/edits_0000000000000000273-0000000000000000273' to transaction ID 272
16/11/12 09:06:43 INFO namenode.FSEditLog: Starting log segment at 273
16/11/12 09:06:43 INFO namenode.FSEditLog: Ending log segment 273
16/11/12 09:06:43 INFO namenode.FSEditLog: Number of transactions: 1 Total time for transactions(ms): 0 Number of transactions batched in Syncs: 0 Number of syncs: 1 SyncTimes(ms): 5 
16/11/12 09:06:43 INFO util.ExitUtil: Exiting with status 0
16/11/12 09:06:43 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at h2m1/192.168.31.101
************************************************************/
```

停止JournalNode节点，`hadoop-daemon.sh stop journalnode`

到这里，你可以像正常启动一个NN一样启动两个HA的NN。

### 管理员命令

```
Usage: haadmin
    [-transitionToActive <serviceId>]
    [-transitionToStandby <serviceId>]
    [-failover [--forcefence] [--forceactive] <serviceId> <serviceId>]
    [-getServiceState <serviceId>]
    [-checkHealth <serviceId>]
    [-help <command>]
```

### 自动故障转换：


在每台ZooKeeper节点机器上执行`zkServer.sh start`命令启动ZooKeeper节点。

#### 组件

自动故障转换在HDFS部署中添加了两个新的组件：ZK quorum和ZKFC进程。

该自动HDFS故障转换依赖ZK的一下特征：

- 故障检测 
	
	集群中每个NN机器都在ZK中有持久化session。如果机器挂掉，ZKsession会失效，提醒其他NN要触发故障转换。
	
- 主NN选择  ZK提供了简单的机制来执行选择一个节点作为主节点。如果当前主节点崩溃，另一个节点会获得在ZK中指明的指定排他锁，然后它自己会成为下一个主节点。



ZKFC是作为ZK客户端的心组件，监控和管理NN的状态。
每一个运行NN的机器上回运行ZKFC，ZKFC会回应：

- 健康监测   
	ZFKC会定时ping，只要NN按时回应一个健康状态，ZFKC就认为该节点是健康的。
	如果节点崩溃，冻结或者进入非健康状态，健康检测器就会将其标记为非健康状态。
	
- ZK的session管理  
	当本地的NN是健康的，ZKFC会持有ZK内开放的session。如果本地NN是主，它也或持有指定的znode锁。该锁用于ZK支持短暂的节点。
	如果session过期，锁节点会自动被删除。
	
- 基于ZK的选举 
	如果本地NN是健康的，ZFKC发现没有其他节点持有znode锁，会尝试请求该锁，如果成功了，则赢得了该选举。然后会负责运行故障转换来使得它本地的NN为主节点。tail故障转移类似于上面描述的手动故障转移：
	- 首先原来的主节点被围住然后本地的NN转换为主节点状态。

#### 部署ZooKeeper集群


#### 开始之前

在开始配置自动故障转换之前，应该关闭集群。如果集群在运行中，可能不会从手动故障转换切换到自动故障转换。

#### 配置自动故障转换

在`hdfs-site.xml`中添加：

```xml
 <property>
   <name>dfs.ha.automatic-failover.enabled</name>
   <value>true</value>
 </property>
```

在`core-site.xml`中添加：

```xml
<property>
   <name>ha.zookeeper.quorum</name>
   <value>h2m1:2181,h2s1:2181,h2s2:2181</value>
 </property>
```

#### 在ZK中初始化HA状态

在NN其中一台机器上执行
```bash
 $HADOOP_PREFIX/bin/hdfs zkfc -formatZK
``` 

结果为：

```
[root@h2m1 hadoop]# ../../bin/hdfs zkfc -formatZK
16/11/12 09:31:52 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
16/11/12 09:31:52 INFO tools.DFSZKFailoverController: Failover controller configured for NameNode NameNode at h2m1/192.168.31.101:8020
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:zookeeper.version=3.4.6-1569965, built on 02/20/2014 09:09 GMT
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:host.name=h2m1
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:java.version=1.7.0_75
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:java.vendor=Oracle Corporation
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:java.home=/usr/local/jdk/jre
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:java.class.path=/usr/local/hadoop271/etc/hadoop:/usr/local/hadoop271/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:{太多，省略}/contrib/capacity-scheduler/*.jar
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:java.library.path=/usr/local/hadoop271/lib/native
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:java.io.tmpdir=/tmp
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:java.compiler=<NA>
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:os.name=Linux
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:os.arch=amd64
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:os.version=2.6.32-431.el6.x86_64
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:user.name=root
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:user.home=/root
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Client environment:user.dir=/usr/local/hadoop271/etc/hadoop
16/11/12 09:31:52 INFO zookeeper.ZooKeeper: Initiating client connection, connectString=h2m1:2181,h2s1:2181,h2s2:2181 sessionTimeout=5000 watcher=org.apache.hadoop.ha.ActiveStandbyElector$WatcherWithClientRef@69e3f60e
16/11/12 09:31:52 INFO zookeeper.ClientCnxn: Opening socket connection to server h2s2/192.168.31.103:2181. Will not attempt to authenticate using SASL (unknown error)
16/11/12 09:31:52 INFO zookeeper.ClientCnxn: Socket connection established to h2s2/192.168.31.103:2181, initiating session
16/11/12 09:31:53 INFO zookeeper.ClientCnxn: Session establishment complete on server h2s2/192.168.31.103:2181, sessionid = 0x3585994ce380000, negotiated timeout = 5000
16/11/12 09:31:53 INFO ha.ActiveStandbyElector: Session connected.
16/11/12 09:31:53 INFO ha.ActiveStandbyElector: Successfully created /hadoop-ha/c in ZK.
16/11/12 09:31:53 INFO zookeeper.ClientCnxn: EventThread shut down
16/11/12 09:31:53 INFO zookeeper.ZooKeeper: Session: 0x3585994ce380000 closed
```

其中`Successfully created /hadoop-ha/c in ZK`中的`/c`为`nameservice`的命名。


### 使用`start-dfs.sh`启动集群

`start-dfs.sh`

成功启动的日志：

```
16/11/12 09:36:27 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Starting namenodes on [h2m1 h2s1]
h2s1: starting namenode, logging to /usr/local/hadoop271/logs/hadoop-root-namenode-h2s1.out
h2m1: starting namenode, logging to /usr/local/hadoop271/logs/hadoop-root-namenode-h2m1.out
h2s2: starting datanode, logging to /usr/local/hadoop271/logs/hadoop-root-datanode-h2s2.out
h2s1: starting datanode, logging to /usr/local/hadoop271/logs/hadoop-root-datanode-h2s1.out
h2m1: starting datanode, logging to /usr/local/hadoop271/logs/hadoop-root-datanode-h2m1.out
Starting journal nodes [h2m1 h2s1 h2s2]
h2s2: starting journalnode, logging to /usr/local/hadoop271/logs/hadoop-root-journalnode-h2s2.out
h2s1: starting journalnode, logging to /usr/local/hadoop271/logs/hadoop-root-journalnode-h2s1.out
h2m1: starting journalnode, logging to /usr/local/hadoop271/logs/hadoop-root-journalnode-h2m1.out
16/11/12 09:36:57 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Starting ZK Failover Controllers on NN hosts [h2m1 h2s1]
h2s1: starting zkfc, logging to /usr/local/hadoop271/logs/hadoop-root-zkfc-h2s1.out
h2m1: starting zkfc, logging to /usr/local/hadoop271/logs/hadoop-root-zkfc-h2m1.out
```

#### 手动启动集群

需要手动启动运行NN的每一个机器上的ZFKC

`$HADOOP_PREFIX/sbin/hadoop-daemon.sh --script $HADOOP_PREFIX/bin/hdfs start zkfc`


疑问：

1. h2m1上的Namenode为什么会挂掉

错误：

1. Incompatible namespaceID for journal Storage Directory /usr/local/hadoop271/journal/node/local/data/c: Nam     eNode has nsId 633782188 but storage has nsId 1638252635

暂时的解决办法，在第一次使用`start-dfs.sh`启动时，NN会报错。
那就在报错的NN节点上再次执行`start-dfs.sh`命令。
