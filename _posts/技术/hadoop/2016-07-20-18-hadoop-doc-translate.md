---
layout: post
title: 18.hadoop-2.7.2官网文档翻译-HDFS用户指南
category: 技术
tags:  Hadoop
keywords: 
description: HDFS用户指南。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsUserGuide.html
---

{:toc}

## 目的

该文档是用户使用HDFS(Hadoop Distributed File System)的开始，HDFS既可以是Hadoop集群的一部分，也可以是独立的分布式文件系统。
HDFS被设置为在许多环境中"just work",HDFS的工作只是对改进配置和诊断特定集群上有很大的帮助。

## 概览

HDFS是Hadoop应用使用的主要分布式存储。HDFS集群首先包括NameNode(管理文件系统的元数据)和DataNode（存储真实数据）。在HDFS架构指南中有对HDFS的详细描述。该用户指南主要处理用户和管理员与HDFS集群的交互。
HDFS架构图描述了NameNode，DataNode和客户端的基本交互。客户端因为文件元数据或文件修改连接NameNode，而在DataNode上直接执行真实的文件IO。

以下是许多用户会感兴趣的一些显著特性：

- Hadoop，包括HDFS，非常适合在通用硬件上进行分布式存储和分布式计算。它具有容错性，可扩展性和非常简单的扩展。
以简单和使用与处理分布式应用的大数据集而知名的MapReduce是Hadoop的组成部分。

- HDFS是高度可配置的，而且默认配置非常适合许多安装。大多数时候，只有对于非常大的集群时，配置才需要调整。

- Hadoop使用java写的，而且支持所有主流平台。

- Hadoop支持类shell命令直接与HDFS交互。

- NameNode和DataNode内置web服务器，这使得很容易的检查集群的当前状态。

- HDFS的新特性和改进会定期执行。下面是HDFS中很有用特性的子集：

	- 文件权限和认证。
	
	- 机架感知：在调度任务和分配存储过程中考虑一个节点的物理位置。
	
	- 安全模式：维护管理模式。
	
	- `fsck`:诊断文件系统健康状态的功能，发现丢失的文件或块。
	
	- `fetchdt`:获取委派token并将其存储在本地文件系统上的文件中。
	
	- 平衡：在DataNode上的数据分布不稳定时调整集群平衡的工具。
	
	- 升级和回滚：在软件升级后，在出现未逾期问题时，可以回滚到升级前的HDFS的状态。
	
	- Secondary NameNode：执行命名空间的周期性检查点，并有助于保持HDFS修改的日志文件大小，该大小必须在NameNode的限制内。

	- 检查点节点：执行命令空间的周期性检查点，有助于减少在HDFS的NameNode内存储日志的大小。替代以往Secondary NameNode的角色，虽然还没有完全固化。NameNode允许多个检查点节点同时存在，只要没有备份节点注册到系统中。
	
	- 备份节点：检查点节点的延伸，除了检验指示，它也从NameNode接受edits流，保持着命名空间的内存复制，该命名空间总是从激活的NameNode的命名空间同步。一次只能有一个备份节点可与NameNode注册。
	

## 前提

下面的文档描述了怎样安装并设置一个Hadoop集群：

- 对第一次的用户请看[单节点安装]({% post_url 2016-07-04-2-hadoop-doc-translate %}){:target="_blank"}

- 大规模分布式集群请看[集群安装]({% post_url 2016-07-04-3-hadoop-doc-translate %}){:target="_blank"}

接下来的文档嘉定用户会安装并运行HDFS而且至少有一个DataNode。因为该文档的目标，所有NameNode和DataNode都运行在相同的物理机器上。

## WEB接口

NameNode和DataNode每个都运行一个内部web服务器，为了显示集群当前状态的基本信息。默认配置下，NameNode前端页面是`http://namenode-name:50070/`。它列出了集群中的DataNode和集群的基本统计。而且web接口也可以用于浏览文件系统。


## shell命令

Hadoop包括了多种类shell命令可以与HDFS和Hadoop支持的其他文件系统直接交互。命令`bin/hdfs dfs -help`列出Hadoop的shell支持的命令。`bin/hdfs dfs -help command-name`显示某个命令的详细帮助信息。
这些命令支持大部分正常的文件系统操作，像复制文件，更改文件权限等等。也支持一些HDFS特有的操作，像改变文件复制数。请多信息请看[文件系统shell指南](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/FileSystemShell.html){:target="_blank"}

### DFSAdmin 命令

命令`bin/hdfs dfsadmin`支持一些HDFS管理员相关的操作。`bin/hdfs dfsadmin -help`命令会列出当前支持的所有命令。比如：

- -report：报告HDFS基本统计信息。有些信息也可以从NameNode的前端页面获得。

- -safemode：尽管通常不需要，但管理员可以手动开启或关闭安全模式。

- -finalizeUpgrade：删除最后一次升级期间的集群备份。

- -refreshNodes：更新允许连接到NameNode的DataNode集的NameNode。NameNode重新从`dfs.hosts`中读取定义的DataNode的主机名,定义在`dfs.hosts`的`dfs.hosts.exclude`主机是集群中的DataNode。
如果在`dfs.hosts`中有多个记录，只有在它里面的主机允许与NameNode注册。`dfs.hosts.exclude`内的记录是需要移除的DataNode。
当他们的所有备份被复制到其他的DataNode上后，该DataNode就会被完全移除。移除节点不会自动关闭，而是不再被选择用来写入新的副本。

- -printTopology ：打印集群的拓扑。显示机架树和连接到轨道想可以被NameNode查看的DataNode。

对于命令的用法，请看[dfsadmin](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html#dfsadmin){:target="_blank"}


## Secondary NameNode


## 检查点节点


## 备份节点

## 入口检查点

## 平衡器

## 机架感知


## 安全模式


## fsck


## fetchdt


## 恢复模式


## 升级和回滚


## DataNode热插拔驱动器

## 文件权限和安全


## 可伸缩性

## 相关文档















































































