---
layout: post
title: ZooKeeper集群安装配置
category: 技术
tags: ZooKeeper
keywords: 
description: ZooKeeper集群的简单安装
---
 
 
{:toc}



### 1.	如何搭建zk集群

- a.前提：

	Zk服务器集群规模不小于3个节点，并且各个服务器之间时间一致

- b.安装：

将zookeeper的压缩包上传到linux，通过winscp软件，暂定为`/usr/local/`下，解压zookeeper压缩包

```cmd
#tar –xzvf  zookeeper-3.4.5.tar.gz
```

为zookeeper-3.4.5改为简单的名字，便于使用

```cmd
#mv zookeeper-3.4.5 zk
```

添加环境变量

```cmd
#vim /etc/profile
```

```bash
export ZOOKEEPER_HOME = /usr/local/zk
```

![修改环境变量](/public/pic/ZooKeeper/zookeeper2.png) 

此处的`PATH = .:$ZOOKEEPER_HOME/bin:$HADOOP_HOME/bin:$JAVVA_HOME/bin:$PATH`,使环境变量生效

```cmd
#source /etc/profile
```


进入zookeeper的配置文件目录

```cmd
#cd /usr/local/zk/conf 
```

复制一份zoo_sample.cfg  并更名为zoo.cfg

```cmd
#cp zoo_sample.cfg  zoo.cfg
```

修改zookeeper的配置文件zoo.cfg

```cmd
#vim zoo.cfg
```


![修改datadir路径](/public/pic/ZooKeeper/zookeeper3.png) 

修改第十三行的datadir路径为自己想要的路径
 

![配置三个zk服务器](/public/pic/ZooKeeper/zookeeper4.png) 

配置三个zk服务器
 
其中第一个端口用来集群成员的信息交换，第二个端口是在leader挂掉时专门用来进行选举leader所用。
创建文件夹data 

```cmd
#mkdir /usr/local/zk/data
```

在data目录下，创建文件myid,并写上对应的zoo.cfg中的标号
例如：
`server.1=hadoop1:2888:3888`
则在hadoop1主机上的myid中添加1

在三台hadoop机器上同步部署zk的文件夹及环境变量

```cmd
#scp -r /etc/profile  hadoop2:/etc/
```


```cmd
#scp -r /etc/profile  hadoop3:/etc/
```

使得环境变量生效

```cmd
#source /etc/profile
```



```cmd
#scp -r /usr/local/zk/  hadoop2:/usr/local/
#scp -r /usr/local/zk/  hadoop3:/usr/local/
```


修改各个主机中的myid文件中的值，以便与zoo.cfg中的配置对应，使得zk能找到对应的机器

```cmd
#echo 2 >/usr/local/zk/data/myid (机器hadoop2上	)
#echo 3 >/usr/local/zk/data/myid  (机器hadoop3上)
```

c)	启动zk集群服务
在每台机器上分别执行`/usr/local/zk/bin/zkServer.sh`文件

```cmd
#/usr/local/zk/bin/zkServer.sh start
```


在这里三台zookeeper服务器会自动选举leader，然后其他的都是follower。


检验每个节点上的zookeeper的角色状态

```cmd
#/usr/local/zk/bin/zkServer.sh status
```

![角色状态1](/public/pic/ZooKeeper/zookeeper5.png) 


![角色状态2](/public/pic/ZooKeeper/zookeeper6.png) 


 
 
### 2.	使用zookeeper

在命令行中执行`/usr/local/zk/bin/zkCli.sh`

![启动客户端1](/public/pic/ZooKeeper/zookeeper7.png) 


![启动客户端2](/public/pic/ZooKeeper/zookeeper8.png) 

 
 
- a.随便输入什么，回车后可以查看提示信息

![查看提示信息](/public/pic/ZooKeeper/zookeeper9.png) 

 
- b.试一试创建的命令：`create /test  test `,创建一个路径/test，设置数据位test

![创建的命令](/public/pic/ZooKeeper/zookeeper10.png) 

 
登录另一台zk服务器的客户端，执行`get /test`命令，同样会看到如上的结果，则证明机器间的数据同步成功

![数据同步](/public/pic/ZooKeeper/zookeeper11.png) 


 
### 3.	配置zookeeper

Zk是通过配置文件zoo.cfg控制，各个机器上的配置文件几乎是相同的，在集群部署时非常方便。

- a.最低配置：

	i.	clientPort：监听客户端连接的端口
	
	ii.	dataDir：存储内存中数据库快照的位置
	
	iii.	tickTime：基本事件单元，毫秒单位，控制心跳和超时，默认为tickTime的两倍
	
- b.高级配置：

	i.	dataLogDir：事务日志写入” dataLogDir”指定的目录，而不是dataDir指定的目录。
	
	ii.	maxClientCnxns：限制连接到zk的客户端数量，限制并发连接的数量，通过IP区分客户端。设置为0或不设置会取消并发连接的控制。
	
	iii.	minSessionTimeout 和maxSessionTimeout
	 
最小会话超时时间和最大会话超时时间，默认最小为tickTime的两倍。最大为20倍。

- c.集群配置：

	i.	initLimit
		允许follower连接并同步到leader的初始化连接时间，以tickTime的倍数表示，当时间超过tickTime的指定倍数时会失败。
		
	ii.	syncLimit：
		leader和follower之间发送消息时请求和应答的时间长度。如果follower在设置的时间内不能和leader通信，那此follower将被丢弃。

### 4.	Zookeeper特性

Zookeeper中指向节点的路径必须使用规范的绝对路径表示，并以斜线”/”分隔，zookeeper中不允许使用相对路径。

- a.Znode：zk目录树中每个节点对应一个znode，每个znode维护者属性结构，包含版本号、时间戳等状态，跟linux的iNode节点作用类似。

	**Znode的主要特征：**
	
		i.	Watches：设置watch(监视器)，节点发生改变时，会触发watch对应操作，会向客户端发送且只一个通知，因为watch只能被触发一次。
		
		ii.	数据访问：zk中每个节点存储的数据需要被原子性操作，每个节点都有ACL，限定了特定用户对目标节点可以执行的操作。
		
		iii.	临时节点：节点分为临时节点和永久节点，节点类型在创建时确定，不能被改变。Zk临时节点的生命周期依赖创建他们的会话，会话结束临时节点结束。临时节点不能有子节点。永久节点不依赖会话，只能在客户端执行删除操作删除。
		
		iv.	顺序节点：创建Znode时，用户可在请求zk路径结尾添加递增计数。
		
- b.Zookeeper中的时间

	i.	Zxid:每一个操作都会使节点收到zxid格式的时间戳，全局有序。每个节点维护三个zxid：cZxid、mZxid、pZxid。

	ii.	版本号：对节点每个操作会使该节点版本号增加，三个版本号：dataVersion(节点数据版本号)、cversion(子节点版本号)、aclVersion(节点所拥有的ACL版本号)

- c.Zookeeper watches：zk可以为所有的读操作设置watch，包括(exists()、getChildren()、getData()).watch是一次性触发器。Watch事件将被异步发送到客户端，并且zk为watch提供了有序的一致性保证。

	Zookeeper的watch分为两类：数据watch和子watch。exists()和getData()负责设置数据watch，getChildren()负责设置子watch。Create()和delete()触发znode的数据watch和子watch
	Watch由客户端所连接的zookeeper服务器在本地维护，非常容易设置、管理和分派。
	当客户端连接新的服务器时，任何会话事件都可能触发watch，当从服务器断开连接时，watch不会被接收，但当客户端重新连接时，先前注册的watch会被重新注册。
	
- d.Zookeeper ACL

	Ids.OPEN_ACL_UNSAFE 对所有的ACL都完全开放
	
	Ids.READ_ACL_UNSAFE 对任何应用程序都只有读权限
	
	Ids.CREATOR_ALL_ACL 节点创建者的所有权限，创建者必须通过服务器认证。
	
- e.zookeeper的一致性：顺序一致性(与被发送顺序一直)、原子性(要么成功要么失败)、单系统镜像(客户端连接到集群的任一服务器看到相同的zookeeper视图)、可靠性(1.客户端成功返回代码成功，否则不知道操作是否生效；2.故障恢复时，任何客户端能看到的执行成功的更新操作将不会回滚)和实时性(特定时间内，客户端看到的系统是实时的，任何系统的改变将被客户端看到，或者被客户端侦测到)
