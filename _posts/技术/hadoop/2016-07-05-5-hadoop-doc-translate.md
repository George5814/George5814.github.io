---
layout: post
title: 5.hadoop-2.7.2官网文档翻译-文件系统命令
category: 技术
tags:  Hadoop
keywords: 
description: 
---

{:toc}

## 概览

文件系统shell包括多种直接与HDFS交互的类shell命令以及其他文件系统，Hadoop的支持。就像本地文件系统，HFTP文件系统，S3文件系统等待。

文件系统shell的调用如下：

```
bin/hadoop fs <args>
```

所有的文件系统shell命令把URI路径作为参数。URI格式如`scheme://authority/path`。

HDFS文件系统的scheme是`hdfs`,本地文件系统的scheme是`file`。scheme 和authority是非强制性的。如果未指定，会使用配置中指定的默认的scheme。
HDFS文件或目录如`/parent/child`可以指定为	` hdfs://namenodehost/parent/child`或简单的`/parent/child`(在你的配置中设置指向`hdfs://namenodehost`).

文件系统中大部分的命令像对应的Unix命令。不同的是每个命令描述的差异。错误信息发送到`stderr`，普通输出发送到`stdout`.

在HDFS的使用中，`hdfs dfs`与`Hadoop fs`是同义词。可以看[命令手册]({% post_url 2016-07-05-4-hadoop-doc-translate %})

### appendToFile

**用法：**` hadoop fs -appendToFile <localsrc> ... <dst>`

追加本地文件系统中单个文件或者多个文件到目标文件系统(HDFS),也可以从`stdin`(标准输入)中读取并追加到目标文件系统中。

- 追加单个文件到hdfs

	`hadoop fs -appendToFile localfile /user/hadoop/hadoopfile`

- 追加多个本地文件到hdfs

	`hadoop fs -appendToFile localfile1 localfile2 /user/hadoop/hadoopfile`

- 追加本地文件到hdfs 

	`hadoop fs -appendToFile localfile hdfs://nn.example.com/hadoop/hadoopfile`

- 读取标准输入到hdfs 

	`hadoop fs -appendToFile - hdfs://nn.example.com/hadoop/hadoopfile`


**错误码：**

	成功：0
	失败：1
	

### cat

**用法：**`hadoop fs -cat URI [URI ...]`

复制原路径文件内容到标准输出`stdout`

**举例：**

- 查看多个hdfs上的文件

	`hadoop fs -cat hdfs://nn1.example.com/file1 hdfs://nn2.example.com/file2`
	
- 查看本地文件和hdfs上的文件

	`hadoop fs -cat file:///file3 /user/hadoop/file4`
	

**错误码：**

	成功：0
	失败：-1
	

### checksum

**用法：**`hadoop fs -checksum URI`

返回文件的checksum信息


**举例：**

- 查看hdfs上的文件校验和

	`hadoop fs -checksum hdfs://nn1.example.com/file1`
	
- 查看本地文件校验和

	`hadoop fs -checksum file:///etc/hosts`
	

### chgrp

**用法：**`hadoop fs -chgrp [-R] GROUP URI [URI ...]`

更改文件所属的组。用户必须是文件的所有者或者是超级用户。
附加信息在[权威指南](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsPermissionsGuide.html)中

**选项：**

- -R 

	该参数选项会递归目录进行更改

### chmod

**用法：**`hadoop fs -chmod [-R] <MODE[,MODE]... | OCTALMODE> URI [URI ...]`

更改文件的权限。用户必须是文件的所有者或者是超级用户。
附加信息在[权威指南](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsPermissionsGuide.html)中

**选项：**

- -R 

	该参数选项会递归目录进行更改

### chown

**用法：**` hadoop fs -chown [-R] [OWNER][:[GROUP]] URI [URI ]`

更改文件的所有人。用户必须是超级用户。
附加信息在[权威指南](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsPermissionsGuide.html)中

**选项：**

- -R 

	该参数选项会递归目录进行更改

### copyFromLocal

**用法：**` hadoop fs -copyFromLocal <localsrc> URI`

和`put`命令相似，但该参数的源文件地址仅限本地文件

**选项：**

- -f

	如果目标文件已经存在就会被强制覆盖

### copyToLocal

**用法：**` hadoop fs -copyToLocal [-ignorecrc] [-crc] URI <localdst>`

和`get`命令相似,但目标文件只能是本地文件

### count

**用法：**`hadoop fs -count [-q] [-h] [-v] <paths>`

记录目录和文件的数量。路径下匹配指定文件模式的大小。
 
`-count`:输出列依次为:目录数量，文件数量，内容大小，路径名称

`-count -q`:输出列依次为：配额，剩余配额，空间配额，剩余空间配额，目录数量，文件数量，内容大小，路径名称

`-h` :显示可读的大小


`-v` :显示header行（Hadoop2.7.2程序中没有改选项）


**举例：**

- 统计hdfs上的多个文件大小

	`hadoop fs -count hdfs://nn1.example.com/file1 hdfs://nn2.example.com/file2` 



- 统计hdfs上的文件大小及配额

	`hadoop fs -count -q hdfs://nn1.example.com/file1` 



- 统计hdfs上的文件大小和配额并以可读方式显示

	`hadoop fs -count -q -h hdfs://nn1.example.com/file1` 

- 统计hdfs上的文件大小和配额并以可读方式显示，并显示header行

	`hdfs dfs -count -q -h -v hdfs://nn1.example.com/file1` 


**错误码：**

	成功：0
	失败：-1
	

### cp

**用法：**` hadoop fs -cp [-f] [-p | -p[topax]] URI [URI ...] <dest>`

将文件从原位置复制到目标文职。该命令允许多个源，但这样的话目标必须是目录

`raw.*`命令空间扩展属性会被保存需要两个条件，一是源和目标文件系统支持它（仅HDFS）；二是所有源和目标路径名称在`/.reserved/raw`中是分层的。决定`raw.*`命令控件的扩展属性被保存是需要依赖于`-p`标识的。

**选项：**

- -f 

	该参数会覆盖掉已经存在的目标
	
- -p 

	该参数会保存文件的属性（timestamps, ownership, permission, ACL, XAttr）。
	如果`-p`指定为`no`参数。会保存`timestamps`,` ownership`,` permission`.
	如果指定`-pa`，然后也因为ACL权限超级组保留许可。
	决定`raw.*`命令控件的扩展属性被保存是需要依赖于`-p`标识的。

**举例：**

- 复制单个文件

	`hadoop fs -cp /user/hadoop/file1 /user/hadoop/file2`

- 复制多个文件到目录

	`hadoop fs -cp /user/hadoop/file1 /user/hadoop/file2 /user/hadoop/dir`

**错误码：**

	成功：0
	失败：-1
	

### createSnapshot 

请看[HDFS 快照向导](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsSnapshots.html)


### deleteSnapshot 

请看[HDFS 快照向导](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsSnapshots.html)

### df

**用法：**`hadoop fs -df [-h] URI [URI ...]`

显示空闲空间

**选项：**

- -h  

	格式化为可读数据
	
**举例：**

	`hadoop dfs -df /user/hadoop/dir1`

### du

**用法：**`hadoop fs -du [-s] [-h] URI [URI ...]`

显示给定目录内的文件和目录所占空间大小，或者是只有一个文件情况下文件的长度。

**选项：**

- -s 

	该参数会把将要显示的文件长度汇总产生而不是单独显示。
	
- -h 

	格式化为可读数据
	
	
**举例：**

`hadoop fs -du /user/hadoop/dir1 /user/hadoop/file1 hdfs://nn.example.com/user/hadoop/dir1`


### dus

显示文件长度的概览。`已经过期`，由`hadoop fs -du -s`替代。

### expunge

**用法：**`hadoop fs -expunge`

清空垃圾

更多信息参考[Hadoop架构向导](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html)中

**选项：**

- -R 

	该参数选项会递归目录进行更改

### find

**用法：**`hadoop fs -find <path> ... <expression> ...`

查找符合指定表达式的所有文件，并且支持将选定的操作应用于它们。如果没有指定路径默认是当前的工作目录。如果没有指定正则表达式默认是`-print`。

以下主要表达式已经被确认：

- -name pattern  
  -iname pattern
  
  如果文件基本名匹配模式使用标准的文件系统扩展名，评估为true。如果`-iname`被使用，匹配不区分大小写。

- -print  
  -print0Always
  
  当前路径名被写到标准输出时评估为真。如果`-print0`表达式被使用，ASCII字符NUll就会被追加在后面。
  

下面的操作被确认：

- expression -a expression  
  expression -and expression  
  expression expression  

	逻辑与运算符用于连接两个表达式。如果两个子表达式都为真返回真。隐含的两个表达式的并置，不需要显式指定。
	
**举例：**

	`hadoop fs -find / -name test -print`

**错误码：**

	成功：0
	失败：-1
	

### get

**用法：**`hadoop fs -get [-ignorecrc] [-crc] <src> <localdst>`

复制文件到本地文件系统。CRC（循环冗余码）校验失败的文件可能用`-ignorecrc`被复制。使用`-crc`,文件和CRC可能被复制。

**举例：**

```
hadoop fs -get /user/hadoop/file localfile

hadoop fs -get hdfs://nn.example.com/user/hadoop/file localfile
```

### getfacl

**用法：**` hadoop fs -getfacl [-R] <path>`

显示文件或目录的ACL(访问控制列表)。如果目录有默认的ACL，也显示默认的ACL

**选项：**

- -R 

	该参数选项会递归显示文件的访问控制列表
	
- path

	文件或目录的路径

**举例：**

```
hadoop fs -getfacl /file

hadoop fs -getfacl -R /dir
```

**错误码：**

	成功：0
	失败：非0
	
	
### getfattr

**用法：**` hadoop fs -getfattr [-R] -n name | -d [-e en] <path>`

显示文件或目录的扩展属性名和值

**选项：**

- -R 

	递归所有文件和目录的属性列表
	
- -n name 
	
	存储命名的扩展属性值
	
- -d 
	
	结合路径名存储所有的扩展属性值

- e encoding 
	
	检索后的编码。合法的编码是`text`,`hex`,`base64`。编码为text字符串的在双引号`""`内，编码为十六进制的和base64的前缀各为0x和0s。

- path 
	
	文件或目录
	
**举例：**

```
hadoop fs -getfattr -d /file

hadoop fs -getfattr -R -n user.myAttr /dir
```


**错误码：**

	成功：0
	失败：非0
		

	
### getfattr

**用法：**` hadoop fs -getfattr [-R] -n name | -d [-e en] <path>`

显示文件或目录的扩展属性名和值

**选项：**

- -R 

	递归所有文件和目录的属性列表
	
- -n name 
	
	存储命名的扩展属性值
	
- -d 
	
	结合路径名存储所有的扩展属性值

- e encoding 
	
	检索后的编码。合法的编码是`text`,`hex`,`base64`。编码为text字符串的在双引号`""`内，编码为十六进制的和base64的前缀各为0x和0s。

- path 
	
	文件或目录
	
**举例：**

```
hadoop fs -getfattr -d /file

hadoop fs -getfattr -R -n user.myAttr /dir
```


**错误码：**

	成功：0
	失败：非0
		

	
### getfattr

**用法：**` hadoop fs -getfattr [-R] -n name | -d [-e en] <path>`

显示文件或目录的扩展属性名和值

**选项：**

- -R 

	递归所有文件和目录的属性列表
	
- -n name 
	
	存储命名的扩展属性值
	
- -d 
	
	结合路径名存储所有的扩展属性值

- e encoding 
	
	检索后的编码。合法的编码是`text`,`hex`,`base64`。编码为text字符串的在双引号`""`内，编码为十六进制的和base64的前缀各为0x和0s。

- path 
	
	文件或目录
	
**举例：**

```
hadoop fs -getfattr -d /file

hadoop fs -getfattr -R -n user.myAttr /dir
```


**错误码：**

	成功：0
	失败：非0
		

	
### getfattr

**用法：**` hadoop fs -getfattr [-R] -n name | -d [-e en] <path>`

显示文件或目录的扩展属性名和值

**选项：**

- -R 

	递归所有文件和目录的属性列表
	
- -n name 
	
	存储命名的扩展属性值
	
- -d 
	
	结合路径名存储所有的扩展属性值

- e encoding 
	
	检索后的编码。合法的编码是`text`,`hex`,`base64`。编码为text字符串的在双引号`""`内，编码为十六进制的和base64的前缀各为0x和0s。

- path 
	
	文件或目录
	
**举例：**

```
hadoop fs -getfattr -d /file

hadoop fs -getfattr -R -n user.myAttr /dir
```


**错误码：**

	成功：0
	失败：非0
		
