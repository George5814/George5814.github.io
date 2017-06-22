---
layout: post
title: 从零学hadoop-搭建基础（单点）的Hdfs环境
category: 技术
tags:  Hadoop
keywords: 
description:   
---


{:toc}

## 前提

- 已经安装好了hadoop

- 配置好了ssh的免密登录

- 还原配置文件，从零开始配置hadoop环境

- 主机h2m1的ip为192.168.2.201


## 实战


### 1. 配置HDFS

`core-site.xml`文件

```xml
<configuration xmlns:xi="http://www.w3.org/2001/XInclude">
        <xi:include href="mountTable.xml">
                <xi:fallback></xi:fallback>
        </xi:include>
        <property>
                <name>fs.defaultFS</name>
                <!--指定hdfs的host:port-->
                <value>hdfs://h2m1:9000</value>
        </property>
</configuration>
```

`hdfs-site.xml`文件

```xml
<configuration>
        <!--配置副本数-->
        <property>
                <name>dfs.replication</name>
                <value>1</value>
                <description>replication num</description>
        </property>
</configuration>
```

### 2. 格式化namenode

命令：`hdfs namenode -format`

日志信息

```property
DEPRECATED: Use of this script to execute hdfs command is deprecated.
Instead use the hdfs command for it.

17/05/26 12:54:29 INFO namenode.NameNode: STARTUP_MSG: 
/************************************************************
STARTUP_MSG: Starting NameNode
STARTUP_MSG:   host = h2m1/192.168.2.201
STARTUP_MSG:   args = [-format]
STARTUP_MSG:   version = 2.7.1
STARTUP_MSG:   classpath = /usr/local/hadoop271/etc/hadoop:/usr/local/hadoop271/share/hadoop/common/lib/apacheds-i18n-2.0.0-M15.jar:/usr/local/hadoop271/share/hadoop/common/lib/jaxb-impl-2.2.3-1.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-beanutils-1.7.0.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-compress-1.4.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/htrace-core-3.1.0-incubating.jar:/usr/local/hadoop271/share/hadoop/common/lib/api-util-1.0.0-M20.jar:/usr/local/hadoop271/share/hadoop/common/lib/asm-3.2.jar:/usr/local/hadoop271/share/hadoop/common/lib/java-xmlbuilder-0.4.jar:/usr/local/hadoop271/share/hadoop/common/lib/mockito-all-1.8.5.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-logging-1.1.3.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-math3-3.1.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/servlet-api-2.5.jar:/usr/local/hadoop271/share/hadoop/common/lib/jackson-jaxrs-1.9.13.jar:/usr/local/hadoop271/share/hadoop/common/lib/jetty-6.1.26.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-httpclient-3.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-io-2.4.jar:/usr/local/hadoop271/share/hadoop/common/lib/gson-2.2.4.jar:/usr/local/hadoop271/share/hadoop/common/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop271/share/hadoop/common/lib/hadoop-auth-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/snappy-java-1.0.4.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-digester-1.8.jar:/usr/local/hadoop271/share/hadoop/common/lib/api-asn1-api-1.0.0-M20.jar:/usr/local/hadoop271/share/hadoop/common/lib/jsp-api-2.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/xmlenc-0.52.jar:/usr/local/hadoop271/share/hadoop/common/lib/activation-1.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/stax-api-1.0-2.jar:/usr/local/hadoop271/share/hadoop/common/lib/jersey-server-1.9.jar:/usr/local/hadoop271/share/hadoop/common/lib/hadoop-annotations-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-configuration-1.6.jar:/usr/local/hadoop271/share/hadoop/common/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/common/lib/log4j-1.2.17.jar:/usr/local/hadoop271/share/hadoop/common/lib/hamcrest-core-1.3.jar:/usr/local/hadoop271/share/hadoop/common/lib/httpcore-4.2.5.jar:/usr/local/hadoop271/share/hadoop/common/lib/zookeeper-3.4.6.jar:/usr/local/hadoop271/share/hadoop/common/lib/jaxb-api-2.2.2.jar:/usr/local/hadoop271/share/hadoop/common/lib/jersey-json-1.9.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-codec-1.4.jar:/usr/local/hadoop271/share/hadoop/common/lib/apacheds-kerberos-codec-2.0.0-M15.jar:/usr/local/hadoop271/share/hadoop/common/lib/netty-3.6.2.Final.jar:/usr/local/hadoop271/share/hadoop/common/lib/slf4j-api-1.7.10.jar:/usr/local/hadoop271/share/hadoop/common/lib/jettison-1.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/curator-framework-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/curator-recipes-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/jets3t-0.9.0.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-net-3.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/avro-1.7.4.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-lang-2.6.jar:/usr/local/hadoop271/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar:/usr/local/hadoop271/share/hadoop/common/lib/guava-11.0.2.jar:/usr/local/hadoop271/share/hadoop/common/lib/junit-4.11.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-cli-1.2.jar:/usr/local/hadoop271/share/hadoop/common/lib/httpclient-4.2.5.jar:/usr/local/hadoop271/share/hadoop/common/lib/jsr305-3.0.0.jar:/usr/local/hadoop271/share/hadoop/common/lib/paranamer-2.3.jar:/usr/local/hadoop271/share/hadoop/common/lib/jetty-util-6.1.26.jar:/usr/local/hadoop271/share/hadoop/common/lib/xz-1.0.jar:/usr/local/hadoop271/share/hadoop/common/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-collections-3.2.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/curator-client-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/lib/jsch-0.1.42.jar:/usr/local/hadoop271/share/hadoop/common/lib/jackson-xc-1.9.13.jar:/usr/local/hadoop271/share/hadoop/common/lib/jersey-core-1.9.jar:/usr/local/hadoop271/share/hadoop/common/lib/commons-beanutils-core-1.8.0.jar:/usr/local/hadoop271/share/hadoop/common/hadoop-nfs-2.7.1.jar:/usr/local/hadoop271/share/hadoop/common/hadoop-common-2.7.1-tests.jar:/usr/local/hadoop271/share/hadoop/common/hadoop-common-2.7.1.jar:/usr/local/hadoop271/share/hadoop/hdfs:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-daemon-1.0.13.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/htrace-core-3.1.0-incubating.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/asm-3.2.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/xercesImpl-2.9.1.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-logging-1.1.3.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/servlet-api-2.5.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jetty-6.1.26.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-io-2.4.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/leveldbjni-all-1.8.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/xmlenc-0.52.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jersey-server-1.9.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/log4j-1.2.17.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/xml-apis-1.3.04.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-codec-1.4.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/netty-3.6.2.Final.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/netty-all-4.0.23.Final.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-lang-2.6.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/guava-11.0.2.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/commons-cli-1.2.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jsr305-3.0.0.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jetty-util-6.1.26.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/hdfs/lib/jersey-core-1.9.jar:/usr/local/hadoop271/share/hadoop/hdfs/hadoop-hdfs-2.7.1-tests.jar:/usr/local/hadoop271/share/hadoop/hdfs/hadoop-hdfs-2.7.1.jar:/usr/local/hadoop271/share/hadoop/hdfs/hadoop-hdfs-nfs-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jaxb-impl-2.2.3-1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jersey-client-1.9.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/zookeeper-3.4.6-tests.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/javax.inject-1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-compress-1.4.1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/asm-3.2.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-logging-1.1.3.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/servlet-api-2.5.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jackson-jaxrs-1.9.13.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jetty-6.1.26.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-io-2.4.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/leveldbjni-all-1.8.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/activation-1.1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/stax-api-1.0-2.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jersey-server-1.9.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/aopalliance-1.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/log4j-1.2.17.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/zookeeper-3.4.6.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/guice-3.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jaxb-api-2.2.2.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jersey-json-1.9.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-codec-1.4.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/netty-3.6.2.Final.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jettison-1.1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/guice-servlet-3.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-lang-2.6.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/guava-11.0.2.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-cli-1.2.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jsr305-3.0.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jetty-util-6.1.26.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/xz-1.0.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jersey-guice-1.9.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/commons-collections-3.2.1.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jackson-xc-1.9.13.jar:/usr/local/hadoop271/share/hadoop/yarn/lib/jersey-core-1.9.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-registry-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-applications-unmanaged-am-launcher-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-common-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-web-proxy-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-nodemanager-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-client-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-tests-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-sharedcachemanager-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-api-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-applicationhistoryservice-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-server-resourcemanager-2.7.1.jar:/usr/local/hadoop271/share/hadoop/yarn/hadoop-yarn-common-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/javax.inject-1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/commons-compress-1.4.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/asm-3.2.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/commons-io-2.4.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/protobuf-java-2.5.0.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/snappy-java-1.0.4.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/leveldbjni-all-1.8.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/jersey-server-1.9.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/hadoop-annotations-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/jackson-mapper-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/aopalliance-1.0.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/log4j-1.2.17.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/hamcrest-core-1.3.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/guice-3.0.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/netty-3.6.2.Final.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/avro-1.7.4.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/guice-servlet-3.0.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/junit-4.11.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/paranamer-2.3.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/xz-1.0.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/jackson-core-asl-1.9.13.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/jersey-guice-1.9.jar:/usr/local/hadoop271/share/hadoop/mapreduce/lib/jersey-core-1.9.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-shuffle-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.1-tests.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-app-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-hs-plugins-2.7.1.jar:/usr/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-client-common-2.7.1.jar:/contrib/capacity-scheduler/*.jar:/usr/local/hadoop271/contrib/capacity-scheduler/*.jar
STARTUP_MSG:   build = https://git-wip-us.apache.org/repos/asf/hadoop.git -r 15ecc87ccf4a0228f35af08fc56de536e6ce657a; compiled by 'jenkins' on 2015-06-29T06:04Z
STARTUP_MSG:   java = 1.7.0_75
************************************************************/
17/05/26 12:54:29 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
17/05/26 12:54:29 INFO namenode.NameNode: createNameNode [-format]
17/05/26 12:54:30 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Formatting using clusterid: CID-4e87e970-bb84-4b06-8d2d-c00332133b6d
17/05/26 12:54:30 INFO namenode.FSNamesystem: No KeyProvider found.
17/05/26 12:54:30 INFO namenode.FSNamesystem: fsLock is fair:true
17/05/26 12:54:31 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
17/05/26 12:54:31 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
17/05/26 12:54:31 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
17/05/26 12:54:31 INFO blockmanagement.BlockManager: The block deletion will start around 2017 五月 26 12:54:31
17/05/26 12:54:31 INFO util.GSet: Computing capacity for map BlocksMap
17/05/26 12:54:31 INFO util.GSet: VM type       = 64-bit
17/05/26 12:54:31 INFO util.GSet: 2.0% max memory 966.7 MB = 19.3 MB
17/05/26 12:54:31 INFO util.GSet: capacity      = 2^21 = 2097152 entries
17/05/26 12:54:31 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
17/05/26 12:54:31 INFO blockmanagement.BlockManager: defaultReplication         = 1
17/05/26 12:54:31 INFO blockmanagement.BlockManager: maxReplication             = 512
17/05/26 12:54:31 INFO blockmanagement.BlockManager: minReplication             = 1
17/05/26 12:54:31 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
17/05/26 12:54:31 INFO blockmanagement.BlockManager: shouldCheckForEnoughRacks  = false
17/05/26 12:54:31 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
17/05/26 12:54:31 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
17/05/26 12:54:31 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
17/05/26 12:54:31 INFO namenode.FSNamesystem: fsOwner             = root (auth:SIMPLE)
17/05/26 12:54:31 INFO namenode.FSNamesystem: supergroup          = supergroup
17/05/26 12:54:31 INFO namenode.FSNamesystem: isPermissionEnabled = true
17/05/26 12:54:31 INFO namenode.FSNamesystem: HA Enabled: false
17/05/26 12:54:31 INFO namenode.FSNamesystem: Append Enabled: true
17/05/26 12:54:31 INFO util.GSet: Computing capacity for map INodeMap
17/05/26 12:54:31 INFO util.GSet: VM type       = 64-bit
17/05/26 12:54:31 INFO util.GSet: 1.0% max memory 966.7 MB = 9.7 MB
17/05/26 12:54:31 INFO util.GSet: capacity      = 2^20 = 1048576 entries
17/05/26 12:54:31 INFO namenode.FSDirectory: ACLs enabled? false
17/05/26 12:54:31 INFO namenode.FSDirectory: XAttrs enabled? true
17/05/26 12:54:31 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
17/05/26 12:54:31 INFO namenode.NameNode: Caching file names occuring more than 10 times
17/05/26 12:54:31 INFO util.GSet: Computing capacity for map cachedBlocks
17/05/26 12:54:31 INFO util.GSet: VM type       = 64-bit
17/05/26 12:54:31 INFO util.GSet: 0.25% max memory 966.7 MB = 2.4 MB
17/05/26 12:54:31 INFO util.GSet: capacity      = 2^18 = 262144 entries
17/05/26 12:54:31 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
17/05/26 12:54:31 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
17/05/26 12:54:31 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
17/05/26 12:54:31 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
17/05/26 12:54:31 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
17/05/26 12:54:31 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
17/05/26 12:54:31 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
17/05/26 12:54:31 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
17/05/26 12:54:31 INFO util.GSet: Computing capacity for map NameNodeRetryCache
17/05/26 12:54:31 INFO util.GSet: VM type       = 64-bit
17/05/26 12:54:31 INFO util.GSet: 0.029999999329447746% max memory 966.7 MB = 297.0 KB
17/05/26 12:54:31 INFO util.GSet: capacity      = 2^15 = 32768 entries
17/05/26 12:54:31 INFO namenode.FSImage: Allocated new BlockPoolId: BP-1696207355-192.168.2.201-1495774471795
17/05/26 12:54:31 INFO common.Storage: Storage directory /tmp/hadoop-root/dfs/name has been successfully formatted.
17/05/26 12:54:32 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
17/05/26 12:54:32 INFO util.ExitUtil: Exiting with status 0
17/05/26 12:54:32 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at h2m1/192.168.2.201
************************************************************/
```

其中的关键信息：

```
# 格式化的节点为h2m1/192.168.2.201
host = h2m1/192.168.2.201

# 使用的java版本
java = 1.7.0_75

# 生成默认的集群id，现在是单点伪分布模式
Formatting using clusterid: CID-4e87e970-bb84-4b06-8d2d-c00332133b6d

# 文件系统使用的公平锁
fsLock is fair:true

# 默认启用ip/主机名检查
dfs.namenode.datanode.registration.ip-hostname-check=true

# 副本和块的默认配置
dfs.block.access.token.enable=false
defaultReplication         = 1
maxReplication             = 512
minReplication             = 1
maxReplicationStreams      = 2
shouldCheckForEnoughRacks  = false
replicationRecheckInterval = 3000

#不启用数据传输加密
encryptDataTransfer        = false
maxNumBlocksToLog          = 1000

# 默认文件系统用户，超级用户组，开启许可，不开启HDFS的HA，开启追加功能
fsOwner             = root (auth:SIMPLE)
supergroup          = supergroup
isPermissionEnabled = true
HA Enabled: false
Append Enabled: true


# 默认不开启ACl，开启XAttrs，并将xattr最大设置为16384
ACLs enabled? false
XAttrs enabled? true
Maximum size of an xattr: 16384

# 参考链接 https://www.iteblog.com/archives/977.html
# 指定达到最小副本数的数据块的百分比，等于0表示可以立即退出安全模式，大于1表示永远处于安全模式
dfs.namenode.safemode.threshold-pct = 0.9990000128746033
# namenode退出安全模式前y有效的Datanode数量,小于0表示namenode退出安全模式前无需考虑datanode的个数.值大于datanode节点数表示永远处于安全模式。
dfs.namenode.safemode.min.datanodes = 0
# 表示满足threshold-pct的前提下，namenode还需要处于安全模式的时间
dfs.namenode.safemode.extension     = 30000

# 为namenode用户提供的类似于top的工具
# 参考链接 https://issues.apache.org/jira/browse/HDFS-6982
dfs.namenode.top.window.num.buckets = 10
dfs.namenode.top.num.users = 10
dfs.namenode.top.windows.minutes = 1,5,25

## 在namenode上启用重新缓存，缓存将会使用总堆大小的3%容量，而且缓存实体过期时间为10分钟
Retry cache on namenode is enabled
Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis

# 为namenode分配新的块池id："BP-" + RAND +"="+ip + namenode.getPort() + "-"+System.currentTimeMillis();
Allocated new BlockPoolId: BP-1696207355-192.168.2.201-1495774471795


# 这条日志告诉我们namenode已经成功格式化，并且为之为"/tmp/hadoop-root/dfs/name"目录
17/05/26 12:54:31 INFO common.Storage: Storage directory /tmp/hadoop-root/dfs/name has been successfully formatted.
```

### 3. 启动单点HDFS

![启动单点HDFS](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-1.png)

如图说明已经启动了Namenode，Datanode和Secondary Namenode

**查看Namenode日志**

发现已经启动了web管理页面功能，而且可以通过`h2m1:50070`在浏览器中访问了。

![启动单点HDFS](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-2.png)


![启动单点HDFS](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-3.png)


在Namenode的日志中发出了警告，默认方式下fsimage和edits都只有一个存储目录,提示可能会由于缺少冗余导致数据丢失。

![namenode日志](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-4.png)


namenode中的一些提示信息

![namenode日志](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-5.png)

datenode注册的一些提示信息

![namenode日志](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-7.png)

**查看DataNode日志**

DataNode初始化
![namenode日志](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-8.png)

启动Datanode的web服务并连接namenode的ipc服务
![namenode日志](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-9.png)


为块池(BloackPool)格式化datanode
![namenode日志](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-10.png)

添加卷并连接Namenode

![namenode日志](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-11.png)

### 4. 浏览器界面访问

`namenode:http://h2m1:50070`

![namenode日志](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-12.png)


`datanode:http://h2m1:50075`

![namenode日志](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hdfs-13.png)


### 配置MapReduce和YARN



`mapred-site.xml`文件

```xml
<configuration>
    <!--配置mapreduce使用的框架为yarn-->
        <property>
                <name>mapreduce.framework.name</name>
                <value>yarn</value>
        </property>
</configuration>
```

`yarn-site.xml`

```xml

```

![hadoop默认端口](http://omsz9j1wp.bkt.clouddn.com/image/hdfs/hadoop-default-port.png)



