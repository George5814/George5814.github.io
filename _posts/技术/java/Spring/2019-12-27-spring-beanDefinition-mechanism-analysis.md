---
layout: post
title:  Spring学习 - BeanDefinition 的机制
category: 技术
tags: Spring Java
keywords: Spring Java
description: 介绍 spring 存储 Bean 信息的载体：BeanDefinition
date: 2019-12-27
author: followtry
published: true
---


## BeanDefinition 介绍

BeanDefinition是 Spring 中一种重要的数据模型定义，用来存储 Spring 的启动期间通过 xml 或者注解方式扫描的需要注入进 Spring的 IOC 容器内的 Bean 信息。


## BeanDefinition 类依赖图

![BeanDefinition 类依赖图](https://raw.githubusercontent.com/George5814/blog-pic/master/image/spring/BeanDefinition2.png)


## BeanDefinition 类信息

### BeanDefinition

接口类，定义BeanDefinition结构的行为

### AbstractBeanDefinition

抽象类，定义了BeanDefinition中使用的大部分成员变量

|变量名|类型|默认值|说明|
|--|--|--|--|
|beanClass|java.lang.Object|-|所属的 Class 的类|
|scope|java.lang.String|-|作用域范围|
|qualifiers|java.util.Map<String, AutowireCandidateQualifier>|||
|instanceSupplier|java.util.function.Supplier|||
|factoryBeanName|java.lang.String|||
|factoryMethodName|java.lang.String|||
|constructorArgumentValues.indexedArgumentValues|ConstructorArgumentValues.Map<Integer, ValueHolder>|||
|constructorArgumentValues.genericArgumentValues|List<org.springframework.beans.factory.config.ConstructorArgumentValues$ValueHolder>|||
|propertyValues.propertyValueList|List<org.springframework.beans.PropertyValue>|||
|propertyValues.processedProperties|Set<java.lang.String>|||
|methodOverrides.overrides|Set<org.springframework.beans.factory.support.MethodOverride>|||
|initMethodName|String|||
|destroyMethodName|String|||
|description|String|||
|role|int|默认为ROLE_APPLICATION||
|resource|org.springframework.core.io.Resource|||
|nonPublicAccessAllowed|boolean| 默认为 true||
|lenientConstructorResolution|boolean| 默认为 true||
|abstractFlag|boolean| 默认为 false|抽象类标记|
|lazyInit|boolean| - ||
|autowireMode|int|AUTOWIRE_NO||
|dependencyCheck|int|DEPENDENCY_CHECK_NONE||
|dependsOn|String[]|-|依赖的集合|
|autowireCandidate|boolean|true|自动装备候选 bean|
|primary|boolean|false|默认为非主要 bean|
|enforceInitMethod|boolean|true||
|enforceDestroyMethod|boolean|true||
|synthetic|boolean|false||

设置默认配置。从BeanDefinitionDefaults中读取

- lazyInit 默认为 null
- autowireMode 默认为AUTOWIRE_NO
- dependencyCheck 默认为DEPENDENCY_CHECK_NONE
- initMethodName 默认为 null
- destroyMethodName 默认为 null




------------------------未完待续-----------------------------




