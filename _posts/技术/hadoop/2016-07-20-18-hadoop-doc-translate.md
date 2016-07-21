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

NameNode存储对文件系统的修改会作为日志追加到原生文件系统的文件上，edits。在NameNode启动时，会从镜像文件，fsimage，中读取HDFS的状态，然后支持从edits日志文件编辑。然后将新的HDFS状态写入到fsimage，并使用空的edits文件开始正常操作。因为NameNode仅在启动时合并fsimage和edits文件，因此在一个非常繁忙的集群中，edits文件有可能会变得非常大。另一个影响是非常大的edits文件会在NameNode下次启动时耗费更长的时间。

而Secondary NameNode会周期性的合并fsimage和edits日志文件并保持edits日志大小在一个限定值内。它通常和主NameNode运行在不同的机器上因为它的内存需求和主NameNode一样。

Secondary NameNode检查点进程的开始被下面两个配置参数控制：

- `dfs.namenode.checkpoint.period`,间隔默认值为1小时，并可指定两个连续检查点之间的最大延时时间。

- `dfs.namenode.checkpoint.txns`，默认设置为一百万，定义了NameNode上非检查点的交易数量，这会强制一个紧急检查点，即使检查点的间隔还没有达到。

**译者注：**即如果没有达到指定量，则按正常的检查点周期合并。如果达到指定量，则紧急启动检查点合并。


Secondary NameNode 在目录，结构与主NameNode目录相同，中存储最新的检查点。因此在必要时，检查点镜像总会准备好被主NameNode读取。

命令用法请参考[secondary namenode](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html#secondarynamenode){:target="_blank"}

## 检查点节点

NameNode使用两个文件保存它的命名空间：fsimage-命名空间最近的检查点，edits-从最近检查点后的更改日志。
在NameNode启动时，会合并faimage和edits更改日志来提供并更新文件系统元数据。NameNode会使用新的HDFS状态覆盖fsimage,并开始新的edits变更日志。

检查点节点周期性创建命名空间的检查点。从NameNode瞎子啊fsimage和edits，并在本地合并，将新的image回传到激活的NameNode。检查点和对应的NameNode通常运行在冉的机器上，因为它需要和NameNode一样的内存。检查点节点子啊配置文件中指定的节点上使用命令`bin/hdfs namenode -checkpoint`启动。

检查点(备份)节点的位置和其附带的web接口通过`dfs.namenode.backup.address`和`dfs.namenode.backup.http-address`配置变量来配置。

检查点节点的检查点进程启动手下面两个配置参数控制：


- `dfs.namenode.checkpoint.period`,间隔默认值为1小时，并可指定两个连续检查点之间的最大延时时间。

- `dfs.namenode.checkpoint.txns`，默认设置为一百万，定义了NameNode上非检查点的交易数量，这会强制一个紧急检查点，即使检查点的间隔还没有达到。

检查点节点在目录，结构与主NameNode目录相同，中存储最新的检查点。因此在必要时，检查点镜像总会准备好被主NameNode读取。

可以在集群的配置文件中指定多个检查点节点。

命令用法请参考[namenode](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html#namenode){:target="_blank"}

## 备份节点

备份节点提供了和检查点节点相同的检查指示功能，比如在保持在内存中，总是同步复制激活NameNode状态的文件系统命名空间等方面。随着从NameNode接收文件系统edits的更改日志流并存储在磁盘上。备份节点也支持将哪些edits用到他自己的内存中的命名空间的副本中，从而创建命名空间的备份。

备份节点不需要从激活的NameNode中下载fsimage和edits文件来创建检查点。同时需要检查点节点或者Secondary NameNode，因为它已经具有了内存中的命名空间状态的最新状态。
备份节点检查点进程更有效，因为只需要将命名空间保存到本地的fsimage文件并重置edits。

因为备份节点要在内存中保存命名空间的副本，因此需要和NameNode一样的内存。

NameNode支持一次一个备份节点。如果备份节点正在使用中，那没有检查点节点会被注册。未来会支持同时多个备份节点。

备份节点与检查点节点使用同样的方式配置，使用`bin/hdfs namenode -backup`命令启动。

备份(检查点)节点的位置和其附带的web接口通过`dfs.namenode.backup.address`和`dfs.namenode.backup.http-address`配置变量来配置。

备份节点的使用提供了非持久化存储运行NameNode的一个选择，会委派所有的持久化命名空间状态到备份节点的责任。
为了这样做，使用`-importCheckpoint`选项启动NameNode，同时使用NameNode配置的`dfs.namenode.edits.dir`指定edits类型非持久性存储目录。

查看完整的创建备份节点和检查点节点背后的动机请看[ HADOOP-4539](https://issues.apache.org/jira/browse/HADOOP-4539){:target="_blank"}

命令用法请参考[namenode](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html#namenode){:target="_blank"}

## 导入检查点

如果所有其他的image和edits文件丢失了，最新的检查点可以被导入到NameNode。为了那样做应该：

- 创建在`dfs.namenode.name.dir`配置变量中指定的空目录。

- 在配置变量`dfs.namenode.checkpoint.dir`指定检查点目录的位置。

- 使用`-importCheckpoint`选项启动NameNode。

NameNode会上传`dfs.namenode.checkpoint.dir`指定目录的检查点并保存到`dfs.namenode.name.dir`设置的NameNode目录中。如果在`dfs.namenode.name.dir`中包含有一个逻辑镜像，NameNode将会失败。
NameNode会校验`dfs.namenode.checkpoint.dir`内镜像的一致性，但不会对它做修改。


命令用法请参考[namenode](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html#namenode){:target="_blank"}

## 平衡器

HDFS的数据可能并不总是被均匀地放置在DataNode上，一个常见的原因是新DataNode添加到现有的集群中。在放置新的块（一个文件存储的数据作为一系列的块）时，NameNode在选择DataNode接受这些块执勤啊会考虑各种参数。
一些考虑因素是：

- 策略保持在同一个节点上的一个块的副本作为写块的节点。

- 需要将块的不同副本传播到机架上，可能会导致集群遭遇整个机架丢失。

-  HDFS的数据均匀的扩散到集群中的DataNode上。

因多重竞争的考虑，数据不可能均匀的放置在DataNode上。HDFS为管理员提供了一个工具来分析块定位并重新平衡DataNode上的数据。
一个简单的平衡器的管理员指南可以从[HADOOP-1652](https://issues.apache.org/jira/browse/HADOOP-1652){:target="_blank"}获得。

命令用法请见[平衡器](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html#balancer){:target="_blank"}

## 机架感知

通常特别大的Hadoop集群会安装在多个机架上，并且同一机架上不同节点间的网络通信量肯定不跨机架的网络通信量更让人满意。
此外，NameNode会尝试将块的多个副本放置在多个机架上以提高容错性。通过配置变量`net.topology.script.file.name`,Hadoop会让集群管理员决定一个节点属于哪一个机架。
当该脚本配置完成后，每个节点运行该脚本决定他们自己的机架id。默认安装假定所有的节点属于同一个机架。该特性和配置更深层的描述在[HADOOP-692](https://issues.apache.org/jira/browse/HADOOP-692){:target="_blank"}中的pdf内。

## 安全模式

在启动NameNode从fsimage和edits日志文件加载文件系统状态期间。NameNode会等待DataNode报告他们自己的块，因此尽管集群中存在足够的副本，但也不要过早的启动块的副本。在该时间段内，NameNode处于安全模式中。
NameNode的安全模式本质上是对HDFS集群的只读模式，它不允许对文件系统或块的任何修改。正常情况下，在DataNode报告完它们的大部分文件系统块可用时，NameNode会自动退出安全模式。

如果需要，可以明确的使用命令`bin/hdfs dfsadmin -safemode`，将HDFS置于安全模式中。


## fsck


## fetchdt


## 恢复模式


## 升级和回滚


## DataNode热插拔驱动器

## 文件权限和安全


## 可伸缩性

## 相关文档















































































