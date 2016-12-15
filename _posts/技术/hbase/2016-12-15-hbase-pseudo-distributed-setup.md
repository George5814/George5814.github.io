---
layout: post
title: HBase分布式安装
category: 技术
tags:  HBase
keywords: 
description:   
---


{:toc}

## 1.单机安装HBase

### 1.1 下载HBase的Bin包

[hbase-1.2.4-bin.tar.gz](http://mirrors.hust.edu.cn/apache/hbase)

`wget http://mirrors.hust.edu.cn/apache/hbase/1.2.4/hbase-1.2.4-bin.tar.gz`

### 解压hbase到指定目录

指定目录为`/usr/local`

```
tar -xzvf hbase-1.2.4-bin.tar.gz
mv hbase-1.2.4 /usr/local/
mv hbase-1.2.4 hbase124
```

### 配置HBase的环境变量

`vim /etc/profile`打开系统环境变量配置文件

添加内容：

```
export HBASE_HOME=/usr/local/hbase124
export PATH=$PATH:$HBASE_HOME/bin
``` 

`source /etc/profile`使得修改生效

`hbase version`检查环境变量配置是否生效，显示"HBase 1.2.4"内容即为已经生效。


### 修改HBase配置

进入`/usr/local/hbase124/conf`目录

修改`hbase-site.xml`，`hbase-env.sh`，`regionservers`文件。

`hbase-env.sh`文件添加配置

```sh
export HBASE_MANAGES_ZK=true
export JAVA_HOME=/usr/local/jdk
```

其他使用默认配置。

`hbase-site.xml`文件在`<configuration>`中配置如下内容：

```xml
<property>
        <name>hbase.rootdir</name>
        <value>hdfs://h2m1:8220/hbase</value>
        <!-- <value>webhdfs://h2m1:50070/user/root/hbase</value> -->
        <!-- <value>hdfs://h2m1:8220/hbase</value> -->
</property>
<property>
        <name>hbase.cluster.distributed</name>
        <value>true</value>
</property>
<property>
        <name>hbase.zookeeper.quorum</name>
        <value>h2m1</value>
</property>
<property>
        <name>dfs.replication</name>
        <value>1</value>
</property>
<property>
<!-- hbase提供的服务地址-->
        <name>hbase.master</name>
        <value>h2m1:60000</value>
</property>
```

`regionservers`文件修改`localhost` 为主机名如(h2m1)


### 启动HBase

保证HDFS和ZK正常运行

执行`start-hbase.sh`启动HBase

### 停止HBase

执行`stop-hbase.sh`停止HBase

### 查看HBase启动状况

- 通过web UI： <http://h2m1:16010/>

- 执行`jps`,查看有`QuorumPeerMain`，`HRegionServer`和`HMaster`。

以上两种方式都能验证HBase是否启动成功。