---
layout: post
title:  Java8升级为Java11遇到的问题
category: 技术
tags: Java
keywords: Java
description: 升级项目版本
date: 2021-09-18
modified_date: 2021-09-19
author: followtry
published: true
istop: false
---

## 描述

将java1.8的项目升级为java11项目时报错，在通过maven命令`mvn clean compile`执行时，maven提示`无效的目标发行版: 11 `。

## 方案

1. 使用IDEA时需要将其编译环境改为java11

路径： ` Intellij IDEA` -> Preference -> Build,... -> Compiler -> Java compiler. 右侧项目版本改为11  

![编译器设置]({{ site.img-prefix }}/image/java/java-idea-compile.png)

设置项目中指定的jdk版本为jdk11
 
![项目中jdk设置]({{ site.img-prefix }}/image/java/java-idea-compile-project.jpg)

2. 切换shell环境

可能当前环境下运行的jdk不是java11或更高版本。可通过`java -version`命令查看当前版本。如果不匹配，可切换版本后重试。



