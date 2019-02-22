---
layout: post
title: SnappyData排序函数比较
category: 技术
tags:  SnappyData
keywords: 
description: 
---


## 排序函数分类

- row_number（）
- rank（）
- dense_rank（）

## 区别

### row_number

常用方式是：`ROW_NUMBER() OVER (PARTITION BY col_id, t.time ORDER BY col_id DESC)`

