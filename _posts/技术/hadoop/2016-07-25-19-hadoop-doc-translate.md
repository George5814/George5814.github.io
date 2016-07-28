---
layout: post
title: 19.hadoop-2.7.2官网文档翻译-HDFS命令指南
category: 技术
tags:  Hadoop
keywords: 
description: HDFS命令指南。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html
---

{:toc}

## 概览

所有的HDFS命令都是调用`bin/hdfs`脚本。无参数调用hdfs脚本会打印所有命令的描述。

用法:`hdfs [SHELL_OPTIONS] COMMAND [GENERIC_OPTIONS] [COMMAND_OPTIONS]`

Hadoop有一个选项的解析框架，采用解析通用选项以及运行类。

|命令选项|描述|
|---|---|
|--config  --loglevel|shell选项的命令集，该文档在[命令手册](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/CommandsManual.html#Overview){:target="_blank"}页|
|GENERIC_OPTIONS|支持多个命令选项的通用集，请看[命令手册](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/CommandsManual.html#Overview){:target="_blank"}页|
|COMMAND COMMAND_OPTIONS|在下面的几段中会有各种命令和其选项的描述。命令被分组为用户命令和管理员命令|


## 用户命令

Hadoop集群的用户使用的命令

### classpath 

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



### dfs 

**用法:**`hdfs dfs [COMMAND [COMMAND_OPTIONS]]`

在Hadoop支持的文件系统上运行文件系统命令，许多`COMMAND_OPTIONS`可以在[文件系统shell指南]({% post_url 2016-07-05-5-hadoop-doc-translate %}){:target="_blank"}中找到。


### fetchdt

**用法:**` hdfs fetchdt [--webservice <namenode_http_addr>] <path>`

|命令选项|描述|
|---|---|
|--webservice https_address|使用http协议替代RPC|
|fileName|存储token的文件名|

从NameNode获取授权token，可以查看[fetchdt]({% post_url 2016-07-20-18-hadoop-doc-translate %}#title13){:target="_blank"}



### fsck

**用法:**

```bash
hdfs fsck <path>
          [-list-corruptfileblocks |
          [-move | -delete | -openforwrite]
          [-files [-blocks [-locations | -racks]]]
          [-includeSnapshots]
          [-storagepolicies] [-blockId <blk_Id>]
```

|命令选项|描述|
|---|---|
|path||
|-delete||
|||
|||
|||
|||
|||
|||
|||
|||
|||
|||
|||


### classpath

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



### classpath

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



### classpath

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



### classpath

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



### classpath

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



### classpath

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



### classpath

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



### classpath

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



### classpath

**用法:**`hdfs classpath`

显示获取Hadoop的jar和需要的库的类路径



