---
layout: post
title: hadoop集群学习笔记(1)
category: 技术
tags:  Hadoop
keywords: 
description: 
---

{:toc}


#### hadoop1.x 中HDFS 

缺点 ：

 1. 当nameNode无法在内存中加载全部元数据时，集群的寿命就到头了；
 1. 权限设计不够彻底
 1. 性能瓶颈（集群的吞吐量受限于单个NameNode的吞吐量）
 1. 仅由一个NameNode，无法隔离各个程序
 1. 集群的可用性（当NameNode宕机后，集群不可用）
 1. NameNode和BlockManager耦合性太高 
 
改进：

 1. 为了水平扩展NameNode，federation使用多个NameNode/NameSpace。使用两个nameNode，组成HDFS的federation（联邦），两个NameNode的元数据是不一样的。一个NameNode节点内存被占满后，存放在另一个NameNode中
 1. nameNode的元数据同时存储在多个节点上。相互独立且不需要相互协调，各自分工管理自己的区域。
 
 集群中每个DateNode要向所有的NameNode注册，并周期性向NameNode发送心跳和块报告，执行来自所有NameNode的命令	
 1. 使用secondaryNameNode恢复NameNode，但会丢失未存入edit中的数据 
 1. NameNode将核心的元数据信息存放在journalNode 节点集群上，也从这里读数据。两个NameNode读写分离；
 写数据的NameNode为active状态，读数据的NameNode为standby状态。
 
**HDFS在HA中，两个NameNode需要使用相同的namespace，自动切换需要使用ZooKeeper集群**

**为什么纵向扩展（增大内存）目前的NameNode不可行？**

 1. 启动慢，50G的heap就启动30分钟至2小时
 1. 在垃圾回收时，出现错误整个集群宕机
 1. 对大的JVM Heap调试困难
 
**Block pool**
 	属于单个命名空间上的一组块
 	
 datanode是物理概念，Block Pool是逻辑概念
 

 
**namespace volume**
 
 - NameNode上的namespace和其对应的BlockPool一起成为namespace volume
 
 -  是管理的基本单位。
 
 - 以namespace volume作为删除和升级的基本单位
 
 
**采用联盟的好处**

- 简单鲁棒性设计

- 良好的向后扩展性



 
#### hadoop 1.x MapReduce

- 主节点：JobTracker，一个
- 从节点：TaskTracker，多个

JobTracker工作：管理用户提交作业和分配资源，作业实时性要求不高

缺点：
 1. 对于实时性和批处理作业，需要搭建不同的集群环境； 
 2. MapReduce职责过多，需要分解
 
 yarn处理资源分配和运行任务，可以解决storm、spark、MapReduce共享资源，存在CPU，内存，网络带宽的，硬盘的竞争问题
 
#### hadoop2.x yarn

yarn节点：
	ResourceManager，只有一个
	NodeManager，可有多个
	
Yarn的RM接收到我们客户提交的	MapReduce程序后，交给某个NodeManger，启动某个进程MRAppMaster[MRAppMaster分配任务]，MRAppMaster会通过RM获得被分配任务节点位置，然后将任务分配给指定节点	



**namenode  格式化(集群格式化)**

`hdfs namenode -format -clusterId  hadoop-cluster-jingzz`

不同节点上格式化NameNode必须有相同的clusterId:`hadoop-cluster-jingzz`

成功格式化后显示一些配置信息

```
************************************************************/
16/05/25 07:19:05 INFO namenode.NameNode: registered UNIX signal handlers for [TERM, HUP, INT]
16/05/25 07:19:05 INFO namenode.NameNode: createNameNode [-format, -clusterId, hadoop-cluster-jingzz]
16/05/25 07:19:06 WARN util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Formatting using clusterid: hadoop-cluster-jingzz
16/05/25 07:19:07 INFO namenode.FSNamesystem: No KeyProvider found.
16/05/25 07:19:07 INFO namenode.FSNamesystem: fsLock is fair:true
16/05/25 07:19:07 INFO blockmanagement.DatanodeManager: dfs.block.invalidate.limit=1000
16/05/25 07:19:07 INFO blockmanagement.DatanodeManager: dfs.namenode.datanode.registration.ip-hostname-check=true
16/05/25 07:19:07 INFO blockmanagement.BlockManager: dfs.namenode.startup.delay.block.deletion.sec is set to 000:00:00:00.000
16/05/25 07:19:07 INFO blockmanagement.BlockManager: The block deletion will start around 2016 May 25 07:19:07
16/05/25 07:19:07 INFO util.GSet: Computing capacity for map BlocksMap
16/05/25 07:19:07 INFO util.GSet: VM type       = 64-bit
16/05/25 07:19:07 INFO util.GSet: 2.0% max memory 966.7 MB = 19.3 MB
16/05/25 07:19:07 INFO util.GSet: capacity      = 2^21 = 2097152 entries
16/05/25 07:19:07 INFO blockmanagement.BlockManager: dfs.block.access.token.enable=false
16/05/25 07:19:07 INFO blockmanagement.BlockManager: defaultReplication         = 3
16/05/25 07:19:07 INFO blockmanagement.BlockManager: maxReplication             = 512
16/05/25 07:19:07 INFO blockmanagement.BlockManager: minReplication             = 1
16/05/25 07:19:07 INFO blockmanagement.BlockManager: maxReplicationStreams      = 2
16/05/25 07:19:07 INFO blockmanagement.BlockManager: shouldCheckForEnoughRacks  = false
16/05/25 07:19:07 INFO blockmanagement.BlockManager: replicationRecheckInterval = 3000
16/05/25 07:19:07 INFO blockmanagement.BlockManager: encryptDataTransfer        = false
16/05/25 07:19:07 INFO blockmanagement.BlockManager: maxNumBlocksToLog          = 1000
16/05/25 07:19:07 INFO namenode.FSNamesystem: fsOwner             = root (auth:SIMPLE)
16/05/25 07:19:07 INFO namenode.FSNamesystem: supergroup          = supergroup
16/05/25 07:19:07 INFO namenode.FSNamesystem: isPermissionEnabled = false
16/05/25 07:19:07 INFO namenode.FSNamesystem: Determined nameservice ID: ns2
16/05/25 07:19:07 INFO namenode.FSNamesystem: HA Enabled: false
16/05/25 07:19:07 INFO namenode.FSNamesystem: Append Enabled: true
16/05/25 07:19:08 INFO util.GSet: Computing capacity for map INodeMap
16/05/25 07:19:08 INFO util.GSet: VM type       = 64-bit
16/05/25 07:19:08 INFO util.GSet: 1.0% max memory 966.7 MB = 9.7 MB
16/05/25 07:19:08 INFO util.GSet: capacity      = 2^20 = 1048576 entries
16/05/25 07:19:08 INFO namenode.FSDirectory: ACLs enabled? false
16/05/25 07:19:08 INFO namenode.FSDirectory: XAttrs enabled? true
16/05/25 07:19:08 INFO namenode.FSDirectory: Maximum size of an xattr: 16384
16/05/25 07:19:08 INFO namenode.NameNode: Caching file names occuring more than 10 times
16/05/25 07:19:08 INFO util.GSet: Computing capacity for map cachedBlocks
16/05/25 07:19:08 INFO util.GSet: VM type       = 64-bit
16/05/25 07:19:08 INFO util.GSet: 0.25% max memory 966.7 MB = 2.4 MB
16/05/25 07:19:08 INFO util.GSet: capacity      = 2^18 = 262144 entries
16/05/25 07:19:08 INFO namenode.FSNamesystem: dfs.namenode.safemode.threshold-pct = 0.9990000128746033
16/05/25 07:19:08 INFO namenode.FSNamesystem: dfs.namenode.safemode.min.datanodes = 0
16/05/25 07:19:08 INFO namenode.FSNamesystem: dfs.namenode.safemode.extension     = 30000
16/05/25 07:19:08 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.window.num.buckets = 10
16/05/25 07:19:08 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.num.users = 10
16/05/25 07:19:08 INFO metrics.TopMetrics: NNTop conf: dfs.namenode.top.windows.minutes = 1,5,25
16/05/25 07:19:08 INFO namenode.FSNamesystem: Retry cache on namenode is enabled
16/05/25 07:19:08 INFO namenode.FSNamesystem: Retry cache will use 0.03 of total heap and retry cache entry expiry time is 600000 millis
16/05/25 07:19:08 INFO util.GSet: Computing capacity for map NameNodeRetryCache
16/05/25 07:19:08 INFO util.GSet: VM type       = 64-bit
16/05/25 07:19:08 INFO util.GSet: 0.029999999329447746% max memory 966.7 MB = 297.0 KB
16/05/25 07:19:08 INFO util.GSet: capacity      = 2^15 = 32768 entries
Re-format filesystem in Storage Directory /root/hadoop/dfs/nn/name ? (Y or N) y
Re-format filesystem in Storage Directory /root/hadoop/dfs/nn/edits ? (Y or N) y
16/05/25 07:19:09 INFO namenode.FSImage: Allocated new BlockPoolId: BP-1177085153-192.168.31.102-1464185949838
16/05/25 07:19:09 INFO common.Storage: Storage directory /root/hadoop/dfs/nn/name has been successfully formatted.
16/05/25 07:19:09 INFO common.Storage: Storage directory /root/hadoop/dfs/nn/edits has been successfully formatted.
16/05/25 07:19:10 INFO namenode.NNStorageRetentionManager: Going to retain 1 images with txid >= 0
16/05/25 07:19:10 INFO util.ExitUtil: Exiting with status 0
16/05/25 07:19:10 INFO namenode.NameNode: SHUTDOWN_MSG: 
/************************************************************
SHUTDOWN_MSG: Shutting down NameNode at h2s1/192.168.31.102
```
