---
layout: post
title:  Mysql--查询MySQL系统设置变量的命令
category: 技术
tags: mysql db web
keywords: mysql db web
description: 
date: 2021-08-06
author: followtry
published: true
---


## Mysql5.7 命令

```sql

-- 显示所有的变量
SHOW VARIABLES;

-- 事务提交级别
-- mysql默认为REPEATABLE-READ。阿里设置的默认级别为READ-COMMITTED
-- 只需要行锁，而不需要gap锁，虽然有幻读和不可重复读
-- 很多人容易搞混不可重复读和幻读，确实这两者有些相似。但不可重复读重点在于update和delete，而幻读的重点在于insert
--在 MySQL InnoDB 中，Repeatable Read 隔离级别不存在幻读问题，对于快照读，InnoDB 使用 MVCC 解决幻读，对于当前读，InnoDB 通过 gap locks 或 next-key locks 解决幻读。
show variables like 'transaction_isolation';


-- 显示innodb下的命令
SHOW  VARIABLES  LIKE 'innodb%';

-- 显示innodb的page大小
show variables like 'innodb_page_size';

-- 显示数据存储目录
show variables like 'datadir';

-- 连接超时时间
show variables like 'connect_timeout';

-- 显示查询缓存设置。 
-- 0 or OFF 默认关闭
-- 1 or ON 缓存所有可缓存的查询结果
-- 2 or DEMAND 按需缓存
show variables like 'query_cache_type';

-- 查询innodb缓存池大小
show variables like 'query_cache_size';

-- redolog写入缓冲池配置
show variables like 'innodb_buffer_pool_size';

-- 同步binlog
-- 0 禁用MySQL将二进制日志到磁盘同步，而是依靠 OS 不时地将二进制日志刷新到磁盘上
-- 1 每次提交事务之前将二进制日志同步到磁盘，这是最安全的设置，但是由于磁盘写入次数增加会对性能产生影响
-- N 在进行 n 次事务提交后，收集的二进制日志将同步到磁盘，较高的值可以提高性能，但会增加数据丢失的风险
show variables like 'sync_binlog';

-- 是否启用binlog
show variables like 'log_bin';

-- innodb关闭模式
-- 0 InnoDB会在关闭之前进行缓慢关闭、完全清除和更改缓冲区合并
-- 1 InnoDB在关机时跳过这些操作，这个过程称为快速关机
-- 2 InnoDB会刷新日志并冷关机，不会丢失已提交的事务，但崩溃恢复操作会使下一次启动花费更长时间
show variables like 'innodb_fast_shutdown';


-- 慢查询功能
show variables like '%slow%';

-- 显示连接信息
show variables like '%connection%';

```