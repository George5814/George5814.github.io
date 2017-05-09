---
layout: post
title: log4j2 按天生成日志文件
category: 技术
tags: Logger
keywords: 
description:  
---

{:toc}

## 1. 目的

以天为时间区间，分隔每天的日志文件。如`info.log.2017-05-08`,`info.log.2017-05-09`,`info.log.2017-05-10`


## 2. 配置

```xml
<RollingFile name="infoLog" fileName="${sys:catalina.home}/logs/esn-palmyy-plugin/info.log" append="true"
					 filePattern="${sys:catalina.home}/logs/esn-palmyy-plugin/info.log.%d{yyyy-MM-dd}">
  <PatternLayout
      pattern="[--%-5level--]	%d{yyyy.MM.dd	HH:mm:ss.SSS}	%class{36}	%L	%M	-	%msg%xEx%n"/>
  <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
  <Policies>
    <TimeBasedTriggeringPolicy  interval="1"/>
  </Policies>
</RollingFile>
```

Log4j2日志配置`RollingFile`标签时必须有`Policies`标签，这是log4j2限定的。

Rolling的意思是当满足一定条件后，就重命名原日志文件用于备份，并重新生成一个新的日志文件。例如需求是每天生成一个日志文件，但是如果一天内的日志文件体积已经超过1G，就重新生成，两个条件满足一个即可。

**RollingFile的属性：**

- fileName  
  指定当前日志文件的位置和文件名称
- filePattern  
  指定当发生Rolling时，文件的转移和重命名规则
- SizeBasedTriggeringPolicy  
  指定当文件体积大于size指定的值时，触发Rolling
- DefaultRolloverStrategy  
  指定最多保存的文件个数

- TimeBasedTriggeringPolicy  
  这个配置需要和filePattern结合使用，注意filePattern中配置的文件重命名规则是`${sys:catalina.home}/logs/esn-palmyy-plugin/info.log.%d{yyyy-MM-dd}`，最小的时间粒度是dd，即按天分隔，`TimeBasedTriggeringPolicy`指定的size是1，结合起来就是每天生成一个新文件。如果改成%d{yyyy-MM-dd HH}，最小粒度为小时，则每一个小时生成一个文件。

  以每天生成一个新文件为例，作为备份的日志文件是在第二天有日志产生时才会将打上时间戳作为备份并生成一个新文件。如，在2015-05-09，info.log产生了10K日志；在2015-05-10时，产生了新的日志，那么已经存在的info.log会被备份为info.log.2015-05-09,并同时产生新的日志文件info.log，并将日志写入新日志文件。

## 总结

刚开始用时配置使用的是

```xml
<RollingFile name="infoLog" fileName="${sys:catalina.home}/logs/esn-palmyy-plugin/info.log" append="true"
					 filePattern="${sys:catalina.home}/logs/esn-palmyy-plugin/info.log.%d{yyyy-MM-dd}">
  <PatternLayout
      pattern="[--%-5level--]	%d{yyyy.MM.dd	HH:mm:ss.SSS}	%class{36}	%L	%M	-	%msg%xEx%n"/>
  <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
  <Policies>
    <TimeBasedTriggeringPolicy  interval="24" modulate="true"/>
  </Policies>
</RollingFile>
```

仔细看看有什么不同吗?


没看出来？在`TimeBasedTriggeringPolicy`标签中加上了`modulate`属性并设置为true，该属性的意思是是否对日志生成时间进行调制。若为true，则日志时间将以0点为边界进行偏移计算。例如第一次日志保存时间是3点，modulate为true，interval是4h。那么下次生成日志时间是4点，08:00，12:00……。而我比较悲催的是在当前晚上九点上线的该日志功能，那么意味着在第二天的九点才会产生新的日志文件，这就导致两天的日志交织在了一起，出现日志的混乱。
