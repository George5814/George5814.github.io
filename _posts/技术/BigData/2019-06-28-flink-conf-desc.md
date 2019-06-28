---
layout: post
title:  flink 配置项介绍
category: 技术
tags: BigData
keywords: 
description: 
---
 
{:toc}


## flink-conf.yaml文件配置

|配置项|说明|默认值|
|---|----|----|
|taskmanager.numberOfTaskSlots|每个 taskManager 上可运行的 TaskSlot的数量，每个 taskslot 运行一个 pipeline|1|
|parallelism.default|flink 程序在未指定并行数量时的默认值|1|
|rest.port|Flink web 页面端口号设置||
|web.submit.enable|是否允许通过 web 页面提交程序|false|
||||
||||
||||
||||


## slaves

文件每行内容指代一个 TaskManager

默认为 1 个 localhost.

如果想启动多个 TaskManager，可以在 slaves 文件中写上多行的机器名

![flink进程](//raw.githubusercontent.com/George5814/blog-pic/master/image/flink/flink-process.png)


## FAQ

### taskManager 变多了

如果原来的 FLink 的集群不停止，又再次执行 start 脚本，新的 JobManager 端口会启动失败，但是 TaskManager 是可以启动起来的，然后这几个 TaskManager 会注册到原来的 JobManager。

解决办法是通过 stop脚本停掉服务。taskManager 停掉数量与 slaves 文件内指定的相同。如果taskManager多一倍，则需要 stop 两次。

会在`/tmp`目录下有 jobManager和 TaskManager 的 pid 文件，stop 会根据 pid 文件内的 id 进行停用。


## 独立部署

只有在独立部署下，conf 目录的 master 文件和 slaves 文件才会有用.
1. master 文件用来指定记录 JobManager
2. slaves 文件用来指定多个 TaskManager


## 集群部署 

集群部署

HA

yarn 配置 

**暂不涉及记录**