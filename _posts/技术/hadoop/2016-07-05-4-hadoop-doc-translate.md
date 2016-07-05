---
layout: post
title: 4.hadoop-2.7.2官网文档翻译-Hadoop命令向导
category: 技术
tags:  Hadoop
keywords: 
description: 
---

{:toc}

## 概要

所有的Hadoop命令都会调用`bin/hadoop`脚本，运行该脚本不带任何参数，将会显示所有命令的描述。

**用法：**hadoop [--config confdir] [--loglevel loglevel] [COMMAND] [GENERIC_OPTIONS] [COMMAND_OPTIONS]

---

|区域|描述|
|--|--|
|--config confdir|覆盖掉默认的配置文件目录。默认是`${HADOOP_HOME}/conf`。|
|--loglevel loglevel|覆盖日志显示水平，合法日志水平为`FATAL`,`ERROR`,`WARN`,`INFO`,`DEBUG`,`TRACE`,默认为`INFO`。|
|GENERIC_OPTIONS|多个命令支持的常用选项集。|
|COMMAND_OPTIONS|在该文档中描述的Hadoop common子项目的操作的多样命令。HDFS和YARN在其他文档中。|

---

### 通用操作

许多子命令提供了通用的配置参数集合来改变他们的行为

---

|通用操作|描述|
|--|--|
|-archives <comma separated list of archives>|执行计算机上用逗号分隔的文档列表，仅适用于job|
|-conf <configuration file>|指定应用的配置文件|
|-D <property>=<value>|使用给定属性的设置值|
|-files <comma separated list of files>|指定要被复制到MapReduce集群的文档列表(由逗号分隔)，仅适用于job|
|-jt <local> or <resourcemanager:port>|指定ResourceManager，仅适用于job|
|-libjars <comma seperated list of jars>|指定添加进classpath的jar文件集合(由逗号分隔)，仅适用于job|

### Hadoop通用命令

所有这些命令从`hadoop`shell中执行。已经被分解为普通用户命令和管理员命令

#### 普通用户命令

该命令集合对Hadoop集群的用户很有用

-	archive

	创建一个Hadoop archive，更多信息可以查看[Hadoop文档向导](sdfds)

- checknative

**用法：**hadoop checknative [-a] [-h]

---


	
|命令操作| 描述|
|----|-----|
|-a|检查所有的库都可用，默认只检查Hadoop的库(window上还检查winutils.exe)|
|-h|显示该帮助信息|

---
	
该命令会检查Hadoop的原生代码。查看[NativeLibraries.html](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/CommandsManual.html#NativeLibraries.html)获取更多信息。

- classpath

**用法：**hadoop classpath [--glob |--jar <path> |-h |--help]

---

|命令操作| 描述|
|----|-----|
|--glob|通配符|
|--jar path|path处写类路径|
|-h, --help|显示该帮助信息|

---
	
显示class path需要获取到Hadoop的jar和需要的库。如果无参调用，会打印命令脚本设置的classpath，可能会在classpath条目中包含通配符。附加选项打印classpath后通配符扩展或将classpath写入一个jar文件的manifest中。
后者在不能用通配符和扩展路径超过命令行最大长度限制的环境中是很有用的。
	
- credential

**用法：**hadoop credential <subcommand> [options]

---

|命令操作| 描述|
|----|-----|
|create alias [-provider provider-path]|提示用户要将凭据存储为给定的别名。在指明`-provider`情况下，`core-site.xml`文件中的`hadoop.security.credential.provider.path`将会被用到|
|delete alias [-provider provider-path] [-f]|用所提供的别名删除凭据。在指明`-provider`情况下，`core-site.xml`文件中的`hadoop.security.credential.provider.path`将会被用到。不使用`-f`,将会显示确认信息。|
|list [-provider provider-path]|所有凭证别名的列表。在指明`-provider`情况下，`core-site.xml`文件中的`hadoop.security.credential.provider.path`将会被用到|

---
	
管理凭证提供者的凭证，密码和秘钥的命令。

该凭证提供者的API接口在Hadoop中允许应用分离和他们怎样存储他们需要的密码和秘钥。为了表明特定的提供者类型和位置，用户必须在`core-site.xml`中提供`hadoop.security.credential.provider.path`配置元素或者在每次执行命令时使用命令参数选项` -provider`。
此提供程序路径是一个逗号分隔的目录列表，该列表指示应该被咨询的供应商列表的类型和位置。
比如下面路径示例：

```
user:///,jceks://file/tmp/test.jceks,jceks://hdfs@nn1.example.com/my/path/test.jceks
```