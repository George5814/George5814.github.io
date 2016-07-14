---
layout: post
title: 13.hadoop-2.7.2官网文档翻译-安全模式中的Hadoop
category: 技术
tags:  Hadoop
keywords: 
description: 安全模式中的Hadoop。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SecureMode.html
---

{:toc}

### 介绍

该文档描述了怎样配置Hadoop安全模式的认证。

默认情况下，Hadoop运行在非安全模式，实际上不需要认证。通过配置Hadoop运行在安全模式。为了使用Hadoop服务，每个用户和服务都需要Kerberos的认证。

Hadoop的安全特性包括：
[认证](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SecureMode.html#Authentication){:target="_blank"}
[服务等级认证](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/ServiceLevelAuth.html){:target="_blank"}
[web控制台认证](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/HttpAuthentication.html){:target="_blank"}
[数据保密](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SecureMode.html#Data_confidentiality){:target="_blank"}


### 认证

#### 最终用户账户

当服务等级认证开始时，使用处于安全模式的Hadoop的最终用户需要被Kerberos认证。最简单认证的方式使用Kerberos的命令`kinit`。

#### Hadoop守护进程的用户账户

确保HDFS和YARN进程运行在不同的*nix用户，比如`hdfs`和`yarn`。确保MapReduce JobHistory server运行不同的用户如`mapred`。

推荐将他们放在同一个*nix组，比如，`hadoop`。群组管理请看[从用户到组的映射](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SecureMode.html#Mapping_from_user_to_group){:target="_blank"}

|用户:组|守护进程|
|--|--|
|hdfs:hadoop|NameNode，Secondary NameNode，JournalNameNode，DataNode|
|yarn:hadoop|ResourceManager，NodeManager|
|mapred:hadoop|MapReduce JobHistory server|


### Hadoop守护进程和用户的Kerberos负责人

为了在Hadoop安全模式中运行Hadoop服务守护进程，Kerberos负责人是必须的，每个服务都会根据适当的权限读取保存在keytab文件内的认证信息

HTTP的web控制台应该被另一个主要的RPC提供服务。

下面会展示Hadoop服务认证的例子

### HDFS 

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

### YARN 

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


### MapReduce JobHistory server

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

#### kerberos负责人到系统用户账户的映射

Hadoop使用`hadoop.security.auth_to_local`（与[kerberos配置文件（krb5.conf）](http://web.mit.edu/Kerberos/krb5-latest/doc/admin/conf_files/krb5_conf.html){:target="_blank"}的`auth_to_local`一样的方式）指定的规则将kerberos负责人映射到系统用户账户。
此外，Hadoop的`auth_to_local`映射支持`/L`标志，那小写字母返回的名字。

默认情况下，如果realm匹配到`default_realm`(通常定义在`/etc/krb5.conf`),会选负责人名字的第一部分作为用户名，
例如，默认规则是`host/full.qualified.domain.name@REALM.TLD`被映射到`host`。

自定义规则可以使用` hadoop kerbname`命令测试。该命令允许你指定一个负责人，并支持Hadoop当前`auth_to_local`的规则集。


#### 将用户映射到组

尽管HDFS上文件被关联到所有人和组，Hadoop自己没有定义组。将用户映射到组是被OS或者LDAP完成的。

可以通过指定映射程序的名字作为`hadoop.security.group.mapping`的值来改变映射的方式。详情请看[HDFS权限向导](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsPermissionsGuide.html){:target="_blank"}

事实上，你需要在Hadoop安全模式中用LDAP使用kerberos管理SSO环境。

#### 代理用户

一些产品如`Apache Oozie`代表最终用户访问Hadoop的服务需要可以模仿最终用户才行。详情请看[代理用户文档]({% post_url 2016-07-14-11-hadoop-doc-translate %}){:target="_blank"}


#### 安全的DataNode

因为DataNode的数据传输协议没有使用Hadoop的RPC框架，DataNode会使用`dfs.datanode.address`和`dfs.datanode.http.address`指定的特有端口验证她自己。该验证基于假设攻击者不能获得root特权。’


当你作为root用户执行`hdfs datanode`命令，服务进程首先会绑定特权端口。然后带着特权，运行`HADOOP_SECURE_DN_USER`指定的用户账户。启动进程会使用`JSVC_HOME`处的jsvc。在启动时必须指定`HADOOP_SECURE_DN_USER`和`JSVC_HOME`作为环境变量(在`hadoop-env.sh`)。

跟2.6.0版本一样，SASL（Simple Authentication and Security Layer）用来验证数据传输协议。在该配置中，对于安全集群不再需要作为root用户使用ysvc和绑定特权端口来启动DataNode。为了在数据传输协议上启用SASL，设置`hdfs-site.xml`中的`dfs.data.transfer.protection`，为`dfs.datanode.address`设置非特权端口，将`dfs.http.policy`设置到HTTPS_ONLY。
并确保`HADOOP_SECURE_DN_USER`环境变量没有定义。

**注意：**如果`dfs.datanode.address`设置到特权端口，就不能讲SASL使用在数据传输协议上。这就是要向后兼容的原因。

为了将一个现有的集群从使用root认证迁移到开始使用SASL。首先确保集群的所有节点都是部署的2.6.0版本及以上，同时没有任何外部应用连接到集群。只有2.6.0及以后版本的HDFS客户端可以使用SASL作为数据传输协议的验证连接到DataNode。因此在迁移之前所有的调用者使用正确的版本是至关重要的。在部署了2.6.0或更新版本后，更新所有的外部应用配置来启用SASL。
如果HDFS客户端启用了SASL，那么就可以成功连接到不管是运行root验证还是SASL验证的DataNode上了。
保证所有客户端的配置**先**更改，**随后**DataNode上的配置更改将**不会使应用程序混乱**。
最后，每个单独的DataNode可以通过更改配置并重启来迁移。在迁移期间，暂时允许一部分DataNode运行root验证，一部分DataNode运行SASL验证的混合状态，因为HDFS客户端启用了SASL，两种方式都能连接。


### 数据保密性


#### RPC数据加密

Hadoop服务端和客户端间的数据传输。在`core-site.xml`中将`hadoop.rpc.protection`设置为`privacy`,可以激活数据加密。

#### 块数据传输上的数据加密

需要将`hdfs-site.xml`中的`dfs.encrypt.data.transfer`设置为`true`，为了激活DataNode上数据传输协议的数据加密。

作为可选项，你可以设置`dfs.encrypt.data.transfer.algorithm`为`3des`或`rc4`来选择指定的加密算法。如果没有指定，那么JCE默认配置系统上使用的，通常为`3DES`。

设置`dfs.encrypt.data.transfer.cipher.suites`为`AES/CTR/NoPadding`来激活AES加密。默认情况下，是没有指定的，因此AES没有被使用。当AES使用时，在`dfs.encrypt.data.transfer.algorithm`中指定的算法仍然在初始化key交换期间使用。AES 的key位长可以通过设置`dfs.encrypt.data.transfer.cipher.key.bitlength`为128,192或256进行配置。默认为128位。

AES提供最大的加密强度和最佳的性能。此时，3DES和RC4在Hadoop集群中更常用。

### HTTP上的数据加密

web控制台和客户端之间的数据传输受SSL保护。

**配置**

#### 允许HDFS和本地文件系统路径

下表中列举了HDFS和本地文件系统中多种路径和推荐的权限：

|文件系统|路径|用户:组|权限|
|--|--|--|--|
|local|dfs.namenode.name.dir|hdfs:hadoop|	drwx------|
|local|dfs.datanode.data.dir|hdfs:hadoop|	drwx------|
|local|	$HADOOP_LOG_DIR|hdfs:hadoop|drwxrwxr-x|
|local|	$YARN_LOG_DIR|yarn:hadoop|drwxrwxr-x|
|local|yarn.nodemanager.local-dirs|yarn:hadoop|drwxr-xr-x|
|local|	yarn.nodemanager.log-dirs|yarn:hadoop|drwxr-xr-x|
|local|container-executor|root:hadoop|--Sr-s--*|
|local|conf/container-executor.cfg|root:hadoop|r-------*|
|hdfs|/|hdfs:hadoop|drwxr-xr-x|
|hdfs|/tmp|hdfs:hadoop|drwxrwxrwxt|
|hdfs|/user|hdfs:hadoop|drwxr-xr-x|
|hdfs|	yarn.nodemanager.remote-app-log-dir|yarn:hadoop|drwxrwxrwxt|
|hdfs|mapreduce.jobhistory.intermediate-done-dir|	mapred:hadoop|drwxrwxrwxt|
|hdfs|mapreduce.jobhistory.done-dir|	mapred:hadoop|	drwxr-x---|


#### 通用配置

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

#### NameNode

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

####  Secondary NameNode

|参数|值|备注|
|--|--|--|
|dfs.namenode.secondary.http-address|	c_nn_host_fqdn:50090||
|dfs.namenode.secondary.https-port|50470||
|dfs.secondary.namenode.keytab.file|/etc/security/keytab/sn.service.keytab	|Secondary  NameNode的kerberos的keytab文件|
|dfs.secondary.namenode.kerberos.principal|sn/_HOST@REALM.TLD|Secondary  NameNode的kerberos的负责人名称|
|dfs.secondary.namenode.kerberos.internal.spnego.principal|HTTP/_HOST@REALM.TLD|Secondary NameNode的HTTP kerberos的负责人名称|



