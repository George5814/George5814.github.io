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

![1](/public/img/posts/hadoop/hadoop-code-1.png)

![2](/public/img/posts/hadoop/hadoop-code-2.png)

![3](/public/img/posts/hadoop/hadoop-code-3.png)

web页面中任务执行结果：

![3](/public/img/posts/hadoop/hadoop-code-4.png)

