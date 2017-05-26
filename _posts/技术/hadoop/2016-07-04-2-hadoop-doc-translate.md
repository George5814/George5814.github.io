---
layout: post
title: 02.hadoop-2.7.2官网文档翻译-单节点集群安装
category: 技术
tags:  Hadoop
keywords: 
description: 
---

{:toc}

### 目标
	
该文档讲述怎样安装并配置单节点Hadoop安装，因此你可以快速使用MapReduce和HDFS执行简单的操作
	
### 先决条件

- 支持平台
	
	- CNU/Linux 支持作为开发和生产环境平台。Hadoop在GNU/Linux系统上已经过2000节点集群的论证。
	- 也支持window，但以下步骤只是针对于Linux。Window上安装Hadoop请移步[wiki page](http://wiki.apache.org/hadoop/Hadoop2OnWindows)
	
- 需要的软件
	
	- JAVA 必须安装，推荐的java版本请看[HadoopJavaVersions](http://wiki.apache.org/hadoop/HadoopJavaVersions)
	- SSH 必须安装，因为在使用Hadoop脚本管理远程Hadoop守护进程的时候必须保证sshd运行。
	
- 安装软件
	
如果你的集群没有需要的软件，需要安装它。

比如在Ubuntu上：
	

```
$sudo apt-get install ssh

$sudo apt-get install rsync
```

### 下载 

为了获得Hadoop分发版，从[Apache下载镜像站](http://www.apache.org/dyn/closer.cgi/hadoop/common/)之一下载最新的稳定版。

### 准备开始Hadoop集群

解压下载的Hadoop版本压缩包。在发布版中，编辑文件`etc/hadoop/hadoop-env.sh`来定义一些参数。如下：

```
#设置安装的java的home目录
export JAVA_HOME=/usr/local/jdk
```

尝试命令:`$bin/hadoop`，该命令将会显示Hadoop脚本的帮助信息。

现在你可以在三种支持模式（`本地模式`,`伪分布模式`,`完全分布式模式`）的任一种中开始你的Hadoop集群了。

### 脱机操作

默认情况下，Hadoop被配置为运行在非分布式模式，作为单java进程，这对调试很有用。
	
下面的例子是复制解压的配置目录作为输入，然后查找并显示每个匹配的给定的正则表达式。输出到指定的输出目录

```
  $ mkdir input
  $ cp etc/hadoop/*.xml input
  $ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar grep input output 'dfs[a-z.]+'
  $ cat output/*
```

### 伪分布操作
	
Hadoop也可以在伪分布式模式下运行在但节点上，每个Hadoop守护进程运行在分开的java进程中。

#### 配置

使用如下配置

修改`etc/hadoop/core-site.xml`

```
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```

修改`etc/hadoop/hdfs-site.xml`

```
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
</configuration>	
```

#### 设置ssh免密码登录

现在检查是否可以不使用秘钥就能登录localhost:`$ssh localhost`。

如果不能免密码登录，执行如下的命令：

```
$ ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa
$ cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys
$ chmod 0600 ~/.ssh/authorized_keys
```

#### 执行

接下来要在本地运行一个MapReduce任务。如果你想要在YARN执行任务，可以看[YARN on Single Node](执行在yarn上的单点)


1. 格式化文件系统 
	
	`$ bin/hdfs namenode -format`


1. 启动NameNode进程和DataNode进程

	`$ sbin/start-dfs.sh`

	该Hadoop的进程日志输入目录为`$HADOOP_LOG_DIR`，默认为`$HADOOP_LOG_DIR`/log。

1. 在web接口上查看NameNode，默认为

	- NameNode ： http://localhost:50070
	
	
1. 创建HDFS目录来执行MapReduce任务

	```
	$ bin/hdfs dfs -mkdir /user
	$ bin/hdfs dfs -mkdir /user/<username>
	```

1. 复制input中的文件到DFS中

	` $ bin/hdfs dfs -put etc/hadoop input`
	

1. 运行已经提供好的例子

	`$ bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.2.jar grep input output 'dfs[a-z.]+'`
	
	
1. 检验输出文件：复制HDFS中output目录下的文件到本地文件系统，并检验他们

```
$ bin/hdfs dfs -get output output
$ cat output/*
```
	
或者直接在HDFS上查看output文件

```
$ bin/hdfs dfs -cat output/*
```
	
1. 操作完成后，停止HDFS的进程

	`$sbin/stop-dfs.sh`
	

1. 单点上的YARN

通过设置几个参数，并在运行ResourceManager 和NodeManager 的进程的条件下，可以伪分布式模式的YARN上运行MapReduce任务

下面假定上文中的1. ~ 4. 步骤已经成功执行

 1. 配置参数，修改`etc/hadoop/mapred-site.xml`
 	
```xml
<configuration>
	<property>
		<name>mapreduce.framework.name</name>
		<value>yarn</value>
	</property>
</configuration>
```


修改`etc/hadoop/yarn-site.xml`

```xml
<configuration>
	<property>
		<name>yarn.nodemanager.aux-services</name>
		<value>mapreduce_shuffle</value>
	</property>
</configuration>
```

2. 启动YARN，ResourceManager进程和NodeManager 进程

	` $ sbin/start-yarn.sh`

3. 在浏览器中查看ResourceManager，默认值为


	- ResourceManager： http://localhost:8088/

4. 运行Mapreduce任务

5. 当完成操作后，停止yarn进程
	
	`$ sbin/stop-yarn.sh`
	
### 完全分布式操作

完全分布式安装，非平凡的集群请看[集群安装]({% post_url 2016-07-04-3-hadoop-doc-translate %}){:title="集群安装"  :target="_blank"}
