---
layout: post
title: 8.hadoop-2.7.2官网文档翻译-文件系统规范
category: 技术
tags:  Hadoop
keywords: 
description: Hadoop文件系统的规范。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/filesystem/index.html
---

{:toc}


### Hadoop文件系统 API定义

这是Hadoop文件系统API规范，它将文件系统的内容模型作为一组路径，这些路径是目录，符号链接或文件。

在这方面，很少有以前的艺术。有多种规格的UNIX文件系统作为一个树节点，但没有公共定义“Unix文件系统作为数据存储访问模型”的概念。

该规范尝试在做这些事：定义Hadoop文件系统的模型和API，多个文件系统可以在应用中实现该API和他们的数据目前的不矛盾的模型。
他并不试图正式指定文件系统的任何并发行为，其他文件行为由HDFS展现作为Hadoop客户端应用通用的要求。

- [介绍](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/filesystem/introduction.html){:target="_blank"}

- [记号](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/filesystem/notation.html){:target="_blank"}

- [模型](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/filesystem/model.html){:target="_blank"}

- [文件系统类](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/filesystem/filesystem.html){:target="_blank"}

- [文件系统数据输入流类](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/filesystem/fsdatainputstream.html){:target="_blank"}

- [文件系统规范测试](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/filesystem/testing.html){:target="_blank"}

- [扩展规范和其测试](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/filesystem/extending.html){:target="_blank"}

	
