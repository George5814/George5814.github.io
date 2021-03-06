---
layout: post
title: 21.hadoop-2.7.2官网文档翻译-使用NFS的HDFS高可用性
category: 技术
tags:  Hadoop
keywords: 
description: 使用NFS的HDFS高可用性。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithNFS.html
---

{:toc}

## 目标

该指南提供了HDFS高可用特性的概览和怎样使用NFS作为Namenode请求的共享存储来配置和管理高可用的HDFS集群。

该文档假设读者对HDFS集群中的常规组件和节点类型有一定了解。详细信息请看HDFS架构指南。

## 注意：使用QJM或协定的共享存储

该指南讨论了在活跃和备用Namenode间使用共享的NFS目录共享edit日志时，怎样配置和使用HDFS高可用特性。

对于怎样使用QJM代替NFS配置HDFS高可用特性，请看[该指南]({% post_url 2016-08-02-20-hadoop-doc-translate %}){:target="_blank"}

## 背景

在Hadoop2.0.0以前，Namenode在HDFS集群中是单点故障（SPOF）。每个集群只有一个Namenode并且如果运行Namenode的机器或进程不可用了，那整个集群也就不可用了，直到Namenode重启或在其他机器上启动。

对HDFS集群整体可用性的影响体现在两个方面：

- 出现了异常事件，比如宕机，这时候集群不可达直到操作者重启Namenode。

- 计划维护事件，如Namenode的机器上软件或硬件升级将会导致集群停机窗口。

HDFS的高可用功能，解决了上述问题，通过在同一集群中提供运行两个冗余节点。形成主/备配置的双击热备选项。

在万一机器崩溃后，会快速切换到新的NameNode上或者一个优雅的管理员发起的计划检修目的的故障转移。

## 架构


在典型的高性能集群中，两个分开的机器被配置为NameNode。在任何时间点都有其中一个NameNode在活跃状态。并且另一个在备用状态。活跃的NameNode负责集群内所有的客户端操作，备份的NameNode只是简单的扮演从节点。
保持足够的装填，在必要时提供快速故障转移。

为了让备用节点保持它的状态与活跃的节点同步，目前的实现需要两个节点都能访问到共享存储设备上的目录（如，NFS挂载在NAS上），这种限制可能会在未来版本中放宽。
当活跃节点的命名空间有任何修改被执行时，它会将修改的日志记录持久化到存储在共享目录的edit日志中。备用节点不间断的监控该目录的edits，并且如果发现edits了，他会将他们复制到它自己的命名空间。
在发生故障转移的时候，备用节点在将它自己激发为活跃状态前会确保它读取了所有的来自于共享存储的edits。这会确保在故障转移发生前的命名空间状态时完全同步的。

为了提供快速失效转移，备份节点拥有最新的集群中关于块位置的信息是非常有必要的。为了实现该目的，DataNode也配置了定位两个NameNode的位置，并将块位置信息和心跳发送到这两个NameNode。

同时只有一个NameNode处于活跃状态对于正确操作高可用的集群是非常有必要的。否则，两个NameNode的命名空间状态将会很快出现差异，机架数据丢失或其他不正确的结果。

为了确保该属性并防止所谓的"split-brain scenario"，管理员必须配置至少为共享存储配置一个回避方法。

在故障转移期间，如果不能确定之前的活跃节点交出它的活跃状态，回避进程负责切断之前的活跃节点对共享edits存储的访问。
这阻止了它对命名空间进行任何进一步的编辑，允许新的活跃节点安全的处理故障转移。


## 硬件资源

为了部署一个高可用集群，你应该准备如下几点：

- NameNode机器 - 运行活跃和备用NameNode的机器应该具有彼此相同的硬件，和非高可用集群中使用等效的硬件。


- 共享存储 - 你需要一个共享目录可以使得两个Namenode都能读写访问。










