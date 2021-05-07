---
layout: post
title:  Spring学习--源码环境安装（IDEA）
category: 技术
tags: Spring Java
keywords: Spring Java
description: 源码环境安装
date: 2021-05-07
author: followtry
published: true
---


### 下载源码

从github上克隆spring-framework项目，并下载到本地。

在build.gradle文件内搜索`repositories`添加上阿里云的依赖，将jar包的maven仓库换为阿里云的

[阿里云指南地址](https://maven.aliyun.com/mvn/guide)

```gradle
  repositories {
    maven { url 'https://maven.aliyun.com/repository/public/' }
    maven { url 'https://maven.aliyun.com/repository/spring/' }
    mavenLocal()
    mavenCentral()
  }
```

### 执行gradle编译

将代码分支切换到`5.2.x`，这样对于jdk8版本可以正常执行编译命令，使用`master`分支，需要jdk9及以上的jdk版本，否则会报错。

按照`import-into-idea.md`的说明执行命令`./gradlew :spring-oxm:compileTestJava`.

```log
Downloading https://services.gradle.org/distributions/gradle-5.6.4-bin.zip
.........................................................................................

Welcome to Gradle 5.6.4!

Here are the highlights of this release:
 - Incremental Groovy compilation
 - Groovy compile avoidance
 - Test fixtures for Java projects
 - Manage plugin versions via settings script

For more details see https://docs.gradle.org/5.6.4/release-notes.html

Starting a Gradle Daemon (subsequent builds will be faster)

> Task :spring-oxm:genJaxb
[ant:javac] : warning: 'includeantruntime' was not set, defaulting to build.sysclasspath=last; set to false for repeatable builds

BUILD SUCCESSFUL in 3m 20s
40 actionable tasks: 33 executed, 7 from cache
```

### 源码导入IDEA

`File` => `New` => `Project from existing sources` => 选择`spring-framework`项目目录下的`build.gradle`文件，IDEA会自动识别为gradle项目。然后会自动下载依赖包

### Coding

项目import完成后，会自动识别出`Resources`和`Sources`，可以开始coding了。

基础的项目`spring-context`依赖`spring-beans`，而`spring-beans`又依赖`spring-core`。阅读代码可以先从`spring-core`开始，从核心点一点点往外扩展。

