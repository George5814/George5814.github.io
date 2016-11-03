---
layout: post
title: 工作一年后的面试
category: 技术
tags: Linux
keywords: 
description: 
---

### 乐视O2O

- redis和mongo的性能和应用场景

	mongo读数据特别快。redis读写都快适合高并发的情况

	redis应用就是高并发。比如你网站同时会有10万个人读取那个数据。mysql连接池就炸了

	mongo的话，放一些字段比较随意的东西。以后可以再加。适合敏捷开发
	
面试问题：

1.	HashMap的get和put内部实现，内部存储结构
2.	HashMap的get怎么实现不同key，相同的hash值时的查找
3.	HashMap在JDK 1.8版本和1.7版本中的实现区别
4.	在数据量大时，HashMap的性能缺陷
5.	ConcurrentHashMap的实现原理，怎样实现并发
6.	并发包中其他常用集合
7.	Sleep和wait的作用
8.	Thread和Runnable在实现线程方面的区别
9.	Spring AOP
10.	Spring事务实现原理
11.	Spring IOC的Document是怎么解析的
12.	Mybatis的xml映射文件是如何解析的
13.	线程池
14.	Redis和mongo的应用场景，优缺点
15.	Mysql索引的原理和优化
16.	ZooKeeper的使用，原理和用途
17.	Kafka的用处，使用场景
18.	Hive 建表，元数据
19.	IOC的其他实现，比如Google的guice
20.	Jdk1.8中新特性的实现原理
21.	Docker操作
22.	JobTracker和TaskTracker之间怎么实现心跳
