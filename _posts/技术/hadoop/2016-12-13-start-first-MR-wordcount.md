---
layout: post
title: 编写第一个MapReduce的wordcount程序
category: 技术
tags:  Hadoop
keywords: 
description: 该MapReduce是基于hadoop2.7环境开发运行
---

{:toc}

## 准备环境

- hadoop2.7.2 集群环境（三个节点，h2m1,h2s1,h2s2）
- jdk 1.7.0_75版本
- centos6.5系统

**该MR代码支持输入源为多个文件**

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
  </dependencies>
</project>
```

新建三个类：`WordCount.java`,`WordCountMapper.java`,`WordCountReduce.java`

假定三个类所在的包为：`cn.followtry.hadoop.demo.mr`

三个类的内容：

`WordCount.java`

```java
package cn.followtry.hadoop.demo.mr;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.followtry.hadoop.demo.HelloHadoop;

public class WordCount {

	public static void main(String[] args) throws IOException {
		if (args == null || args.length < 2) {
			System.out.println("用法：至少需要两个参数,最后一个为输出目录，其他为输入文件路径");
			System.exit(-1);
		}
		StringBuilder inputPaths = new StringBuilder();
		String outpathDir;
		int len = args.length - 1;
		for (int i = 0; i < len; i++) {
			inputPaths.append(args[i]);
			if (i < len - 1) {
				inputPaths.append(",");
			}
		}
		outpathDir = args[len];
		//检查输出目录是否存在，存在则直接删除目录
		rmExistsOutputDir(outpathDir);
		JobConf conf = new JobConf(WordCount.class);
		conf.setJobName("word count mapreduce demo");
		conf.setMapperClass(WordCountMapper.class);
		conf.setReducerClass(WordCountReduce.class);
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);
		
		//在命令行mainclass后的第一个参数作为输入参数
		FileInputFormat.setInputPaths(conf, inputPaths.toString());
		//在命令行mainclass后的第二个参数作为输出参数
		FileOutputFormat.setOutputPath(conf, new Path(outpathDir));
		
		JobClient.runJob(conf);
	}

	private static void rmExistsOutputDir(String outpathDir) throws FileNotFoundException, IOException {
		// 将本地文件上传到hdfs。
		Configuration config = new Configuration();
		FileSystem fs = FileSystem.get(URI.create("webhdfs://h2m1:50070"), config);
		Path output = new Path(outpathDir);
		if (fs.exists(output)) {
			System.out.println("目录" + outpathDir + "已经存在,正在删除...");
			if (fs.delete(output, true)) {
				System.out.println("目录" + outpathDir + "已经删除");
			}else {
				System.out.println("目录" + outpathDir + "删除失败");
				
			}
		} else {
			System.out.println("目录" + outpathDir + "不存在");
		}
	}
}

```

`WordCountMapper.java`文件

```java
package cn.followtry.hadoop.demo.mr;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class WordCountMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>{

	private static final int ONE = 1;

	@Override
	public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		String line = value.toString();
		if (StringUtils.isNotEmpty(line)) {
			String[] words = line.split(" ");
			for (String word : words) {
				output.collect(new Text(word), new IntWritable(ONE));
			}
		}
	}
}

```

`WordCountReduce.java`文件

```java
package cn.followtry.hadoop.demo.mr;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordCountReduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

	private static final Logger LOGGER = LoggerFactory.getLogger(WordCountReduce.class);

	@Override
	public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output,
			Reporter reporter) throws IOException {
		int count = 0;
		while (values.hasNext()) {
			values.next();
			count++;
		}
		LOGGER.info("统计{}的次数为{}", key, count);
		output.collect(key, new IntWritable(count));
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

`hadoop jar wordcount.jar  cn.followtry.hadoop.demo.mr.WordCount   /user/root/input/file1.txt /user/root/output/`
或者
`hadoop jar wordcount.jar  cn.followtry.hadoop.demo.mr.WordCount    viewfs://hadoop-cluster-jingzz/user/root/input/file1.txt   /user/root/output/`

输入为全路径，`hadoop-cluster-jingzz`为RM的集群名称。

部分执行日志显示：

```
16/12/13 04:15:13 INFO demo.HelloHadoop: 目录/user/root/output/已经存在,正在删除...
目录/user/root/output/已经存在,正在删除...
目录/user/root/output/已经删除
16/12/13 04:15:13 INFO demo.HelloHadoop: 目录/user/root/output/已经删除
16/12/13 04:15:15 WARN mapreduce.JobResourceUploader: Hadoop command-line option parsing not performed. Implement the Tool interface and execute your application with ToolRunner to remedy this.
16/12/13 04:15:15 INFO mapred.FileInputFormat: Total input paths to process : 1
16/12/13 04:15:15 INFO mapreduce.JobSubmitter: number of splits:2
16/12/13 04:15:16 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1481615539888_0004
16/12/13 04:15:16 INFO impl.YarnClientImpl: Submitted application application_1481615539888_0004
16/12/13 04:15:16 INFO mapreduce.Job: The url to track the job: http://h2m1:8088/proxy/application_1481615539888_0004/
16/12/13 04:15:16 INFO mapreduce.Job: Running job: job_1481615539888_0004
16/12/13 04:15:25 INFO mapreduce.Job: Job job_1481615539888_0004 running in uber mode : false
16/12/13 04:15:25 INFO mapreduce.Job:  map 0% reduce 0%
16/12/13 04:15:32 INFO mapreduce.Job:  map 50% reduce 0%
16/12/13 04:16:00 INFO mapreduce.Job:  map 100% reduce 17%
16/12/13 04:16:01 INFO mapreduce.Job:  map 100% reduce 100%
16/12/13 04:16:02 INFO mapreduce.Job: Job job_1481615539888_0004 completed successfully
16/12/13 04:16:02 INFO mapreduce.Job: Counters: 55
        File System Counters
                FILE: Number of bytes read=329
                FILE: Number of bytes written=355404
                FILE: Number of read operations=0
                FILE: Number of large read operations=0
                FILE: Number of write operations=0
                HDFS: Number of bytes read=467
                HDFS: Number of bytes written=80
                HDFS: Number of read operations=9
                HDFS: Number of large read operations=0
                HDFS: Number of write operations=2
                VIEWFS: Number of bytes read=0
                VIEWFS: Number of bytes written=0
                VIEWFS: Number of read operations=0
                VIEWFS: Number of large read operations=0
                VIEWFS: Number of write operations=0
        Job Counters 
                Killed map tasks=1
                Launched map tasks=3
                Launched reduce tasks=1
                Data-local map tasks=3
                Total time spent by all maps in occupied slots (ms)=53297
                Total time spent by all reduces in occupied slots (ms)=25951
                Total time spent by all map tasks (ms)=53297
                Total time spent by all reduce tasks (ms)=25951
                Total vcore-seconds taken by all map tasks=53297
                Total vcore-seconds taken by all reduce tasks=25951
                Total megabyte-seconds taken by all map tasks=54576128
                Total megabyte-seconds taken by all reduce tasks=26573824
        Map-Reduce Framework
                Map input records=13
                Map output records=26
                Map output bytes=271
                Map output materialized bytes=335
                Input split bytes=216
                Combine input records=0
                Combine output records=0
                Reduce input groups=9
                Reduce shuffle bytes=335
                Reduce input records=26
                Reduce output records=9
                Spilled Records=52
                Shuffled Maps =2
                Failed Shuffles=0
                Merged Map outputs=2
                GC time elapsed (ms)=1257
                CPU time spent (ms)=4820
                Physical memory (bytes) snapshot=515735552
                Virtual memory (bytes) snapshot=2546524160
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

执行命令：`hdfs dfs -cat /user/root/output/part-00000`

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
