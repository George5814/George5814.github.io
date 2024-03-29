---
layout: post
title: mysql命令
category: 技术
tags:  Mysql
keywords: 
description: 
---

## 前提

安装好mysql数据库

**特别注意**在更改用户权限后，修改使用`flush privileges`刷新权限表。

## mysql 命令

### 创建用户

`insert into mysql.user(Host,User,Password) values("%","test",password("1234"));`


### 创建数据库

`create database testDB`

### 为用户授权

授权格式：`grant 权限 on 数据库.* to 用户名@登录主机 identified by "密码";`

#### 授权test用户拥有testDB数据库的所有权限（某个数据库的所有权限）

`grant all privileges on testDB.* to test@localhost identified by '1234';`

`flush privileges;`//刷新系统权限表

#### 指定部分权限给一用户

`grant select,delete,update,create,drop on *.* to test@"%" identified by "1234";`

并使用`FLUSH PRIVILEGES` 刷新。

### 分区表

#### 查看表状态

`show table status;`

#### 查看表分区信息

```sql
-- 查看表的分区信息
 select 
  TABLE_SCHEMA as dbName,
  TABLE_NAME as tableName,
  partition_name as `partition`,  
  PARTITION_ORDINAL_POSITION as ordinal,
  PARTITION_METHOD as method,
  partition_expression as expr,  
  partition_description as `desc`,
  table_rows  
from information_schema.partitions  where 
  table_schema = schema()  
  and table_name='test'; 
```