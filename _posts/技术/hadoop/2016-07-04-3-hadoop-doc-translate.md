---
layout: post
title: 3.hadoop-2.7.2官网文档翻译-集群安装
category: 技术
tags:  Hadoop
keywords: 
description: 
---

{:toc}

## Hadoop 集群安装

### 目标

该文档描述了怎样在几个节点到非常多的节点（上千个）上安装并配置hadoop集群。为了玩Hadoop，你可能首先想要把他安装在单一机器上。（请看[单节点安装]({% post_url 2016-07-04-2-hadoop-doc-translate %}){:title="单节点安装"  :target="_blank"}）

该文档不涉及高级论题，比如安全或者HA（高可用）

### 先决条件

- 安装java

- 下载稳定版的Hadoop

### 安装
	
安装Hadoop集群通常涉及到在集群所有机器上解包软件，或者通过你系统的包管理系统安装。
把硬件按照功能划分是很重要的。

通常集群中的一个机器专门被设计作为NameNode，另一个机器作为ResourceManager。
他们都是主节点，其他的服务（如：WEB应用代理服务器，MapReduce任务历史服务器）通常根据负载，运行在专用硬件或共享的基础上。

集群中剩余的机器同时作为DataNode和NodeManager，他们都是从节点。

### 在非安全模式下配置Hadoop

Hadoop的java配置有两种重要的配置文件驱动：

- 只读的默认配置

	core-default.xml,   
	hdfs-default.xml,  
	yarn-default.xml,  
	mapred-default.xml

- 站点指定配置
	
	etc/hadoop/core-site.xml,   
	etc/hadoop/hdfs-site.xml,   
	etc/hadoop/yarn-site.xml,  
	etc/hadoop/mapred-site.xml
	
此外，可以控制bin/下的Hadoop脚本，通过` etc/hadoop/hadoop-env.sh`和`etc/hadoop/yarn-env.sh`设置站点指定的值。

为了配置Hadoop集群，你会需要配置Hadoop守护进程执行的`environment`,就像Hadoop的守护进程的配置参数。

HDFS守护进程是`NameNode`，`SecondaryNameNode`和`DataNode`。

YARN守护进程是`ResourceManager`,`NodeManager`和`WebAppProxy`。

如果MapReduce被使用了，然后MapReduce的任务历史服务也将运行。对于大量安装，这些通常运行在单独的主机上。

#### 配置Hadoop守护进程的环境

管理员应该使用` etc/hadoop/hadoop-env.sh`,作为一个选项，`etc/hadoop/mapred-env.sh`和`etc/hadoop/yarn-env.sh`脚本用来配置站点指定的自定义的Hadoop守护进程处理环境。

至少是，你必须指定JAVA_HOME，使得他再每一个远程机器上正确的定义。

---

|Daemon|Environment Variable|
|--|--|
|NameNode|HADOOP_NAMENODE_OPTS|
|DataNode|HADOOP_DATANODE_OPTS|
|Secondary NameNode|HADOOP_SECONDARYNAMENODE_OPTS|
|ResourceManager|YARN_RESOURCEMANAGER_OPTS|
|NodeManager|YARN_NODEMANAGER_OPTS|
|WebAppProxy|YARN_PROXYSERVER_OPTS|
|Map Reduce Job History Server|HADOOP_JOB_HISTORYSERVER_OPTS|

---

举个例子：为了配置NameNode使用`parallelGC`,应该在`hadoop-env.sh`中添加上`export HADOOP_NAMENODE_OPTS="-XX:+UseParallelGC"`。

其他的例子请看`etc/hadoop/hadoop-env.sh`。

其他你可能会自定义的有用的配置参数包括：

- HADOOP_PID_DIR ：守护进程的处理id文件存储的目录

- HADOOP_LOG_DIR ： 守护进程的日志文件存储目录，如果不存在日志文件会自动创建。

- HADOOP_HEAPSIZE / YARN_HEAPSIZE ： 最大可用的堆大小，单位为`MB`.
比如如果该变量设置为1000，那heap将会被设置为1000MB。通过用来为守护进程配置堆大小。默认情况下，值为1000.如果你想要配置值分别为每一个守护进程使用，你可以使用它。

大多数情况下，你应该指定`HADOOP_PID_DIR`和`HADOOP_LOG_DIR`目录，他们只能被正在运行的Hadoop进程上的用户覆盖。否则就会有链接攻击的可能性。

惯例下，会在系统的shell环境中配置`HADOOP_PREFIX `，比如一个简单的脚本包含在`/etc/profile.d`目录中：

```
HADOOP_PREFIX=/path/to/hadoop
export HADOOP_PREFIX
```

---

|Daemon|Environment Variable|
|--|--|
|ResourceManager|YARN_RESOURCEMANAGER_HEAPSIZE|
|NodeManager|YARN_NODEMANAGER_HEAPSIZE|
|WebAppProxy|HADOOP_DATANODE_OPTS|
|Map Reduce Job History Server|HADOOP_DATANODE_OPTS|

---

#### 配置Hadoop的守护进程

这一节讨论在给定的配置文件中指定的重要参数：

- etc/hadoop/core-site.xml

---

|参数|值 |备注|
|--|--|--|
|fs.defaultFS|hdfs://host:port/|NameNode的URI|
|io.file.buffer.size|131072|用于序列文件中读写缓存的大小|

---

- etc/hadoop/hdfs-site.xml
	
	- 配置NameNode

	---
	
		
	|参数|值 |备注|
	|--|--|--|
	|dfs.namenode.name.dir|用来存储NameNode和事务持久日志的本地文件系统路径|如果是以逗号分隔的目录列表，那么name的表会被复制到所有的目录中，为了冗余|
	|dfs.hosts / dfs.hosts.exclude|DataNode的许可和限制列表|如果必要的话，用这些文件来控制可访问的DataNode的列表|
	|dfs.blocksize|268435456|在大文件系统中HDFS的默认块大小为256M|
	|dfs.namenode.handler.count|100|更多的NameNode服务线程来处理大量的DataNode的RPC|
	
	
	---

	- 配置DataNode

	---
	
		
	|参数|值 |备注|
	|--|--|--|
	|dfs.datanode.data.dir|用逗号分隔本地文件系统的路径列表，用来存储DataNode块|如果是以逗号分隔的目录列表，那么数据建辉被存储在所有的目录中，这些目录通常是在不同的设备上|
	
	---	
	

- etc/hadoop/yarn-site.xml

	- 配置ResourceManager 和NodeManager
	
	---
	
	|参数|值 |备注|
	|--|--|--|
	|yarn.acl.enable|true / false|是否开启访问控制列表(ACL),默认为关闭|
	|yarn.admin.acl|管理员访问控制列表|设置管理员的访问控制列表。ACL是以逗号分离的组，组内用户以逗号分离。默认值为*，意味着任何人。Special值意味着没有人可以访问。|
	|yarn.log-aggregation-enable|false|配置是否启用日志聚集功能|
	
	
	---
	
	- 配置ResourceManager
	
	---
	
	|参数|值 |备注|
	|--|--|--|
	|yarn.resourcemanager.address|host:port|集群提交作业的RM地址,如果host:port 设置了，会覆盖在`yarn.resourcemanager.hostname`中设置的hostname|
	|yarn.resourcemanager.scheduler.address|host:port| AM通知调度器获取资源的地址。如果设置了，会覆盖在`yarn.resourcemanager.hostname`中设置的hostname|
	|yarn.resourcemanager.resource-tracker.address|host:port|NodeManager的地址。如果设置了，会覆盖在`yarn.resourcemanager.hostname`中设置的hostname|
	|yarn.resourcemanager.admin.address|host:port|管理的命令的地址。如果设置了，会覆盖在`yarn.resourcemanager.hostname`中设置的hostname|
	|yarn.resourcemanager.webapp.address|host:port|web界面的地址。如果设置了，会覆盖在`yarn.resourcemanager.hostname`中设置的hostname|
	|yarn.resourcemanager.hostname|host|单一的主机名可以设置在所有的`yarn.resourcemanager*address`资源中，RM组件的默认端口|
	|yarn.resourcemanager.scheduler.class|调度类|CapacityScheduler（推荐），公平调度器(推荐)，FIFO调度器|
	|yarn.scheduler.minimum-allocation-mb|单位为`MB`|RM中每个容器需要分配的最小内存限制|
	|yarn.scheduler.maximum-allocation-mb|单位为`MB`|RM中每个容器需要分配的最大内存限制||
	|yarn.resourcemanager.nodes.include-path |允许的NM列表|如果必要的话，用这些文件控制可访问的NodeManager的列表|
	|yarn.resourcemanager.nodes.exclude-path |禁止的NM列表|如果必要的话，用这些文件控制可访问的NodeManager的列表|
	
	---
	
	- 配置NodeManager
	
	---
	
	|参数|值 |备注|
	|--|--|--|
	|yarn.nodemanager.resource.memory-mb|单位为`MB`|给NodeManager的可用物理内存。默认NM所有可以获得的资源作为可用资源来运行容器|
	|yarn.nodemanager.vmem-pmem-ratio|任务可以超过物理内存的虚拟内存最大使用比例|每个任务超过物理内存的虚拟内存使用率通过该比率限制。通过该比率限制，在NodeManager上任务使用的虚拟内存的总量可能超过他的物理内存使用量|
	|yarn.nodemanager.local-dirs|中间数据的写入位置，本地文件系统各种以逗号分隔的路径列表|多路径帮助分担IO操作|
	|yarn.nodemanager.log-dirs|日志文件在本地文件系统的位置，以逗号分隔的路径列表|多路径帮助分担IO操作|
	|yarn.nodemanager.log.retain-seconds|10800|如果`log-aggregation`被关闭，默认的仅适当的在NodeManager上保留的日志时间(秒为单位)。|
	|yarn.nodemanager.remote-app-log-dir|/logs|应用日志被移动完成的HDFS的目录，需要设置适当的限制。如果`log-aggregation`被开启则仅applicable|
	|yarn.nodemanager.remote-app-log-dir-suffix|logs|远程日志目录的后缀。日志将被聚合到`${yarn.nodemanager.remote-app-log-dir}/${user}/${thisParam}`.如果`log-aggregation`被开启则仅applicable|
	|yarn.nodemanager.aux-services|mapreduce_shuffle|Shuffle 服务需要为MapReduce应用设置|
	
	---
	
	- 配置History 服务器(需要移到别处)
	
	---
	
	|参数|值 |备注|
	|--|--|--|
	|yarn.log-aggregation.retain-seconds|-1|保留聚合日志的时间。-1为不生效。注意设置过小，会有NameNode的垃圾邮件|
	|yarn.log-aggregation.retain-check-interval-seconds|-1|检查被聚合的日志保留的时间区间。如果设置为0或负数，那么值将被计算Wie十分之一的汇总日志保留时间。之一，设置太小会有NameNode的垃圾邮件。|
	
	---
	
- etc/hadoop/mapred-site.xml

	- 配置MapReduce应用
	
	---
	
	|参数|值 |备注|
	|--|--|--|
	|mapreduce.framework.name|yarn|执行框架设置为Hadoop YARN|
	|mapreduce.map.memory.mb|1536|映射(map)的大资源限制|
	|mapreduce.map.java.opts|-Xmx1024M|映射(map)的子JVM的大堆规模限制|
	|mapreduce.reduce.memory.mb|3072|归约(reduce)的|
	|mapreduce.reduce.java.opts|-Xmx2560M|归约(reduce)的子JVM的大堆规模限制|
	|mapreduce.task.io.sort.mb|512|数据排序效率的高内存限制|
	|mapreduce.task.io.sort.factor|100|文件排序时更多的流一次合并|
	|mapreduce.reduce.shuffle.parallelcopies|50|reduce平行复制到从大量map中获取输出的更高的数量设置|
	
	---
	
	- 配置MapReduce的JobHistory服务器
	
		
	---
	
	|参数|值 |备注|
	|--|--|--|
	|mapreduce.jobhistory.address|host:port|MapReduce的JobHistory服务器地址，默认端口为10020|
	|mapreduce.jobhistory.webapp.address|host:port|MapReduce的JobHistory服务器UI地址，默认端口为19888|
	|mapreduce.jobhistory.intermediate-done-dir|/mr-history/tmp|MapReduce任务写历史文件的目录|
	|mapreduce.jobhistory.done-dir|/mr-history/done|MapReduce的JobHistory服务器管理历史文件的目录|
	
	---	
	
	
### 监控NodeManager的健康状态


## 未完明日再续