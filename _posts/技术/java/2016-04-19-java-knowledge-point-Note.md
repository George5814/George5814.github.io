---
layout: post
title: java知识点札记
category: 技术
tags: java
keywords: 
description: 
---

### 1. 密码存储
	密码加密存储并只保存密码的md5摘要

### 2. int和Integer默认值
	int的默认值为0，而Integer的默认值为null
	
### 3.java 中添加跨域
	HttpServletResponse response
	response.addHeader("Access-Control-Allow-Origin","*");
