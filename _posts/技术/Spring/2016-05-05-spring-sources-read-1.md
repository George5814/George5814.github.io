---
layout: post
title: Spring源码阅读笔记（1）
category: 技术
tags:  Spring
keywords: 
description: 对Spring源码阅读的简单笔记
---

{:toc}

### IOC容器初始化过程

- Resource定位

- BeanDefinition载入

- 向IOC容器注册BeanDefinition的过程


### 其他

- 依赖注入一般发生在应用第一次通过getBean()向容器索取Bean的时候
