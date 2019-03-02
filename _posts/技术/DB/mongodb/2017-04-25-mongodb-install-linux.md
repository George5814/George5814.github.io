---
layout: post
title: Mongodb 学习之linux版本安装（一）
category: 技术
tags: MongoDB
keywords: 
description: 
---

### 1.	从官网下载mongodb最新版本

`#wget http://fastdl.mongodb.org/linux/mongodb-linux-x86_64-rhel62-3.2.4.tgz`
	 
![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-1.png)


### 2.	解压压缩包到指定目录(例如:/opt/local/mongodb-64-3.2.4)

`#tar -xzvf mongodb-linux-64-3.2.4.tgz  -C  /opt/local/mongodb-64-3.2.4`

![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-2.png)
 
### 3.	切换到解压目录 

`#cd /opt/local/ mongodb-64-3.2./bin`


Mongo操作的命令就在该目录中。

![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-3.png)

### 4.	为了方便操作使用，将mongo添加到环境变量中

`#vim /etc/profile`
 
![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-4.png)

使得修改生效

`#source /etc/profile`

### 5.	创建数据库目录

`#mkdir –p /data/db`

### 6.	Mongo解压即可用，直接执行命令
 
![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-5.png)

![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-6.png)
 


### 7.	客户端工具

截图中客户端工具已经连接上服务器

![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-7.png)
 
服务端相应日志

![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-8.png)
 
### 8.	Mongo客户端帮助命令

![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-9.png)
 
### 9.	Mongod 服务端帮助命令

![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-10.png)

![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-11.png)

![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-12.png)

![mongo](//raw.githubusercontent.com/George5814/blog-pic/master/image/mongodb/mongodb-install-13.png)
 
	 
 
 
