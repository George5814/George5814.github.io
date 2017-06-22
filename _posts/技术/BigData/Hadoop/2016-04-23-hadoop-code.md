---
layout: post
title: Hadoop代码拾忆
category: 技术
tags: Hadoop
keywords: 
description: 不定期更新
---
 
{:toc}

### 伪分布式运行hadoop自带Wordcount程序

```bash
 [root@h2m1 ~]# hadoop jar /opt/local/hadoop271/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.1.jar wordcount hdfs:///user/root/input hdfs:///user/root/output
 
```

运行截图：

![1](http://omsz9j1wp.bkt.clouddn.com/image/hadoop2/hadoop-code-1.png)

![2](http://omsz9j1wp.bkt.clouddn.com/image/hadoop2/hadoop-code-2.png)

![3](http://omsz9j1wp.bkt.clouddn.com/image/hadoop2/hadoop-code-3.png)

web页面中任务执行结果：

![4](http://omsz9j1wp.bkt.clouddn.com/image/hadoop2/hadoop-code-4.png)

### hadoop设置成功之后，只是对master格式化

不需要对datanode节点格式化，格式化实质上说对NameNode的元数据区进行格式化。

### 启动成功namenode和yarn集群的标识

截图：

namenode格式化成功：

![5](http://omsz9j1wp.bkt.clouddn.com/image/hadoop2/hadoop-code-5.png)

namenode、datanode和secondary namenodes停止成功：

![6](http://omsz9j1wp.bkt.clouddn.com/image/hadoop2/hadoop-code-6.png)

集群yarn启动成功：

![7](http://omsz9j1wp.bkt.clouddn.com/image/hadoop2/hadoop-code-7.png)


### namenode -format 再次格式化时错误

每次使用`hdfs namenode -format`格式化文件系统时会产生新的namenodeId，如果自定义的temp目录或datanode和namenode目录data、name不为空就会失败，只要将该部分内容清空即可。




