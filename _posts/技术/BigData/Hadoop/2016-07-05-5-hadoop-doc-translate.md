---
layout: post
title: 05.hadoop-2.7.2官网文档翻译-文件系统命令
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

请看[HDFS 快照指南](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsSnapshots.html)


### deleteSnapshot 

请看[HDFS 快照指南](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsSnapshots.html)

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

更多信息参考[Hadoop架构指南](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html)中

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
		

	
### getmerge

**用法：**`hadoop fs -getmerge [-nl] <src> <localdst>`

将源目录和目标目录作为输入，将在src目录的文件关联到目标本地文件。
选项`-nl` 可以在每个文件的最后添加新的一行。

	
**举例：**

```
hadoop fs -getmerge -nl /src /opt/output.txt

hadoop fs -getmerge -nl /src/file1.txt /src/file2.txt /output.txt
```


**错误码：**

	成功：0
	失败：非0
		
### help

**用法：**`hadoop fs -help`

显示该帮助文档

	
### ls

**用法：**`hadoop fs -ls [-d] [-h] [-R] <args>`

**选项：**

- -R 

	递归显示所有子目录列表
	
- -h 
	
	文件大小格式化为易读数据
	
- -d 
	
	将目录作为纯文件
	

`ls`的文件返回的状态格式化为:`权限`,`复制数量`,`所有者id`,`所在组id`,`文件大小`,`修改日期 `,`修改时间`,`文件名称`。
	
`ls`的目录返回的状态格式化为：`权限`,`所有者id`,`所在组id`,`修改日期 `,`修改时间`,`目录名称`。	

同一个目录中的文件默认是按照文件名称排序的。

**举例：**

```
hadoop fs -ls /user/hadoop/file1
```


**错误码：**

	成功：0
	失败：-1
		

	
### lsr

**用法：**`hadoop fs -lsr <args>`

递归显示目录，文件及所有子目录的文件。

**过期：**已经被`hadoop fs -ls -R`代替

	
### mkdir

**用法：**`hadoop fs -mkdir [-p] <paths>`

将url作为参数并且创建目录

**选项：**

- -p 

	与unix系统的`mkdir -p`类似，在创建目录时如果父目录不存在就会先创建父目录
	
**举例：**

```
hadoop fs -mkdir /user/hadoop/dir1 /user/hadoop/dir2

hadoop fs -mkdir hdfs://nn1.example.com/user/hadoop/dir  hdfs://nn2.example.com/user/hadoop/dir
```


**错误码：**

	成功：0
	失败：-1
	
	
### moveFromLocal

**用法：**`hadoop fs -moveFromLocal <localsrc> <dst>`

与`put`命令相似，除了源路径本地文件或目录在它被复制后会删除。
	
### moveToLocal

**用法：**`hadoop fs -moveToLocal [-crc] <src> <dst>`

显示"尚未实现"信息
	
### mv

**用法：**`hadoop fs -mv URI [URI ...] <dest>`

将文件从源位置移动到目标位置。该命令允许多个源，但目标位置必须是目录。

跨文件系统移动文件是不允许的。

**举例：**

```
hadoop fs -mv /user/hadoop/file1 /user/hadoop/file2

hadoop fs -mv hdfs://nn.example.com/file1 hdfs://nn.example.com/file2 hdfs://nn.example.com/file3 hdfs://nn.example.com/dir1
```


**错误码：**

	成功：0
	失败：-1
	
	
### put

**用法：**`hadoop fs -put <localsrc> ... <dst>`

将本地文件系统的一个或多个文件复制到目标文件系统。也可以从标准输入读入并写到目标文件系统

	
**举例：**

```
hadoop fs -put localfile /user/hadoop/hadoopfile

hadoop fs -put localfile1 localfile2 /user/hadoop/hadoopdir

hadoop fs -put localfile hdfs://nn.example.com/hadoop/hadoopfile

##Reads the input from stdin
hadoop fs -put - hdfs://nn.example.com/hadoop/hadoopfile 
```


**错误码：**

	成功：0
	失败：-1

### renameSnapshot

请看[HDFS快照指南](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/HdfsSnapshots.html)
	
### rm

**用法：**`hadoop fs -rm [-f] [-r |-R] [-skipTrash] URI [URI ...]`

删除参数中指定的文件集合

**选项：**

- -R
- -r 

	递归目录及其下的所有文件和目录
	
- -f  
	
	不显示判断信息或者在文件不存在时也不提示
	
- -skipTrash 
	
	忽略垃圾，如果启用。会立即删除指定文件。在目录超出配额删除文件时会很有用。

**举例：**

```
hadoop fs -rm hdfs://nn.example.com/file /user/hadoop/emptydir
```


**错误码：**

	成功：0
	失败：-1
	
	
### rmdir

**用法：**`hadoop fs -rmdir [--ignore-fail-on-non-empty] URI [URI ...]`

删除一个目录

**选项：**

- --ignore-fail-on-non-empty 

	在使用通配符时，如果目录中还有文件不提示失败
	
**举例：**

```
hadoop fs -rmdir /user/hadoop/emptydir
```


	
### rmr

**用法：**` hadoop fs -rmr [-skipTrash] URI [URI ...]`

递归删除

**已经过期**，被`hadoop fs -rm -r`替代


	
### setfacl

**用法：**`hadoop fs -setfacl [-R] [-b |-k -m |-x <acl_spec> <path>] |[--set <acl_spec> <path>]`

设置文件或者目录的访问控制列表

**选项：**

-  -b
	
	删除所有除了基本的ACL的记录。该记录保留对`user`,`group`,`other`权限位的兼容性。

-  -k

	删除默认的ACL
	
-  -R

	支持递归操作
	
-  -m

	修改ACL，新的记录将会被添加到ACL，并且存在的记录也会保存。

-  -x

	删除指定的ACL记录，其他的记录仍然保留。
	
-  --set

	完全替换ACL，丢弃所有已存在的记录。ACL的记录列表必须包含对`user`,`group`,`other`的权限位兼容的记录。

-  acl_spec

	逗号分隔的ACL记录列表
	

- path

	要修改的文件或目录的路径
	
**举例：**

```
hadoop fs -setfacl -m user:hadoop:rw- /file
hadoop fs -setfacl -x user:hadoop /file
hadoop fs -setfacl -b /file
hadoop fs -setfacl -k /dir
hadoop fs -setfacl --set user::rw-,user:hadoop:rw-,group::r--,other::r-- /file
hadoop fs -setfacl -R -m user:hadoop:r-x /dir
hadoop fs -setfacl -m default:user:hadoop:r-x /dir
```

**错误码：**

	成功：0
	失败：非0
	
	

	
### setfattr

**用法：**`hadoop fs -setfattr -n name [-v value] | -x name <path>`

为文件或目录设置扩展舒心的名称和值

**选项：**

- -n

- -v value

	扩展属性值。该值有三种不同的编码方法。
	如果参数是双引号括起来的，那么值是双引号中的string值。
	如果参数前缀是0x或0X，那么是个十六进制的数。
	如果参数以0s或0S开头，那么是base64编码。

- -x name

	移除扩展属性

- path

	文件或目录的路径


	
**举例：**

```
hadoop fs -setfattr -n user.myAttr -v myValue /file
hadoop fs -setfattr -n user.noValue /file
hadoop fs -setfattr -x user.myAttr /file
```

**错误码：**

	成功：0
	失败：非0
	
	

	
### setrep

**用法：**`hadoop fs -setrep [-R] [-w] <numReplicas> <path>`

改变文件的备份因子。如果目录经是一个目录，那该命令递归改变该目录树内的所有文件的备份因子。

**选项：**

- -w

	在需要等待备份完成的时候设置该参数。可以设置一个非常长的时间。
	

- -R

	该标识为向后兼容性。未生效。

	
	
**举例：**

```
hadoop fs -setrep -w 3 /user/hadoop/dir1
```

**错误码：**

	成功：0
	失败：-1
	

	
### stat

**用法：**`hadoop fs -stat [format] <path> ...`

按照指定的格式显示对`path`路径下的文件或目录的统计。格式化参数为：`%b`块、`%F`类型、`%g`所有者的组名、`%n`名称、`%o`块大小、`%r`备份、`%u`所有者的用户名、`%y,%Y`修改时间。
`%y`显示UTC时间"yyyy-MM-dd HH:mm:ss",`%Y`显示自UTC1970/01/01的毫秒数。如果没有指定格式参数，默认为`%y`

**举例：**

```
hadoop fs -stat "%F %u:%g %b %y %n" /file
```

**错误码：**

	成功：0
	失败：-1
	

	
### tail

**用法：**`hadoop fs -tail [-f] URI`

在标准输出中显示文件最后1K字节的内容

**选项：**

- -f

	当文件增加时，将追加的数据显示出来,与unix类似。
	
**举例：**

```
hadoop fs -tail pathname
```

**错误码：**

	成功：0
	失败：-1
	
	
### test

**用法：**`hadoop fs -test -[defsz] URI`

在标准输出中显示文件最后1K字节的内容

**选项：**

- -d

	路径是一个目录，返回0

- -e

	路径存在，返回0

- -f

	路径是文件，返回0

- -s

	路径不为空，返回0

- -z

	文件大小为0，返回0
	
**举例：**

```
hadoop fs -test -e filename
```

	
### text

**用法：**`hadoop fs -text <src>`

需要一个源文件，并以文本格式输出文件。文件允许格式为zip和TextRecordInputStream
	
	
### touchz

**用法：**` hadoop fs -touchz URI [URI ...]`

创建一个空文件

	
**举例：**

```
hadoop fs -touchz pathname
```

**错误码：**

	成功：0
	失败：-1
	
	
### truncate

**用法：**`hadoop fs -truncate [-w] <length> <paths>`

截断指定文件模式指定的长度匹配的所有文件。

**选项：**

- -w

	该命令等待块恢复完成。如果必要，没有`-w`文件可能仍会在恢复中的同时保留非关闭一段时间。
	在这段时间内，文件不能被重新追加。
	
**举例：**

```
hadoop fs -truncate 55 /user/hadoop/file1 /user/hadoop/file2
hadoop fs -truncate -w 127 hdfs://nn1.example.com/user/hadoop/file1
```

	
### usage

**用法：**`hadoop fs -usage command`

显示单个命令的帮助信息

