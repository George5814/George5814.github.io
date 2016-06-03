---
layout: post
title: mybatis-generator错误集合
category: 技术
tags:  Mybatis
keywords: 
description: 
---

{:toc}

### 1.报找不到META-INF错误

- 错误截图：

![找不到META-INF](/public/pic/mybatis/mybatis-generator-error-1.png "找不到META-INF")

- 错误原因：
	
	
	src/main/resources下缺少META-INF目录
	

- 解决办法：

	//新建META-INF目录
	src/main/resources 右键  ->  New -> Source folder -> Folder name : META-INF
	

### 2. 自动生成时只有insert相关方法


- 错误截图：

![生成只有insert方法](/public/pic/mybatis/mybatis-generator-error-2.png "生成只有insert方法")

- 错误原因：
	
	数据库中的表没有配置主键
	

- 解决办法：

	为表字段配置主键
	
	
### 3. 通过配置生成表

取巧的办法：
	
	将创建表的sql写入insert等标签中即可。只要能保证sql正确，即可创建表。
	