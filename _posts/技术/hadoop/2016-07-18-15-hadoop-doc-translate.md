---
layout: post
title: 15.hadoop-2.7.2官网文档翻译-Hadoop的http web认证
category: 技术
tags:  Hadoop
keywords: 
description: Hadoop的http web认证。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/HttpAuthentication.html
---

{:toc}


## 介绍

该文档描述了怎样配置需要用户认证的Hadoop HTTP的web控制台。

默认情况下，Hadoop HTTP的web控制台（JobTracker，NameNode，TaskTracker和DataNode）允许任何形式的认证访问.

类似于Hadoop的RPC，Hadoop的HTTP web控制台可以配置为要求使用HTTP SPENGO协议(支持IE和火狐浏览器)kerberos身份认证。

此外，HTTP web控制台支持Hadoop的伪/简单验证等效。如果该选项启用。用户必须使用user.name查询字符串参数的第一个浏览器交互指定的用户名。
比如：`http://localhost:50030/jobtracker.jsp?user.name=babu`。

如果web控制台需要自定义的认证机制，需要实现一个插件来支持代替的认证机制（详细请参考Hadoop的权威指南里的`AuthenticatorHandler`）。

接下来描述怎样配置需要用户认证的web控制台。

##　配置

接下来的属性是在集群中所有节点的`core-site.xml`文件中的。

`hadoop.http.filter.initializers`:增加该属性`org.apache.hadoop.security.AuthenticationFilterInitializer`初始化类

`hadoop.http.authentication.type`:定义web控制台使用的认证，支持的值为`simple`，`kerberos`和`#AUTHENTICATION_HANDLER_CLASSNAME#`,默认值为`simple`。

`hadoop.http.authentication.token.validity`:标识认证token失效期（单位为秒），默认是36000。

`hadoop.http.authentication.signature.secret.file`:认证token的的签名秘钥文件。集群的所有节点，JobTracker，NameNode，TaskTracker，DataNode，都应该有相同的秘钥。默认值为`$user.home/hadoop-http-auth-signature-secret`。**重要：**该文件只有运行守护进行的Unix用户可读。

`hadoop.http.authentication.cookie.domain`:存储认证token的cookie所用的domain。为了集群所有节点的认证都能正常工作，domain必须设置正确。没有默认值，cookie没有domain，只有cookie的工作。

**重要:**当使用IP地址时，浏览器会忽略cookie的domain设置。该设置在集群所有节点上正确的工作,需要使用`hostname.domain`配置生成URL。

`hadoop.http.authentication.simple.anonymous.allowed`:指明使用`simple`认证时，异步请求被允许。默认值为true。

`hadoop.http.authentication.kerberos.principal`:在使用kerberos认证时，指明kerberos负责人用于Hadoop端。每个kerberos的http SPNEGO详述必须让负责人短名称是`HTTP`。默认值为`HTTP/_HOST@$LOCALHOST`,`_HOST`被HTTP服务器器的绑定地址代替。

`hadoop.http.authentication.kerberos.keytab`:Http终端使用的kerberos负责人证书的keytab文件的位置。


## CORS

为了启用跨域支持，请设置下面的参数：

在`core-site.xml`中添加`org.apache.hadoop.security.HttpCrossOriginFilterInitializer`到属性`hadoop.http.filter.initializers`。也需要在`core-site.xml`中设置如下属性：

|属性|默认值|描述|
|---|---|---|
|hadoop.http.cross-origin.enabled|false|开启所有web服务的跨域支持|
|hadoop.http.cross-origin.allowed-origins|逗号分隔的允许跨域的域列表,`*`允许和模式允许|
|hadoop.http.cross-origin.allowed-methods|GET,POST,HEAD|逗号分隔的允许跨域的方法列表|
|hadoop.http.cross-origin.allowed-headers|X-Requested-With,Content-Type,Accept,Origin|逗号分隔的允许的header列表|
|hadoop.http.cross-origin.max-age|1800|缓存时间|







