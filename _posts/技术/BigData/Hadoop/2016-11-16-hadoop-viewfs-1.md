---
layout: post
title: HDFS的viewfs指南
category: 技术
tags:  Hadoop
keywords: 
description: HDFS的viewfs指南
---

{:toc}

## 介绍

可视化文件系统（ViewFS）提供了一种管理多个Hadoop文件系统命名空间(或成命名空间卷)的方式。
对于在HDFS的Federation有多个NN，多个NS的集群是非常有效的。ViewFS类似于一些Unix/Linux系统的客户端安装表。
ViewFS可以用来创建个性化的NS视图和每个集群公用的视图。

该指南的上下文提出了Hadoop系统有多个集群，每个集群可以联合在多个命名空间内，也描述了联邦HDFS中的ViewFS提供给每个集群全局的NS，
因此应用可以以类似于单个联邦的方式运转。

## 古老世界（联邦之前）

### 单NN集群

在HDFS Federation之前的古老时间中呢，一个集群只有一个NN，其为集群提供单一的文件系统命名空间。

假如有多个集群，每个集群的文件系统命名空间（FSNS）都是完全独立和互斥的。

此外，集群间也没有物理存储的共享（比如，DN不会在集群间共享）。

每个集群的`core-site.xml`都有一个配置属性设置了集群的NN的默认文件系统。

```xml
<property>
  <name>fs.default.name</name>
  <value>hdfs://namenodeOfClusterX:port</value>
</property>
```

这样的配置属性允许你使用相对命名来解决相对于集群NN的路径问题。
比如,路径`/foo/bar`使用上面的配置映射为`hdfs://namenodeOfClusterX:port/foo/bar`。

该配置属性在集群的每个网关都会设置，并且在集群的关键服务，如JobTracker和Oozie上也会有。

### 路径名用法方式

因此在`core-site.xml`设置如上的集群X中，典型的路径名为：

1. `/foo/bar`
	- 该方式与`hdfs://namenodeOfClusterX:port/foo/bar`相等。
	
2. `hdfs://namenodeOfClusterX:port/foo/bar`
	- 如果这是合法路径，这种方式比`/foo/bar`更好，因为在需要时，它允许应用和它的数据显式的移动到另一个集群中

3. `hdfs://namenodeOfClusterY:port/foo/bar`
	- 这是另一个集群，如集群Y，的路径名的URL。
	另外，该命令用于从Y集群将文件复制到Z集群。`distcp hdfs://namenodeClusterY:port/pathSrc hdfs://namenodeClusterZ:port/pathDest`
	

4. `webhdfs://namenodeClusterX:http_port/foo/bar` 和 `hftp://namenodeClusterX:http_port/foo/bar`
	
	- 这两个分别是是通过WEebHDFS文件系统和HFTP文件系统访问文件的文件系统URL。
**注意:**WebHDFS 和HFTP使用NN上的HTTP端口，而不是RPC端口。

5. `http://namenodeClusterX:http_port/webhdfs/v1/foo/bar` 和` http://proxyClusterX:http_port/foo/bar`
	- 这两个分别是通过WEBHDFS 的REST接口和HDFS代理访问文件的HTTP协议的URL。

### 路径名使用最佳实践

当使用集群时，推荐使用第一中类型的路径名而不是全限定的像URL的方式。全限定的URL像地址，不允许应用移动他们的数据。

## 新世界 - Federation 和ViewFs

### 如何看集群

假如这有多个集群，每个集群有一到多个NN，每个NN有它自己的NS。一个NN仅仅属于一个集群。集群内的NN共享集群的物理存储。
跨集群的NS像以前一样独立。

基于存储需要，操作会决定存储在集群内的每一个NN上。比如，他们可能在一个NN中放置了所有的用户数据(`/user/<username>`)，
所有的食物数据(`/data`)在另一个NN上，所有的项目(`/project`)也在两一个NN上等等。

### 每个集群使用ViewFS的全局NS

为了古老世界提供透明度，ViewFS文件系统用于创意每个集群有一个独立的集群NS视图，与古老世界的NS相似。
客户端安装表就像Unix的安装表，他们使用古老的命名约定安置新的NS卷。接下来会展示安装表安装四个NS卷(`/user`,`/data`,`/project`,`/tmp`)

![每个集群典型的安装表](/public/pic/hadoop/viewfs_TypicalMountTable.png)

ViewFs实现了Hadoop文件系统接口，就像HDFS一样和本地文件系统。

感觉上这是比较琐细的的文件系统，只允许连接到其他文件系统。因为ViewFS实现了Hadoop文件系统接口，对Hadoop工具是透明的。比如，所有的shell命令可以在VIewFS工作，就像在HDFS和本地文件系统上一样。

安装表的安装点在标准的Hadoop配置文件中指定。在每个集群的配置中，那些集群的默认文件系统设置到安装表，如：

```xml
<property>
  <name>fs.default.name</name>
  <value>viewfs://clusterX</value>
</property>
```

在URL中的`vierFs://`后的授权是安装表的名字。推荐一个集群的安装表的名字以集群的名字命名。
然后如上配置，Hadoop文件系统会在Hadoop配置文件中寻找名叫“clusterX”的安装表。
对于整个集群操作会安排所有的网关和服务机器包含该安装表，因此，对于每个集群，就像上面说的，针对哪个集群的默认文件系统会设置到ViewFS中。

### 路径名用法方式

在`core-site.xml`中设置的默认文件系统使用安装表的集群X上，典型的路径名是：

1. `/foo/bar`
	- 该写法与`viewfs://clusterX/foo/bar`相等。如果这样的路径名用于古老的无联邦的世界里，然后可以透明的过渡到联邦上。
	
2. `viewfs://clusterX/foo/bar`
	- 如果是合法路径名，比`/foo/bar`号，因为应用和它的数据可以在需要的时候透明的移到另一个集群上。
	
3. `viewfs://clusterY/foo/bar`
	- 这是另一个集群Y的路径名的URL引用。另外，该命令可以将文件从集群Y复制到Z：
` distcp viewfs://clusterY:/pathSrc viewfs://clusterZ/pathDest`

4. `viewfs://clusterX-webhdfs/foo/bar` 和 `viewfs://clusterX-hftp/foo/bar`
	这些是分别通过WEBHDFS文件系统和HFTP文件系统访问文件的接口

5. `http://namenodeClusterX:http_port/webhdfs/v1/foo/bar` 和 `http://proxyClusterX:http_port/foo/bar`
	- 这些是分别通过WEBHDFS的REST接口和HDFS代理访问文件的HTTP协议的URL。

### 路径名用法最佳实践

在一个集群中时，推荐使用类型1替代类型2的全限定URL。进一步说，应用不应该使用安装点的知识，并使用像 `hdfs://namenodeContainingUserDirs:port/joe/foo/bar`的路径在个别NN中引用文件，而应该使用`/user/joe/foo/bar`

```bash
rename /user/joe/myStuff /data/foo/bar
```

在新世界中，如果`/user`和`/data`存储在一个集群的不同NN上，如上命令做法是行不通的。






















