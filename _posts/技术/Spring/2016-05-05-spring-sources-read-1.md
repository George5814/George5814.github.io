---
layout: post
title: Spring源码阅读笔记（1）
category: 技术
tags:  Spring
keywords: 
description: IOC容器的初始化过程
---

{:toc}

## 1.IOC容器初始化过程

- Resource定位

**以FileSystemXmlApplicationContext的资源定义调用为例**

[ FileSystemXmlApplicationContext(configLocations,refresh,parent)](){:title="org.springframework.context.support.FileSystemXmlApplicationContext.FileSystemXmlApplicationContext(String[], boolean, ApplicationContext)"} =>   
  [refresh()](){:title="org.springframework.context.support.AbstractApplicationContext.refresh()"} =>  
  [obtainFreshBeanFactory()](){:title="org.springframework.context.support.AbstractApplicationContext.obtainFreshBeanFactory()"} =>  
  [refreshBeanFactory()](){:title="org.springframework.context.support.AbstractRefreshableApplicationContext.refreshBeanFactory()"} =>  
  [loadBeanDefinitions(beanFactory)](){:title="org.springframework.context.support.AbstractXmlApplicationContext.loadBeanDefinitions(DefaultListableBeanFactory)"} =>  
  [loadBeanDefinitions(beanDefinitionReader)](){:title="org.springframework.context.support.AbstractXmlApplicationContext.loadBeanDefinitions(XmlBeanDefinitionReader)"} =>  
  [reader.loadBeanDefinitions(configLocations)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String...)"} =>  
  [loadBeanDefinitions(locations)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String...)"} =>  
  [loadBeanDefinitions(location)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String)"} =>   
  [loadBeanDefinitions(location,null)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String, Set<Resource>)"} =>   
  [resourceLoader.getResource(location)](){:title="org.springframework.context.support.GenericApplicationContext.getResource(String)"} =>  
  [this.resourceLoader.getResource(location)](){:title="org.springframework.core.io.DefaultResourceLoader.getResource(String)"} =>   
  [getResourceByPath(location)](){:title="org.springframework.context.support.FileSystemXmlApplicationContext.getResourceByPath(String)"} =>  
  [FileSystemXmlApplicationContext](){:title="org.springframework.context.support.FileSystemXmlApplicationContext"} 
  
  **结束**

 
- BeanDefinition载入

[ FileSystemXmlApplicationContext(configLocations,refresh,parent)](){:title="org.springframework.context.support.FileSystemXmlApplicationContext.FileSystemXmlApplicationContext(String[], boolean, ApplicationContext)"} =>   
  [refresh()](){:title="org.springframework.context.support.AbstractApplicationContext.refresh()"} =>  
  [obtainFreshBeanFactory()](){:title="org.springframework.context.support.AbstractApplicationContext.obtainFreshBeanFactory()"} =>  
  [refreshBeanFactory()](){:title="org.springframework.context.support.AbstractRefreshableApplicationContext.refreshBeanFactory()"} => 
  [loadBeanDefinitions(beanFactory)](){:title="org.springframework.context.support.AbstractXmlApplicationContext.loadBeanDefinitions(DefaultListableBeanFactory)"} =>  
  [loadBeanDefinitions(beanDefinitionReader)](){:title="org.springframework.context.support.AbstractXmlApplicationContext.loadBeanDefinitions(XmlBeanDefinitionReader)"} =>  
  [reader.loadBeanDefinitions(configLocations)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String...)"} =>  
  [loadBeanDefinitions(locations)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String...)"} =>  
  [loadBeanDefinitions(location)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String)"} =>   
  [loadBeanDefinitions(location,null)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(String, Set<Resource>)"} =>  
  [loadBeanDefinitions(resources)](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader.loadBeanDefinitions(Resource...)"} =>  
  [loadBeanDefinitions(resource)](){:title="org.springframework.beans.factory.xml.XmlBeanDefinitionReader.loadBeanDefinitions(Resource)"} =>  
  [loadBeanDefinitions(new EncodedResource(resource))](){:title="org.springframework.beans.factory.xml.XmlBeanDefinitionReader.loadBeanDefinitions(EncodedResource)"} =>  
  [doLoadBeanDefinitions(inputSource, encodedResource.getResource())](){:title="org.springframework.beans.factory.xml.XmlBeanDefinitionReader.doLoadBeanDefinitions(InputSource, Resource)"} =>  
  [registerBeanDefinitions(doc, resource)](){:title="org.springframework.beans.factory.xml.XmlBeanDefinitionReader.registerBeanDefinitions(Document, Resource)"} =>  
  [documentReader.registerBeanDefinitions(doc, createReaderContext(resource))](){:title="org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader.registerBeanDefinitions(Document, XmlReaderContext)"} =>  
  [doRegisterBeanDefinitions(root)](){:title="org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader.doRegisterBeanDefinitions(Element)"} =>  
  [parseBeanDefinitions(root, this.delegate)](){:title="org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader.parseBeanDefinitions(Element, BeanDefinitionParserDelegate)"} =>  
  [BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry())](){:title="org.springframework.beans.factory.support.BeanDefinitionReaderUtils.registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)"} =>  

**载入结束**
	
	
	
	
	
	
- 向IOC容器注册BeanDefinition的过程
	
紧接着BeanDefinition载入的最后一步

  [BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry())](){:title="org.springframework.beans.factory.support.BeanDefinitionReaderUtils.registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)"} =>
  [registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition())](){:title="org.springframework.beans.factory.support.DefaultListableBeanFactory.registerBeanDefinition(String, BeanDefinition)"}
  
bean在经过一系列检验成功后，注册进[beanDefinitionMap](){:title="this.beanDefinitionMap.put(beanName, beanDefinition);"}
  


**说明：**

- 初始化过程中，通过refresh()启动整个调用

- 使用的IOC容器为[DefaultListableBeanFactory](){:title="org.springframework.beans.factory.support.DefaultListableBeanFactory"}

- 具体的资源载入是在[XmlBeanDefinitionReader](){:title="org.springframework.beans.factory.xml.XmlBeanDefinitionReader"} 读入BeanDefinition时完成，在其基类[AbstractBeanDefinitionReader](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader"}中可看到。

- 载入过程的启动，可以在[AbstractBeanDefinitionReader](){:title="org.springframework.beans.factory.support.AbstractBeanDefinitionReader"}中的`loadBeanDefinitions(String... locations)`中看到

- 解析BeanDefinition，使用了[BeanDefinitionParserDelegate](){:title="org.springframework.beans.factory.xml.BeanDefinitionParserDelegate"}
 
- 解析成功的BeanDefinition，最终存入[beanDefinitionMap](){:title="org.springframework.beans.factory.support.DefaultListableBeanFactory.beanDefinitionMap"}中，类型为`java.util.concurrent.ConcurrentHashMap`。



## 2.refresh()步骤

**待添加refresh每一步详细笔记**

### 2.1.**prepareRefresh**

准备context refresh的环境
 	
- 设置开始时间为当前时间；
 
- 设置active的标识为true；
 
- 初始化属性源（加载属性文件，并将 placeholders 替换为配置文件中的属性源）-调用子类方法

```JAVA

initPropertySources();
getEnvironment().validateRequiredProperties();

```
 
 
### 2.2.**obtainFreshBeanFactory**

通知子类刷新内部beanFactory并返回刷新后的beanFactory
 	
- 刷新beanFactory

```java
refreshBeanFactory();

```

	bean存在，就销毁bean；
	
	bean不存在，就创建bean(org.springframework.beans.factory.support.DefaultListableBeanFactory)，并加载BeanDefinition

- 获取刷新后的beanFactory（同步方法synchronized）

```java
ConfigurableListableBeanFactory beanFactory = getBeanFactory();
```

### 2.3.**prepareBeanFactory** 

准备context使用的Beanfactory,即上一步成功刷新并获取的Beanfactory

- 通知内部Beanfactory使用context的类加载器等

```java

beanFactory.setBeanClassLoader(getClassLoader());
beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));
```

- 配置Beanfactory的context回调

```java
beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
```

- MessageSource注册为bean；普通工厂中Beanfactory接口不注册为解析类型

```java
beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
beanFactory.registerResolvableDependency(ResourceLoader.class, this);
beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
beanFactory.registerResolvableDependency(ApplicationContext.class, this);
```

- 检测LoadTimeWeaver的beanName是否存在

```java
if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
	beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
	// Set a temporary ClassLoader for type matching.
	beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
}
```

- 注册默认的环境bean

```java
if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
	beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
}
if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
	beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
}
if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
	beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
}
```
 
### 2.4.**postProcessBeanFactory**：对上下文子类中的beanfactory进行后置处理

修改已经标准初始化完成的ApplicationContext内部Beanfactory。此时所有的BeanDefinition将会被加载，但没有bean将被实例化。
允许注册指定的`BeanPostProcessors`等

- 寄存器请求会话范围

```java
beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext, this.servletConfig));
beanFactory.ignoreDependencyInterface(ServletContextAware.class);
beanFactory.ignoreDependencyInterface(ServletConfigAware.class);

WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, this.servletContext);
WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, this.servletContext, this.servletConfig);
```

	注册web application Context的范围
	
	注册环境bean ("servletContext", "contextParameters", "contextAttributes")
	


 
### 2.5.**invokeBeanFactoryPostProcessors**

依据指定的顺序，实例化并调用已经注册为beans的BeanFactoryPostProcessor。必须在单例实例化之前调用

### 2.6.**registerBeanPostProcessors**

依据指定的顺序，实例化并调用已经注册为beans的BeanFactoryPostProcessor。必须在任何application的beans实例化之前调用
 
### 2.7.**initMessageSource**
 
初始化消息源
 
### 2.8.**initApplicationEventMulticaster**
 
初始化context的事件多路广播，注册为单例bean
 
### 2.9.**onRefresh**

初始化主题能力

```java
this.themeSource = UiApplicationContextUtils.initThemeSource(this);
```
 
### 2.10.**registerListeners**
 
将实现了ApplicationListener接口的bean注册为监听器

- 首先注册静态指定的监听器

- 发布早期应用事件现在我们终于有了一个多路广播
 
### 2.11.**finishBeanFactoryInitialization**

- 实例化context的BeanFactory

- 实例化所有的非懒加载的单例bean
 
### 2.12.**finishRefresh**
 
 
- 初始化context的生命周期处理器

- 首先刷新context的生命周期处理器

- 发送最终的刷新事件

- 如果已经激活，将context注册进LiveBeansView

### 2.13.**resetCommonCaches**

重置Spring公共缓存

```java
ReflectionUtils.clearCache();
ResolvableType.clearCache();
CachedIntrospectionResults.clearClassLoader(getClassLoader());
```

### 总结：
	
	初始化过程主要完成了在IOC容器中建立BeanDefinition的数据映射，并没有IOC容器对Bean的依赖关系进行注入。
	

**依赖注入一般发生在应用第一次通过getBean()向容器索取Bean的时候**

**下一篇的[Spring源码阅读笔记（2）](/2016/05/11/spring-sources-read-2.html){:title="IOC容器的依赖注入"  :target="_blank"}中将会详细讲解**
