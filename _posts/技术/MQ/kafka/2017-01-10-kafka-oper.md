---
layout: post
title: kafka的基本操作
category: 技术	
tags:  Kafka
keywords: 
description: 简单描述kafka的基本操作
---
 
{:toc} 

部分操作在[kafka集群安装]({% post_url 2016-05-14-kafka-setup %}){:title="kafka集群安装"  :target="_blank"}中。

### 查看topic信息

查看所有topic

`kafka-topics.sh  --describe --zookeeper h2m1:2181`

查看指定的topic,可以是多个topic，用逗号分隔。

`kafka-topics.sh  --describe --zookeeper h2m1:2181 --topic test2,test`