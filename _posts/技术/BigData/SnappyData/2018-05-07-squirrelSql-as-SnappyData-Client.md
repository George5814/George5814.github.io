---
layout: post
title: Squirrel-sql客户端连接SnappyData手册
category: 技术
tags:  SnappyData
keywords: 
description: 
---


# Squirrel-sql客户端下载

1.  **squirrel-sql客户端是java开发，理论上支持所有的实现了JDBC接口规范的DB驱动。**

2.  [squirrel-sql下载地址](http://www.squirrelsql.org/#installation "squirrel-sql下载地址")
3.  [Squirrel源码地址](https://sourceforge.net/p/squirrel-sql/git/ci/master/tree/ "Squirrel源码地址")

# squirrel-sql安装  

第一个界面如下图，然后全部选择Next，直到安装完成。

![squirrel1](http://omsz9j1wp.bkt.clouddn.com/image/snappydata/Squirrel_1.png)

![squirrel1](http://omsz9j1wp.bkt.clouddn.com/image/snappydata/Squirrel_2.png)

![squirrel1](http://omsz9j1wp.bkt.clouddn.com/image/snappydata/Squirrel_3.png)

![squirrel1](http://omsz9j1wp.bkt.clouddn.com/image/snappydata/Squirrel_4.png)

![squirrel1](http://omsz9j1wp.bkt.clouddn.com/image/snappydata/Squirrel_5.png)

![squirrel1](http://omsz9j1wp.bkt.clouddn.com/image/snappydata/Squirrel_6.png)


# 配置修改

"应用程序" finder 中找到**SQuirreLSQL.app，右击 "显示包内容"。路径下 contents/MacOS/squirrel-sql.sh**，将该脚本中的

`SQUIRREL_SQL_HOME='dirname "$0"'/Contents/Resources/Java'`替换为`SQUIRREL_SQL_HOME='/Applications/SQuirreLSQL.app/Contents/Resources/Java'`


解决办法出自：[https://stackoverflow.com/questions/46370597/launching-squirrel-sql-client-on-mac-os](https://stackoverflow.com/questions/46370597/launching-squirrel-sql-client-on-mac-os "https://stackoverflow.com/questions/46370597/launching-squirrel-sql-client-on-mac-os")

# 配置SnappyData驱动

## 双击打开Squirrel-sql后，配置SnappyData的驱动如图

![配置SnappyData驱动](http://omsz9j1wp.bkt.clouddn.com/image/snappydata/Squirrel_7.png)


## 创建新的SnappyData会话

![squirrel1](http://omsz9j1wp.bkt.clouddn.com/image/snappydata/Squirrel_8.png)

连接上SnappyData服务后的界面，更多功能就需要自主探索了。理论上JDBC接口规范支持的功能应该是都支持的。

![Squirrel操作界面](http://omsz9j1wp.bkt.clouddn.com/image/snappydata/Squirrel_9.png)






