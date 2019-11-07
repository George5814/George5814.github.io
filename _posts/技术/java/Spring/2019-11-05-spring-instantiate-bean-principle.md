---
layout: post
title: Spring源码学习 - Bean实例化机制
category: 技术
tags:  Spring Java
keywords: Spring Java instantiate
description: Bean实例化机制
modify: 2019-11-05
published: false
---


## 入口

`org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean`

## 底层调用

`org.springframework.beans.factory.support.SimpleInstantiationStrategy#instantiate`


## refresh的调用位置

1. `org.springframework.boot.context.embedded.EmbeddedWebApplicationContext#refresh`
2. `org.springframework.web.servlet.view.ResourceBundleViewResolver#initFactory`
3. `org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext#AnnotationConfigEmbeddedWebApplicationContext(java.lang.String...)`
4. `org.springframework.context.support.GenericGroovyApplicationContext#GenericGroovyApplicationContext(java.lang.String...)`
5. `org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(java.lang.String[], java.lang.Class<?>, org.springframework.context.ApplicationContext)`
6. `org.springframework.boot.SpringApplication#refresh`
7. `org.springframework.jca.context.SpringContextResourceAdapter#createApplicationContext`
8. `org.springframework.web.context.ContextLoader#configureAndRefreshWebApplicationContext`
9. `org.springframework.web.servlet.FrameworkServlet#configureAndRefreshWebApplicationContext`
10. `org.springframework.context.support.FileSystemXmlApplicationContext#FileSystemXmlApplicationContext(java.lang.String[], boolean, org.springframework.context.ApplicationContext)`
11. `org.springframework.context.support.GenericGroovyApplicationContext#GenericGroovyApplicationContext(org.springframework.core.io.Resource...)`
12. `org.springframework.web.filter.DelegatingFilterProxy#findWebApplicationContext`
13. `org.springframework.boot.context.embedded.XmlEmbeddedWebApplicationContext#XmlEmbeddedWebApplicationContext(java.lang.String...)`
14. `org.springframework.context.support.GenericXmlApplicationContext#GenericXmlApplicationContext(java.lang.Class<?>, java.lang.String...)`
15. `org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext#AnnotationConfigEmbeddedWebApplicationContext(java.lang.Class<?>...)`
17. `org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext(java.lang.Class<?>...)`
18. `org.springframework.web.servlet.view.XmlViewResolver#initFactory`
19. 


### aware 的处理类: `org.springframework.context.support.ApplicationContextAwareProcessor`

在`org.springframework.context.support.AbstractApplicationContext#prepareBeanFactory`中添加该处理器