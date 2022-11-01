---
layout: post
title:  jedis的redis/clients/util/SafeEncoder找不到
category: 技术
tags: jedis Java springboot
keywords: jedis Java
description: 
date: 2021-09-27
author: followtry
published: true
---

## 环境

- spring-boot: 2.1.18.RELEASE
- spring-boot-starter-data-redis: 2.1.18.RELEASE
- spring-data-redis: 2.1.21.RELEASE
- jedis: 3.2.0

## 现象及解决方式

新项目中将redis的驱动换为jedis，使用了3.2.0版本，在本地编译时没有问题。但是在启动时会报错。

![报错信息]({{ site.baseurl }}/img/redis/jedis-safeencoder-not-found.jpg)

尝试了升级jedis仍然不管用,发现即使有`SafeEncoder`类但是仍然提示找不到。偶然间发现类型相同，但是类路径不同。
将报错的类路径`redis.clients.util.SafeEncoder`在搜索引擎里搜索了下，发现该路径是jedis的2.9.0版本中的。


![SafeEncoder的不同类路径]({{ site.baseurl }}/img/redis/jedis-safeencoder-path-not-match.jpg)

因此将版本降级为2.9.0后，项目成功启动起来。


## 升级

另外，也可以通过升级spring-boot版本的形式将`spring-data-redis`的版本一并升级。
我将`spring-boot`升级为了`2.2.13.RELEASE`，相应的`spring-data-redis`版本升级为了`2.3.7.RELEASE`，这样也可以解决`SafeEncoder`找不到的问题。



## 分析

主要就是因为`spring-data-redis`包中的`org.springframework.data.redis.connection.jedis.JedisConverters`类对`jedis`的依赖版本不同 ，导致jar包中引入的类路径也不同。

-  在`spring-data-redis`的`2.3.7.RELEASE`版本中是引入的`import redis.clients.jedis.util.SafeEncoder;`
-  在`spring-data-redis`的`2.1.18.RELEASE`版本中是引入的`import redis.clients.util.SafeEncoder;`