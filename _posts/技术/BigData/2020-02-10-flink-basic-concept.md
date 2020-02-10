---
layout: post
title:  flink基本 API 的概念
category: 技术
tags: BigData  Flink
keywords: BigData Flink  Concept
description:  简要记述下 Flink 使用的基本概念
date: 2020-02-10
author: followtry
published: false
---
 
{:toc}


## Flink编程步骤剖析

1. 获取执行环境
2. 加载或生产初始化的数据
3. 指定在此数据上的转换方式（如 Map,reduce,Filter等）
4. 指定计算后的结果需要输出的位置（如 std out,Kafka,ES，Mysql等）
5. 触发Flink Job 的执行


### 获取执行环境的方式

对于 Stream 来说，通过`StreamExecutionEnvironment`的几种方法来获取不同的环境

``` java

// 获取当前 Job运行的环境，可能是 Flink 的独立集群，也可能是运行在 yarn 上的 Flink 应用
getExecutionEnvironment()

// 创建 本地的环境，有利用在 IDE 中进行调试使用
createLocalEnvironment()

//创建远程的环境
createRemoteEnvironment(String host, int port, String... jarFiles)

```

### 加载或生产初始化数据

通过`ExecutionEnvironment`的几个方法可以从不同地方获得数据。

1. 从文件读取 `readTextFile`、`readFile`、``、``、
2. 从 socket 读取 `socketTextStream`
3. 从外部程序如 kakfa 中读取。 `addSource(new FlinkKafkaConsumer08<>(...))`
4. 从集合中读取 `fromCollection`、`fromElements`、`fromParallelCollection`、`generateSequence`

### 输出结果

1. writeAsText
2. writeAsCsv
3. print / printToErr
4. writeUsingOutputFormat
5. writeToSocket
6. addSink : 自定义的sink 的 Function，比如写入到 kafka 中。

通过 write 开头的都不用于调试目的，线上使用 addSink，然后通过自定义的 sink 或者 connector 来使用。

## 延迟评估

在`main`方法执行的时候，数据加载和转换不会立即执行，而是每个算子都创建并添加到 Flink 程序的执行计划中。只有程序在明确执行到`execute`方法时才会执行。程序是在`本地运行`还是在`Flink 集群中运行`取决于执行环境的类型。


## 指定 key

一些算子(如join,coGroup,keyBy,groupBy)需要定义在集合元素上的一个key，而其他的一些算子（Reduce,GroupReduce,Aggregate,Windows）需要在它们被应用前允许数据依据某些 key 来分组。

Flink 的数据模型不是基于键值对的，key 是虚拟的，只是用来给分组算子使用的。

### 定义元组的 key



```java
DataStream<Tuple3<Integer,String,Long>> input = // [...]
//对于元组 Tuple3<Integer,String,Long>而言,以 Tuple3的第一个元素作为 Key 进行分组
KeyedStream<Tuple3<Integer,String,Long>,Tuple> keyed = input.keyBy(0)


//对于元组 Tuple3<Integer,String,Long>而言,以 Tuple3的第一个和第二个元素作为 Key 进行分组
eyedStream<Tuple3<Integer,String,Long>,Tuple> keyed = input.keyBy(0,1)
```

### 使用字段表达式定义 key

示例 1

```java
// some ordinary POJO (Plain old Java Object)
public class WC {
  public String word;
  public int count;
}
DataStream<WC> words = // [...]
//使用字段名 word 来定义 key
DataStream<WC> wordCounts = words.keyBy("word").window(/*window specification*/);

```

**字段表达式语法**

1. 使用 POJO 对象的字段名称
2. 如果使用 Tuple的字段，则使用 f0 到 f5 来指定从第一个到第六个字段
3. 如果使用内嵌 java 对象的字段来表示，可以使用类似`user.addr`来表示User 对象引用内的 addr 字段。
4. 如果选择全部类型，则可以使用`*`通配符。


### 使用 key Selector Function 来定义 key

通过 KeySelectorFunction 可以自定义 key，可以是一个字段，也可以是其他自己想设置的 key 生成算法。


## 执行转换换方法

### 实现接口

```java
class MyMapFunction implements MapFunction<String, Integer> {
  public Integer map(String value) { return Integer.parseInt(value); }
};
data.map(new MyMapFunction());
```

### 匿名类

```java
data.map(new MapFunction<String, Integer> () {
  public Integer map(String value) { return Integer.parseInt(value); }
});
```

### Java8的 lambda 方法

```java
data.filter(s -> s.startsWith("http://"));

data.reduce((i1,i2) -> i1 + i2);
```

### Rich functions

继承抽象类`RichMapFunction`,通过该 Function 可以定义Operator 的生命周期。

## 支持的数据类型

1. Java Tuples 和 Scala Case Classes
2. java 的 POJO 类。该类必须是 public 类型的，必须有 public 的无参构造方法。注册的序列化程序必须支持字段的类型。
3. 原始类型。 Integer, String, and Double
4. 常规的类
5. Values 实现了 Flink 的 Value 接口的类型。需要实现Value接口和 read，write 接口
6. Hadoop 的 Writables
7. 指定的类型

## 控制时延

因为网络不可能一个一个的传输，这样会导致不必要的网络通信，因此就存在缓存。在 flink 中，缓存的大小可以通过配置文件调整。可以很好的优化系统的吞吐。对于数据频繁的情况，可以将延时设置小一些，而对于数据比较低频的情况，可以将延时设置的大些。
通过`env.setBufferTimeout(timeoutMillis)`方法来控制缓存超时时间。如果最大化缓存，设置`setBufferTimeout(-1)`,如果最小化缓存，可使得超时时间接近于 0ms，如 5ms。不要设置为 0ms，因为会导致严重的性能下降。


## 测试

### 调试

如果需要再本地 IDE 启动调试程序，那么需要启动本地执行环境才可以。

```java
//直接创建本地的运行环境
final StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment();
DataStream<Integer> myInts = env.fromElements(1, 2, 3, 4, 5);
DataStream<Tuple2<String, Integer>> myTuples = env.fromCollection(data);
DataStream<Long> myLongs = env.fromCollection(longIt, Long.class);
```

### 集合数据源

flink 提供了集合方式提供少量测试数据一遍测试更容易。测试完之后，soruce 和 sink 都可以很简单的被替换掉。

### Iterator Data Sink

flink 提供了一个 sink 来收集 DataStream 输出的结果，以便于测试和调试。

```java
DataStream<Tuple2<String, Integer>> myResult = ...
Iterator<Tuple2<String, Integer>> myOutput = DataStreamUtils.collect(myResult)
```









