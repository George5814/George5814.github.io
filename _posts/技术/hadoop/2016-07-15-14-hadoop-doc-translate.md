---
layout: post
title: 14.hadoop-2.7.2官网文档翻译-服务级别的授权指南
category: 技术
tags:  Hadoop
keywords: 
description: 服务级别的授权指南。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/ServiceLevelAuth.html
---

{:toc}


## 目标

该文档描述了怎样配置和管理hadoop服务级别的授权

## 前提

确保安装了hadoop，配置和设置正确。更多信息请查看:

- 对第一次用户的[单节点安装]({% post_url 2016-07-04-2-hadoop-doc-translate %}){:target="_blank"}

- [分布式集群]({% post_url 2016-07-04-3-hadoop-doc-translate %}){:target="_blank"}


##  概览

服务级授权是初始授权机制来确保连接到特定的hadoop服务的客户有必要的，预配置的，有权限和授权访问的特定的服务。比如，MapReduce集群可以使用该机制允许一个配置列表的用户或组提交任务。

`$HADOOP_CONF_DIR/hadoop-policy.xml`配置文件用于定义各种hadoop服务的访问控制列表。

在其他访问控制检查，如文件权限检查、作业队列的访问控制等之前，对服务级授权进行。

## 配置

该段描述了怎样通过配置文件`$HADOOP_CONF_DIR/hadoop-policy.xm`配置服务级别授权。

### 启用服务级别授权

默认情况下，hadoop的服务级别授权是关闭了，为了开启他。需要在`$HADOOP_CONF_DIR/core-site.xml`中的`hadoop.security.authorization`属性设置为true。

### hadoop服务和配置属性

下面列举了多种hadoop服务和他们的配置：

ACL(访问控制列表，access control list)

|属性|服务|
|--|--|
|security.client.protocol.acl|客户端协议的ACL，用于分布式文件系统上的用户码|
|security.client.datanode.protocol.acl|ClientDatanodeProtocol的ACL，块回复的客户端-DataNode协议|
|security.datanode.protocol.acl|DatanodeProtocol的ACL，用于DataNode和NameNode间通信|
|security.inter.datanode.protocol.acl|InterDatanodeProtocol的ACL，更新生成时间戳的内部DataNode协议|
|security.namenode.protocol.acl|NamenodeProtocol的ACL，用于secondary NameNode与NameNode的通信|
|security.job.client.protocol.acl|JobSubmissionProtocol的ACL，为了任务提交，查询任务状态等，用于客户端与ResourceManager通信|
|security.job.task.protocol.acl|TaskUmbilicalProtocol的ACL，用于map和reduce任务与NodeManager的通信|
|security.refresh.policy.protocol.acl	|RefreshAuthorizationPolicyProtocol的ACL，用于dfsadmin与rmadmin 命令刷新安全策略生效|
|security.ha.service.protocol.acl|HA服务协议的ACL，用于HAAdmin 管理激活的和备用的NameNode状态|


### 访问控制列表

`$HADOOP_CONF_DIR/hadoop-policy.xml`定义了每个hadoop服务的访问控制列表。每个访问控制列表有一个简单的格式：

user和groups都是逗号分隔的名称列表。两个列表间以空格分隔。

比如：`user1,user2 group1,group2`

如果只提供一个组列表，则在该行的开头添加一个空格，相当于一个逗号分隔的用户列表后面的空格或者没有什么意味着只有一组给定用户。

指定值为`*`意味着所有的用户都可以访问该服务。

如果服务的访问控制列表没有定义，`security.service.authorization.default.acl`的值将会生效。如果`security.service.authorization.default.acl`没有设定,`*`会生效。

- 在某些情况下会阻塞访问控制列表，需要为一个服务指定阻塞的访问控制列表。该指定的用户或组的列表将没有权限访问该服务。阻塞访问控制列表的格式与访问控制列表的格式相同。
阻塞访问控制列表可以通过`$HADOOP_CONF_DIR/hadoop-policy.xml`指定。属性名通过后缀".blocked"得到。  
举例：阻塞访问控制列表的属性`security.client.protocol.acl`将会是`security.client.protocol.acl.blocked`。  
对于一个服务，可能会同时指定访问控制列表和阻塞访问控制列表。如果用户在访问控制列表中同时没在阻塞访问控制列表中，就可以访问服务。  
如果服务的阻塞访问控制列表没有定义，`security.service.authorization.default.acl.blocked`的值会生效。如果`security.service.authorization.default.acl.blocked`未定义，阻塞访问控制列表就会为空。

### 刷新服务级别授权配置

NameNode和ResourceManager的服务级别授权配置可以在不重启任何hadoop主进程的情况下更改。
集群管理员可以更改主节点上的`$HADOOP_CONF_DIR/hadoop-policy.xml`并通过切换`dfsadmin`和`rmadmin`的` -refreshServiceAcl`命令通知NameNode和ResourceManager加载各自的配置。

NameNode刷新服务级别授权配置:`$ bin/hdfs dfsadmin -refreshServiceAcl`

ResourceManager舒心服务级别授权配置：`$ bin/yarn rmadmin -refreshServiceAcl`

当然，也可以使用`$HADOOP_CONF_DIR/hadoop-policy.xml`中的`security.refresh.policy.protocol.acl`的属性限制访问某些用户或组刷新服务级别授权的配置。

- 使用ip地址，host名和IP地址范围列表的对服务的访问控制可以基于客户端IP地址控制。也可以使用指定ip地址，host名和ip地址范围的机器集合限制访问服务。每个服务的属性命令来自于相应的acl名称。如果acl的属性名称是`security.client.protocol.acl`,host列表的属性名会是`security.client.protocol.hosts`。  
如果一个服务的主机列表没有定义,会使用`security.service.authorization.default.hosts`的值，如果`security.service.authorization.default.hosts`值没有设置，会使用`*`。      
指定主机的阻塞列表是可能的。  只有在主机列表的这些机器不能访问。没有再阻塞列表中的允许访问服务。属性名通过后缀`.blocked`获取。
举例：阻塞主机列表的属性名`security.client.protocol.hosts`将会是`security.client.protocol.hosts.blocked`。   
如果一个服务的阻塞注解列表没有定义,会使用`security.service.authorization.default.hosts.blocked`的值，如果`security.service.authorization.default.hosts.blocked`的值也没有设定，阻塞主机列表为空。

### 例子

只允许用户'alice'和'bob'和在'mapreduce'组的用户可以将任务提交到MapReduce集群

```
<property>
     <name>security.job.client.protocol.acl</name>
     <value>alice,bob mapreduce</value>
</property>

```

只允许运行DataNode的，并属于DataNode组的用户可以与NameNode通信

```
<property>
     <name>security.datanode.protocol.acl</name>
     <value>datanodes</value>
</property
```


允许所有的用户作为DFS的客户访问HDFS集群

```
<property>
     <name>security.client.protocol.acl</name>
     <value>*</value>
</property>
```













