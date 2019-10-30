---
layout: post
title: Springboot 参考手册学习 - 1. SpringBoot 实现原理
category: 技术
tags:  Spring Springboot Java
keywords: 
description: 深入学习定制Spring标签
published: false
---


## spring Boot 实现原理

获取 starter 的配置类的流程：

`@SpringBootApplication` =>  
`EnableAutoConfiguration` => 
`AutoConfigurationImportSelector.selectImports` => 
`AutoConfigurationImportSelector.getCandidateConfigurations` => 
`SpringFactoriesLoader.loadFactoryNames` =>  
`PropertiesLoaderUtils.loadProperties` =>
读取`FACTORIES_RESOURCE_LOCATION="META-INF/spring.factories`文件


将 starter 的`META-INF/spring.factories`中的Configuration类预先读取。该配置类一般会配置`@Configuration`和`@Bean`注解，通过这两个注解实例化 Bean。而 Bean 的实例化逻辑是在`@Bean`注解的方法内自定义实现的。如果使得该 Bean 可以被 Spring 管理，需要引入 Spring 的`BeanDefinitionRegistryPostProcessor`，将创建好的`BeanDefinition`注册进`BeanDefinitionRegistry`中。


## SpringBoot 启动过程

`SpringApplication#run` => 
`SpringApplication#getRunListeners` => 
`SpringApplication.prepareEnvironment -> StandardServletEnvironment` =>
`SpringApplication#getSpringFactoriesInstances` => 
`SpringFactoriesLoader#loadFactoryNames` => 
`SpringApplication#createSpringFactoriesInstances` => 
`SpringApplication#createApplicationContext -> AnnotationConfigServletWebServerApplicationContext`  => 
`SpringApplication#prepareContext` => 
`SpringApplication#load` => 
`SpringApplication#createBeanDefinitionLoader` => 
`BeanDefinitionLoader#load` => 
`BeanDefinitionLoader#isComponent` => 
`AnnotationUtils#findAnnotation` => 
`AnnotatedBeanDefinitionReader#register` => 
`SpringApplication#refreshContext` => 
`org.springframework.context.support.AbstractApplicationContext#refresh` =>
`AnnotationConfigServletWebServerApplicationContext#prepareRefresh` => 
`AbstractApplicationContext#obtainFreshBeanFactory` => 
`AbstractApplicationContext#getBeanFactory` => 
`AbstractApplicationContext#prepareBeanFactory` => 
`org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors` =>  **关键位置，scan 的入口处**
`PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors` => 
`PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors` => 
`BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry` => 
`ConfigurationClassPostProcessor#processConfigBeanDefinitions` => 
`ConfigurationClassParser#parse` => 
`ConfigurationClassParser#parse(AnnotationMetadata, java.lang.String)：202` => 
`ConfigurationClassParser#processConfigurationClass` => 
`ConfigurationClassParser#doProcessConfigurationClass` => 
`org.springframework.context.annotation.ComponentScanAnnotationParser#parse` =>  **解析 basePackage 并扫描类的位置**
`org.springframework.context.annotation.ClassPathBeanDefinitionScanner#doScan` => **扫描类**



1. `SpringApplication#createSpringFactoriesInstances` : 通过从`spring.factories`中获取到的`SpringApplicationRunListener`的实现类，将其实例化

2. `SpringApplication#createApplicationContext` 默认创建`AnnotationConfigApplicationContext`作为上下文。servlet 创建`AnnotationConfigServletWebServerApplicationContext`
3. `AnnotationUtils#findAnnotation` : **会递归查询类上声明的注解，直到找到指定注解`@Component`或者什么都没有找到**
4. `SpringApplication#refreshContext`:开始刷新上下文
5. `AbstractApplicationContext#refresh`: 核心加载方法
6. `ConfigurationClassPostProcessor`： 处理ConfigBeanDefinition的注册
7. `ConfigurationClassParser`： 解析`@Configuration`注解，会查询出`@ComponentScan`注解
   1. 关键方法：parse开头的方法
8. `ClassPathBeanDefinitionScanner`: 注解扫描类，将注解的类都扫描出并转为 BeanDefinition 注册进 Spring 中
9.  `AbstractApplicationContext#invokeBeanFactoryPostProcessors`: 扫描注解类并注册进 Spring 中