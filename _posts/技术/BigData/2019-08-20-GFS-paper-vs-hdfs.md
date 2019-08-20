---
layout: post
title:  GFS 论文学习和 HDFS 实现的比较
category: 技术
tags: BigData
keywords: 
description: 
---
 
{:toc}

# 论文翻译

[GFS 的中文翻译版本](https://blog.csdn.net/xuleicsu/article/details/526386)

# 学习笔记

## GFS paper 学习

### GFS 与以往分布式文件系统的不同点

1. 容错性。
   部分错误不再被当错异常，而是视为常见情况处理。实时地监控、错误检测、容错、自动恢复对系统来说必不可少。
2. 面向大文件存储，
   几个 GB 或更大。TB 甚至 PB 规模的数据集时，传统的 KB级别大小(现有如 NTFS 等文件系统的文件块大小)的文件块很难管理，必须重新设计。做到对大文件高效，对小文件支持但不做优化。
3. 一次写多次读。
   通过添加新数据完成更新操作，而不是更改已有数据。因为文件比较大，客户机不做缓存。而**添加操作成为性能优化和原子性保证的焦点**
4. 两种读操作
   1. 大量数据的流式读。同一客户端的连续操作通常读文件的连续区域
   2. 少量数据的随机读。性能敏感的对少量数据读操作分类并批量处理。
5. 大量数据的连续写。也支持少量数据的随机写，但不必高效。
6. 支持同一文件的多用户并发写的添加操作语义。


### 系统接口

GFS 提供了相似的文件系统界面，但没有实现标准的 POSIX 的 api

### 体系结构

![GFS 架构图](//raw.githubusercontent.com/George5814/blog-pic/master/image/hdfs/gfs-arch.png)

#### 各节点介绍

1. GFS master :
   1. 维护文件系统的元数据(metadata),包括命名空间、访问控制信息、文件到块的映射和块的当前位置。
   2. 负责系统的集中调度： chunk 租约管理，孤儿块的垃圾收集，chunkserver 间的块迁移。
   3. 与 chunkserver 定期 heartBeat，给 chunkserver 传递指定和收集其状态。
2. GFS chunkserver： GFS 的数据存储节点。一个文件会被分为多个固定大小的 chunk(默认 64M)，每个 chunk 有全局唯一句柄，64 位的 chunkId。每个 chunk会被复制到多个 chunkserver 上，做数据冗余，保证可靠性。
3. GFS client： 嵌入在应用代码中与 GFS 服务端(master 和 chunkserver)通信代表应用来读写数据。client 只与 master 沟通数只限于数据存储的元数据。而数据的读写交换是与 chunkserver 进行的。**client 不缓存文件数据，但是会缓存与 master 交互的元数据。**

客户端和 chunkserver不缓存文件数据，因为文件内容可能太大而无法缓存。

#### 单 master

单 master 可以简化设计并使得 master 可以根据全局情况作出先进的块放置和复制决定。因此master 要尽量少的参与读写交互，避免成为系统瓶颈。**由此就会有主 chunkserver 的租约机制**

#### 读操作流程

1. client使用固定的块大小将应用程序指定的文件名和字节偏移转换成文件的一个块索引（chunk index）
2. client 给master发送一个包含文件名和块索引的请求
3. master回应对应的chunk handle和副本的位置（多个副本）。
4. client以文件名和块索引为键缓存这些信息。（handle和副本的位置）
5. Client 向其中一个副本发送一个请求，指定chunk handle和块内的一个字节区间。

**除非缓存信息失效或文件被重新打开，否则 client 对同一个块的请求不再与 master 交互**

#### 块规模

选择使用较大的块(64M),hdfs 中默认 256M，可以减少 master 存储的元数据的大小。

好处：

1. 减少 client 与 master 的交互。
2. client可能有多个操作，与 chunkserver 保持较长 tcp 连接可减少网络负载。







# 参考文献
> [典型分布式系统分析: GFS](https://www.cnblogs.com/xybaby/p/8967424.html)