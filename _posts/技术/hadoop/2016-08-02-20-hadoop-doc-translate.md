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

在高可用集群中，要注意到备用NameNode也会验证命名空间的检查点状态，因此不必在高可用集群中运行Secondary NameNode，CheckpointNode，BackupNode 。事实上这样做是个错误。这也允许重新配置一个非高可用的HDFS集群成为一个个可用的集群来重用以前分配给Secondary NameNode的硬件。


## 部署

### 配置概要

与联邦配置类似，高可用配置向后兼容并且允许现有的单个NameNode配置工作而不必变化。新的配置设计用于建中所有的节点可能拥有相同的配置而不需要在不同类型节点的不同机器上部署不同的配置文件。

就像HDFS联邦，HA集群重用了`nameservice ID`来唯一标识单个HDFS实例，其事实上可能包含了多个高可用的NameNode。此外，一种新的叫做`NameNode ID`的抽象添加进HA。
集群中每个确切的NameNode拥有不同的`NameNode ID`来区分它。为了在所有的NameNode上支持单一配置文件。相关配置参数的后缀为`nameservice ID`和`NameNode ID`

### 配置详情

为了配置高可用的NameNode，你必须在你的`hdfs-site.xml`配置文件中添加几个配置选项。

这些配置的顺序是不重要的，但是你为`dfs.nameservices`和`dfs.ha.namenodes.[nameservice ID]`选择的值将会决定哪些跟随的key。
因此，你应该在设置其他配置选项前确定这些值。

- `dfs.nameservices` -新命名服务的逻辑名称
	
	为该命名服务选择一个逻辑名称，比如“mycluster”，并且用该逻辑名作为该配置选项的值。你可以随意选择逻辑名称。它将会用于配置和作为集群中HDFS权限组件的绝对路径。
	
	**注意：**如果你也使用HDFS联邦，该配置的设置应该也包含其他命名服务的列表，HA或其他，以逗号分隔的列表。
	
	```xml
	<property>
	  <name>dfs.nameservices</name>
	  <value>mycluster</value>
	</property>
	```

- `dfs.ha.namenodes.[nameservice ID] `命名服务中每个NameNode的唯一标识。

	使用逗号分隔的`NameNode ID`列表配置。这将被DataNode用于判定集群中所有的NameNode。比如，如果你之前使用"mycluster"作为`nameservice ID`,并且你想要使用"nn1"和"nn2"作为NameNode的特定id，你需要做出这样的配置：
	
	```
	<property>
	  <name>dfs.ha.namenodes.mycluster</name>
	  <value>nn1,nn2</value>
	</property>
	```
	
	**注意：**当前，每个命名服务中只允许最多配置两个NameNode。

- `dfs.namenode.rpc-address.[nameservice ID].[name node ID]` - 每个NameNode监听的完全合格的RPC地址

	与上面的`PRC-address`类似，设置两个NameNode的HTTP服务器的地址用来监听。例如：
	
	```XML
	<property>
	  <name>dfs.namenode.http-address.mycluster.nn1</name>
	  <value>machine1.example.com:50070</value>
	</property>
	<property>
	  <name>dfs.namenode.http-address.mycluster.nn2</name>
	  <value>machine2.example.com:50070</value>
	</property>
	```
	
	**注意：**如果开启的Hadoop的安全特性，应该为每个NameNode设置`https-address`
	


- `dfs.namenode.shared.edits.dir` - 标识NameNode要写入和读取edits的JNs组的URI。
	
	JournalNode地址的一个配置，JournalNode提供共享edits存储，被活跃NameNode写和被备用NameNode读以保持活跃NameNode上所有文件系统的变化都是最新的。尽管你必须指定几个JournalNode地址，**你应该只配置这些URI中的一个。**
	URI的格式应该是这样：`qjournal://*host1:port1*;*host2:port2*;*host3:port3*/*journalId*`。
	
	`Journal ID`是该命名服务的唯一标识，允许JournalNode的单个集去为多个联合的命名系统提供存储。尽管不需要，但重用命名服务的id作为journal的标识是个很好的想法。
	
	举例来说，如果该集群的JournalNode运行在机器“node1.example.com”,“node2.example.com”和“node3.example.com”上，并且命名服务id是“mycluster”，那么你要使用下面设置的值（JournalNode默认端口时8485）。
	
	```xml
	<property>
	  <name>dfs.namenode.shared.edits.dir</name>
	  <value>qjournal://node1.example.com:8485;node2.example.com:8485;node3.example.com:8485/mycluster</value>
	</property>
	```
	

- `dfs.client.failover.proxy.provider.[nameservice ID]` -HDFS客户端用于连接活跃NameNode的java类

	配置java类的名称，用于DFS客户端判定哪个NameNode当前是活跃的和哪一个NameNode当前正在为客户端请求服务。当前的唯一实现是`ConfiguredFailoverProxyProvider`,因此，除非你使用自定义的一个，否则使用这个。例如：
	
	```xml
	<property>
	  <name>dfs.client.failover.proxy.provider.mycluster</name>
	  <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
	</property>
	```
	
- `dfs.ha.fencing.methods` -用于在故障转移期间回避活跃NameNode的java类或者脚本的列表

	在任何给定的时间都只有一个NameNode节点处于活跃状态，这对于系统的正确性是可取的。
	**重要的是，当使用QJM时，只有一个NameNode被允许向JournalNode写数据，因此没有潜在的来自`split-brain scenario`的可能会破坏文件系统的元数据。**
	然而，当故障转移发生时，先前的活跃的NameNode可以为客户端提供读请求服务仍然是有可能的，直到尝试向JournalNode写数据时，NameNode才会关闭，这时原来的活跃NameNode才会过时。
	因为这个原因，在使用QJM时配置一些回避方法是可取的。然而，为了在回避机制失败的事件发生时，提高系统的可用性，配置回避方法,列表中最后一个回避方法保证返回成功这也是可取的。
	**注意：**如果你选择不使用回避方法，你仍然必须要为该设置做一些配置，比如："shell(/bin/true)"
	
	故障转移期间使用的回避方法被配置为“carriage-return-separated ”列表，其将按顺序被尝试，直到有一个回避成功。
	这有两个方法：shell和sshfence。更多关于自定义实现回避方法的信息请看`org.apache.hadoop.ha.NodeFencer`类。
	
	**sshfence** - SSH连接到活跃NameNode并杀死进程
	
	`sshfence`操作SSH连接到目标节点，并使用*fuser*干掉监听在服务的TCP端口的进程。为了使得该回避选项生效，必须免密码SSH连接到目标节点。一次，必须配置`dfs.ha.fencing.ssh.private-key-files`选项，以逗号分隔的SSH私钥文件列表。
	比如：
	
	```xml
	    <property>
	      <name>dfs.ha.fencing.methods</name>
	      <value>sshfence</value>
	    </property>
	
	    <property>
	      <name>dfs.ha.fencing.ssh.private-key-files</name>
	      <value>/home/exampleuser/.ssh/id_rsa</value>
	    </property>
	```
	
	可选的，也可以配置费标准的用户名或密码来验证SSH，也可以为SSH配置超时时间，单位毫秒，在超出时间后，认为该回避方法是失败的。可能会配置如下：
	
	```xml
	    <property>
	      <name>dfs.ha.fencing.methods</name>
	      <value>sshfence([[username][:port]])</value>
	    </property>
	    <property>
	      <name>dfs.ha.fencing.ssh.connect-timeout</name>
	      <value>30000</value>
	    </property>
	```
	
	**shell** - 随便运行一个shell命令来回避活跃NameNode
	
	该*shell*回避方法运行随便一个shell命令。可以配置如下：
	
	```xml
	    <property>
	      <name>dfs.ha.fencing.methods</name>
	      <value>shell(/path/to/my/script.sh arg1 arg2 ...)</value>
	    </property>
	```
	
	'('和')'内的字符串时正确的bash shell命令，可能不包括任何关闭的圆括号。
	
	该命令将运行环境设置为包含所有当前Hadoop的配置变量，在配置的key中使用'_'来替代任何的'.'字符。该配置使用的是已经在任何NameNode指定配置改进的一般格式 - 比如`dfs_namenode_rpc-address`将包含目标节点的RPC地址，甚至通过该配置可以指定变量` dfs.namenode.rpc-address.ns1.nn1`。
	
	另外，参照目标节点的下面的变量也可以使用：
	
	|变量|描述|
	|---|---|
	|$target_host|要回避的节点的主机名|
	|$target_port|要回避的节点的IPC端口|
	|$target_address|以上两个，合并为host:port|
	|$target_nameserviceid|要回避的NameNode节点的命名服务的id|
	|$target_namenodeid|要回避的NameNode节点的namenode id|
	
	这些环境变量也可能被shell命令替换。比如：
	
	```xml
	    <property>
	      <name>dfs.ha.fencing.methods</name>
	      <value>shell(/path/to/my/script.sh --nameservice=$target_nameserviceid $target_host:$target_port)</value>
	    </property>
	```
	
	如果shell命令返回退出码0，那回避被判定成功。如果返回其他退出码，回避不成功，然后将会尝试列表中下一个回避方法。
	
	**注意：**此回避方法未实现任何超时操作，如果必须超时。应该在shell脚本自身被实现（比如，fork一个子shell在指定时间秒内杀死它的父shell）
	

- `fs.defaultFS` - 在什么都没给定情况下，Hadoop的FS客户端使用的默认路径前缀
	
	可选的，你可能现在使用新的高可用的逻辑URI为Hadoop客户端配置默认的路径。如果你早起使用"mycluster"作为命名服务的id，这将是你的HDFS路径的权威部分的值。
	在`core-site.xml`中，可能会向下面这样配置：
	
	```xml
		<name>fs.defaultFS</name>
		<value>hdfs://mycluster</value>
	```
	

- `dfs.journalnode.edits.dir` - JournalNode守护进程将会将它的本地状态存储的路径
	
	这是JNs存储的edits和其他本地状态的JournalNode机器上的绝对路径。该配置中你可能只需要使用一个路径。
	通过运行多个分隔的JournalNode为该数据提供冗余，或者在本地的RAID中配置该目录。
	比如：
	
	```xml
	<property>
	  <name>dfs.journalnode.edits.dir</name>
	  <value>/path/to/journal/node/local/data</value>
	</property>
	```
	


### 部署详情


在所有必须的配置选项被设置后，当你运行它们时，你必须先在机器集上启动JournalNode的守护进程。这可以使用命令`hadoop-daemon.sh start journalnode`完成。并等待守护进程在每个相关的机器上被启动起来。

一旦JournalNode被启动，必须开始同步两个高可用Namenode的磁盘上的元数据。

- 如果你正在建立一个新的HDFS集群，你应该首先在一个Namenode上运行格式化命令(`hdfs namenode -format`)。

- 如果已经对Namenode格式化了，或者非高可用集群转为了高可用集群，你应该复制你的Namenode上元数据目录的内容到其他的Namenode上面。未格式化的Namenode要在本机上通过运行命令(`hdfs namenode -bootstrapStandby`)。
运行该命令要确保JournalNode(通过**dfs.namenode.shared.edits.dir**)包含足够的edits处理使得可以启动两个Namenode。

- 如果你将非HANamenode转为HA Namenode，应该运行命令`hdfs namenode -initializeSharedEdits`,该命令会用来自本地Namenode的edits目录edits数据初始化JournalNode。


在这一点上，你可以开始两个高可用Namenode，即使你通常启动一个Namenode。

你可以分别浏览它们配置的HTTP地址访问每一个Namenode的web页面。你会注意到，已经配置过的访问地址的Namenode会是高可用状态(不管是"备用"还是"活跃")。
每当高可用的Namenode启动，它最初是在备用状态。


### 管理的命令


现在你的高可用Namenode已经配置并启动了，你有机会获得一些额外的命令来管理你的高可用HDFS集群。特别指出，你应该熟练使用`hdfs haadmin`命令的所有子命令。
无其他参数运行该命令会显示如下的用法信息：

```
Usage: haadmin
    [-transitionToActive <serviceId>]
    [-transitionToStandby <serviceId>]
    [-failover [--forcefence] [--forceactive] <serviceId> <serviceId>]
    [-getServiceState <serviceId>]
    [-checkHealth <serviceId>]
    [-help <command>]
```

该指南描述了每个子命令的高级使用方法。你可以运行"hdfs haadmin -help <command>"命令来查看每个子命令的用法信息。


- **transitionToActive **和**transitionToStandby** - 转换给定Namenode的状态为活跃或备用
	
	这些子命令会各自使得给定的Namenode转换为活跃或备用状态。**这些命令不会尝试确认任何回避，因此应该很少被用到。**作为替代,子命令`hdfs haadmin -failover`倒是更有可能会被用到。
	

- **failover ** - 发起两个Namenode间的故障转移

该子命令会使得故障转移从第一个提供的Namenode到第二个Namenode。如果第一个是备用状态，该命令简单的将第二个转换为活跃状态而不会报错。如果第一个Namenode是活跃状态，会尝试优雅地转换为备用状态。如果失败了，回避方法(**dfs.ha.fencing.methods**中配置的)将会被尝试直到有一个会成功。
只有这一步完成后，才会将第二个Namenode转换为活跃状态。如果没有回避方法执行成功，第二个Namenode将不会转换为活跃状态，并且会返回错误信息。

- **getServiceState ** -判定给定的Namenode是活跃还是备用

连接到提供的Namenode上判定它的状态，在标准输出中显示"standby"或"active"。该子命令可能用于定时调度任务或监控基于Namenode当前是"standby"或"active"判断不同行为的脚本。

- **checkHealth ** - 检查给定Namenode的健康

连接到给定的Namenode上检查它的健康状态。NameNode是能够对自身进行诊断，包括检查内部服务是否按预期运行。
如果Namenode健康会返回0，否则返回非零。监控目的可能会使用该命令。

	**注意：**这还没有实现，并且目前已知返回成功，除非给定的Namenode完全移除。
	

## 自动故障切换

### 介绍

上一段描述了怎样配置手动故障转移。在那一块，系统不会自动触发从活跃到备用Namenode的失效转移，甚至如果活跃节点失败了。
本段会描述怎样配置并部署自动故障转移。

### 组件


### 部署ZooKeeper


### 你开始之前


### 配置自动故障转移



### 在ZooKeeper中初始化HA状态


### 使用`start-dfs.sh`启动集群


### 手动启动集群



### 安全访问ZooKeeper


### 验证自动故障转移


## 自动失效转移问答


## 启用高可用的HDFS的升级/最终/回滚





