---
layout: post
title:  Spring学习 - 元数据存储结构 -- BeanDefinition 和 AnnotationMetadata 的机制
category: 技术
tags: Spring Java
keywords: Spring Java
description: 介绍 spring 存储 Bean 信息的载体：BeanDefinition 和 AnnotationMetadata
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


## AnnotatedBeanDefinition

注解用接口

新增接口方法

- getMetadata
- getFactoryMethodMetadata


用于获取注解的元数据和方法的元数据

## GenericBeanDefinition

AbstractBeanDefinition的主要实现类。

GenericBeanDefinition是标准bean定义的一站式服务。与任何bean定义一样，它允许指定一个类加上可选的构造函数参数值和属性值。此外，可以通过“parentName”属性灵活地配置来自父bean定义的派生。通常，使用这个GenericBeanDefinition类的目的是注册用户可见的bean定义(post-processor可以对其进行操作，甚至可能重新配置父名称)。使用RootBeanDefinition / ChildBeanDefinition来预先确定父/子关系。

新增`parentName`属性

## ScannedGenericBeanDefinition

## AnnotatedGenericBeanDefinition

## ConfigurationPropertiesValueObjectBeanDefinition

------------------------未完待续-----------------------------

## 各个实现类的应用场景是哪些？

**TODO**


## AnnotationMetadata介绍

![AnnotationMetadata类依赖图](https://raw.githubusercontent.com/George5814/blog-pic/master/image/spring/AnnotationMetadata.png)

AnnotationMetadata是描述 Bean 上注解信息的数据结构，用来对 Bean 的注解元数据进行存储。主要的实现类有`SimpleAnnotationMetadata`和`StandardAnnotationMetadata`。

## 怎么

