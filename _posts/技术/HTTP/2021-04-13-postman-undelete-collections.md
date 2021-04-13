---
layout: post
title:  postman恢复误删除的collections
category: 技术
tags:  postman Java
keywords: postman Java
description: 
date: 2021-04-13
author: followtry
published: true
---

### 误删除接口

使用postman，在将collections移动到另一个工作空间时，误以为share到另一个工作空间后，两份数据互相独立，可以将原工作空间的collections删除。但删除后发现新的工作空间里也没有了该collections。突然就一阵着急，那可是一百多个api接口，一年的心血啊。唉，postman能不能恢复呢。然后就是网上一顿搜，找到了该文章 <https://blog.csdn.net/sylan15/article/details/82120521>

终于发现在postman的web端官网上可以恢复已删除的collections信息。
**当然能恢复误删除collections的前提是已经登录了账号并开启了同步功能**

### 登录官网操作恢复

访问<https://web.postman.co/me/trash>查看已被删除的collections。

![postman的web端恢复已删除collections](https://raw.githubusercontent.com/George5814/blog-pic/master/image/http/postman-web-restore.jpg)


### 查看

#### 恢复collections

![](https://raw.githubusercontent.com/George5814/blog-pic/master/image/http/postman-collections-restore_2.png)

#### 查看已恢复的collections

![](https://raw.githubusercontent.com/George5814/blog-pic/master/image/http/postman-collections-restore-3.png)


### 参考文档

> <https://blog.csdn.net/sylan15/article/details/82120521>
