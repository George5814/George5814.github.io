---
layout: post
title: log4j日志发送邮件配置实战
category: 技术
tags: Logger
keywords: 
description:  
---

{:toc}

## 目的

对于系统错误信息和出错原因，系统能够及时主动的将错误信息发送给指定人邮件，及时发现问题及时处理。

## 实战

### 引入依赖包


```xml
<properties>
    <slf4j.version>1.7.2</slf4j.version>
    <log4j.version>1.2.17</log4j.version>
</properties>
<dependencies>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jcl-over-slf4j</artifactId>
    <version>${slf4j.version}</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>${slf4j.version}</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>${slf4j.version}</version>
</dependency>
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>${log4j.version}</version>
</dependency>
</dependencies>
```

log4j的version必须在1.2.16及以上，否则使用qq邮箱会报`530 Error: A secure connection is requiered(such as ssl)`错误！！

### 配置属性文件

```properties
log4j.rootLogger=info,stdout,A4

# 发送邮件日志的配置
log4j.logger.MailLogger=error,MAIL
log4j.appender.MAIL=org.apache.log4j.net.SMTPAppender
log4j.appender.MAIL.Threshold=ERROR
log4j.appender.MAIL.BufferSize=1024
log4j.appender.MAIL.SMTPHost=smtp.qq.com
log4j.appender.MAIL.Subject=ErrorMessage
log4j.appender.MAIL.From=${userName}@qq.com
log4j.appender.MAIL.To=${otherUserName0}@foxmail.com,${otherUserName1}@foxmail.com
log4j.appender.MAIL.SMTPUsername=${userName}@qq.com
log4j.appender.MAIL.SMTPPassword=${授权码}
log4j.appender.MAIL.SMTPPort=587
log4j.appender.MAIL.layout=org.apache.log4j.PatternLayout
log4j.appender.MAIL.layout.ConversionPattern=[%-5p][%t] %d - %c %l - %m%n
```

其中:

- ${otherUserName0}为待发送人员邮箱前缀，多个收件人用逗号分隔

- ${userName}为发件人邮箱前缀

- ${授权码}为qq邮箱设置中获取的授权码

- SMTPPort：设置该属性可以避免qq邮箱报错`530 Error: A secure connection is requiered(such as ssl)`,该属性在log4j的1.2.16及以上版本才存在

没有将`MailLogger`加入到`rootLogger`中是为了只在指定位置调用发邮件日志功能而不是整个系统都使用。

### java中引用

```java
private static final Logger LOGGERMAIL=LoggerFactory.getLogger("MailLogger");

public static void main(String[] args) {
    try {
        String str=null;
        System.out.println(str.length());
    } catch(Exception e) {
        LOGGERMAIL.error("===发送日志邮件===",e.toString());
    }
}
```
