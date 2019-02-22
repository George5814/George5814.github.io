---
layout: post
title: dataflow论文阅读笔记
category: 技术
tags: dataProcess
keywords: 
description: 
---
 
{:toc}


dataflow原文：<https://ai.google/research/pubs/pub43864>

# dateflow模型出现的背景

 - 无边界、乱序、大规模的数据越来越普遍
 - 数据使用者的复杂需求
    - 按事件发生时间顺序进行计算
    - 按数据自身的特征进行窗口计算
    - 立即得到数据分析的结果


## 现代数据处理系统演进

```mermaid
graph TD;
a -> b;
b -> c;
 ```




1.[MapReduce,Hadoop,pig,hive,spark] -> 2. 
    



