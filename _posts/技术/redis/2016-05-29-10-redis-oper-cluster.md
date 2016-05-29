---
layout: post
title: redis 10：redis cluster命令操作
category: 技术
tags:  Redis
keywords: 
description: 不定期更新
---

{:toc}


### 1.查看cluster节点信息

`redis-cli -c -h host -p port cluster nodes`

`-c` ：代表集群cluster的意思

![查看cluster节点信息](/public/pic/redis/redis-cmd-cluster-info.png "查看cluster节点信息")

详细含义：

- 节点ID
- host:port
- 标志: master, slave, myself, fail, …
- 如果是个从节点, 这里是它的主节点的NODE ID
- 集群最近一次向节点发送 PING 命令之后， 过去了多长时间还没接到回复。.
- 节点最近一次返回 PONG 回复的时间。
- 节点的配置纪元（configuration epoch）：详细信息请参考[Redis 集群规范 ](http://www.redis.cn/topics/cluster-spec.html "Redis 集群规范"){:target="_blank"}。
- 本节点的网络连接情况：例如 connected 。
- 节点目前包含的槽：例如 127.0.0.1:7001 目前包含号码为 5960 至 10921 的哈希槽。


### 3.查看指定主节点的从节点信息

`redis-cli -c -h host -p port cluster slaves master_id`

![查看指定主节点的从节点信息](/public/pic/redis/redis-cmd-cluster-slave.png "查看指定主节点的从节点信息")

详细含义：

- slave节点id
- host:port
- 标识:slave
- 所属master的节点id
- 集群最近一次向节点发送 PING 命令之后， 过去了多长时间还没接到回复。.
- 节点最近一次返回 PONG 回复的时间。
- 节点的配置纪元（configuration epoch）：详细信息请参考[Redis 集群规范 ](http://www.redis.cn/topics/cluster-spec.html "Redis 集群规范"){:target="_blank"}。
- 本节点的网络连接情况：例如 connected 。



### 2.查看指定key所在的hash slot

`redis-cli -c -h h2s1 -p 6379 cluster keyslot key`

![查看cluster节点信息](/public/pic/redis/redis-cmd-cluster-keyslot.png "查看cluster节点信息")



