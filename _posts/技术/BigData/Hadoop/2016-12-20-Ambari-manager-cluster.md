---
layout: post
title: Ambari服务器管理集群
category: 技术
tags:  Hadoop
keywords: 
description:   
---


{:toc}

## 操作步骤

通过访问<http://h2m1:8080/#/installer/step0>根据向导设置集群

### 命名集群

![命名集群](/public/pic/hadoop/Ambari-conf-guide-1.png)

点Next按钮。

### 选择HDP版本

默认选择最高，因为没有Apache的版本，只能选择hortonworks。

![选择版本号](/public/pic/hadoop/Ambari-conf-guide-2.png)

点Next按钮。

### 添加FQDN（全限定域名）和ssh私钥文件

FQDN即主机名。
ssh私钥文件可以从远程集群机器的`~/.ssh/`下获得，如文件`id_rsa`
指定ssh的账户和访问端口后，默认账户为`root`，端口号为22。

![添加主机和私钥](/public/pic/hadoop/Ambari-conf-guide-3.png)

点`Register and Confirm`按钮


### 确认主机

稍等等待5到10分钟后会显示最终操作结果:installing -> Registering -> Success/Failed

![确认主机](/public/pic/hadoop/Ambari-conf-guide-4.png)

点Next按钮。

可能会报错，因为指定的文件`repomd.xml`在国内镜像中找不到对应版本。可以将Success主机的`*.repo`复制到报错机器的对应位置，如将h2m1的源复制到h2s2上
`scp -r  /etc/yum.repos.d/ root@h2s2:/etc/`，这样一般就会确认成功了。

![确认主机](/public/pic/hadoop/Ambari-conf-guide-5.png)

点Next按钮。


### 选择要安装的服务

该选择可以指定要安装那些服务，比如HDFS,YARN或HBase，代替手工安装。

![安装的服务](/public/pic/hadoop/Ambari-conf-guide-6.png)

此处我自选了`Ambari Infra`、`Ambari Metrics`，一个当前对我来说无关痛痒的软件。HDFS,YARN和HBase我已经在指定的主机上安装了。

选择好要安装的服务，点Next按钮。

### 分配主节点

![分配主节点](/public/pic/hadoop/Ambari-conf-guide-7.png)

### 安装，开始和测试

![](/public/pic/hadoop/Ambari-conf-guide-12.png)




比较详细的一个教程

<http://www.cnblogs.com/riyueyuzhuzhu/p/5584703.html>