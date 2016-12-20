---
layout: post
title: Ambari服务器安装
category: 技术
tags:  Hadoop
keywords: 
description:   
---


{:toc}

### 准备环境

- CentOS 6.5 64位虚拟机
- jDK v1.7.0_75

### 安装Ambari服务器 

#### 下载Ambari公共库文件

```
wget http://public-repo-1.hortonworks.com/ambari/centos6/2.x/updates/2.4.2.0/ambari.repo
```

####  添加下载源

```
 mv ambari.repo  /etc/yum.repos.d/
```


#### 清理yum并检查新添加的源是否生效

```
yum clean all

yum list | grep ambari
```


#### 安装

```
yum install ambari-server
```

使用系统的yum从上面指定的源下载对应的Ambari的RPM包，大约700兆左右。

#### 设置

```
ambari-server setup
```

会有一些交互操作，可以按照自己设置，也可以一直回车使用默认设置。
JDK可以配置为自定义的JDK。


#### 启动

```
ambari-server start
```

#### 检查8080端口是否被监听

```
netstat -tnlp | grep java | grep 8080
```


#### 访问

Ambari默认启动会监听`8080`端口。

`http://h2m1:8080/`


**参考文献**

> 1. Ambari学习15_CentOS6.5下安装Ambari2.4全过程  
	链接：<http://blog.csdn.net/wang_zhenwei/article/details/53198988>

