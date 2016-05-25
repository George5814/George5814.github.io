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

