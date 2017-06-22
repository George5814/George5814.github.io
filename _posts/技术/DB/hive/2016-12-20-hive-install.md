---
layout: post
title: Hive的安装
category: 技术
tags:  Hive
keywords: 
description:   
---

## Hive安装

**注意点：**

在正常运行hive之前，必须先正常启动HDFS和YARN（带有MR）集群。

### 下载包

`wget https://mirrors.tuna.tsinghua.edu.cn/apache/hive/hive-2.1.1/apache-hive-2.1.1-bin.tar.gz`


### 解压更名

```sh
tar -xzvf apache-hive-2.1.1-bin.tar.gz 
mv apache-hive-2.1.1-bin /usr/local/
mv apache-hive-2.1.1-bin/ hive211
```

### 添加环境变量


```sh
vim /etc/profile
	export HIVE_HOME=/usr/local/hive211
	export PATH=$PATH:$HIVE_HOME/bin

source /etc/profile
```

### 修改hive的配置

```
cp hive-env.sh.template  hive-env.sh
cp hive-default.xml.template hive-site.xml

```


conf/hive-site.xml文件内的

将值改为false，否则会报错`message:Version information not found in metastore.`

```xml
<property>
	<name>hive.metastore.schema.verification</name>
	<value>false</value>
</property>
```

在最后添加上

```xml
  <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://host:port/hive?createDatabaseIfNotExist=true</value>
  </property>
  <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
  </property>
  <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>root</value>
  </property>
  <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>root</value>
  </property>
```

### 安装mysql


卸载自带mysql，重新安装新的mysql

```sh
# 查看mysql是否安装
rpm -qa | grep mysql

#普通删除
rpm -e mysql

#强力删除
rpm -e --nodeps mysql

#yum查找mysql版本
yum list | grep mysql

#安装mysql
yum install -y mysql-server mysql mysql-deve

#查看mysql安装版本
rpm -qi mysql-server

#启动mysql服务
service mysqld start

#停止mysql服务
service mysqld stop

#检查mysql是否开机启动
chkconfig --list | grep mysqld

#设置mysql开机启动
chkconfig mysqld on

#在第一次未设置mysql的root用户密码情况下设置用户密码
mysqladmin -u root password 'root'

```

```mysql
修改mysql的users表，使得root用户允许远程登录
mysql>update user set host = '%' where user = 'root' limit 1;

```


### 启动hive

直接执行`hive`,会报错`Unable to instantiate org.apache.hadoop.hive.ql.metadata.SessionHiveMetaStoreClient`,因为没有启动hive的`Metastore Server`服务

#### 启动Metastore Server

后台启动：`hive --service metastore & `

启动`hive`会提示：`Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. tez, spark) or using Hive 1.X releases`
即在hive2.x版本中，MapReduce作为计算框架已经过期了，推荐使用tez或者spark作为计算框架。

#### 启动hiveserver2服务，可以通过web页面访问

修改配置

```xml
<property>
	<name>hive.server2.webui.host</name>
	<value>h2m1</value>
	<description>The host address the HiveServer2 WebUI will listen on</description>
</property>
<property>
	<name>hive.server2.webui.port</name>
	<value>10002</value>
	<description>The port the HiveServer2 WebUI will listen on. This can beset to 0 or a negative integer to disable the web UI</description>
</property>
```

启动：`hive --service hiveserver2 & `

浏览器访问：<http://h2m1:10002>

表信息存储在mysql的hive数据库的`TBLS`表中。



## 产生的错误信息


|错误信息|原因|解决方案|
|--|--|--|
|`Unable to instantiate org.apache.hadoop.hive.ql.metadata.SessionHiveMetaStoreClient`|没有正常启动Hive 的 Metastore Server服务进程。 |启动Hive 的 Metastore Server服务进程：hive --service metastore &|
|message:Version information not found in metastore.|schema校验默认开启了|"关闭校验：`<property><name>hive.metastore.schema.verification</name><value>false</value></property>`|
|datanucleus.autoCreateTables|需要设置允许自动创建表格|设置属性:`  <property><name>datanucleus.schema.autoCreateTables</name><value>true</value></property>`|
|`java.lang.RuntimeException: The root scratch dir: /tmp/hive on HDFS should be writable. Current permissions are: rwx--x--x`|当前用户没有写权限|更改目录权限：`hadoop fs -chmod 777 /tmp/hive`|
|`${system:java.io.tmpdir%7D/$%7Bsystem:user.name%7D`|没有指定本地存储的临时目录|指定配置：`" <property><name>system:java.io.tmpdir</name><value>/usr/local/hive211/tmp</value></property><property><name>system:user.name</name><value>hive</value></property>"`|
|`message:One or more instances could not be made persistent`||执行`${HIVE_HOME}/bin/schematool -dbType mysql -initSchema`|
|Attempt to do update or delete using transaction manager that does not support these operations||查看该文章：<https://cwiki.apache.org/confluence/display/Hive/Hive+Transactions#HiveTransactions-NewConfigurationParametersforTransactions>|
|Attempt to do update or delete on table default.t1 that does not use an AcidOutputFormat or is not bucketed|目前只有ORCFileformat支持AcidOutputFormat，默认格式为TextInputFormat，不仅如此建表时必须指定参数('transactional' = true)|直接在创建表时指定桶和转换。如：`create table test(id int ,name string )clustered by (id) into 2 buckets stored as orc TBLPROPERTIES('transactional'='true');`|


## 执行示例

### 查看所有数据库

```
hive>show databases;
```

### 应用指定数据库

```
hive>use dbName;
```

### 查看所有表

```
hive>show tables;
```

### 创建表

```
hive> create table t1(a int ,b string);
```

### 插入表数据

```
hive> insert into t1(a,b) values(12,"jingzz")
```


### 获取表数据


该操作不会执行MR任务

```
hive> select a,b from t1;
```

### 获取总记录数

该操作也会启动MR任务

```
hive> select count(a) from t1;
```


### 删除表

将MySql中指定表的描述删除，将HDFS上存储的表对应的数据删除；

```
hive>drop table t1;
```

### 重命名表

将test表重命名为t1。

```
hive> alter table test rename to t1;
```



## 示例配置

成功执行增删改查和ACID操作后的配置为:

```xml

  <!-- 应用mysql相关配置-->
 <property>
        <name>javax.jdo.option.ConnectionURL</name>
        <value>jdbc:mysql://h2m1:3306/hive?createDatabaseIfNotExist=true</value>
  </property>
  <property>
        <name>javax.jdo.option.ConnectionDriverName</name>
        <value>com.mysql.jdbc.Driver</value>
  </property>
  <property>
        <name>javax.jdo.option.ConnectionUserName</name>
        <value>root</value>
  </property>
  <property>
        <name>javax.jdo.option.ConnectionPassword</name>
        <value>root</value>
  </property>
  
  <!-- 允许自动创建表 -->
  <property>
        <name>datanucleus.schema.autoCreateTables</name>
        <value>true</value>
  </property>

   <!-- 指定临时目录和用户 -->
  <property>
        <name>system:java.io.tmpdir</name>
        <value>/usr/local/hive211/tmp</value>
  </property>
  <property>
        <name>system:user.name</name>
        <value>hive</value>
  </property>
  <property>
        <name>hive.support.concurrency</name>
        <value>true</value>
  </property>
  <property>
        <name>hive.exec.dynamic.partition.mode</name>
        <value>nonstrict</value>
  </property>
  <property>
        <name>hive.txn.manager</name>
        <value>org.apache.hadoop.hive.ql.lockmgr.DbTxnManager</value>
  </property>
  <property>
        <name>hive.compactor.initiator.on</name>
        <value>true</value>
  </property>
  <property>
        <name>hive.compactor.worker.threads</name>
        <value>2</value>
  </property>

```


参考文献：
> 1.Hive的ACID特性参考文章：<https://cwiki.apache.org/confluence/display/Hive/Hive+Transactions>
