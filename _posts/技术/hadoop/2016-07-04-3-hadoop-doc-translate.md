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
	|mapreduce.reduce.memory.mb|3072|归约(reduce)的大资源限制|
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

Hadoop提供了一种机制，管理员可以通过配置NodeManager定期运行管理员提供的脚本来检测节点是否健康。

通过执行任何他们在脚本中选择的检查点，管理员可以检测节点是否处于健康状态。如果脚本发现节点处于非健康状态，它会向标准输出打印一条以`ERROR`开头的一行记录。NodeManager定期产生脚本并且将其检出。
如上所述，如果脚本检出中包含了`ERROR`,该节点的状态会被报告为`unhealthy `,节点将会被ResourceManager加入黑名单。该节点将不会被分配任务。然而，NodeManager会继续运行脚本，因此如果该节点又变成`healthy`状态，它会被自动从ResourceManager的node黑名单中移除。
节点的健康是随着脚本的输出变化的，如果它是`unhealthy`,可以在管理员的ResourceManager的WEB页面中获得。节点健康的时间也会显示在web界面上。

接下来的参数可以用来控制在`etc/hadoop/yarn-site.xml`中的节点健康状态的监控脚本。

		
---

|参数|值 |备注|
|--|--|--|
|yarn.nodemanager.health-checker.script.path|节点健康检测脚本路径|检查节点的健康状态|
|yarn.nodemanager.health-checker.script.opts|脚本的操作选项|脚本检查节点健康状态的选项|
|yarn.nodemanager.health-checker.script.interval-ms|间隔时间(单位：ms)|运行健康脚本的时间间隔|
|yarn.nodemanager.health-checker.script.timeout-ms|脚本执行超时时间(单位：ms)|健康脚本执行的超时时间|


---

如果只是一些本地磁盘坏了，健康检查脚本不应当给出`ERROR`信息。NodeManager可以周期性检查本地磁盘的健康（特别是检查` nodemanager-local-dirs`和`nodemanager-log-dirs`的指定位置），并且在达到的坏目录的数量阀值(基于在`yarn.nodemanager.disk-health-checker.min-healthy-disks`配置属性里设置的值)后，整个节点会被标记为`unhealthy`，该信息也会被发送到ResourceManager。
启动磁盘或者在启动磁盘中的故障是有健康检查脚本确定的。

### 从文件

 文件`etc/hadoop/slaves`中存在所有的从节点的`hostname`或者ip的列表，每个一行。Helper脚本将会使用`etc/hadoop/slaves`文件一次性在许多主机上运行命令。该文件不用于任何基于java的Hadoop配置。为了使用该设计，运行Hadoop的用户的ssh必须被信任(即免密码登录)。
 

### Hadoop 机架感知

许多Hadoop组件是机架感知和利用网络拓扑的性能和安全性。通过调用管理员配置模块，Hadoop的守护进程获取集群中从节点的机架信息。
可以查看[Rack Awareness](sdfdsf)文档获取更多具体信息。

**强烈推荐在启动HDFS之前配置机架感知能力**

### 日志

Hadoop通过apache的通用日志框架使用[log4j](http://logging.apache.org/log4j/2.x/)记录日志。
编辑`etc/hadoop/log4j.properties`文件自定义Hadoop守护进程的日志配置(日志格式等等)。

### 操作Hadoop集群

一旦所有必须的配置完成后，分发文件到所有机器的`HADOOP_CONF_DIR`目录。在所有机器上应该是相同的目录。

一般情况下，推荐HDFS和YARN以不同的用户身份运行。在大多数配置中，HDFS使用'hdfs'账户执行，YARN通常使用'yarn'账户执行。



#### Hadoop启动

为了启动Hadoop集群，你需要同时启动HDFS集群和YARN集群。

第一次使用HDFS，必须先格式化，格式化新的分布式文件系统如HDFS：

```
$ $HADOOP_PREFIX/bin/hdfs namenode -format <cluster_name>
```

在指定作为NameNode的HDFS节点上使用如下命令启动HDFS的NameNode

```
$ $HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs start namenode
```

在指定作为DataNode的HDFS节点上使用如下命令启动HDFS的DataNode

```
$ $HADOOP_PREFIX/sbin/hadoop-daemons.sh --config $HADOOP_CONF_DIR --script hdfs start datanode
```

如果`etc/hadoop/slaves`和ssh免密码登录设置成功。所有的HDFS进程可以使用一个公用脚本启动。

```
$ $HADOOP_PREFIX/sbin/start-dfs.sh
```

在指定作为ResourceManager的YARN节点上使用如下命令启动YARN

```
$ $HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start resourcemanager
```

在指定作为NodeManager的YARN节点上使用如下命令运行脚本

```
$ $HADOOP_YARN_HOME/sbin/yarn-daemons.sh --config $HADOOP_CONF_DIR start nodemanager
```

启动一个单独的`WebAppProxy`代理服务器。在作为`WebAppProxy`的YARN节点上运行如下命令，如果有多个服务器作为负载均衡，那么应该在每个机器上运行该脚本。

```
$ $HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR start proxyserver
```


如果`etc/hadoop/slaves`和ssh免密码登录设置成功。所有的YARN进程可以使用一个公用脚本启动。

```
$ $HADOOP_PREFIX/sbin/start-yarn.sh
```

使用以下命令，在设置作为mapred的机器上启动MapReduce的JobHistory服务器

```
$ $HADOOP_PREFIX/sbin/mr-jobhistory-daemon.sh --config $HADOOP_CONF_DIR start historyserver
```

#### Hadoop停止

在设计为NameNode的HDFS节点上执行如下命令停止NameNode

```
$ $HADOOP_PREFIX/sbin/hadoop-daemon.sh --config $HADOOP_CONF_DIR --script hdfs stop namenode
```

在设计为DataNode的HDFS节点上执行如下命令停止DataNode

```
$ $HADOOP_PREFIX/sbin/hadoop-daemons.sh --config $HADOOP_CONF_DIR --script hdfs stop datanode
```

如果`etc/hadoop/slaves`和ssh免密码登录设置成功。所有的HDFS进程可以使用一个公用脚本停止。

```
$ $HADOOP_PREFIX/sbin/stop-dfs.sh
```

在设计为ResourceManager的YARN节点上停止ResourceManager

```
$ $HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR stop resourcemanager
```


在设计为NodeManager的YARN节点上停止NodeManager

```
$ $HADOOP_YARN_HOME/sbin/yarn-daemons.sh --config $HADOOP_CONF_DIR stop nodemanager
```

如果`etc/hadoop/slaves`和ssh免密码登录设置成功。所有的YARN进程可以使用一个公用脚本停止。

```
$ $HADOOP_PREFIX/sbin/stop-yarn.sh
```

在设计为`WebAppProxy`的YARN节点上停掉`WebAppProxy`服务器。如果由多个用于负载均衡的服务器，需要在每个机器上运行该命令

```
$ $HADOOP_YARN_HOME/sbin/yarn-daemon.sh --config $HADOOP_CONF_DIR stop proxyserver
```

在Mapred的节点上停掉MapReduce JobHistory 服务器

```
$ $HADOOP_PREFIX/sbin/mr-jobhistory-daemon.sh --config $HADOOP_CONF_DIR stop historyserver
```

### WEB 接口

一旦Hadoop集群启动，运行如下描述的各组件的WEB界面

|守护进程|web接口|备注|
|--|--|--|
|NameNode|http://nn_host:port/|默认端口为50070|
|ResourceManager|http://rm_host:port/|默认端口为8088|
|MapReduce JobHistory server|http://jns_host:port/|默认端口为19888|
