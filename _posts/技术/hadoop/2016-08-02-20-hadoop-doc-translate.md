---
layout: post
title: 20.hadoop-2.7.2官网文档翻译-使用仲裁日志管理器的HDFS高可用性
category: 技术
tags:  Hadoop
keywords: 
description: 使用仲裁日志管理器的HDFS高可用性。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html
---

{:toc}

## 目标

该指南提供了HDFS高可用特性的概览，和使用仲裁日志管理器(QJM)怎样配置和管理一个高可用的HDFS集群。


## 注意：使用QJM(仲裁日志管理器)或者常规共享存储

该指南讨论了使用QJM怎样配置和使用HDFS的HA特性来在活跃和备用NameNode间共享edits日志。

对于怎样使用NFS代替QJM作为共享存储来配置HDFS的HA特性文章，请看[使用了NFS的HDFS高可用特性](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithNFS.html){:target="_blank"}


## 背景

在Hadoop2.0.0 之前，NameNode在HDFS集群中是单点故障的(SPOF)。每个集群只能有一个NameNode，并且如果运行NameNode的机器或进程不可访问时，整个集群也就不可访问知道NameNode重启或者其他机器替代。

影响HDFS集群整体可用性的方法主要有两种：

- 发生意外比如，机器崩溃，集群将会不可访问，知道NameNode重启。

- 计划维修项目：如NameNode机器的软件或硬件升级会导致集群的停机时间窗口。

HDFS的高可用功能，解决了上述问题，通过在同一集群中提供运行两个冗余节点。形成主/备配置的双击热备选项。

在万一机器崩溃后，会快速切换到新的NameNode上或者一个优雅的管理员发起的计划检修目的的故障转移。


## 架构

在典型的高性能集群中，两个分开的机器被配置为NameNode。在任何时间点都有其中一个NameNode在活跃状态。并且另一个在备用状态。活跃的NameNode负责集群内所有的客户端操作，备份的NameNode只是简单的扮演从节点。
保持足够的装填，在必要时提供快速故障转移。

为了让备用节点保持它的状态与活跃的节点同步，这两个节点间通过叫"JournalNodes"(JNs)的一组单独的守护进程通信。当活跃节点的命名空间有任何修改被执行时，它会将修改的日志记录持久化到大部分的JNs中。
备用节点能够从JNs上读取edits，并且持续监控edit日志的变化。当备用节点发现edits时，它会将他们复制到它自己的命名空间。在发生故障转移的时候，备用节点在将它自己激发为活跃状态前会确保它读取了所有的来自于JNs的edits。
这会确保在故障转移发生前的命名空间状态时完全同步的。

为了提供快速失效转移，备份节点拥有最新的集群中关于块位置的信息是非常有必要的。为了实现该目的，数据节点也配置了连个NameNode的位置，并将块位置信息和心跳发送到这两个NameNode。

同时只有一个NameNode处于活跃状态对于正确操作高可用的集群是非常有必要的。否则，两个NameNode的命名空间状态将会很快出现差异，机架数据丢失或其他不正确的结果。

为了确保该属性并防止所谓的"split-brain scenario"，JournalNodes将会仅仅允许在同一时刻单个NameNode作为写者。

在故障转移期间，成为活跃状态的NameNode会简单的接管向JournalNodes写的角色，这样会组织其他继续为活跃状态的NameNode节点，允许新的活跃节点安全的处理故障转移。


## 硬件资源

为了部署一个高可用集群，你应该准备如下几点：

- NameNode机器 - 运行活跃和备用NameNode的机器应该具有彼此相同的硬件，和非高可用集群中使用等效的硬件。

- JournalNode机器  - 运行JournalNode的机器。JournalNode的守护进程相对轻量级，因此这些守护进程可以合理的和其他Hadoop守护进程分布在一起。比如，NameNode，JobTracker或者YARN的ResourceManager等。
**注意：**至少要有三个JournalNode节点，但是为了实际增加西戎可以容忍的失败次数，你应该运行奇数个JNs(如，3,5,7等等)。
**注意：**当运行N个JournalNode时，系统可以容忍至多(N-1)/2个失败，并继续正确运行。

在高可用集群中，要注意到备用NameNode也会验证命名空间的检查点状态，因此不必在高可用集群中运行Secondary NameNode，CheckpointNode，BackupNode 。事实上这样做会出错。这也允许重新配置一个非高可用的HDFS集群成为一个个可用的集群来重用以前分配给Secondary NameNode的硬件。




