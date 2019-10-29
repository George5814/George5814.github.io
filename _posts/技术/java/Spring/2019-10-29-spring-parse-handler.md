---
layout: post
title: Spring学习 - Spring 的解析器
category: 技术
tags:  Spring Java
keywords: 
description: Spring 解析器浏览并记录笔记
published: false
---

## spring core

## SimplePropertyNamespaceHandler

简单属性处理器

## UtilNamespaceHandler

用于命名空间`util`下的处理器

#### ConstantBeanDefinitionParser

常量解析器

#### PropertyPathBeanDefinitionParser

属性路径解析器

#### ListBeanDefinitionParser

list 解析器

#### SetBeanDefinitionParser

set 解析器

#### MapBeanDefinitionParser

map 解析器

#### PropertiesBeanDefinitionParser

属性配置解析器

## spring aop

### AopNamespaceHandler

切面的命名空间`aop`处理器

#### ConfigBeanDefinitionParser

配置的 bean 解析器，用于`<aop:config>`

#### AspectJAutoProxyBeanDefinitionParser

代理的解析器。用于`aspectj-autoproxy`或`@AspectJ-style`

#### ScopedProxyBeanDefinitionDecorator

scope代理的解析器，用于`<aop:scoped-proxy/>`

## spring context

### ContextNamespaceHandler

`context`命名空间下的处理器

#### PropertyPlaceholderBeanDefinitionParser

解析属性的占位符

####  PropertyOverrideBeanDefinitionParser

属性覆盖

####  AnnotationConfigBeanDefinitionParser

解析注解配置

#### ComponentScanBeanDefinitionParser

扫描注解标记的 bean

#### LoadTimeWeaverBeanDefinitionParser

编织器解析器

#### SpringConfiguredBeanDefinitionParser

Spring 配置的 Bean 解析器

####  MBeanExportBeanDefinitionParser

mBean的暴露

####  MBeanServerBeanDefinitionParser

mBean 的服务


### LangNamespaceHandler

`lang`命名空间下的处理器，处理多语言脚本，如 groovy，bsh，std

#### org.springframework.scripting.groovy.GroovyScriptFactory

groovy 的脚本工厂

#### org.springframework.scripting.bsh.BshScriptFactory

bash 的脚本工厂

#### org.springframework.scripting.support.StandardScriptFactory

标准的脚本工厂

#### ScriptingDefaultsParser

默认脚本解析器

### TaskNamespaceHandler

`task`命名空间下的处理器

#### AnnotationDrivenBeanDefinitionParser

注解驱动解析器

#### ExecutorBeanDefinitionParser


线程池解析器

#### ScheduledTasksBeanDefinitionParser

调度任务解析器

#### SchedulerBeanDefinitionParser

调度线程池解析器


## spring tx

### TxNamespaceHandler

`tx`命名空间下的处理器

#### TxAdviceBeanDefinitionParser

事务通知解析器

#### AnnotationDrivenBeanDefinitionParser

事务注解驱动

#### JtaTransactionManagerBeanDefinitionParser

事务管理器

## spring mvc

### MvcNamespaceHandler

`mvc`命名空间下的处理器

#### AnnotationDrivenBeanDefinitionParser

注解驱动解析器

#### DefaultServletHandlerBeanDefinitionParser

默认的 servlet 处理器

#### InterceptorsBeanDefinitionParser

拦截器解析器

#### ResourcesBeanDefinitionParser

资源解析器

#### ViewControllerBeanDefinitionParser

可视化 controller 解析器

#### ViewControllerBeanDefinitionParser

状态 controller 解析器

#### TilesConfigurerBeanDefinitionParser

配置解析器

#### GroovyMarkupConfigurerBeanDefinitionParser

groovy 解析器

#### ScriptTemplateConfigurerBeanDefinitionParser

脚本引擎配置

#### CorsBeanDefinitionParser

核心解析器




 
