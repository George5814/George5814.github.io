---
layout: post
title: 基于MapReduce新的API的编程Demo-wordCount
category: 技术
tags:  Hadoop
keywords: 
description: 该MapReduce是基于MR的新API开发，基于hadoop2.7环境运行
---

{:toc}

## 概述

进行MapReduce开发暂且认为分为8步，依次为：

1. 获取输入输出路径参数
1. 删除已经存在输出目录
1. 根据系统类型进行configuration的配置
1. 获取Job实例
1. 为Job配置Jar,MapperClass,CombinerClas,ReducerClass
1. 为Job配置输出的key和value类型
1. 设置文件格式化
1. 提交Job并等待完成

## 准备环境

- hadoop2.7.2 集群环境（三个节点，h2m1,h2s1,h2s2）
- jdk 1.7.0_75版本
- centos6.5系统

**该MR代码支持输入源为多个文件或多个目录，不可以文件和目录混合作为输入源**

## 搭建程序

使用eclipse新建maven程序，开发在window环境，运行在linux环境

在maven的pom.xml文件中配置

```
<?xml version="1.0"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>brief-hadoop-demo</artifactId>
  <properties>
  	<hadoop.version>2.7.2</hadoop.version>
  </properties>
  <dependencies>
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-client</artifactId>
        <version>${hadoop.version}</version>
    </dependency>
    <!-- 测试 -->
    <dependency>
		<groupId>junit</groupId>
		<artifactId>junit</artifactId>
	</dependency>
	
    <!-- 日志 -->
	 <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>jcl-over-slf4j</artifactId>
   	</dependency>
   	<dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
   	</dependency>
   	<dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-log4j12</artifactId>
   	</dependency>
    <dependency>
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
    </dependency>
  </dependencies>
</project>
```

新建三个类：`WordCountV2.java`,`WordCountMapperV2.java`,`WordCountReduceV2.java`,`HDFSOper.java`

四个类的内容：

`WordCountV2.java`

```java

package cn.followtry.hadoop.demo.v2.mr;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.followtry.hadoop.demo.hdfs.HDFSOper;

/**
 * 
 *  brief-hadoop-demo/cn.followtry.hadoop.demo.v2.mr.WordCountV2
 * @author 
 *		jingzz 
 * @since 
 *		2016年12月14日 上午10:03:48
 */
public class WordCountV2 {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs == null || otherArgs.length < 2) {
			System.out.println("用法：\n"
					+ "     至少需要两个参数,最后一个为输出目录，其他为输入文件路径");
			System.exit(-1);
		}
		StringBuilder inputPaths = new StringBuilder();
		String outpathDir;
		int len = otherArgs.length - 1;
		for (int i = 0; i < len; i++) {
			inputPaths.append(otherArgs[i]);
			if (i < len - 1) {
				inputPaths.append(",");
			}
		}
		outpathDir = otherArgs[len];
		//检查输出目录是否存在，存在则直接删除目录
		HDFSOper.rmExistsOutputDir(outpathDir);
		
		Job job = Job.getInstance(conf, "wordCount v2 demo");
		
		job.setJarByClass(WordCountV2.class);
		
		job.setMapperClass(WordCountMapV2.class);
		job.setCombinerClass(WordCountReduceV2.class);
		job.setReducerClass(WordCountReduceV2.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.setInputPaths(job, inputPaths.toString());
		FileOutputFormat.setOutputPath(job, new Path(outpathDir));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

```

`WordCountMapperV2.java`文件

```java
package cn.followtry.hadoop.demo.v2.mr;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * 
 *  brief-hadoop-demo/cn.followtry.hadoop.demo.v2.mr.WordCountMapV2
 * @author 
 *		jingzz 
 * @since 
 *		2016年12月14日 上午10:25:07
 */
public class WordCountMapV2 extends Mapper<LongWritable, Text, Text, IntWritable>{

	private static final int ONE = 1;
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		String line = value.toString();
		if (StringUtils.isNotEmpty(line)) {
			String[] words = line.split(" ");
			for (String word : words) {
				context.write(new Text(word), new IntWritable(ONE));
			}
		}
	}
}

```

`WordCountReduceV2.java`文件

```java
package cn.followtry.hadoop.demo.v2.mr;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *  brief-hadoop-demo/cn.followtry.hadoop.demo.v2.mr.WordCountReduceV2
 * @author 
 *		jingzz 
 * @since 
 *		2016年12月14日 上午10:25:28
 */
public class WordCountReduceV2 extends Reducer<Text, IntWritable, Text, IntWritable> {

	private static final Logger LOGGER = LoggerFactory.getLogger(WordCountReduceV2.class);
	
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		int count = 0;
		for (IntWritable intValue : values) {
			count += intValue.get();
		}
		LOGGER.info("统计{}的次数为{}", key, count);
		context.write(key, new IntWritable(count));
	}

}

```

`HDFSOper.java`文件

```java
package cn.followtry.hadoop.demo.hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *  brief-hadoop-demo/cn.followtry.hadoop.demo.hdfs.HDFSOper
 * @author 
 *		jingzz 
 * @since 
 *		2016年12月14日 上午10:26:23
 */
public class HDFSOper {

	private static final Logger LOGGER = LoggerFactory.getLogger(HDFSOper.class);

	/**
	 * 删除指定的目录
	 * @author jingzz
	 * @param outpathDir 指定要删除的目录路径
	 * @return 删除返回true,不存在或没有删除返回false
	 * @throws FileNotFoundException 文件未找到异常
	 * @throws IOException IO异常
	 */
	public static boolean rmExistsOutputDir(String outpathDir) throws FileNotFoundException, IOException {
		boolean hasDel = false;
		// 将本地文件上传到hdfs。
		Configuration config = new Configuration();
		FileSystem fs = FileSystem.get(URI.create("webhdfs://h2m1:50070"), config);
		Path output = new Path(outpathDir);
		if (hasDel = fs.exists(output)) {
			LOGGER.info("目录{}已经存在,正在删除...", outpathDir);
			System.out.println("目录" + outpathDir + "已经存在,正在删除...");
			if (hasDel = fs.delete(output, true)) {
				System.out.println("目录" + outpathDir + "已经删除");
				LOGGER.info("目录{}已经删除", outpathDir);
			} else {
				System.out.println("目录" + outpathDir + "删除失败");
				LOGGER.info("目录{}删除失败", outpathDir);

			}
		} else {
			System.out.println("目录" + outpathDir + "不存在");
			LOGGER.info("目录{}不存在", outpathDir);
		}
		return hasDel;
	}
}

```


## 打包发布

### 打包

项目(右键) --> Export --> java(jar file) --> next --> jar file(browse,指定输出位置) --> finish。

### 上传到hadoop linux服务器


## 创建并将输入文件上传到hdfs

比如：

输入文件`file1.txt`内容如下：

```
hello world
hello world
hello world2
hello world2
hello world3
hello world4
hello world5
hello world5
hello world5
hello world6
hello world7
hello world8
hello world8
```

执行`hdfs dfs -put -f  file1.txt /user/root/input/file1.txt`命令，上传输入文件

## 执行

`hadoop jar wordcount.jar  cn.followtry.hadoop.demo.v2.mr.WordCountV2   /user/root/input/file1.txt /user/root/output/`
或者
`hadoop jar wordcount.jar  cn.followtry.hadoop.demo.v2.mr.WordCountV2   viewfs://hadoop-cluster-jingzz/user/root/input/file1.txt   /user/root/output/`

输入为全路径，`hadoop-cluster-jingzz`为RM的集群名称。

部分执行日志显示：

```
16/12/13 18:10:07 INFO hdfs.HDFSOper: 目录/user/root/output已经存在,正在删除...
目录/user/root/output已经存在,正在删除...
目录/user/root/output已经删除
16/12/13 18:10:07 INFO hdfs.HDFSOper: 目录/user/root/output已经删除
16/12/13 18:10:08 INFO input.FileInputFormat: Total input paths to process : 2
16/12/13 18:10:08 INFO mapreduce.JobSubmitter: number of splits:2
16/12/13 18:10:09 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1481615539888_0016
16/12/13 18:10:09 INFO impl.YarnClientImpl: Submitted application application_1481615539888_0016
16/12/13 18:10:09 INFO mapreduce.Job: The url to track the job: http://h2m1:8088/proxy/application_1481615539888_0016/
16/12/13 18:10:09 INFO mapreduce.Job: Running job: job_1481615539888_0016
16/12/13 18:10:18 INFO mapreduce.Job: Job job_1481615539888_0016 running in uber mode : false
16/12/13 18:10:18 INFO mapreduce.Job:  map 0% reduce 0%
16/12/13 18:10:28 INFO mapreduce.Job:  map 100% reduce 0%
16/12/13 18:10:36 INFO mapreduce.Job:  map 100% reduce 100%
16/12/13 18:10:36 INFO mapreduce.Job: Job job_1481615539888_0016 completed successfully
16/12/13 18:10:36 INFO mapreduce.Job: Counters: 54
        File System Counters
                FILE: Number of bytes read=160
                FILE: Number of bytes written=356056
                FILE: Number of read operations=0
                FILE: Number of large read operations=0
                FILE: Number of write operations=0
                HDFS: Number of bytes read=436
                HDFS: Number of bytes written=99
                HDFS: Number of read operations=9
                HDFS: Number of large read operations=0
                HDFS: Number of write operations=2
                VIEWFS: Number of bytes read=0
                VIEWFS: Number of bytes written=0
                VIEWFS: Number of read operations=0
                VIEWFS: Number of large read operations=0
                VIEWFS: Number of write operations=0
        Job Counters 
                Launched map tasks=2
                Launched reduce tasks=1
                Data-local map tasks=2
                Total time spent by all maps in occupied slots (ms)=13091
                Total time spent by all reduces in occupied slots (ms)=5876
                Total time spent by all map tasks (ms)=13091
                Total time spent by all reduce tasks (ms)=5876
                Total vcore-seconds taken by all map tasks=13091
                Total vcore-seconds taken by all reduce tasks=5876
                Total megabyte-seconds taken by all map tasks=13405184
                Total megabyte-seconds taken by all reduce tasks=6017024
        Map-Reduce Framework
                Map input records=15
                Map output records=30
                Map output bytes=314
                Map output materialized bytes=166
                Input split bytes=242
                Combine input records=30
                Combine output records=12
                Reduce input groups=11
                Reduce shuffle bytes=166
                Reduce input records=12
                Reduce output records=11
                Spilled Records=24
                Shuffled Maps =2
                Failed Shuffles=0
                Merged Map outputs=2
                GC time elapsed (ms)=268
                CPU time spent (ms)=2090
                Physical memory (bytes) snapshot=520069120
                Virtual memory (bytes) snapshot=2547580928
                Total committed heap usage (bytes)=281157632
        Shuffle Errors
                BAD_ID=0
                CONNECTION=0
                IO_ERROR=0
                WRONG_LENGTH=0
                WRONG_MAP=0
                WRONG_REDUCE=0
        File Input Format Counters 
                Bytes Read=0
        File Output Format Counters 
                Bytes Written=0
``` 

执行命令：`hf -cat /user/root/output/part-r-00000`

显示执行结果：

```
hello   13
world   2
world2  2
world3  1
world4  1
world5  3
world6  1
world7  1
world8  2
```
