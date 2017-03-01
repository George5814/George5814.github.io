---
layout:  post
title: 记一次Spring Scheduler莫名不执行的一次堆栈分析
category: 技术
tags: Java
keywords: 
description: 通过工具分析Scheduler线程的堆栈情形

---

{:toc}

### 1.打印指定进行的JVM的堆栈信息

```bash

#查看指定应用所在tomcat的进程id
ps -ef | grep app_name
#查看指定进程所含线程
ps -Lf pid > thread.log
#查看进程下线程的资源占用情况
top -p pid -H

#将指定进程的堆栈情况重定向到指定文件
jstack pid > stack.log
```

### 分析线程

因遇到了Spring的Scheduler不再调度执行的问题。
分析堆栈日志找到对应的线程的堆栈信息

```java
"pool-1-thread-1" #22 prio=5 os_prio=0 tid=0x00007feef0168000 nid=0x7c54 runnable [0x00007fef20ed6000]
   java.lang.Thread.State: RUNNABLE
        at java.net.SocketInputStream.socketRead0(Native Method) 
        at java.net.SocketInputStream.socketRead(SocketInputStream.java:116)
        at java.net.SocketInputStream.read(SocketInputStream.java:170)
        at java.net.SocketInputStream.read(SocketInputStream.java:141)
        at sun.security.ssl.InputRecord.readFully(InputRecord.java:465)
        at sun.security.ssl.InputRecord.read(InputRecord.java:503)
        at sun.security.ssl.SSLSocketImpl.readRecord(SSLSocketImpl.java:973)
        - locked <0x00000000aaf72eb0> (a java.lang.Object)
        at sun.security.ssl.SSLSocketImpl.performInitialHandshake(SSLSocketImpl.java:1375)
        - locked <0x00000000aaf72fd8> (a java.lang.Object)
        at sun.security.ssl.SSLSocketImpl.startHandshake(SSLSocketImpl.java:1403)
        at sun.security.ssl.SSLSocketImpl.startHandshake(SSLSocketImpl.java:1387)
        at org.apache.http.conn.ssl.SSLConnectionSocketFactory.createLayeredSocket(SSLConnectionSocketFactory.java:394)
        at org.apache.http.conn.ssl.SSLConnectionSocketFactory.connectSocket(SSLConnectionSocketFactory.java:353)
        at org.apache.http.impl.conn.DefaultHttpClientConnectionOperator.connect(DefaultHttpClientConnectionOperator.java:141)
        at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.connect(PoolingHttpClientConnectionManager.java:353)
        at org.apache.http.impl.execchain.MainClientExec.establishRoute(MainClientExec.java:380)
        at org.apache.http.impl.execchain.MainClientExec.execute(MainClientExec.java:236)
        at org.apache.http.impl.execchain.ProtocolExec.execute(ProtocolExec.java:184)
        at org.apache.http.impl.execchain.RetryExec.execute(RetryExec.java:88)
        at org.apache.http.impl.execchain.RedirectExec.execute(RedirectExec.java:110)
              at org.apache.http.impl.client.InternalHttpClient.doExecute(InternalHttpClient.java:184)
        at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:82)
        at org.apache.http.impl.client.CloseableHttpClient.execute(CloseableHttpClient.java:107)
        at com.yonyou.esn.palmyy.utils.HttpUtil.put(HttpUtil.java:152)
        at com.yonyou.esn.palmyy.service.impl.EsnDataServiceImpl.modifyEsnCorp(EsnDataServiceImpl.java:98)
        at com.yonyou.esn.palmyy.service.impl.EsnDataServiceImpl$$FastClassBySpringCGLIB$$7518e96c.invoke(<generated>)
        at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204)
        at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:720)
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157)
        at org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint.proceed(MethodInvocationProceedingJoinPoint.java:85)
        at cn.followtry.validation.base.logger.LoggingAspectBase.loggingAround(LoggingAspectBase.java:55)
        at cn.followtry.validation.base.logger.LoggingAspectBase.serviceLogAround(LoggingAspectBase.java:26)
        at cn.followtry.validation.base.logger.LoggingAspect.onServiceInvoked(LoggingAspect.java:27)
        at sun.reflect.GeneratedMethodAccessor39.invoke(Unknown Source)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:497)
        at org.springframework.aop.aspectj.AbstractAspectJAdvice.invokeAdviceMethodWithGivenArgs(AbstractAspectJAdvice.java:629)
        at org.springframework.aop.aspectj.AbstractAspectJAdvice.invokeAdviceMethod(AbstractAspectJAdvice.java:618)
        at org.springframework.aop.aspectj.AspectJAroundAdvice.invoke(AspectJAroundAdvice.java:70)
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:168)
        at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:92)
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
        at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:655)
        at com.yonyou.esn.palmyy.service.impl.EsnDataServiceImpl$$EnhancerBySpringCGLIB$$af0274cf.modifyEsnCorp(<generated>)
        at com.yonyou.esn.palmyy.service.impl.SyncDataServiceImpl.ModifyCorpInfo(SyncDataServiceImpl.java:107)
        at com.yonyou.esn.palmyy.service.impl.SyncDataServiceImpl.syncCorpData(SyncDataServiceImpl.java:397)
        at com.yonyou.esn.palmyy.service.impl.SyncDataServiceImpl.doSyncDept2Esn(SyncDataServiceImpl.java:363)
        at com.yonyou.esn.palmyy.service.impl.SyncDataServiceImpl.syncDeptInfo2Esn(SyncDataServiceImpl.java:322)
        at com.yonyou.esn.palmyy.service.impl.SyncDataServiceImpl.syncDept2Esn(SyncDataServiceImpl.java:298)
        at com.yonyou.esn.palmyy.service.impl.SyncDataServiceImpl.sync2Esn(SyncDataServiceImpl.java:770)
        at com.yonyou.esn.palmyy.service.impl.SyncDataServiceImpl.syncAll(SyncDataServiceImpl.java:764)
        at com.yonyou.esn.palmyy.service.impl.SyncDataServiceImpl$$FastClassBySpringCGLIB$$d88e65cf.invoke(<generated>)
        at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204)
        at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:720)
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157)
        at org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint.proceed(MethodInvocationProceedingJoinPoint.java:85)
        at cn.followtry.validation.base.logger.LoggingAspectBase.loggingAround(LoggingAspectBase.java:55)
        at cn.followtry.validation.base.logger.LoggingAspectBase.serviceLogAround(LoggingAspectBase.java:26)
        at cn.followtry.validation.base.logger.LoggingAspect.onServiceInvoked(LoggingAspect.java:27)
        at sun.reflect.GeneratedMethodAccessor39.invoke(Unknown Source)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:497)
        at org.springframework.aop.aspectj.AbstractAspectJAdvice.invokeAdviceMethodWithGivenArgs(AbstractAspectJAdvice.java:629)
        at org.springframework.aop.aspectj.AbstractAspectJAdvice.invokeAdviceMethod(AbstractAspectJAdvice.java:618)
        at org.springframework.aop.aspectj.AspectJAroundAdvice.invoke(AspectJAroundAdvice.java:70)
        at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:168)
        at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:92)
 		at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
        at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:655)
        at com.yonyou.esn.palmyy.service.impl.SyncDataServiceImpl$$EnhancerBySpringCGLIB$$6a4e7fd2.syncAll(<generated>)
        at sun.reflect.GeneratedMethodAccessor422.invoke(Unknown Source)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:497)
        at org.springframework.scheduling.support.ScheduledMethodRunnable.run(ScheduledMethodRunnable.java:65)
        at org.springframework.scheduling.support.DelegatingErrorHandlingRunnable.run(DelegatingErrorHandlingRunnable.java:54)
        at org.springframework.scheduling.concurrent.ReschedulingRunnable.run(ReschedulingRunnable.java:81)
        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
        at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$201(ScheduledThreadPoolExecutor.java:180)
        at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:293)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
        at java.lang.Thread.run(Thread.java:745)
        
```

在dump中，线程一般存在如下几种状态：

1. NEW,至今尚未启动的线程状态

1. RUNNABLE，线程处于执行中

1. BLOCKED，线程被阻塞

1. WAITING，线程正在等待

1. TIMED_WAITING， 具有指定等待时间的某一等待线程的线程状态。某一线程因为调用以下带有指定正等待时间的方法之一而处于定时等待状态。

1. TERMINATED,已经终止的线程


其中

- "nid=0x7c54"是指线程的id，0x7c54是线程id的16进制显示。

- java.lang.Thread.State: RUNNABLE 是指该线程还在运行中。

通过对堆栈的分析可以得出，该线程阻塞在了socket网络连接上

```
	java.net.SocketInputStream.socketRead0(Native Method) 
  	at sun.security.ssl.SSLSocketImpl.readRecord(SSLSocketImpl.java:973)
   	 //说明线程对地址为0x00000000aaf72eb0对象进行了加锁；
   	 - locked <0x00000000aaf72eb0> (a java.lang.Object)
   	at sun.security.ssl.SSLSocketImpl.performInitialHandshake(SSLSocketImpl.java:1375)
		- locked <0x00000000aaf72fd8> (a java.lang.Object)
```

**在stackoverflow上有相似的问题:<http://stackoverflow.com/questions/25968769/got-stuck-at-java-net-socketinputstream-socketread0native-method>**

**在CSDN上有HttpClient超时阻塞的分析:<http://blog.csdn.net/hengyunabc/article/details/22716911>**

> Apache HttpClient应该是最常用的Java http组件了。
	这货有个坑爹的地方，对于Apache HttpClient，如果对方不回应，或者网络原因不返回了，那么HttpClient会一直阻塞。
	这种情况在公网可能比较容易碰到。
