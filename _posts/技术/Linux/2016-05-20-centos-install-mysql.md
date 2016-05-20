---
layout: post
title: Centos7上安装Mysql
category: 技术
tags: Linux
keywords: 
description:  
---

{:toc}


在Centos的库中Mysql已经被Mariadb数据库替代，如果想继续在centos上安装mysql就需要自己下载安装包

```bash
# wget http://dev.mysql.com/get/mysql-community-release-el7-5.noarch.rpm

# rpm -ivh mysql-community-release-el7-5.noarch.rpm

# yum install mysql-community-server
``` 

运行成功后重启mysql服务:

```bash
# service mysqld  restart  或者 #/etc/init.d/mysqld restart

```

初始安装的mysql的root用户是没有密码的，配置root密码

```bash
# MySQL -uroot
mysql> set password for ‘root’@‘localhost’ = password('mypasswd');
mysql> exit
```
