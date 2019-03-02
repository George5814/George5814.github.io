---
layout: post
title: java排错
category: 技术
tags: Java
keywords: 
description: 
---

{:toc}


### 1.错误提示：

```java
	org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.reflection.ReflectionException: Error instantiating class com.skd.domain.User with invalid types () or values (). Cause: java.lang.NoSuchMethodException: com.skd.domain.User.<init>()
		at org.mybatis.spring.MyBatisExceptionTranslator.translateExceptionIfPossible(MyBatisExceptionTranslator.java:75)
		at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:371)
		at $Proxy14.selectList(Unknown Source)
		at org.mybatis.spring.SqlSessionTemplate.selectList(SqlSessionTemplate.java:198)
		at org.apache.ibatis.binding.MapperMethod.executeForMany(MapperMethod.java:119)
		at org.apache.ibatis.binding.MapperMethod.execute(MapperMethod.java:63)
		at org.apache.ibatis.binding.MapperProxy.invoke(MapperProxy.java:52)
		at $Proxy18.getAllUserInfo(Unknown Source)
		at com.skd.service.impl.UserImpl.getAllUserInfo(UserImpl.java:20)
		at com.skd.test.TestService.testGetAllUser(TestService.java:74)
		at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
		at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
		at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
```
		
#### 问题原因和解决方法：

	因为com.skd.domain.User缺少了无参构造方法，添加无参构造方法即可解决问题
	
### 2. 错误：dubbo javassist.ClassPath

在dubbo启动的过程中报错误：`java.lang.ClassNotFoundException: javassist.ClassPath`，
调试发现代码：`Class<?> clazz = Class.forName(line, true, classLoader)`执行加载类`com.alibaba.dubbo.common.compiler.support.JavassistCompiler`时报：`java.lang.NoClassDefFoundError: javassist/ClassPath`，这个错误最后被加工最终变成上边的错误。可是查看项目下发现依赖的包javassist-3.18.1-GA.jar命名存在，打开这些jar包，其中的javassist.ClassPath的确存在啊，于是我换了个版本javassist-3.18.2-GA.jar程序跑起来了。那么到底是怎么回事呢？。
其实就是因为JavassistCompile使用了类`javassist.ClassPath`。经过一点点的比较，最后发现 javassist-3.18.1-GA.jar这个包是有问题的，因为其MAINFEST.FM文件是打不开的，解压以后也报各种Class文件格式错误，看来应该是这个问题了，时间紧张也不打算深入验证了，总之当大家出现这种问题时，也算是一种可能问题吧，希望能为大家解决问题提供线索

**解决办法：**

maven项目中在引入dubbo包的同时还要引入javassist即：依赖包

```xml
	<properties>
	<dubbo.version>2.8.5</dubbo.version>
	<javassist_version>3.15.0-GA</javassist_version>
	</properties>

	<dependencies>
	<dependency>
        <groupId>org.javassist</groupId>
        <artifactId>javassist</artifactId>
        <version>${javassist_version}</version>
    </dependency>
    
	<dependency>
		<groupId>com.alibaba</groupId>
		<artifactId>fastjson</artifactId>
		<version>${fastjson.version}</version>
	</dependency>
	</dependencies>
```


### 3. 找不到Maps.newConcurrentMap()方法

**错误提示：**

```java
java.lang.NoSuchMethodError: com.google.common.collect.Maps.newConcurrentMap()Ljava/util/concurrent/ConcurrentMap;
	at org.apache.curator.framework.listen.ListenerContainer.<init>(ListenerContainer.java:36)
	at org.apache.curator.framework.imps.CuratorFrameworkImpl.<init>(CuratorFrameworkImpl.java:113)
	at org.apache.curator.framework.CuratorFrameworkFactory$Builder.build(CuratorFrameworkFactory.java:145)
	at com.dangdang.ddframe.reg.zookeeper.ZookeeperRegistryCenter.init(ZookeeperRegistryCenter.java:103)
	at com.yonyou.worktime.service.scheduler.impl.SchedulerServiceImpl.scheduleJob(SchedulerServiceImpl.java:121)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:302)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(ReflectiveMethodInvocation.java:190)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157)
	at com.alibaba.druid.support.spring.stat.DruidStatInterceptor.invoke(DruidStatInterceptor.java:72)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:208)
	at com.sun.proxy.$Proxy69.scheduleJob(Unknown Source)
	at com.yonyou.worktime.web.group.TestSchedulerController.startScheduler(TestSchedulerController.java:39)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.springframework.web.bind.annotation.support.HandlerMethodInvoker.invokeHandlerMethod(HandlerMethodInvoker.java:178)
	at org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter.invokeHandlerMethod(AnnotationMethodHandlerAdapter.java:444)
	at org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter.handle(AnnotationMethodHandlerAdapter.java:432)
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:959)
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:893)
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:968)
	at org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:870)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:648)
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:844)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:729)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:292)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:207)
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:52)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:240)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:207)
	at com.alibaba.druid.support.http.WebStatFilter.doFilter(WebStatFilter.java:123)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:240)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:207)
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:121)
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:240)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:207)
	at com.yonyou.worktime.web.organization.LoginSecurityFilter.doFilter(LoginSecurityFilter.java:66)
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:346)
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:262)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:240)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:207)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:212)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:106)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:502)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:141)
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:79)
	at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:616)
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:88)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:522)
	at org.apache.coyote.http11.AbstractHttp11Processor.process(AbstractHttp11Processor.java:1095)
	at org.apache.coyote.AbstractProtocol$AbstractConnectionHandler.process(AbstractProtocol.java:672)
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1500)
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.run(NioEndpoint.java:1456)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
	at java.lang.Thread.run(Thread.java:745)

```

**错误原因：**
	
- 1.没有引入包`guava-xxx.jar`；

- 2.引入的包重复并查找到以前的包`google-collect-xxx.jar`中，导致找不到该类方法。
	
	如同时引入了`guava-19.0.jar`和`google-collect-snapshot-20080530.jar`就会导致该问题。

![guava-19.0.jar包](//raw.githubusercontent.com/George5814/blog-pic/master/image/error/google-error-1.png)

![google-collect-snapshot-20080530.jar包](//raw.githubusercontent.com/George5814/blog-pic/master/image/error/google-error-2.png)
	
**解决办法：**

	针对错误1，需要引入`guava-xxx.jar`；
	   
	   
	针对错误2，需要移除项目中的`google-collect-snapshot-20080530.jar`。
	

### 4.错误：@within pointcut expression is only supported at Java 5 compliance level or above

**错误提示**

```java
java.lang.IllegalArgumentException: error the @within pointcut expression is only supported at Java 5 compliance level or above
	at org.aspectj.weaver.tools.PointcutParser.parsePointcutExpression(PointcutParser.java:315)
	at org.springframework.aop.aspectj.AspectJExpressionPointcut.buildPointcutExpression(AspectJExpressionPointcut.java:207)
	at org.springframework.aop.aspectj.AspectJExpressionPointcut.checkReadyToMatch(AspectJExpressionPointcut.java:193)
	at org.springframework.aop.aspectj.AspectJExpressionPointcut.getClassFilter(AspectJExpressionPointcut.java:170)
	at org.springframework.aop.support.AopUtils.canApply(AopUtils.java:220)
	at org.springframework.aop.support.AopUtils.canApply(AopUtils.java:279)
	at org.springframework.aop.support.AopUtils.findAdvisorsThatCanApply(AopUtils.java:311)
	at org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator.findAdvisorsThatCanApply(AbstractAdvisorAutoProxyCreator.java:118)
	at org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator.findEligibleAdvisors(AbstractAdvisorAutoProxyCreator.java:88)
	at org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator.getAdvicesAndAdvisorsForBean(AbstractAdvisorAutoProxyCreator.java:69)
	at org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.wrapIfNecessary(AbstractAutoProxyCreator.java:347)
	at org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.postProcessAfterInitialization(AbstractAutoProxyCreator.java:299)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsAfterInitialization(AbstractAutowireCapableBeanFactory.java:422)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1583)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:545)
	... 20 more

```

**错误原因：**

aspectjweaver包的版本过低。

**解决办法**

在maven项目中更换依赖版本如下

```xml
<properties>
	<aspectjweaver.version>1.8.9</aspectjweaver.version>
</properties>
<dependencies>
<dependency>
	<groupId>org.aspectj</groupId>
	<artifactId>aspectjweaver</artifactId>
	<version>${aspectjweaver.version}</version>
</dependency> 
</dependencies>
```




### 5.错误：Invalid classpath publish/export dependency /brief-comm. Project entries not supported

**错误提示**

**错误原因：**

暂时不知

**解决办法**

在eclipse -> {project} -> 属性 -> Deployment Assembly ->右侧将引入的项目删除并重新添加。

**在eclipse中将引入其他模块的项目部署到tomcat中，需要在Deployment Assembly处添加引入的项目。**


