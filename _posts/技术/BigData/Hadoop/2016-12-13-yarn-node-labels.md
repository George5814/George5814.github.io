---
layout: post
title: YARN上节点标签
category: 技术
tags:  Hadoop
keywords: 
description: YARN上节点标签
---

{:toc}

节点标签是一组具有相似特征的节点，应用程序可以指定在哪里运行。

现在只支持节点分区，如下：

- 一个节点只有一个节点分区，因此一个集群可以通过节点分区分割为互不相交的几个子集群。默认，节点属于`DEFAULT`分区（partition=""）。

- 用户需要配置每个分区上不同的队列有多少资源可以使用。

- 以下有两种节点分区：
	
	- 专有的：会通过精确匹配节点分区将容器分配到节点。比如，请求“X”分区将会给节点分配分区“X”，请求“DEFAULT”分区将会分配到“DEFAULT”分区节点。
	
	- 非专有的： 如果一个分区为非专有的，它会分享闲置资源给请求DEFAULT分区的容器。
	

用户可以指定可以被每个队列访问的节点标签的集合，一个应用只可以使用包含该应用的队列可访问节点标签的子集。

## 特性

节点标签现在支持一下几点功能：

- 集群分区 -每个节点可以被指定一个标签，因此集群可以被分隔为几个互斥的更小的分区。

- 队列上的节点标签的ACL -用户可以在每个队列上设置可访问的节点标签。因此一些节点只可以被指定的队列访问。

- 指定一个队列可访问分区上资源的百分率 - 用户可以像这样设置百分率：队列A可以访问节点（label=hbase）上30%的资源。
这样的百分率会在资源管理器中保持一致。

- 在资源请求中指定所需要的节点标签  只有节点具有相同的标签时才会被分配。如果没有资源请求中指定的节点标签，那么资源请求只会被分配DEFAULT分区上的资源。

- 可操作性

	- 节点标签和节点标签映射可以在RM重启后恢复。
	- 更新节点标签 -在RM运行时，管理员可以在节点上和队列上的标签。

## 配置

**设置资源管理器启用节点标签**

在`yarn-site.xml`中设置一下属性

|属性|值|
|--|--|
|yarn.node-labels.fs-store.root-dir |hdfs://namenode:port/path/to/store/node-labels/|
|yarn.node-labels.enabled |true|

**注意：**

- 确保`yarn.node-labels.fs-store.root-dir`设置的目录已经创建并且RM有权限访问该目录。

- 如果用户想要将节点标签存储到RM的本地文件系统（替代HDFS），路径配置要像这样：`file:///home/yarn/node-label`

### 添加/修改节点标签列表并node-to-labels映射到YARN

- 添加集群的节点标签列表：
	
	- 执行`yarn rmadmin -addToClusterNodeLabels "label-1"`来添加节点标签。
	
	- 如果用户没有指定(exclusive=...),默认为true。
	
	- 运行 `yarn cluster --list-node-labels `或者`yarn  cluster  -lnl`检查新添加的标签在集群中可见。

- 添加标签到节点：
	
	- 执行`yarn rmadmin -replaceLabelsOnNode "node1[:port]=label1,label3 node2=label2"`,添加label1和label3到节点node1，添加label2到节点node2。
	如果用户没有指定端口号，会直接将标签添加到该节点上运行的所有NM上。而且该标签名必须在集群的节点标签列表中存在。

**注意：**
	
	标签名称只能包含：范围为{0-9, a-z, A-Z, -, _}，符号为英文的
	node1和node2为运行NM的主机名。


## 节点标签的Schedulers配置

### Capacity Scheduler配置

|属性|值|
|--|--|
|`yarn.scheduler.capacity.<queue-path>.capacity `|设置队列可以访问的DEFAULT分区的节点的百分率。DEFAULT分区下属子节点的和必须等于100|
|`yarn.scheduler.capacity.<queue-path>.accessible-node-labels `|管理员需要制定每个队列可以访问的标签，以逗号分隔，比如"hbase,storm"意味着队列可以访问访问标签hbase和storm。所有队列都可以访问没有标签的节点，用户不能制定。如果没有指定该属性，将会集成它的parent。如果用户想明确指定队列可以访问的没有标签的节点，只输入空格作为值|
|`yarn.scheduler.capacity.<queue-path>.accessible-node-labels.<label>.capacity `|设置队列可以访问的属于`label`分区的节点的百分率。`label`管理的子节点的容量的总和必须等于100，默认为0|
|`yarn.scheduler.capacity.<queue-path>.accessible-node-labels.<label>.maximum-capacity`|与`yarn.scheduler.capacity.<queue-path>.maximum-capacity`类似，每个队列的最大标签容量，默认为100|
|`yarn.scheduler.capacity.<queue-path>.default-node-label-expression`|像`hbase`这样的值，意味着如果应用在提交到队列时的资源请求没有指定节点标签，会使用'hbase'作为默认节点标签表达式。默认为空，因此应用会从没有标签的节点获取资源。|


#### 节点标签配置的例子

假设我们的队列结构如下：

	
		       root
			/    |   \
	engineer   sales  marketing
	

在集群中有5个节点(主机名为：h1...h5)，每个24G内存，24个虚核，h5有GPU，因此管理员添加`GPU`标签给h5。

假定用户的Capacity Scheduler配置如下：

```
yarn.scheduler.capacity.root.queues=engineering,marketing,sales
yarn.scheduler.capacity.root.engineering.capacity=33
yarn.scheduler.capacity.root.marketing.capacity=34
yarn.scheduler.capacity.root.sales.capacity=33

yarn.scheduler.capacity.root.engineering.accessible-node-labels=GPU
yarn.scheduler.capacity.root.marketing.accessible-node-labels=GPU

yarn.scheduler.capacity.root.engineering.accessible-node-labels.GPU.capacity=50
yarn.scheduler.capacity.root.marketing.accessible-node-labels.GPU.capacity=50

yarn.scheduler.capacity.root.engineering.default-node-label-expression=GPU

```

说明：root.engineering/marketing/sales.capacity=33，因此每个队列可以保证没有分区的资源的三分之一，因此他们可以使用h1...h4的三分之一的资源，即24 * 4 * (1/3) = (32G内存，32虚核)

并且engineering/marketing有权限访问GPU分区。每个engineering/marketing队列可以获取分区GPU上的二分之一的资源。24 * 0.5 =（12G内存，12虚核）

**注意：**

- 完成CapacityScheduler配置后，需要执行`yarn rmadmin -refreshQueues`来应用改变。

- 去RM的WEB页面的scheduler 页面检查是否配置成功。

## 为应用指定节点标签

应用可以使用一下的java接口指定节点标签

- `ApplicationSubmissionContext.setNodeLabelExpression(..) `设置应用所有容器的节点标签表达式。

- `ResourceRequest.setNodeLabelExpression(..) `为个别的资源请求设置节点标签表达式。

- 在`ApplicationSubmissionContext`指定`setAMContainerResourceRequest.setNodeLabelExpression`指明AM容器期望的节点标签。

## 监控

### 通过web页面监控

通过下面的web连接监控标签相关的属性

- 节点页面：<http://RM-Address:port/cluster/nodes>,可以获取每个节点的标签。

- 节点标签页：<http://RM-Address:port/cluster/nodelabels>,可以获取类型，激活NM数量，没法分区的总资源。

- Scheduler 页：<http://RM-Address:port/cluster/scheduler>,可以获取到每个队列的标签相关设置，队队列分区的资源使用。

### 命令行监控

- 使用命令`yarn cluster --list-node-labels `或`yarn cluster -lnl`获取集群标签

- 使用命令`yarn node -status <NodeId>`获取给定的节点的节点状态。此处的NodeId为NM的`host:port`,可以通过`yarn node  -list`查看。

报告示例：

```
Node Report : 
        Node-Id : h2m1:56512
        Rack : /default-rack
        Node-State : RUNNING
        Node-Http-Address : h2m1:8042
        Last-Health-Update : Tue 13/Dec/16 12:52:19:691PST
        Health-Report : 
        Containers : 0
        Memory-Used : 0MB
        Memory-Capacity : 8192MB
        CPU-Used : 0 vcores
        CPU-Capacity : 8 vcores
        Node-Labels : label1
```
