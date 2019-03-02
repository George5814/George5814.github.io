---
layout: post
title: Hadoop错误信息处理
category: 技术
tags: Hadoop
keywords: 
description: 不定期更新
---
 
{:toc}

### 1.找不到dfs.namenode.servicerpc-address

**错误提示：**

```
Incorrect configuration: namenode address dfs.namenode.servicerpc-address or dfs.namenode.rpc-address is not configured.
```

**错误原因：**
	
	未配置`fs.defaultFS`属性(2.7.1版本)

**解决办法**
	
在{hadoop_home}/etc/hadoop/core-site.xml中配置相关属性

```xml
 	<!--接收client连接的RPC接口 -->
    <property>
            <name>fs.defaultFS</name>
            <value>hdfs://h2m1:9000</value>
    </property>

```

**解决后运行截图**
	
![hadoop RPC访问地址](//raw.githubusercontent.com/George5814/blog-pic/master/image/hadoop2/hadoop-error-1.png)


### 2.配置hadoop集群时出现错误

**错误提示：**

```
java.lang.NoClassDefFoundError: org/apache/hadoop/security/authorize/RefreshAuthorizationPolicyProtocol
```

**错误原因：**

	未配置HADOOP_COMMON_HOME变量，或者改变量设置错误
	
**解决办法**
	
	正确设置HADOOP_COMMON_HOME为hadoop的主目录，检查/etc/profile文件和{hadoop_home}/etc/hadop/hadoop-env.sh文件

**解决后运行截图**

![hadoop RPC访问地址](//raw.githubusercontent.com/George5814/blog-pic/master/image/hadoop2/hadoop-error-2.png)


![hadoop RPC访问地址](//raw.githubusercontent.com/George5814/blog-pic/master/image/hadoop2/hadoop-error-3.png)


### 3.寻找主机出现错误

**错误提示**

![hadoop 的NameNode访问不到主机](//raw.githubusercontent.com/George5814/blog-pic/master/image/hadoop/not-connect-host.png)

**错误原因**
	
	hosts文件中配置的ip<-->有错误
	

**解决办法**

	修改ip为正确的值
	
