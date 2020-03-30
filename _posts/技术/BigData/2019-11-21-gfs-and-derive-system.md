---
layout: post
title:  GFS 及其衍生系统
category: 技术
tags: BigData GFS
keywords: BigData GFS  TFS HDFS Colossus google 分布式系统  分布式文件系统 云存储
description: 
date: 2019-11-22
author: followtry
published: false
---


## 理论来源

google 在 2003 年发布的论文：<https://ai.google/research/pubs/pub51>

## GFS

### 诉求

基于 Google 需要存储爬取的网页文件，并给 PageRank 算法等其他系统使用。要求是**连续的，大文件，一次写入多次读取**。

### 架构

构建在廉价机器上的可自动容错和恢复的大型分布式文件系统。

#### 角色

1. master
   1. 管理整个集群的元数据。包括**文件及chunkserver的命名空(相当于目录)**,**文件到chunk间的映射**,**chunk 的位置信息**及**chunk 租约管理**,**垃圾回收无用的chunk**,**chunk复制**,
2. ChunkServer
   1. 存储数据的地方，数据以 chunk 的方式存储。每个 chunk 都会被 master 分配集群全局唯一的 64 位句柄。
   2. chunk 以普通文件方式在 linux 上存储，并默认保存三份副本。
3. GFS 客户端
   1. 应用程序访问 GFS 集群的入口，**不遵循 POSIX规范**，以库的方式存在。会缓存从 master 获取的元数据，但是不会获取 chunkserver 上获取的数据。


架构图

![gfs- arch](https://raw.githubusercontent.com/George5814/blog-pic/master/image/hdfs/gfs-arch.png)


### 优点

#### 租约机制

为了减少客户端对 Master 机器的访问，避免造成性能瓶颈。Master 会在三个 chunk 副本中选一个主副本，通过租约机制将写权限授权给它。由祝福本来控制文件的读写。租期一般为 60s,可以续期延长。

#### 一致性模型

GFS 面向`append`而不是`overwrite`而设计。

追加成功的记录在GFS中各副本间是**严格一致的**。只要给客户端返回追加成功，则说明GFS 所有副本都至少成功追加了一次。如果有追加失败，客户端将会重试。

多客户端并发追加，可能会导致数据不连续。


#### 追加流程


![gfs- arch](https://raw.githubusercontent.com/George5814/blog-pic/master/image/hdfs/gfs-append.png)

**特色**

1. 追加操作使用`pipeline`方式，以便缩短数据存储的时间。
   1. client 找所有副本中最近的一个【副本A】开始传递数据。
   2. 【副本A】接收到一定数据后，开始转发到离其最近的【副本B】
   3. 然后依次往下传。直到最后一个副本写入成功，给客户端返回数据写入成功。此时数据写入了临时文件。
2. 控制流和数据流分开
   1. 前提是：每次追加的数据量都比较大


数据传输最理想的时间公式:`B/T + RL`

1. B: 传输的字节数
2. T：网络吞吐量
3. R：副本的数量
4. L：节点之间的延时

##### 可能存在的问题

1. 主副本租约过期失去chunk 修改操作的授权
   1. 猜测：客户端会重新请求 master 获取最新的 chunk 元数据，然后再操作控制流。即使过期不影响数据流上的数据写入。
2. 主副本或备副本所在的 chunkserver 出现故障
   1. 猜测：客户端等待超时失败，或 chunkserver 往下一节点复制时发现异常，中断并上报给上一节点，最终上报给客户端和 master。然后 master 会重新分配新的 chunk，客户端再次重试。

#### 容错机制

##### maste 容错

1. 以**日志+checkpoint 的方式**进行，并且保留一台热备机器。所有的元数据修改操作都发送到热备机器才算成功。
2. GFS 依赖Chubby（开源的为 Zookeeper）,来进行选主，保证同时只有一台 master


#### Chunkserver



### 缺点

#### master 单点问题

### 应用场景

GFS 的设计初衷是为了满足批量读写文件的需求。

## Colossus

### 架构

### 优点

### 缺点

### 应用场景

## HDFS

### 架构

### 优点

### 缺点

### 应用场景

## TFS

### 架构

### 优点

### 缺点

### 应用场景

## FastDFS

### 架构

### 优点

### 缺点

### 应用场景
