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

从NameNode获取授权token，可以查看[fetchdt]({% post_url 2016-07-20-18-hadoop-doc-translate %}#title14){:target="_blank"}



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
|path|开始从该路径检查|
|-delete|删除损坏的问价|
|-files|打印出被检查的文件|
|-files -blocks|打印出块报告|
|-files -blocks -locations|打印出每个块的位置|
|-files -blocks -racks|打印出DataNode位置的网络拓扑|
|-includeSnapshots|如果给定的路径指明了快照目录或在快照目录下，则包含快照数据|
|-list-corruptfileblocks|打印出丢失的块的列表和他们包含的文件。|
|-move|将损坏的文件移到/lost+found|
|-openforwrite|打印打开的文件|
|-storagepolicies|打印出块的存储策略汇总|
|-blockId|打印出块的信息|

运行HDFS文件系统检查功能。更多信息请查看[fsck]({% post_url 2016-07-20-18-hadoop-doc-translate %}#title13){:target="_blank"}


### getconf

**用法:**

```bash
   hdfs getconf -namenodes
   hdfs getconf -secondaryNameNodes
   hdfs getconf -backupNodes
   hdfs getconf -includeFile
   hdfs getconf -excludeFile
   hdfs getconf -nnRpcAddresses
   hdfs getconf -confKey [key]
```

|命令选项|描述|
|---|---|
|-namenodes	|获取集群的NameNode列表|
|-secondaryNameNodes|获取集群的secondaryNameNode列表|
|-backupNodes|获取集群备份节点列表|
|-includeFile|获取定义可以加入集群的DataNode包含的文件路径|
|-excludeFile|获取定义可以加入集群的DataNode排除的文件路径|
|-nnRpcAddresses|获取NameNode的RPC地址|
|-confKey [key]|从配置文件出获取指定的key|

从配置目录，后处理程序中获取配置信息

### groups

**用法:**`hdfs groups [username ...]`

返回给定的一个或多个用户的组信息


### lsSnapshottableDir

**用法:**`hdfs lsSnapshottableDir [-help]`

|命令选项|描述|
|---|---|
|-help|显示帮助信息|

获取snapshottable目录的列表，在以超级用户运行时，会返回所有snapshottable 的目录，否则返回你那些属于当前用户的目录。


### jmxget

**用法:**`hdfs jmxget [-localVM ConnectorURL | -port port | -server mbeanserver | -service service]`

|命令选项|描述|
|---|---|
|-help|显示帮助信息|
|-localVM ConnectorURL|在同一机器上连接VM|
|-port mbean server port|指定mbean的服务端口，如果没有指定，会尝试连接同一VM的MBean服务|
|-service|默认指定JMS服务，DataNode或NameNode|

丢弃来自于某个服务的JMX信息

### oev



**用法:**`hdfs oev [OPTIONS] -i INPUT_FILE -o OUTPUT_FILE`

**必选的命令行参数**

|命令选项|描述|
|---|---|
|-i,--inputFile arg|要处理的edits文件，扩展名为xml意味着是xml格式，任何其他的文件名都意味着是二进制格式|
|-o,--outputFile arg|输出文件的名称。如果指定文件存在，会被覆盖。格式文件可以使用`-p`选项定义。|

**可选的命令行参数**

|命令选项|描述|
|---|---|
|-f,--fix-txids|在输入时重新编号传输的id，这样就没有缺失或非法的传输id了|
|-h,--help|显示用法信息或退出|
|-r,--ecover|当读取二进制的edits日志时，使用恢复模式。这将让你选择跳过edits日志的损坏的部分。|
|-p,--processor arg|选择适合image文件的处理类型，当前支持的是二进制（Hadoop用户的原生二进制格式）、xml（默认，xml格式）和stats（显示edits文件的统计信息）|
|-v,--verbose|更多冗长的输出，打印输入和输出文件名。对于写文件的处理器，也显示在屏幕上。在巨大的image文件时该操作将会增加处理的时间，默认为false|

Hadoop离线edits查看器。

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



