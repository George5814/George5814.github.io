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
	
![hadoop RPC访问地址](/public/img/posts/hadoop/hadoop-error-1.png)
