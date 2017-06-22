---
layout: post
title: 11.hadoop-2.7.2官网文档翻译-代理用户-超级用户对其他用户的代表
category: 技术
tags:  Hadoop
keywords: 
description: Hadoop超级用户对其他用户的代表。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/Superusers.html
---

{:toc}

### 介绍

该文档描述了一个超级用户怎样代表另一个用户提交任务或访问HDFS。

### 使用案例

下一节描述的代码示例适用于以下用例。

用户名为`super`的超级用户想要代表用户`joe`提交任务和访问HDFS。该超级用户有kerberos认证，但是用户`joe`没有。
该任务需要作为用户`joe`运行并且NameNode上任何文件的访问都要作为用户`joe`去做。
这需要用户`joe`可以使用超级用户的kerberos认证连接NameNode或者Job Tracker连接。换句话说，超级用户在扮演用户`joe`

其他产品比如`apache Oozie`也需要这样。

### 代码示例

在该例子中`super`的认证将被用于登录，代理用户ugi对象将会为`joe`创建。

该代理用户ugi对象的方法doAs是扮演的操作。

```java
//Create ugi for joe. The login user is 'super'.
UserGroupInformation ugi = UserGroupInformation.createProxyUser("joe", UserGroupInformation.getLoginUser());
ugi.doAs(new PrivilegedExceptionAction<Void>() {
  public Void run() throws Exception {
    //Submit a job
    JobClient jc = new JobClient(conf);
    jc.submitJob(conf);
    //OR access hdfs
    FileSystem fs = FileSystem.get(conf);
    fs.mkdir(someFilePath);
  }
}

```


### 配置

你可以使用属性`hadoop.proxyuser.$superuser.hosts`配置代理用户，同时配置`hadoop.proxyuser.$superuser.groups`和`hadoop.proxyuser.$superuser.users`全部或其中一个。

如下方式指定`core-site.xml`,只能来自`host1`,和`host2`超级用户`super`来模仿组`group1`和`group2`的用户。

```xml
   <property>
     <name>hadoop.proxyuser.super.hosts</name>
     <value>host1,host2</value>
   </property>
   <property>
     <name>hadoop.proxyuser.super.groups</name>
     <value>group1,group2</value>
   </property>

```

如果这些配置不存在，扮演将不会被允许，连接也会失败。

如果安全性要求不高，通配符`*`可以用以允许所有的主机所有用户。
举例说明，如下在`core-site.xml`中指定，从任何主机访问的用户`oozie`可以扮演任何组的任何用户。

```xml
 <property>
    <name>hadoop.proxyuser.oozie.hosts</name>
    <value>*</value>
  </property>
  <property>
    <name>hadoop.proxyuser.oozie.groups</name>
    <value>*</value>
  </property>
```

属性`hadoop.proxyuser.$superuser.hosts`可接受ip地址列表，CIDR格式的ip地址范围，主机名。

比如,像下面指定的，来自在`10.222.0.0-15`范围和`10.113.221.221`的注解可以扮演用户`user1`和`user2`。

```xml
 <property>
     <name>hadoop.proxyuser.super.hosts</name>
     <value>10.222.0.0/16,10.113.221.221</value>
   </property>
   <property>
     <name>hadoop.proxyuser.super.users</name>
     <value>user1,user2</value>
   </property>
```

### 附加说明

如果集群运行在[安全模式](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SecureMode.html){:target="_blank"},超级用户必须有kerberos证书才能扮演其他用户。

这个特性不能使用授权令牌。如果超级用户将它拥有的授权令牌添加到带里用户ugi会报错，但是这将允许代理用户连接到具有超级用户特权的服务。

然而，如果超级用户不想将授权令牌给`joe`,必须首先扮演`joe`然后为`joe`获取一个授权令牌。就像上面的代码示例一样的方式，将其添加到`joe`的ugi。
在这种方式下，授权令牌将会拥有像`joe`一样的拥有者。































