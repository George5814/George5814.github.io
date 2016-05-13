---
layout: post
title: Spring源码阅读笔记（2）
category: 技术
tags:  Spring
keywords: 
description: IOC容器的依赖注入
---

{:toc}

上一篇的[Spring源码阅读笔记（1）]({% post_url 2016-05-05-spring-sources-read-1 %}){:title="IOC容器的依赖注入"}简单讲解了IOC容器的初始化过程，接下来讲解IOC容器的依赖注入。

**依赖注入一般发生在应用第一次通过getBean()向容器索取Bean的时候，但也可以在BeanDefinition中控制lazy-init属性实现Bean的预实例化，即完成依赖注入的过程，在初始化时完成。**

## 1.IOC容器依赖注入Bean实例化过程

从`org.springframework.beans.factory.support.AbstractBeanFactory.getBean(String)`开始

[doGetBean(name, null, null, false)](){:title="org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(String, Class<T>, Object[], boolean)"} =>  
[createBean(beanName, mbd, args)](){:title="org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(String, RootBeanDefinition, Object[])"} =>  
[doCreateBean(beanName, mbdToUse, args)](){:title="org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(String, RootBeanDefinition, Object[])"} =>  
[createBeanInstance(beanName, mbd, args)](){:title="org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(String, RootBeanDefinition, Object[])"} =>  
[instantiateBean(beanName, mbd)](){:title="org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean(String, RootBeanDefinition)"} =>  
[getInstantiationStrategy().instantiate(mbd, beanName, parent)](){:title="org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(RootBeanDefinition, String, BeanFactory)"} =>  
[BeanUtils.instantiateClass(constructorToUse)](){:title="org.springframework.beans.BeanUtils.instantiateClass(Constructor<T>, Object...)"} 或者[instantiateWithMethodInjection(bd, beanName, owner)](){:title="org.springframework.beans.factory.support.CglibSubclassingInstantiationStrategy.instantiateWithMethodInjection(RootBeanDefinition, String, BeanFactory)"} =>  
[](){:title=""} =>  




### Bean实例化的方式：

 1. 使用工厂方法实例化

[instantiateUsingFactoryMethod(beanName, mbd, args)](){:title="org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(String, RootBeanDefinition, Object[])"}

  2. 使用容器的autowire特性，调用构造方法实例化 
  3. 使用默认的构造方法实例化

     




- hehehe
  - xixi
     -  de

**使用了`CGLIB`库生成java的字节码** 

