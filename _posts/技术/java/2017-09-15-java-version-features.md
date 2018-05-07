---
layout: post
title: java各版本新特性
category: 技术
tags: Java
keywords: 
description: 描述整理从java5-java9各版本新特性
---


{:toc}

## java9


1. Modular System（Jigsaw Project） 模块化
1. Http2 Client  支持Http2和新的客户端
1. Process API Enhance 进程接口增强
1. Try-With-Resources  来自java7，在java9中有优化。减少finally语句编写
1. Diamond Operator Extension java7提供的钻石语法扩展到匿名内部类
1. Interface Private Method 接口提供私有方法，为java8中的默认方法或静态方法服务
1. JShell  增强java语言的动态特性，可以像脚本语言一样运行
1. JCMD sub-command 诊断命令，并移除了Jhat
1. Optional To Stream 对java8中提供的Optional支持Stream操作
1. Immutable Set 增加不可变集合实现。ImmutableCollections
1. Unified JVM Logging 统一的JVM日志系统
1. Publish-Subscribe Framework Reactive流的消息订阅发布框架，，由Flow提供.增强CompletableFuture的API和其他一些提升
1. Variable Handles  禁止应用中使用Unsafe，但提供了invoke包提供类似原子核Unsafe操作
1. Мulti-Resolution Image API 多分辨率图像的API
1. 轻量级的JSON API 
1. G1作为默认的垃圾回收器并移除java8中标记为过期的GC回收器,将CMS标记为过期
1. H5风格的javadoc，并可以搜索
1. 解析JS的引擎nashorn，实现了ES6指定的功能。<http://openjdk.java.net/jeps/292>
1. SHA-3算法，废弃SHA-1算法
1. New Version-String Scheme 新的版本字符串方案
1. UTF-8 Property Resource Bundles UTF-8的属性资源包，更改ResourceBundle类的默认文件编码，以将属性文件从ISO-8859-1加载到UTF-8。
1. java9中支持的Unicode标准到V8
1. New HotSpot Build System 替换为的HotSpot构建系统
1. 将Applet API标记为过期，开发者只能从Java Web或者安装应用上二选一。在有意向移除的时间点上回将其注解Deprecated标注为forRemoval=true

更多特性请看<http://openjdk.java.net/projects/jdk9/>

## java8
1. Java语言的新特性

    1. Lambda表达式

    1. Functional接口

    1. 接口的默认方法与静态方法

    1. 方法引用 
Class::new,Class:static_method,Class::Method,instance::Method

    1. 重复注解

    1. 更好的类型推测机制

    1. 扩展注解的支持

1. Java编译器的新特性

    1. 参数名字

1. Java 类库的新特性
    1. Optional功能

    1. Stream API

    1. Date/Time API (JSR 310)

    1. JavaScript引擎Nashorn

    1. Base64 

    1. 并行（parallel）数组

    1. 并发（Concurrency） ConcurrentHashMap

1. 新的Java工具

    1. Nashorn引擎: jjs

    1. 类依赖分析器jdeps

1. Java虚拟机（JVM）的新特性
    1. PermGen被替换为metaspace

1. Optimize java.text.DecimalFormat.format 优化DecimalFormat
