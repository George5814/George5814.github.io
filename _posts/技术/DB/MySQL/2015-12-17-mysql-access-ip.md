---
layout: post
title: 设置MYSQL允许通过IP访问
category: 技术
tags:  Mysql
keywords: 
description: 
---

{:toc}

## 错误背景：

  Navicat mysql客户端连接时，使用localhost和127.0.0.1能连接数据库，使用本机的ip地址10.2.116.17不能访问。用户为root

## 错误示例：

`SQL Error (1130): Host '10.2.116.17' is not allowed to connect to this MySQL server`     

## 错误原因：

名为mysql数据库内user表里root用户对应记录的host字段值为localhost。因此mysql限制root用户只允许本地登录，不允许ip登录。

## 解决方案：

使用命令行界面登录mysql： 

```mysql
    c:>mysql -uroot -pyourpwd

    //打开要使用的数据库mysql

    mysql>use mysql;

    //将hsot字段值设为%，允许所有ip访问。只在测试时使用

    mysql>update user set host = '%'  where user ='root';

    //这一步很重要，权限更改需要刷新权限

    mysql> flush privileges;

    //查看是否host字段已经改为%

   mysql>select 'host','user' from user where user='root';
```

## 解决结果：

         再次通过客户端，通过ip访问时就会显示连接成功了。祝你好运！！
