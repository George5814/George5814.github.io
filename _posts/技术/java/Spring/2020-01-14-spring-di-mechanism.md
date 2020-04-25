---
layout: post
title:  Spring学习 - 依赖注入的实现原理
category: 技术
tags: Spring Java
keywords: Spring Java
description: 依赖注入的实现原理
date: 2020-01-14
author: followtry
published: false
---


## 背景

在 IOC 的基础上，Spring 是如何实现 DI（依赖注入）的。

## 什么是 DI

通常在 ClassA 中依赖 ClassB 的实例时，是在 A 代码中显示实例化 B。而通过 DI 功能，可以实现 B 在 A 中只声明，不显示实例化，而让 Spring 框架在启动时通过 xml 或者注解的方式在实例化 A 时，运行时判断依赖的 B 对象，然后通过反射的方式动态创建B 的对象，并继续通过 Field 或者 Method 方式反射赋值。

## Spring 实现 DI 的方式有哪些



## Spring 是怎么实现 DI 的


============================================================================================================================================

实例化流程
org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBeanInstance # 反射实例化空的 Bean 信息

- BeanUtils.instantiateClass(constructorToUse)

org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean
- org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation
- org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor#postProcessProperties





org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#initializeBean


=========================================================================================================================


org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#buildAutowiringMetadata

org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean #中的行的AutowiredAnnotationBeanPostProcessor开始会注入依赖的 bean
AbstractAutowireCapableBeanFactory的 1428 行的AutowiredAnnotationBeanPostProcessor开始会注入依赖的 Bean

org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#postProcessProperties #处理依赖注入
org.springframework.beans.factory.annotation.InjectionMetadata#inject
org.springframework.beans.factory.annotation.InjectionMetadata#inject  # 从此处开始注入依赖的实例 DI
org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveDependency
org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency
org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveMultipleBeans  # 从此处获取多个实例 **核心解决方案**
org.springframework.beans.factory.support.DefaultListableBeanFactory#getBeansOfType(java.lang.Class<T>, boolean, boolean)
org.springframework.beans.factory.support.DefaultListableBeanFactory#findAutowireCandidates  **核心中的核心** 其实 Spring 是通过 Type 来获取到相应的注入的 Bean 的
org.springframework.beans.factory.config.DependencyDescriptor#resolveCandidate # 会直接调用BeanFactory#getBean来生成实例
org.springframework.beans.factory.support.DefaultListableBeanFactory#addCandidateEntry




org.springframework.beans.factory.BeanFactoryUtils#beansOfTypeIncludingAncestors(org.springframework.beans.factory.ListableBeanFactory, java.lang.Class<T>)