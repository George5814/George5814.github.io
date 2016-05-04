---
layout: post
title: spark 错误集锦
category: 技术
tags: Spark
keywords: 
description: 
---
 
{:toc}
基于hadoop2.7.1集群

系统为centos 6.4 ,64bit

### 1.spark-shell 不能执行

错误信息：
	
Java HotSpot(TM) 64-Bit Server VM warning: INFO: os::commit_memory(0x0000000755500000, 2863661056, 0) failed; error='Cannot allocate memory' (errno=12)#
- 1.There is insufficient memory for the Java Runtime Environment to continue.
- 2.Native memory allocation (malloc) failed to allocate 2863661056 bytes for committing reserved memory.
- 3.An error report file with more information is saved as:
- 4./datac/spark/app-20150123091936-0000/89/hs_err_pid2247.log

错误原因：
	执行内存不够，最小约370M


解决办法：

1. 
```
# spark-shell --executor-memory 500M  --driver-memory 500M 
```

2. 
 取消spark-default.sh的所有自定义设置
