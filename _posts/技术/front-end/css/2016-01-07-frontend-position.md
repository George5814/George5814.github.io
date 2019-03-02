---
layout: post
title: 前端定位
category: 技术
tags:  Javascript
keywords: 
description: 
---


## position的值， relative和absolute分别是相对于谁进行定位的？
 
- absolute :生成绝对定位的元素， 相对于最近一级的 定位不是 static 的父元素来进行定位。

- fixed （老IE不支持）生成绝对定位的元素，相对于浏览器窗口进行定位。

- relative 生成相对定位的元素，相对于其在普通流中的位置进行定位。

- static 默认值。没有定位，元素出现在正常的流中
