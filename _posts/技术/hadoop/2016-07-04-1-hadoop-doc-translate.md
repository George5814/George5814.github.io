---
layout: post
title: 1.hadoop-2.7.2官网文档翻译-概述
category: 技术
tags:  Hadoop
keywords: 
description: 
---

{:toc}

## Apache Hadoop 2.7.2

Apache Hadoop 2.7.2 是2.x.y正式版产品线的小修改正式版本，基于之前的2.7.1稳定版构建。

这里是主要特性和改进的简短概述：

- Common

	- 使用Http代理服务器的认证得到修复。在通过代理访问WEBHDFS时是很有用的。
	- 新的Hadoop度量库允许直接写Graphite。
	- 规范HCFS（Hadoop Compatible Filesystem ）

- HDFS

	- 支持POSIX风格文件系统的扩展属性
	- 使用离线镜像查看器，客户端现在可以通过WEBHDFS的api接口浏览fsimage
	- NFS网关已经有了一系列的支持改善和BUG修复，Hadoop的端口映射不再需要运行在网关上，
	并且网关现在可以拒绝来自特权端口的连接。
	- SecondaryNameNode ，JournalNode和DataNode的WEb UI已经使用H5和JS修改过。
	
- YARN 
	
	- YARN的Rest API已经支持写/改操作。用户可以通过REST API提交或者杀死应用。
	- YARN内的时间线存储器，用于存储为应用生成和应用指定的信息，支持通过Kerberos认证
	- 公平调度支持动态水平用户队列，用户队列在运行时被动态创建于任何指定的父队列下。


## 入门

Hadoop文档包含Hadoop入门所需要的信息。
从[单节点安装]({% post_url 2016-07-04-2-hadoop-doc-translate %}){:title="单节点安装"  :target="_blank"}开始，会告诉你怎样安装单节点Hadoop。
然后移步到[集群安装]({% post_url 2016-07-04-3-hadoop-doc-translate %}){:title="集群安装"  :target="_blank"}去学习怎样安装多节点Hadoop
