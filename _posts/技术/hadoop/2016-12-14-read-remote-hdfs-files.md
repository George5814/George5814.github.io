---
layout: post
title: window下Eclipse远程只读HDFS上的文件
category: 技术
tags:  Hadoop
keywords: 
description: 亲测该插件只是有利于连接远程HDFS集群，访问集群上的文件，但不能删除和修改。而且该插件对于win上的eclipse远程调试MapReduce应用没有多大帮助，直接以java Application运行与Map/Reduce运行的结果是一样的。
---

{:toc}

## 准备环境

### 运行环境

- hadoop2.7.2 集群环境（三个节点，h2m1,h2s1,h2s2）
- jdk 1.7.0_75版本
- centos6.5系统

### 开发环境

- windows7 企业版 64位
- 8G内存
- eclipse 4.5
- 插件 hadoop-eclipse-plugin-2.6.0.jar


### 配置插件

- 将插件[hadoop-eclipse-plugin-2.6.0.jar](http://pan.baidu.com/s/1skNzjSL){:target="_blank"}
复制到Eclipse安装目录下的plugins目录下，重启Eclipse。

- window --> show view --> other --> MapReduce Tools --> Map/Reduce Locations 点击打开MapReduce配置窗口。

![MR定位窗口](/public/pic/hadoop/MRDev-location.png)

右击 --> New Hadoop Location --> 显示配置页面

![MR定位窗口](/public/pic/hadoop/MRDev-location-2.png)

**说明：**

**Location Name：**随便填

**Map/Reduce(V2) Master:**
	
Host: MapReduce主节点的Ip地址

port:hdfs-default.xml中配置的dfs.datanode.address对应端口（默认为50010），此端口为DataNode数据传输的端口。
如果没有配置或不知道可以通过<http://host:50070/dfshealth.html#tab-datanode>地址查看

![MR定位窗口](/public/pic/hadoop/MRDev-location-3.png)

**DFS M/R Master Host :**
	
Host:可以默认使用Map/Reduce(V2) Master指定的Host

Port: `core-site.xml`中`fs.defaultFS`指定的端口。如果配置了ViewFs和Federation，则为`hdfs-site.xml`中的`dfs.namenode.rpc-address.<namespaceId>`的值。
	
	
**User name :** 为运行HDFS的用户名

### 查看Hdfs下的文件

Eclipse 的Project Explore 下的DFS Location下一般配置正确就可以查看了。

![在Eclipse中查看HDfs中的文件](/public/pic/hadoop/MRDev-location-hdfs-file.png)

**注意：**在eclipse中显示的HDFS中的文件的内容只能查看不能修改，因为当前查看的用户不是运行HDFS的用户。


**截止次为止，该插件的功能就基本介绍完了，亲自测试该插件不能提交远程MR程序。**	 

