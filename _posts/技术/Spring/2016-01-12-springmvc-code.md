---
layout: post
title: springmvc 的问题笔记
category: 技术
tags:  Spring
keywords: 
description: 
---

{:toc}

 
### 1.springMVC 的@response 中文乱码问题

解决办法：

```java
@RequestMapping 中添加 produces = "plain/text; charset=UTF-8"  解决@ResponseBody乱码问题

@RequestMapping(value = "/msgFlowAnal/{flag}", produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseBody
public String  msgFlowAnal(@PathVariable("flag") int flag,HttpServletRequest request){
    //逻辑代码
}
```

### 2.springMVC restFul 形式url，即url中包含参数

```java
@RequestMapping(value = "/day_active/{year}/{month}/{day}", produces = MediaType.APPLICATION_JSON_VALUE)

//{day:.+}包含所有的"."及其后的内容
@RequestMapping(value = "/day_active/{year}/{month}/{day:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
```
