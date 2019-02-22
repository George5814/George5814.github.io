---
layout: post
title: mysql性能优化-优化Sql语句
category: 技术
tags:  Mysql
keywords: 
description: 翻译自Mysql5.7的英文参考手册第8章
---


## 优化Sql语句

不论是通过解释器直接下载还是通过API的在幕后提交，可以明确数据库应用的核心逻辑是通过sql语句执行的。本节有助于提高所有种类的Mysql应用。该指南涉及到sql读写操作、Sql操作的幕后开销和在特定场景中使用的操作，如数据库监视。

### 优化Select语句

select语句形式的查询执行所有的数据库上的查找操作。调整这些语句时最优先的策略，无论时为了动态网页亚秒级访问还是产生隔夜的巨大的报表。

