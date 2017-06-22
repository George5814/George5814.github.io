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

HDFS支持`fsck`命令检查各种矛盾。它被设计来报告多个文件的问题，比如，文件或副本块中丢失块。不像原生文件系统生的传统`fsck`工具，该命令不纠正它所检测到的错误。通常NameNode自动校正大部分可恢复的故障。
默认情况下，`fsck`会忽略打开的文件但是提供了一个选项来选择报告期间的所有文件。HDFS的`fsck`命令不是一个Hadoop的shell命令。它以`bin/hdfs fsck`方式运行。对于命令用法请看[fsck](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html#fsck){:target="_blank"}。
fsck命令可以运行在完整的文件系统或文件的子集上。



## fetchdt

HDFS支持`fetchdt`来获取委派token并将其存储在本地文件系统的文件中，该token可以用于从非安全的客户端访问安全的服务器（如NameNode）。
用RPC或HTTPS(在kerberos上)获取token，因此在运行前（运行kinit获取tickets）需要暂存kerberos的票证。
HDFS的`fetchdt`命令不是Hadoop的shell命令。可以以`bin/hdfs fetchdt DTfile`方式运行。在你获取到token后，你可以再不适用kerberos的ticker情况下运行HDFS命令。 环境变量`HADOOP_TOKEN_FILE_LOCATION`来设置授权token的文件。
命令用法请参考[fetchdt](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html#fetchdt){:target="_blank"}


## 恢复模式

通常，你会配置多个元数据存储位置。然而，如果一个存储位置终端了，你可以从其他的存储位置的其中一个读取元数据。

然而，如果唯一的存储位置终端了你该怎么办呢？这种情况下，指定NameNode启动模式为恢复模式，它允许你恢复大部分的数据。

你可以使用命令`namenode -recover`启动NameNode为恢复模式。

当处于恢复模式时，NameNode会在命令行下交互提示你在可能采取的行动，你可以恢复你的数据。

如果不想要提示，你可以使用`-force`选项，该选项会强制恢复模式总是选择第一个选项，通常，这会是最主要的选择。

因为恢复模式会造成你丢失数据，在使用它之前，你应该总是备份你的edit日志和fsimage。

## 升级和回滚

当升级存在的就能时，就像任何软件升级，可能会有新的bug或不兼容的改变影响现存的应用，而且也不易及早发现。在任何非平凡的HDFS安装，它不是一个松散的任何数据的选项，别说从无到有重启HDFS。

HDFS允许管理员回退到Hadoop早期版本并将集群的状态回滚到升级之前。HDFS的设计在[Hadoop升级](http://wiki.apache.org/hadoop/Hadoop_Upgrade){:target="_blank"}的wiki文档中有详细描述。
HDFS可以同时有一个这样的备份。在升级前，管理员需要使用命令`bin/hadoop dfsadmin -finalizeUpgrade`删除已存在的备份。
接下来简要介绍典型的升级程序：

- 在升级Hadoop软件之前，如果存在备份，则清理掉.`dfsadmin -upgradeProgress`状态可以告诉用户使用集群需要被清理。

- 停止集群并且部署新版本的Hadoop。

- 使用`-upgrade`选项运行新版本（`bin/start-dfs.sh -upgrade`）。

- 大部分时间，集群都会很好的工作。一旦新的HDFS工作良好（也许在操作几天后），清理升级。注意直到集群完成，删除在升级前存在的文件然后释放DataNode上真正的内存空间。

- 如果需要回退到先前的版本，

	- 停掉集群，部署先前版本的Hadoop。
	
	- 在NameNode上运行回滚命令(`bin/hdfs namenode -rollback`) 
	
	- 使用`rollback`选项启动集群。
	

在升级到一个新版HDFS时，必须重命名或删除在新版HDFS中预留的任何路径。如果在升级过程中NameNode遭遇了预留路径。它会打印如下信息：

```
/.reserved is a reserved path and .snapshot is a reserved path component in this version of HDFS. Please rollback and delete or rename this path, or upgrade with the -renameReserved [key-value pairs] option to automatically rename these paths during upgrade.
```

指定` -upgrade -renameReserved [optional key-value pairs]`会使得NameNode在启动时自动重命名任何预留路径。比如，重命名所有命名为.snapshot路径为.my-snapshot和.reserved为.myreserved。用户要指定`-upgrade -renameReserved .snapshot=.my-snapshot,.reserved=.my-reserved`。

如果没有`-renameReserved`的k-v键值对被指定。NameNode会使用`.<LAYOUT-VERSION>.UPGRADE_RENAMED`为预留路径添加后缀。如，`.snapshot.-51.UPGRADE_RENAMED`

这有些该重命名进程的警告，如果可能的话，在升级前，推荐执行命令`hdfs dfsadmin -saveNamespace`。这是因为数据不一致可能会导致编辑日志操作引用到自动重命名的文件的目的地。

## DataNode热插拔驱动器

DataNode支持热插拔驱动。用户可以在没有关闭DataNode情况下，关闭或替换HDFS数据列。下面简要教书了典型的热插拔步骤：

- 如果是新的存储目录，用户应该将其格式化然后适当的安装它们。

- 用户更新DataNode配置`dfs.datanode.data.dir`来反映要在使用中的数据卷目录。

- 用户运行`dfsadmin -reconfig datanode HOST:PORT start`启动配置进程。用户可以使用`dfsadmin -reconfig datanode HOST:PORT status`查询配置任务的运行状态。

- 一旦配置的任务完成，用户可以改下载被移除的数据卷目录并从磁盘上物理删除。

## 文件权限和安全

文件权限设置与其他家族平台,像linux，类似。当前，安全限制在单文件权限。启动NameNode的用户在HDFS被作为超级用户处理。未来的HDFS版本会支持像kerberos的用户认证和数据传输加密那样的网络认证协议。
详细讨论请看权限指南。


## 可伸缩性

Hadoop当前支持运行上千节点的集群。[PowerBy](http://wiki.apache.org/hadoop/PoweredBy){:target="_blank"}文档列出了部署超大Hadoop集群的组织。
每个集群的HDFS有一个NameNode。当前NameNode可获得的总内存是主要的可扩展限制。在非常大的集群中，增加存储在HDFS文件平均大小可以随着团簇尺寸的增加而不增加内存需求的NameNode。
默认配置不适合非常大的集群。[FAQ](http://wiki.apache.org/hadoop/FAQ){:target="_blank"}文档列出了超大Hadoop集群的配置优化

## 相关文档

用户指南是在HDFS上工作的一个好的开始。当用户指南继续完善。这将是Hadoop和HDFS的非常健康的文档。下面列出了未来泰索的开始点：

- [Hadoop站点](http://hadoop.apache.org/){:target="_blank"}:Hadoop站点的主页。

- [Hadoop wiki](http://wiki.apache.org/hadoop/FrontPage){:target="_blank"}:Hadoop wiki的文档，不像作为Hadoop源码树的一部分的正式文档。Hadoop wiki是由Hadoop社区定期编辑。

- [FAQ](http://wiki.apache.org/hadoop/FAQ){:target="_blank"}:FAQ页面。

- [Hadoop的java API](http://hadoop.apache.org/docs/r2.7.2/api/index.html){:target="_blank"}:

- Hadoop用户邮件列表：在Hadoop.apache.org中的用户。

- 浏览[hdfs-default.xml](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/hdfs-default.xml){:target="_blank"}:包含对大部分配置变量的简要描述。

- [Hadoop命令指南]({% post_url 2016-07-25-19-hadoop-doc-translate %}){:target="_blank"}:Hadoop命令用法。

