---
layout: post
title: 10.hadoop-2.7.2官网文档翻译-原生库指南
category: 技术
tags:  Hadoop
keywords: 
description: Hadoop原生库指南。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/NativeLibraries.html
---

{:toc}

### 概览

该指南描述了原生的Hadoop库和原生共享库的一小部分讨论。

注意:依赖你的环境，属于`原生库`可能为你需要编译的所有`*.so`，属于`原生压缩`指的是所有你需要编译并特别是明确要压缩的`*.so`。然而，目前该文档仅仅称呼原生Hadoop库`libhadoop.so`.
针对libHDFS库`libhdfs.so`的文档在[这里](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/LibHdfs.html){:target="_blank"}

### 原生Hadoop库

因为性能原因，Hadoop某些组件是原生实现的，java实现不可用。这些组件可以在叫做原生Hadoop库的单一动态链接库中获得。在*nix平台上该库叫做`libhadoop.so`。


### 用法

使用原生Hadoop库相当容易：

1. 检验组件  

1. 检验支持的平台

1. 下载Hadoop的发行版本，包含了原生Hadoop库的预编译版本；或者编译你自己的原生Hadoop库版本。不管是编译还是下载，库的名字是相同的:`libhadoop.so`。

1. 安装压缩编解码器(zlib-1.2,gzip-1.2):

	- 如果你下载库，安装一个或多个你想在你的开发中使用的压缩编解码器的开发包。
	
	- 如果你编译库，会强制安装上面的两个开发包
	
1. 检查运行时的日志文件


### 组件

原生的Hadoop库包含多种组件：

- 压缩编解码器(bzip2,lz4,snappy,zlib)

- [Hadoop简短的本地读](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/ShortCircuitLocalReads.html){target="_blank"}和
[HDFS集中缓存管理](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/CentralizedCacheManagement.html){target="_blank"}

- CRC32校验实现

### 支持平台

原生Hadoop库仅仅支持*nix平台。该库不能在Cygwin和Mac OS X平台使用。

原生库主要用于GNU/Linux平台并测试了以下Linux发行版：

- RHEL4/Fedora

- Ubuntu 

- Gentoo

以上所有发行版的32位和64位原生Hadoop库会在各自的32位和64位jvm上运行。


### 下载

预编译的32位的i386的linux原生Hadoop库可以作为Hadoop发行版本的一部分获得。在`lib/native`目录。你可以从Hadoop通用版本中获得Hadoop发行版。

要确保你安装了开发中要用到的压缩编解码器`zlib`或`gzip`,两个都安装也可。


### 构建

原生的Hadoop库是用ANSII C缩写，使用GNU的自动工具链（`autoconf`,`autoheader`,`automake`,`autoscan`,`libtool`）编译。意味着应该在任何平台通过标准的C编译器和GNU自动工具链编译库。

在目标平台你需要安装以下的包：

- C编译器(比如,GNU C 编译器)

- GNU自动工具链：`autoconf`,`autoconf`,`libtool`

- zlib开发包（稳定版 >= 1.2.0）

- openssl 开发包(比如，libssl-dev)

一旦安装了必备的包，使用标准的Hadoop的POM.xml文件，使用原生标志编译原生Hadoop库。

```
$ mvn package -Pdist,native -DskipTests -Dtar
```

你可以在`$ hadoop-dist/target/hadoop-2.7.2/lib/native`查看最新编译的库。

请注意以下几点：

- 为了编译原生的Hadoop库，会在目标平台强制安装在zlib和gzip开发包。然而，对于部署，如果进指向使用一个编解码器，只需要安装一个包。

- 为了编译和部署原生Hadoop库，在目标平台上安装依赖于32位或者64位JVM的正确的32位或者64位的库是必须要的。


###　运行时

`bin/hadoop`脚本通过系统属性`-Djava.library.path=<path>`确保原生库在库路径上。

在运行期间，为你的MapReduce任务检查Hadoop日志文件。

- 如果所有事都正确，会显示`DEBUG util.NativeCodeLoader - Trying to load the custom-built native-hadoop library... INFO util.NativeCodeLoader - Loaded the native-hadoop library`

- 如果有些出现错误，会显示` INFO util.NativeCodeLoader - Unable to load native-hadoop library for your platform... using builtin-java classes where applicable`


### 检查

`NativeLibraryChecker`是检查原生库是否被正确安装的工具。你可以以如下命令启动`NativeLibraryChecker`:

```
$ hadoop checknative -a

   14/12/06 01:30:45 WARN bzip2.Bzip2Factory: Failed to load/initialize native-bzip2 library system-native, will use pure-Java version
   14/12/06 01:30:45 INFO zlib.ZlibFactory: Successfully loaded & initialized native-zlib library
   Native library checking:
   hadoop: true /home/ozawa/hadoop/lib/native/libhadoop.so.1.0.0
   zlib:   true /lib/x86_64-linux-gnu/libz.so.1
   snappy: true /usr/lib/libsnappy.so.1
   lz4:    true revision:99
   bzip2:  false
```

### 原生共享库

你可以使用`DistributedCache `分配和软连接那些库文件来加载任何原生共享库。

该例子展示了你应该怎样分发一个共享库`mylib.so`，并同一个MapReduce任务加载它。

1. 首先将库赋值到HDFS上：` bin/hadoop fs -copyFromLocal mylib.so.1 /libraries/mylib.so.1`

1. 作业启动程序应该包含以下：

```
DistributedCache.createSymlink(conf); 
DistributedCache.addCacheFile("hdfs://host:port/libraries/mylib.so. 1#mylib.so", conf);
```

1. MapReduce任务可以包含:` System.loadLibrary("mylib.so");`

注意：如果你已经下载了或者编译了原生Hadoop库，你不需要使用`DistibutedCache`来使得你的MapReduce任务可以获得库。













