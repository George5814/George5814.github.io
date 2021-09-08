---
layout: post
title:  Mysql--MySQL的入门基础知识点
category: 技术
tags: mysql db web
keywords: mysql db web
description: 
date: 2021-09-08
author: followtry
published: true
---


## Mysql入门基础知识点

### 权限变更

用户登录后，MySQL的连接器会从权限表里查询出拥有的权限并缓存在该连接中。管理员新调整的**新权限只在此后新建立的链接中生效**。


### 客户端连接断开时间

客户端如果太长时间没动静，连接器就会自动将它断开。这个**时间是由参数 wait_timeout 控制的，默认值是 8 小时**。

### 长连接和短连接

数据库里面，**长连接**是指连接成功后，如果客户端持续有请求，则一直使用同一个连接。**短连接**则是指每次执行完很少的几次查询就断开连接，下次查询再重新建立一个。


全部使用长连接后，你可能会发现，有些时候 MySQL 占用内存涨得特别快，这是因为 MySQL 在执行过程中临时使用的内存是管理在连接对象里面的。这些资源会在连接断开的时候才释放。所以如果长连接累积下来，可能导致内存占用太大，被系统强行杀掉（OOM），从现象看就是 MySQL 异常重启了。

解决方案： 

1. 定期断开长连接。使用时间长或者有大查询时
2. MySQL5.7及更高版本中，执行`mysql_reset_connection`来重新初始化资源，会将连接重新初始化，但不需要重新建立和鉴权

### 查询缓存

在实际执行前MySQL会先查缓存，缓存有数据则直接返回。但是如果更新频繁则缓存基本不生效，缓存仅对配置类等变更少的表性能提高明显。

按需缓存，设置MySQL参数`query_cache_type`为`DEMAND`。需要缓存的Sql中增加`SQL_CACHE`,如`select SQL_CACHE * from T where ID=10;`。

**Mysql8.0已经删除了查询缓存功能**

### 分析器

MySQL接收到未命中缓存的请求Sql时，会先做词法分析（识别出字符串所代表的含义），再做语法分析（检查Sql语法是否正确）。

```sql
selelct * from mysql;
-- ERROR 1064 (42000): You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'selelct * from mysql' at line 1
```

**解决了做什么的问题**

### 优化器

经过了分析器的词法分析和语法分析后，就需要来判断使用什么方式查询更快。比如选择哪个索引，或者表join的顺序。

**解决了怎么做的问题**

### 执行器

按照分析器和优化器的结果，查询存储引擎来获取数据。


## 日志


`redo log`（重做日志）

`binlog`（归档日志）

三点不同：

|redo log|binlog|
|--|--|
|Innodb特有的日志|MySQL的server层的日志，所有引擎都可以使用|
|物理日志，记录了在哪一页上做了哪些修改|是逻辑日志，记录的是这个语句的原始逻辑。|
|循环写，空间固定|追加写，一个文件写完换另一个新文件继续写|

### redo log

MySQL在更新时使用了`WAL`(write ahead loging)的技术。关键点是先写日志，在写磁盘。

当一条记录更新时，innodb会将记录先记在`redo log`中，并更新内存，这样就算完成更新了。而同时innodb在适当时候，会将redo日志操作记录更新进磁盘中。`redo log`的大小是固定的。且

有两个游标`write pos`和`checkpoint`，`write pos`标记为写的位置，`checkpoint`标记为刷盘的位置。因为`redo log`的文件是循环使用的。如果`write pos`追上了`checkpoint`，则`redo log`就写满了，必须暂停新的更新，将`redo log`刷盘后再更新。而`redo log`可以保证即使MySQL宕机重启，还可以保证数据不丢失，这个机制叫做`crash-safe`

### 更新流程

以`update T set a = a+1 where id = 2`为例

1. 引擎用树搜索找到`id=2`这行。如果这行本来在内存中，则直接返回给执行器，否则从磁盘读入内存，返回给执行器。
1. 执行器拿到引擎给的数据，并执行`a = a+1`操作，得到一条新的记录。
1. 执行器调用引擎接口写入这行数据。
1. 引擎将这行数据更新到内存中，同时新将这个更新操作记录到`redo log`,此时的`redo log`处于prepare状态。
1. 引擎告知执行器执行完成，随时可以提交事务。
1. 执行器生成该操作的binlog，并将binglog写入磁盘。
1. 执行器调用引擎的事务提交接口，引擎把刚刚写入的`redo log`改成commit状态，更新完成。

此处引擎的`redo log`做了prepare和commit操作，典型的两段式提交。


如果数据库crash后，恢复数据时，发现redo log中有一条处于prepare状态的更新记录，怎么处理？写binlog是在redo log中出现prepare记录且没有置为commit的时候进行的，所以没法知道崩溃的时候处在(binlog还没写入)和(binlog已经写入但是redolog还没置为commit)两种可能状态。这个问题怎么解决呢？

答： 如果配置的是双1,那么事务提交成功一定写了binlog,并刷盘, 崩溃后如果redolog和binlog都不存在,那么事务回滚,如果已经写入binlog,则事务会提交


## 事务

ACID（Atomicity、Consistency、Isolation、Durability，即原子性、一致性、隔离性、持久性

当数据库上有多个事务同时执行的时候，就可能出现脏读（dirty read）、不可重复读（non-repeatable read）、幻读（phantom read）的问题，为了解决这些问题，就有了“隔离级别”的概念。

SQL 标准的事务隔离级别包括：读未提交（read uncommitted）、读提交（read committed）、可重复读（repeatable read）和串行化（serializable ）

在“可重复读”隔离级别下，这个视图是在事务启动时创建的，整个事务存在期间都用这个视图。在“读提交”隔离级别下，这个视图是在每个 SQL 语句开始执行的时候创建的。




