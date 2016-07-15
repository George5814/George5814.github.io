---
layout: post
title: 13.hadoop-2.7.2官网文档翻译-安全模式中的Hadoop
category: 技术
tags:  Hadoop
keywords: 
description: 安全模式中的Hadoop。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SecureMode.html
---

{:toc}

## 介绍

该文档描述了怎样配置Hadoop安全模式的认证。

默认情况下，Hadoop运行在非安全模式，实际上不需要认证。通过配置Hadoop运行在安全模式。为了使用Hadoop服务，每个用户和服务都需要Kerberos的认证。

Hadoop的安全特性包括：
[认证](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SecureMode.html#Authentication){:target="_blank"}
[服务等级认证](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/ServiceLevelAuth.html){:target="_blank"}
[web控制台认证](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/HttpAuthentication.html){:target="_blank"}
[数据保密](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SecureMode.html#Data_confidentiality){:target="_blank"}


## 认证

### 最终用户账户

当服务等级认证开始时，使用处于安全模式的Hadoop的最终用户需要被Kerberos认证。最简单认证的方式使用Kerberos的命令`kinit`。

### Hadoop守护进程的用户账户

确保HDFS和YARN进程运行在不同的*nix用户，比如`hdfs`和`yarn`。确保MapReduce JobHistory server运行不同的用户如`mapred`。

推荐将他们放在同一个*nix组，比如，`hadoop`。群组管理请看[从用户到组的映射](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SecureMode.html#Mapping_from_user_to_group){:target="_blank"}

|用户:组|守护进程|
|--|--|
|hdfs:hadoop|NameNode，Secondary NameNode，JournalNameNode，DataNode|
|yarn:hadoop|ResourceManager，NodeManager|
|mapred:hadoop|MapReduce JobHistory server|


## Hadoop守护进程和用户的Kerberos负责人

为了在Hadoop安全模式中运行Hadoop服务守护进程，Kerberos负责人是必须的，每个服务都会根据适当的权限读取保存在keytab文件内的认证信息

HTTP的web控制台应该被另一个主要的RPC提供服务。

下面会展示Hadoop服务认证的例子

## HDFS 

NameNode主机上的NameNode keytab文件应该如下这样：

```bash
$ klist -e -k -t /etc/security/keytab/nn.service.keytab
Keytab name: FILE:/etc/security/keytab/nn.service.keytab
KVNO Timestamp         Principal
   4 07/18/11 21:08:09 nn/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 nn/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 nn/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
```


Secondary NameNode主机上的Secondary NameNode keytab文件应该如下这样：

```
$ klist -e -k -t /etc/security/keytab/sn.service.keytab
Keytab name: FILE:/etc/security/keytab/sn.service.keytab
KVNO Timestamp         Principal
   4 07/18/11 21:08:09 sn/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 sn/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 sn/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
```


每台DataNode主机上的DataNode keytab文件应该如下这样：

```
$ klist -e -k -t /etc/security/keytab/dn.service.keytab
Keytab name: FILE:/etc/security/keytab/dn.service.keytab
KVNO Timestamp         Principal
   4 07/18/11 21:08:09 dn/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 dn/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 dn/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
```

## YARN 

ResourceManager主机上的ResourceManager  keytab文件应该如下这样：

```
$ klist -e -k -t /etc/security/keytab/rm.service.keytab
Keytab name: FILE:/etc/security/keytab/rm.service.keytab
KVNO Timestamp         Principal
   4 07/18/11 21:08:09 rm/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 rm/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 rm/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
```

NodeManager 主机上的NodeManager  keytab文件应该如下这样：

```
$ klist -e -k -t /etc/security/keytab/nm.service.keytab
Keytab name: FILE:/etc/security/keytab/nm.service.keytab
KVNO Timestamp         Principal
   4 07/18/11 21:08:09 nm/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 nm/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 nm/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)

```


## MapReduce JobHistory server

MapReduce JobHistory Server 主机上的MapReduce JobHistory Server  keytab文件应该如下这样：

```
$ klist -e -k -t /etc/security/keytab/jhs.service.keytab
Keytab name: FILE:/etc/security/keytab/jhs.service.keytab
KVNO Timestamp         Principal
   4 07/18/11 21:08:09 jhs/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 jhs/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 jhs/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-256 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (AES-128 CTS mode with 96-bit SHA-1 HMAC)
   4 07/18/11 21:08:09 host/full.qualified.domain.name@REALM.TLD (ArcFour with HMAC/md5)
```

### kerberos负责人到系统用户账户的映射

Hadoop使用`hadoop.security.auth_to_local`（与[kerberos配置文件（krb5.conf）](http://web.mit.edu/Kerberos/krb5-latest/doc/admin/conf_files/krb5_conf.html){:target="_blank"}的`auth_to_local`一样的方式）指定的规则将kerberos负责人映射到系统用户账户。
此外，Hadoop的`auth_to_local`映射支持`/L`标志，那小写字母返回的名字。

默认情况下，如果realm匹配到`default_realm`(通常定义在`/etc/krb5.conf`),会选负责人名字的第一部分作为用户名，
例如，默认规则是`host/full.qualified.domain.name@REALM.TLD`被映射到`host`。

自定义规则可以使用` hadoop kerbname`命令测试。该命令允许你指定一个负责人，并支持Hadoop当前`auth_to_local`的规则集。


### 将用户映射到组

尽管HDFS上文件被关联到所有人和组，Hadoop自己没有定义组。将用户映射到组是被OS或者LDAP完成的。

可以通过指定映射程序的名字作为`hadoop.security.group.mapping`的值来改变映射的方式。详情请看[HDFS权限指南](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsPermissionsGuide.html){:target="_blank"}

事实上，你需要在Hadoop安全模式中用LDAP使用kerberos管理SSO环境。

### 代理用户

一些产品如`Apache Oozie`代表最终用户访问Hadoop的服务需要可以模仿最终用户才行。详情请看[代理用户文档]({% post_url 2016-07-14-11-hadoop-doc-translate %}){:target="_blank"}


### 安全的DataNode

因为DataNode的数据传输协议没有使用Hadoop的RPC框架，DataNode会使用`dfs.datanode.address`和`dfs.datanode.http.address`指定的特有端口验证她自己。该验证基于假设攻击者不能获得root特权。’


当你作为root用户执行`hdfs datanode`命令，服务进程首先会绑定特权端口。然后带着特权，运行`HADOOP_SECURE_DN_USER`指定的用户账户。启动进程会使用`JSVC_HOME`处的jsvc。在启动时必须指定`HADOOP_SECURE_DN_USER`和`JSVC_HOME`作为环境变量(在`hadoop-env.sh`)。

跟2.6.0版本一样，SASL（Simple Authentication and Security Layer）用来验证数据传输协议。在该配置中，对于安全集群不再需要作为root用户使用ysvc和绑定特权端口来启动DataNode。为了在数据传输协议上启用SASL，设置`hdfs-site.xml`中的`dfs.data.transfer.protection`，为`dfs.datanode.address`设置非特权端口，将`dfs.http.policy`设置到HTTPS_ONLY。
并确保`HADOOP_SECURE_DN_USER`环境变量没有定义。

**注意：**如果`dfs.datanode.address`设置到特权端口，就不能讲SASL使用在数据传输协议上。这就是要向后兼容的原因。

为了将一个现有的集群从使用root认证迁移到开始使用SASL。首先确保集群的所有节点都是部署的2.6.0版本及以上，同时没有任何外部应用连接到集群。只有2.6.0及以后版本的HDFS客户端可以使用SASL作为数据传输协议的验证连接到DataNode。因此在迁移之前所有的调用者使用正确的版本是至关重要的。在部署了2.6.0或更新版本后，更新所有的外部应用配置来启用SASL。
如果HDFS客户端启用了SASL，那么就可以成功连接到不管是运行root验证还是SASL验证的DataNode上了。
保证所有客户端的配置**先**更改，**随后**DataNode上的配置更改将**不会使应用程序混乱**。
最后，每个单独的DataNode可以通过更改配置并重启来迁移。在迁移期间，暂时允许一部分DataNode运行root验证，一部分DataNode运行SASL验证的混合状态，因为HDFS客户端启用了SASL，两种方式都能连接。


## 数据保密性


### RPC数据加密

Hadoop服务端和客户端间的数据传输。在`core-site.xml`中将`hadoop.rpc.protection`设置为`privacy`,可以激活数据加密。

### 块数据传输上的数据加密

需要将`hdfs-site.xml`中的`dfs.encrypt.data.transfer`设置为`true`，为了激活DataNode上数据传输协议的数据加密。

作为可选项，你可以设置`dfs.encrypt.data.transfer.algorithm`为`3des`或`rc4`来选择指定的加密算法。如果没有指定，那么JCE默认配置系统上使用的，通常为`3DES`。

设置`dfs.encrypt.data.transfer.cipher.suites`为`AES/CTR/NoPadding`来激活AES加密。默认情况下，是没有指定的，因此AES没有被使用。当AES使用时，在`dfs.encrypt.data.transfer.algorithm`中指定的算法仍然在初始化key交换期间使用。AES 的key位长可以通过设置`dfs.encrypt.data.transfer.cipher.key.bitlength`为128,192或256进行配置。默认为128位。

AES提供最大的加密强度和最佳的性能。此时，3DES和RC4在Hadoop集群中更常用。

## HTTP上的数据加密

web控制台和客户端之间的数据传输受SSL保护。

**配置**

### 允许HDFS和本地文件系统路径

下表中列举了HDFS和本地文件系统中多种路径和推荐的权限：

|文件系统|路径|用户:组|权限|
|--|--|--|--|
|local|dfs.namenode.name.dir|hdfs:hadoop|	`drwx------`|
|local|dfs.datanode.data.dir|hdfs:hadoop|	`drwx------`|
|local|	$HADOOP_LOG_DIR|hdfs:hadoop|`drwxrwxr-x`|
|local|	$YARN_LOG_DIR|yarn:hadoop|`drwxrwxr-x`|
|local|yarn.nodemanager.local-dirs|yarn:hadoop|`drwxr-xr-x`|
|local|	yarn.nodemanager.log-dirs|yarn:hadoop|`drwxr-xr-x`|
|local|container-executor|root:hadoop|`--Sr-s--*`|
|local|conf/container-executor.cfg|root:hadoop|`r-------*`|
|hdfs|/|hdfs:hadoop|`drwxr-xr-x`|
|hdfs|/tmp|hdfs:hadoop|`drwxrwxrwxt`|
|hdfs|/user|hdfs:hadoop|`drwxr-xr-x`|
|hdfs|	yarn.nodemanager.remote-app-log-dir|yarn:hadoop|`drwxrwxrwxt`|
|hdfs|mapreduce.jobhistory.intermediate-done-dir|	mapred:hadoop|`drwxrwxrwxt`|
|hdfs|mapreduce.jobhistory.done-dir|	mapred:hadoop|	`drwxr-x---`|


### 通用配置

为了启用Hadoop上的RPC验证，将`hadoop.security.authentication`属性值设置为`kerberos`,并设置适当的下面列出的安全相关设置

下面的属性应该设置在集群所有节点的`core-site.xml`中。

|参数|值|备注|
|--|--|--|
|hadoop.security.authentication|kerberos|默认没有验证，`kerberos`启用kerberos验证|
|hadoop.security.authorization|true|启用[RPC服务等级验证](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/ServiceLevelAuth.html){:target="_blank"}|
|hadoop.rpc.protection|authentication|`authentication`：只验证(默认)；`integrity`:除了authentication的完整性检查；`privacy`:除了integrity的数据加密|
|hadoop.security.auth_to_local|RULE:exp1 RULE:exp2 … DEFAULT|该值是包含新行字符的字符串，正则的格式请看[kerberos文档](http://web.mit.edu/Kerberos/krb5-latest/doc/admin/conf_files/krb5_conf.html){:target="_blank"}|
|hadoop.proxyuser.superuser.hosts|host1,host2|逗号分隔的主机，允许超级用户扮演用户的来源主机;`*`意味着通配所有主机。|
|hadoop.proxyuser.superuser.groups|group1,group2|被超级用户扮演的用户所属的群组，群组列表由逗号分隔。`*`意味着通配所有群组。|

---

### NameNode

|参数|值|备注|
|--|--|--|
|dfs.block.access.token.enable|true|启用HDFS块访问token来保证安全操作|
|dfs.https.enable|true|已经过期,使用`dfs.http.policy`|
|dfs.http.policy|`HTTP_ONLY` or `HTTPS_ONLY` or `HTTP_AND_HTTPS`|`HTTPS_ONLY`:关闭http访问；该操作优先于`dfs.https.enable`和`hadoop.ssl.enabled`。如果使用SASL来验证数据传输协议替换DataNode上的root，并使用特权端口，那么该属性必须设置为`HTTPS_ONLY`来保证HTTP 服务器的验证。请看`dfs.data.transfer.protection`|
|dfs.namenode.https-address|nn_host_fqdn:50470||
|dfs.https.port|50470||
|dfs.namenode.keytab.file|/etc/security/keytab/nn.service.keytab|NameNode的kerberos的keytab文件|
|dfs.namenode.kerberos.principal|nn/_HOST@REALM.TLD|NameNode的kerberos的负责人名称|
|dfs.namenode.kerberos.internal.spnego.principal|HTTP/_HOST@REALM.TLD|NameNode的HTTP kerberos的负责人名称|

---

###  Secondary NameNode

|参数|值|备注|
|--|--|--|
|dfs.namenode.secondary.http-address|	c_nn_host_fqdn:50090||
|dfs.namenode.secondary.https-port|50470||
|dfs.secondary.namenode.keytab.file|/etc/security/keytab/sn.service.keytab	|Secondary  NameNode的kerberos的keytab文件|
|dfs.secondary.namenode.kerberos.principal|sn/_HOST@REALM.TLD|Secondary  NameNode的kerberos的负责人名称|
|dfs.secondary.namenode.kerberos.internal.spnego.principal|HTTP/_HOST@REALM.TLD|Secondary NameNode的HTTP kerberos的负责人名称|


---

### DataNode

|参数|值|备注|
|--|--|--|
|dfs.datanode.data.dir.perm|700||
|dfs.datanode.address|0.0.0.0:1004|为了确保已经启动服务器的安全，安全DataNode必须使用特权端口。这意味着服务器必须通过JSVC启动。或者，如果使用了SASL验证数据传输协议，必须设置为非特权端口。请看`dfs.data.transfer.protection`|
|dfs.datanode.http.address|0.0.0.0:1006|为了确保启动的服务器的安全，安全DataNode必须使用特权端口。这意味着服务器必须通过JSVC启动。|
|dfs.datanode.https.address|0.0.0.0:50470||
|dfs.datanode.keytab.file|/etc/security/keytab/dn.service.keytab|DataNode上的kerberos 的keytab文件|
|dfs.datanode.kerberos.principal|dn/_HOST@REALM.TLD|DataNode上的kerberos的负责人名称|
|dfs.encrypt.data.transfer|false|如果使用数据加密设置为'true'|
|dfs.encrypt.data.transfer.algorithm||如果使用数据加密的算法可选`3des`或`rc4`|
|dfs.encrypt.data.transfer.cipher.suites||如果使用AES加密可选`AES/CTR/NoPadding`|
|dfs.encrypt.data.transfer.cipher.key.bitlength||使用AES加密数据的key位长度，可选128, 192或 256 |
|dfs.data.transfer.protection||`authentication`：只验证;`integrity`:除了验证外的完成性检查；`privacy`:除了完整性检查外的数据加密，该属性为默认;设置该值启用SASL。如果启动了,`dfs.datanode.addres` 必须设置为非特权端口，`dfs.http.policy`必须设置为`HTTPS_ONLY`,`HADOOP_SECURE_DN_USER`环境变量不能设置|


---

### WebHDFS

|参数|值|备注|
|--|--|--|
|dfs.web.authentication.kerberos.principal|http/_HOST@REALM.TLD|WebHDFS上的kerberos负责人名称|
|dfs.web.authentication.kerberos.keytab|/etc/security/keytab/http.service.keytab|WebHDFS上的kerberos 的keytab文件|


---

### ResourceManager

|参数|值|备注|
|--|--|--|
|dfs.resourcemanager.keytab|/etc/security/keytab/rm.service.keytab|ResourceManager上的kerberos 的keytab文件|
|dfs.resourcemanager.principal|rm/_HOST@REALM.TLD|ResourceManager上的kerberos负责人名称|

--- 

### NodeManager

|参数|值|备注|
|--|--|--|
|yarn.nodemanager.keytab|/etc/security/keytab/nm.service.keytab|NodeManager上的kerberos 的keytab文件|
|yarn.nodemanager.principal|nm/_HOST@REALM.TLD|NodeManager上的kerberos负责人名称|
|yarn.nodemanager.container-executor.class|org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor|使用LinuxContainerExecutor|
|yarn.nodemanager.linux-container-executor.group|hadoop|NodeManager的Unix组|
|yarn.nodemanager.linux-container-executor.path|/path/to/bin/container-executor|Linux Container Executor的执行路径|

---


### Configuration for WebAppProxy

`WebAppProxy`提供了应用程序暴露的web应用和最终用户间的代理。如果启用了安全模式，在用户进入潜在的不安全的web应用时会有提醒。使用代理的认证和授权被其他的特定web应用管理。

|参数|值|备注|
|--|--|--|
|yarn.web-proxy.address|WebAppProxy host:port for proxy to AM web apps.|host：port;如果与`yarn.resourcemanager.webapp.address`一样，或者没有定义，`ResourceManager`会运行代理。否则需要启动单独的代理服务器。|
|yarn.web-proxy.keytab|/etc/security/keytab/web-app.service.keytab|WebAppProxy的kerberos的keytab的文件|
|yarn.web-proxy.principal|wap/_HOST@REALM.TLD|WebAppProxy的kerberos的负责人名称|

---

### LinuxContainerExecutor

`ContainerExecutor`被YARN框架使用来定义任何容器的启动和控制。

下面是YARN可获得Executor：

|ContainerExecutor|描述|
|--|--|
|DefaultContainerExecutor|Yarn用来管理容器执行的默认执行器。容器进行与NodeManager拥有同样的unix用户|
|LinuxContainerExecutor|仅支持Linux，该执行器运行容器作为yarn用户提交程序(完全安全模式)或者作为一个专用的用户(默认无)没有启用安全性。当全安全启动，该执行器需要所有用户账户在容器启动的集群节点上创建。它使用`setuid`执行，包括在Hadoop发布版本中。NodeManager使用这个可以启动和杀死容器。`setuid`执行切换到提交到应用的用户并启动或杀死容器。为了获得最大的安全性，该执行器设置了严格的权限、容器使用的本地文件和目录的用户/组关系，如共享对象，jars，中间文件，日志文件等。需要注意的是，除了应用所有者和NodeManager，因为这，没有其他用户可以访问任何本地文件和目录，包括哪些作为分布式缓存的部分。|

为了构建`LinuxContainerExecutor`可执行，运行：

```
 $ mvn package -Dcontainer-executor.conf.dir=/etc/hadoop/
```

` -Dcontainer-executor.conf.dir`内的路径应该是`setuid`执行配置文件所在的集群节点的路径，该执行文件应该安装在` $HADOOP_YARN_HOME/bin`。

该执行文件必须有指定权限6050 或者`--Sr-s---`，用户为root用户，组为NodeManager的unix用户所在的没有普通应用用户的指定的组，比如hadoop。如果任何应用用户属于该指定组，安全将会被损坏。该指定组的名字应该在`conf/yarn-site.xml`和`conf/container-executor.cfg`的配置属性`yarn.nodemanager.linux-container-executor.group`中指定。

比如，NodeManager以用户`yarn`的身份运行，该用户是hadoop组的用户，并属于主组。用户`yarn`和其他用户`alice`(应用提交这)都是它的成员，alice不属于hadoop组。如上描述`setuid/getuid`执行应该被设置为6050或`--Sr-s---`，用户为yarn，组为hadoop，而且yarn为hadoop的组成员（alice不是hadoop成员）

`LinuxTaskController`需要路径包含和引导到`yarn.nodemanager.local-dirs`和`yarn.nodemanager.log-dirs`指定的目录在目录的权限表`conf/container-executor.cfg`中设置755权限。


该执行文件需要名为`container-executor.cfg`的配置文件通过mvn在配置目录提取。

配置文件需要被运行NodeManager的用户所拥有（用户yarn），组属于任何人，而且权限位0400或`r--------`。

执行文件需要从`conf/container-executor.cfg`文件提取出来的如下配置条目。如下条目应该以简单的k-v形式存在，每行一个。

|参数|值|备注|
|--|--|--|
|yarn.nodemanager.linux-container-executor.group|hadoop|NodeManager的unix用户组。`container-executor`bin的拥有者应该属于该组。应该与NodeManager被配置的值形同。该配置需要确认	`container-executor`的bin的安全访问。|
|banned.users|hdfs,yarn,mapred,bin|被禁止的用户|
|allowed.system.users|foo.bar|允许的系统用户|
|min.user.id|1000|被禁止的其他用户（小于该值的超级用户）|

这是本地文件系统权限的linuxcontainerexecutor相关的各种路径的要求：

|文件系统|路径|用户：组|权限|
|--|--|--|--|
|local|container-executor|root:hadoop|`--Sr-s--*`|
|local|conf/container-executor.cfg|root:hadoop|`r-------*`|
|local|yarn.nodemanager.local-dirs|yarn:hadoop|`drwxr-xr-x`|
|local|yarn.nodemanager.log-dirs|yarn:hadoop|`drwxr-xr-x`|

---

### MapReduce JobHistory Server

|参数|值|备注|
|--|--|--|
|mapreduce.jobhistory.addres|MapReduce JobHistory Server host:port|默认端口时10020|
|mapreduce.jobhistory.keytab|/etc/security/keytab/jhs.service.keytab|MapReduce JobHistory Server的kerberos的keytab文件|
|mapreduce.jobhistory.principal|jhs/_HOST@REALM.TLD|MapReduce JobHistory Server的kerberos的负责人名称|














