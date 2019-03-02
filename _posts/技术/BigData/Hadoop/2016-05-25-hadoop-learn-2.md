---
layout: post
title: hadoop集群学习笔记(1)
category: 技术
tags:  Hadoop
keywords: 
description: hdfs faderation 配置
---

{:toc}


### 1.修改hdfs-site.xml

修改配置如下，并复制到集群的其他机器中

```xml
<configuration>
        <!--副本的份数-->
        <property>
                <name>dfs.replication</name>
                <value>3</value>
                <description>replication num</description>
        </property>
        <property>
                <name>dfs.namenode.name.dir</name>
                <value>file:/root/hadoop/dfs/nn/name</value>
                <description>name location</description>
        </property>
        <property>
                <name>dfs.namenode.edits.dir</name>
                <value>file:/root/hadoop/dfs/nn/edits</value>
                <description>edit file location</description>
        </property>
        <property>
                <name>dfs.namenode.checkpoint.dir</name>
                <value>file:/root/hadoop/dfs/snn/name</value>
                <description>seconde name file location</description>
        </property>
        <property>
                <name>dfs.namenode.checkpoint.edits.dir</name>
                <value>file:/root/hadoop/dfs/snn/edits</value>
                <description>seconde edit file location</description>
        </property>
        <property>
                <name>dfs.datanode.data.dir</name>
                <value>file:/root/hadoop/dfs/dn/data</value>
                <description>data location</description>
        </property>
        <property>
                <name>dfs.webhdfs.enabled</name>
                <value>true</value>
        </property>
        <property>
                <name>dfs.permissions</name>
                <value>false</value>
        </property>
        <!-- nameNode1,namenode2 -->
        <property>
                <name>dfs.nameservices</name>
                <value>ns1,ns2</value>
        </property>
        <!-- namenode1 config  -->
        <property>
                <name>dfs.namenode.rpc-address.ns1</name>
                <value>h2m1:8020</value>
        </property>
        <property>
                <name>dfs.namenode.servicerpc-address.ns1</name>
                <value>h2m1:8021</value>
        </property>
 		<property>
                <name>dfs.namenode.secondary.http-address.ns1</name>
                <value>h2m1:9001</value>
                <description>secondary namenode web config</description>
        </property>
        <property>
                <name>dfs.namenode.secondary.https-address.ns1</name>
                <value>h2m1:9002</value>
                <description>secondary namenode web config</description>
        </property>
        <!-- namenode2 config  -->
        <property>
                <name>dfs.namenode.rpc-address.ns2</name>
                <value>h2s1:8020</value>
        </property>
        <property>
                <name>dfs.namenode.servicerpc-address.ns2</name>
                <value>h2s1:8021</value>
        </property>
        <property>
                <name>dfs.namenode.secondary.http-address.ns2</name>
                <value>h2s1:9001</value>
                <description>secondary namenode web config</description>
        </property>
        <property>
                <name>dfs.namenode.secondary.https-address.ns2</name>
                <value>h2s1:9002</value>
                <description>secondary namenode web config</description>
        </property>
</configuration>
```



### 2. 修改core-site.xml

作为NameNode faderation的节点修改如下的配置将`h2m1`修改为自定主机名或ip

```xml
 <property>
        <name>fs.defaultFS</name>
        <value>hdfs://h2m1:9000</value>
</property>

```

### 3. 删除logs、dfs目录

```
rm -rf /root/hadoop/dfs/  ### 自定义的dfs位置

rm -rf /usr/local/hadoop271/logs/
```

### 4. 格式化NameNode faderation节点，每个都需要执行命令

`hdfs namenode -format -clusterId clusterCustomId` clusterCustomId为指定的id，faderation节点必须相同，否则不会加入faderation集群

### 5.启动hdfs

`start-dfs.sh` 
