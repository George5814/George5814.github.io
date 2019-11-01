---
layout: post
title: Spring源码学习 - Scanner的工作原理
category: 技术
tags:  Spring Java
keywords: Spring Java genBeanName
description: Scanner的工作原理解析
modify: 2019-11-01
published: true
---

Spring 自带的类扫描器为`ClassPathBeanDefinitionScanner`,在使用注解`@ComponentScan`,或者xml 配置`<context:component-scan/>`时都会调用该扫描器扫描指定的目标类。

`ClassPathBeanDefinitionScanner`最重要的方法是`doScan`,通过该方法完成 BeanDefinition 的生成和注册，
在扫描时会配置默认的过滤器用于扫描,比如`@Component`,`@ManagedBean`,`@Named`。

## 扫描流程

扫描并注册流程

1. 解析 basePackages，将其分隔为数组，然后供`doScan`方法调用
2. 在`scanCandidateComponents`中将其 basePackage 转换为 Class 路径,`classpath*:/cn/followtry/**/*.class`
3. 通过默认的资源加载器,也就是类加载器`ClassLoader`解析指定的路径上的资源。
4. 判断资源是否可读,即是否存在。存在则创建元数据读入器`MetadataReader`。
5. 判断是否是候选的组件。先判断是否在排除的过滤器集合中，再判断是否在包含的过滤器集合中，对在包含过滤器中的组件还需要判断是否是匹配上的Condition。**可见排除(exclude),的级别比包含(include)高**
6. 对于判断为候选的组件，会生成`ScannedGenericBeanDefinition`作为转换为`BeanDefinition`的载体。
7. 将Resource 信息设置进`ScannedGenericBeanDefinition`，然后再判断其是否为候选组件，如果是则添加到候选组件列表中。判断条件为该 Bean通过元数据判断是独立的，并且是具体类 或者 抽象类但是有`@Lookup`注解。参考`ClassPathScanningCandidateComponentProvider#isCandidateComponent`
8. 对于`AbstractBeanDefinition`类型的`BeanDefinition`设置默认值和`Autowire`的候选值。参考`ClassPathBeanDefinitionScanner#postProcessBeanDefinition`
9.  对于`AnnotatedBeanDefinition`类型的`BeanDefinition`，会处理一些公共类型的注解。如`@Lazy`,`@Primary`,`@DependsOn`,`@Role`,`@Description`等注解，会将这些注解的 value 数据补充进`BeanDefinition`。参考方法:`AnnotationConfigUtils#processCommonDefinitionAnnotations`
10. 检查候选者信息，如果检查通过则新注册 Bean。判断条件为：1.该 beanName 是否已经注册。2.对于已注册的情况，检查新老 bean 是否兼容（类型相同，source 相同，两个 bean 定义相同）。如果不兼容会抛出异常。参考`ClassPathBeanDefinitionScanner#checkCandidate`
11. 构建持有器`BeanDefinitionHolder`，然后将其注册进 Spring 容器内。参考`ClassPathBeanDefinitionScanner#registerBeanDefinition`。



**注意：扫描工具只会将候选的类筛选出来并注册 Bean 信息，但此时的 Bean 还未实例化。只是类的基本信息已经注册进 Spring 容器。**

## 调用入口

sourceClass为参数类，如`cn.followtry.boot.java.BriefSpringbootApplication`

### org.springframework.context.annotation.ComponentScanAnnotationParser

`ComponentScanAnnotationParser`是给注解`@ComponentScan`使用的，该类没有标记`public`修饰符，只能在当前包下使用。入口方法`ComponentScanAnnotationParser#parse`。刚方法内新实例化了`ClassPathBeanDefinitionScanner`。并根据注解`@ComponentScan`的设置值为 scan 设置参数。
如设置

1. useDefaultFilters ： 是否使用默认过滤器
2. nameGenerator ： 指定的命名生成器
3. scopedProxy: 代理范围
4. scopeResolver ： 代理解析器
5. resourcePattern ： 资源模式
6. includeFilters：包含的过滤器
7. excludeFilters：排除的过滤器
8. lazyInit： 懒加载
9. basePackages ： 基础扫描包路径
10. basePackageClasses： 指令类，将其所属的包设作为 basePakcage

设置好了这些参数后，就可以调用扫描器的`scanner.doScan`方法扫描指定的资源了。而入口方法`ComponentScanAnnotationParser#parse`是在`ConfigurationClassParser#doProcessConfigurationClass`中调用的。即先判断并解析了`@Configuration`注解后，才会判断该注解标记的类上是否有`@ComponentScan`注解

**basePackages 和 basePackageClasses 解析的包路径会合并去重。两个可以都设置也可以只设置一个。**

### org.springframework.context.annotation.ComponentScanBeanDefinitionParser

`ComponentScanBeanDefinitionParser`和`ComponentScanAnnotationParser`允许设置的参数基本相同。需要在方法`configureScanner`中创建 Scanner实例并为其设置好配置的属性值。

和注解方式的区别是：`ComponentScanBeanDefinitionParser`不可以设置`basePackageClasses`


在 Spring 初始化解析 xml 配置标签的时候，方法`DefaultBeanDefinitionDocumentReader#parseBeanDefinitions`中的`delegate.parseCustomElement`会调用`ComponentScanAnnotationParser.parse`来完成`<context:component-scan/>`的解析。

此处的`Parser`的查找需要命名空间处理器`NamespaceHandler`的协助，Spring会通过解析命名空间和`META-INF/spring.handlers`中配置的关联关系找到`ContextNamespaceHandler`,然后通过解析xml配置中的解析器名称，就可以找到已经初始化好的解析器。这样就可以调用解析器的解析方法了。

