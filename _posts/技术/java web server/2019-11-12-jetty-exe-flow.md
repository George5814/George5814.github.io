---
layout:  post
title: Jetty 执行流程
category: 技术
tags: Jetty
keywords: 
description:  Jetty 执行流程
published: false
---

## 配置内容

jetty 的配置信息


```xml
<?xml version="1.0"?>
<!DOCTYPE Configure PUBLIC "-//Mort Bay Consulting//DTD Configure//EN" "http://jetty.eclipse.org/configure.dtd">
<Configure id="Server" class="org.eclipse.jetty.server.Server">

    <Set class="org.eclipse.jetty.util.resource.Resource" name="defaultUseCaches">false</Set>
    <New class="java.io.File">
        <Arg>
            <SystemProperty name="java.io.tmpdir"/>
        </Arg>
        <Call name="toURI">
            <Call id="url" name="toURL">
                <Call name="openConnection">
                    <Set name="defaultUseCaches">false</Set>
                </Call>
            </Call>
        </Call>
    </New>

    <New id="ServerLog" class="java.io.PrintStream">
        <Arg>
            <New class="org.eclipse.jetty.util.RolloverFileOutputStream">
                <Arg><SystemProperty name="jetty.logs" default="./logs"/>/<SystemProperty name="jetty.appkey" default="jetty"/>.log.yyyy_mm_dd</Arg>
                <Arg type="boolean">true</Arg>
                <Arg type="int">10</Arg>
                <Arg><Call class="java.util.TimeZone" name="getTimeZone"><Arg>GMT+8</Arg></Call></Arg>
                <Arg type="string">yyyy-MM-dd</Arg>
                <Arg type="string"></Arg>
                <Get id="ServerLogName" name="datedFilename"/>
            </New>
        </Arg>
    </New>
    <Call class="org.eclipse.jetty.util.log.Log" name="info"><Arg>Redirecting stderr/stdout to <Ref id="ServerLogName"/></Arg></Call>
    <Call class="java.lang.System" name="setErr"><Arg><Ref id="ServerLog"/></Arg></Call>
    <Call class="java.lang.System" name="setOut"><Arg><Ref id="ServerLog"/></Arg></Call>

    <Set name="ThreadPool">
        <New class="org.eclipse.jetty.util.thread.QueuedThreadPool">
            <Arg>
                <New class="java.util.concurrent.ArrayBlockingQueue">
                    <Arg type="int">4000</Arg>
                </New>
            </Arg>
            <Set name="minThreads">20</Set>
            <Set name="maxThreads">200</Set>
        </New>
    </Set>

    <Call name="addConnector">
        <Arg>
            <New class="org.eclipse.jetty.server.nio.SelectChannelConnector">
                <Set name="host"><SystemProperty name="jetty.host"/></Set>
                <Set name="port"><SystemProperty name="jetty.port" default="8080"/></Set>
                <Set name="maxIdleTime">5000</Set>
                <Set name="Acceptors">4</Set>
                <Set name="statsOn">false</Set>
                <Set name="requestHeaderSize">12288</Set>
                <!-- Set name="confidentialPort">8063</Set -->
                <Set name="lowResourcesConnections">5000</Set>
                <Set name="lowResourcesMaxIdleTime">2000</Set>
            </New>
        </Arg>
    </Call>

    <Array id="plusConfig" type="java.lang.String">
        <!-- <Item>org.eclipse.jetty.webapp.WebInfConfiguration</Item> -->
        <Item>org.eclipse.jetty.webapp.WebXmlConfiguration</Item>
        <!-- <Item>org.eclipse.jetty.webapp.MetaInfConfiguration</Item> -->
        <Item>org.eclipse.jetty.webapp.FragmentConfiguration</Item>
        <Item>org.eclipse.jetty.plus.webapp.EnvConfiguration</Item>
        <Item>org.eclipse.jetty.plus.webapp.PlusConfiguration</Item>
        <!-- <Item>org.eclipse.jetty.annotations.AnnotationConfiguration</Item> -->
        <Item>org.eclipse.jetty.webapp.JettyWebXmlConfiguration</Item>
        <Item>org.eclipse.jetty.webapp.TagLibConfiguration</Item>
        <Item>org.eclipse.jetty.webapp.WebInfConfiguration</Item>
        <Item>org.eclipse.jetty.webapp.MetaInfConfiguration</Item>
        <Item>org.eclipse.jetty.annotations.AnnotationConfiguration</Item>
    </Array>

    <Set name="handler">
        <New id="Handlers" class="org.eclipse.jetty.server.handler.HandlerCollection">
            <Set name="handlers">
                <Array type="org.eclipse.jetty.server.Handler">
                    <Item>
                        <New class="org.eclipse.jetty.webapp.WebAppContext">
                            <Set name="tempDirectory"><SystemProperty name="java.io.tmpdir" default="/tmp"/></Set>
                            <Set name="resourceBase"><SystemProperty name="jetty.webroot"/></Set>
                            <Set name="contextPath"><SystemProperty name="jetty.context"/></Set>
                            <Set name="parentLoaderPriority">true</Set>
                            <!-- <Set name="defaultsDescriptor"><SystemProperty name="jetty.home"/>/etc/webdefault.xml</Set> -->
                            <Set name="configurationClasses"><Ref id="plusConfig"/></Set>
                        </New>
                    </Item>
                    <Item>
                        <New class="org.eclipse.jetty.server.handler.RequestLogHandler">
                            <Set name="requestLog">
                                <New id="RequestLogImpl" class="org.eclipse.jetty.server.NCSARequestLog">
                                    <Set name="filename"><SystemProperty name="jetty.logs" default="./logs"/>/<SystemProperty name="jetty.appkey" default="jetty"/>.request.log.yyyy_mm_dd</Set>
                                    <Set name="filenameDateFormat">yyyy-MM-dd</Set>
                                    <Set name="retainDays">90</Set>
                                    <Set name="append">true</Set>
                                    <Set name="extended">false</Set>
                                    <Set name="logCookies">false</Set>
                                    <Set name="LogTimeZone">GMT+8</Set>
                                    <Set name="logLatency">true</Set>
                                </New>
                            </Set>
                        </New>
                    </Item>
                </Array>
            </Set>
        </New>
    </Set>

    <Set name="stopAtShutdown">true</Set>
    <Set name="sendServerVersion">true</Set>
    <Set name="sendDateHeader">true</Set>
    <Set name="gracefulShutdown">1000</Set>
    <Set name="dumpAfterStart">false</Set>
    <Set name="dumpBeforeStop">false</Set>

</Configure>
```

## 执行流程

1. `AbstractLifeCycle#start` => 
2. `org.eclipse.jetty.server.Server#doStart` =>
2. `org.eclipse.jetty.server.handler.HandlerWrapper#doStart` =>
2. `HandlerCollection#start` =>
2. `WebAppContext#start` =>
3. `WebXmlConfiguration.preConfigure找到项目中的 WEB-INF/web.xml` =>
4. `ContextHandler.doStart` =>
5. 通过 SPI 方式找到了`javax.servlet.ServletContainerInitializer`接口的 Spring 实现类`org.springframework.web.SpringServletContainerInitializer` =>
6. `org.eclipse.jetty.webapp.IterativeDescriptorProcessor#process解析 web.xml 文件内容。StandardDescriptorProcessor为标准的处理器` =>
7. `org.eclipse.jetty.server.Server#doStart` =>
8. `org.eclipse.jetty.server.Server#doStart` =>
9.  `org.eclipse.jetty.server.Server#doStart` =>
10. `org.eclipse.jetty.server.Server#doStart` =>
11. `org.eclipse.jetty.server.Server#doStart` =>
12. `org.eclipse.jetty.server.Server#doStart` =>
13. `org.eclipse.jetty.server.Server#doStart` =>
14. `org.eclipse.jetty.server.Server#doStart` =>
15. 
