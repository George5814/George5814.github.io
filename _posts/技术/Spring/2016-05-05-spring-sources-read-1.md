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

**以FileSystemXmlApplicationContext的资源定义调用为例**

[ FileSystemXmlApplicationContext(configLocations,refresh,parent)](){:title="org.springframework.context.support.FileSystemXmlApplicationContext.FileSystemXmlApplicationContext(String[], boolean, ApplicationContext)"} => 
[refresh()](){:title="org.springframework.context.support.AbstractApplicationContext.refresh()"} =>
[obtainFreshBeanFactory()](){:title="org.springframework.context.support.AbstractApplicationContext.obtainFreshBeanFactory()"} =>
[refreshBeanFactory()](){:title="org.springframework.context.support.AbstractApplicationContext.refreshBeanFactory()"} =>
[loadBeanDefinitions(beanFactory)](){:title="org.springframework.context.support.AbstractRefreshableApplicationContext.loadBeanDefinitions(DefaultListableBeanFactory)"} =>
[loadBeanDefinitions(beanDefinitionReader)](){:title="org.springframework.context.support.AbstractXmlApplicationContext.loadBeanDefinitions(XmlBeanDefinitionReader)"} =>
[reader.loadBeanDefinitions(configLocations)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String...)"} =>
[loadBeanDefinitions(location)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String)"} =>
[loadBeanDefinitions(String)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String, Set<Resource>)"} =>
[resourceLoader.getResource(location)](){:title="org.springframework.core.io.ResourceLoader.getResource(String)"} =>
[this.resourceLoader.getResource(location)](){:title="org.springframework.core.io.ResourceLoader.getResource(String)"} =>
[getResourceByPath(location)](){:title="org.springframework.core.io.DefaultResourceLoader.getResourceByPath(String)"} =>
[getResourceByPath(path)](){:title="org.springframework.core.io.DefaultResourceLoader.getResourceByPath(String)"} =>
[getResourceByPath(path) ](){:title="org.springframework.context.support.FileSystemXmlApplicationContext.getResourceByPath(String)"} =>
[FileSystemXmlApplicationContext](){:title="org.springframework.context.support.FileSystemXmlApplicationContext"} 结束

 
- BeanDefinition载入

- 向IOC容器注册BeanDefinition的过程


**说明：**

- 初识化过程中，通过refresh()启动整个调用

- 使用的IOC容器为[DefaultListableBeanFactory](){:title="org.springframework.beans.factory.support.DefaultListableBeanFactory"}

- 具体的资源载入是在[XmlBeanDefinitionReader](){:title="org.springframework.beans.factory.xml.XmlBeanDefinitionReader"} 读入BeanDefinition时完成，在其基类[AbstractBeanDefinitionReader](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader"}中可看到。

- 载入过程的启动，可以在[AbstractBeanDefinitionReader](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader"}中的`loadBeanDefinitions(String... locations)`中看到

### 其他

- 依赖注入一般发生在应用第一次通过getBean()向容器索取Bean的时候
