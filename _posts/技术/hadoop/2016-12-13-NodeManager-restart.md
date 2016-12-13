---
layout: post
title: NodeManager的重启
category: 技术
tags:  Hadoop
keywords: 
description: NodeManager的重启
---

{:toc}

#### 介绍

该文档给出了NodeManager（NM）重启的概要，开启NM在重启时不会丢失运行在节点上的活跃容器的特性。在该高度上，NM存储任一必要状态到本地状态存储，处理容器管理的请求。
当NM重启时，会首先为多个子系统加载状态恢复。并且然后让那些子系统使用已经加载的状态执行恢复。

## 启用NM的重启

步骤1.为了启用NM重启功能，需要在`yarn-site.xml`中将如下属性设置为true。

|属性|值|
|--|--|
|yarn.nodemanager.recovery.enabled |true,默认为false|

步骤2.配置NM可以保存它的运行状态的本地文件系统目录的路径

|属性|值|
|--|--|
|yarn.nodemanager.recovery.dir|默认为`$hadoop.tmp.dir/yarn-nm-recovery`|

步骤3.为NM配置合法的RPC地址

|属性|值|
|--|--|
|yarn.nodemanager.address |通过`yarn.nodemanager.address`,暂时端口（默认为0）不能用于NM的RPC服务器指定。因为会使得NM在重启后和重启前使用不同的端口。这样会使得在NM重启之前连接的NM正常运行的客户端中断。使用地址及给定端口号明确指定`yarn.nodemanager.address`是启用NM重启的前提。|

步骤4.辅助服务

- YARN集群中的NM可以配置为运行辅助服务。为了完全的NM重启功能，YARN支持任何辅助服务配置支持回复。这通常包含：
	
	1. 避免使用暂时端口导致先前可以运行的客户端在重启后终端。
	2. 当NM重启并重新初始化辅助服务时，通过重新加载之前的状态使得辅助服务自己支持可恢复性。
	
- 上述的简单的例子是MR的辅助服务`ShuffleHandler`,ShuffleHandler遵从上面两个条件，因此用户/管理员对它不用做任何事情就能支持NM的重启：
	
	1. 配置属性`mapreduce.shuffle.port`控制Nm主机上ShuffleHandler绑定的端口。默认么有临时端口。
	2. ShuffleHandler服务也支持NM重启后恢复到之前的状态。 



