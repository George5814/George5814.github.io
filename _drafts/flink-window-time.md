---
layout: post
title:  flink window 和 time 学习笔记
category: 技术
tags: BigData
keywords: flink,bigdata,timewindow
description: flink窗口事件的学习笔记
modify: 2019-10-16
published: true
---
 
{:toc}

学习视频位置： <https://www.bilibili.com/video/av49401210>

### Keyed Windows

window 编程的伪代码

```java
stream
    .keyedBy(...) //
    .window(...)    // required: "assigner"
    .trigger(...)  // optional: "triger" else default trigger
    .evictor(...)  // optional: "evictor" else no evictor
    .allowedLateness(...)  // optional: "allowedLateness" else zero
    .sideOutputLateData(...)  // optional: "output tag" else no side output for late data
    .reduce/aggregate/fold/apply(...) //required :"function"
    .getSideOutput(...)  // optional: "output tag"
    
```


### Non-Keyed Windows

```java
stream
    .windowAll(...)    // required: "assigner"
    .trigger(...)  // optional: "triger" else default trigger
    .evictor(...)  // optional: "evictor" else no evictor
    .allowedLateness(...)  // optional: "allowedLateness" else zero
    .sideOutputLateData(...)  // optional: "output tag" else no side output for late data
    .reduce/aggregate/fold/apply(...) //required :"function"
    .getSideOutput(...)  // optional: "output tag"
    
```

