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

**当前机器为h2m1**

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
        <!-- 此处只能配置hdfs协议，value为${dfs.namenode.rpc-address}/hbase -->
        <value>hdfs://h2m1:8220/hbase</value>
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

**注意：**

`$HBASE_HOME/conf/hbase-site.xml`的`hbase.rootdir`的主机和端口号与
`$HADOOP_HOME/conf/core-site.xml`的`fs.defaultFS`的主机和端口号一致

`regionservers`文件修改`localhost` 为主机名如(h2m1)


### 启动HBase

保证HDFS和ZK正常运行

执行`start-hbase.sh`启动HBase

### 停止HBase

执行`stop-hbase.sh`停止HBase

### 查看HBase启动状况

- 通过web UI： [Master](http://h2m1:16010/),[RegionServer](http://h2m1:16030/)

- 执行`jps`,查看有`QuorumPeerMain`，`HRegionServer`和`HMaster`。

以上两种方式都能验证HBase是否启动成功。


## 2.集群安装HBase

机器集群为`h2m1`,`h2s1`,`h2s2`

### 2.1 修改配置

修改`h2m1`上的hbase配置

`hbase-env.sh`文件中将`export HBASE_MANAGES_ZK=true`改为`export HBASE_MANAGES_ZK=false`。
为了关闭HBase的自管里ZK，使用单独搭建的ZK集群

`hbase-site.xml`中的属性`hbase.zookeeper.quorum`值修改为`h2m1,h2s1,h2s2`。这三台机器上都启动了ZK集群。

`regionservers`文件中配置需要跑HRegionServer服务的机器节点，当前为`h2m1`,`h2s1`,`h2s2`三台机器。

### 2.2 分发安装包

将`h2m1`机器下的`/usr/local/hbase124`目录和/etc/profile文件远程复制到`h2s1`,`h2s2`上。

```
scp -r hbase124/ root@h2s1:/usr/local/
scp -r hbase124/ root@h2s2:/usr/local/
scp /etc/profile  root@h2s1:/etc/
scp /etc/profile  root@h2s2:/etc/
```

在`h2s1`,`h2s2`每一台机器上执行`source /etc/profile`。

### 2.3 启动HBase集群

启动顺序

1. 启动zookeeper集群

1. 启动HDFS集群

1. 启动YARN集群（可选，如果只用到HDFS可以不用启动YARN）

1. 启动HBase集群(在主节点上执行`start-hbase.sh`脚本)  
	
	进程启动顺序：
	- HQuorumPeer(可以使用zookeeper进程)
	- HMaster
	- HRegionServer
	- HMaster（备用HMaster）
	




###　2.4 判断集群启动情况


- 通过命令行每台机器上执行`jps`,主节点含有`QuorumPeerMain`，`HRegionServer`和`HMaster`,从节点含有`QuorumPeerMain`，`HRegionServer`。

- 通过web UI,[访问HMaster服务](http://h2m1:16010/);访问HRegionServer从节点:<http://h2m1:16030/>,<http://h2s1:16030/>,<http://h2s2:16030/>，并都能正常打开页面，表明HBase集群启动成功。


## HMaster从节点

可以在其他机器上启动HMaster的从节点，上限为9个，即总共10个HMaster节点。

- 在其他从节点主机上上启动的HMaster可以作为备用HMaster。

- 在`${HBASE_HOME}/conf`下创建backup-masters,添加上作为备用HMaster节点的主机名或ip。
这样在启动HBase集群时就会启动备用HMaster节点。

- `local-master-backup.sh`在HMaster同一节点上启动备用HMaster

	```sh
	# 在HMaster主机上启动三个备用HMaster，端口分别为主HMaster端口向后偏移2,3,4个位置。即16012，16013,16014
	local-master-backup.sh start 2 3 4
	```

该方式在物理主机（A）崩溃时，A上的所有备用节点也会失效。



