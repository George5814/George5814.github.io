---
layout: post
title: Spring源码学习 - 1. 核心类
category: 技术
tags:  Spring Java
keywords: 
description: 学习记录 Spring 的核心类
published: false
---


## spring注解类扫描

`org.springframework.context.annotation.ClassPathBeanDefinitionScanner`

流程:

`ClassPathBeanDefinitionScanner#scan` => 
`ClassPathScanningCandidateComponentProvider#findCandidateComponents` =>
`ResourcePatternResolver#getResources` =>
`ClassPathScanningCandidateComponentProvider#isCandidateComponent` =>
`ClassPathScanningCandidateComponentProvider#isConditionMatch` => 
`ConfigurationClassUtils#isConfigurationCandidate` => 
`` => 
`` => 
`` => 
`` => 
`` =>

ClassPathBeanDefinitionScanner: 扫描 class的工具类
ResourcePatternResolver： 解析资源的抽象接口
ConfigurationClassUtils： 判断是否指定注解的工具类，**该类特别重要，所有对于注解的 match 判断都在这**
    1. isFullConfigurationCandidate方法： 判断注解`@Configuration`
    2. isLiteConfigurationCandidate方法： 判断注解`@Component`,`@ComponentScan`,`@Import`,`@ImportResource`,`@Bean`

AnnotationUtils.AliasDescriptor#from： 获取`@AliasFor`注解





 
