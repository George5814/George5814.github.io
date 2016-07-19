---
layout: post
title: 17.hadoop-2.7.2官网文档翻译-实现Hadoop中Dapper-like追踪
category: 技术
tags:  Hadoop
keywords: 
description: 实现Hadoop中Dapper-like追踪。官网地址为：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/Tracing.html
---

{:toc}

## Hadoop中Dapper-like追踪

### HTrace

[HDFS-5274](https://issues.apache.org/jira/browse/HDFS-5274){:target="_blank"}
添加了通过HDFS跟踪请求的支持，使用的开源的跟踪库[ Apache HTrace](https://git-wip-us.apache.org/repos/asf/incubator-htrace.git){:target="_blank"}，
设置跟踪是很简单的，但它需要对你的客户端代码做很小的修改。

### 采样器

使用`core-site.xml`属性`hadoop.htrace.sampler`配置采样器。值可以是`NeverSampler`, `AlwaysSampler` 或 `ProbabilitySampler`。

NeverSampler： HTrace 一直是关闭的；

AlwaysSampler：HTrace一直是开启的；

ProbabilitySampler： HTrace在最高级别持续时间的一定百分比是开启的。

```xml
 <property>
    <name>hadoop.htrace.sampler</name>
    <value>NeverSampler</value>
  </property>
```

### SpanReceiver

追踪系统的工作是收集结构叫做“Spans”的信息。通过实现SpanReceiver接口，由你决定选择你想怎样接收该信息。

这是定义的一个方法：`public void receiveSpan(Span span);`。

在`core-site.xml`属性`hadoop.htrace.spanreceiver.classes`中通过用逗号分隔的继承SpanReceiver接口的类的全类名配置你想用的SpanReceivers。

```xml
<property>
    <name>hadoop.htrace.spanreceiver.classes</name>
    <value>org.apache.htrace.impl.LocalFileSpanReceiver</value>
  </property>
  <property>
    <name>hadoop.htrace.local-file-span-receiver.path</name>
    <value>/var/log/hadoop/htrace.out</value>
  </property>
``` 

如果你使用HTrace附带的span receiver，可以省略前缀包名：

```xml
  <property>
    <name>hadoop.htrace.spanreceiver.classes</name>
    <value>LocalFileSpanReceiver</value>
  </property>
```

### 设置ZipkinSpanReceiver

你可以使用`ZipkinSpanReceiver`（使用[Zipkin](https://github.com/twitter/zipkin){:target="_blank"}手机和显示追踪的数据）来替代自己实现SpanReceiver 接口。

为了使用`ZipkinSpanReceiver`,需要先下载并安装[Zipkin](https://github.com/twitter/zipkin){:target="_blank"}。

你也需要将jar包`htrace-zipkin`添加的每个节点的Hadoop类路径下。下面是个例子：

```bash
  $ git clone https://github.com/cloudera/htrace
  $ cd htrace/htrace-zipkin
  $ mvn compile assembly:single
  $ cp target/htrace-zipkin-*-jar-with-dependencies.jar $HADOOP_HOME/share/hadoop/common/lib/


```

`ZipkinSpanReceiver`的样板配置如下显示。通过将他们添加到NameNode和DataNode的`core-site.xml`中，`ZipkinSpanReceiver`会在启动时初始化。除了服务端之外，你也需要在客户端添加此配置。

```xml
  <property>
    <name>hadoop.htrace.spanreceiver.classes</name>
    <value>ZipkinSpanReceiver</value>
  </property>
  <property>
    <name>hadoop.htrace.zipkin.collector-hostname</name>
    <value>192.168.1.2</value>
  </property>
  <property>
    <name>hadoop.htrace.zipkin.collector-port</name>
    <value>9410</value>
  </property>
```


### 跟踪配置的动态更新

可以使用`hadoop trace`命令查看和更新每个服务器的追踪配置。你必须通过`-host`选项指定NameNode或DataNode的IPC服务地址。
如果你想更新所有服务器的配置，你需要在所有服务器上运行该命令。

命令`hadoop trace -list`显示与id相关联的已加载的span接收器列表。

```bash
  $ hadoop trace -list -host 192.168.56.2:9000
  ID  CLASS
  1   org.apache.htrace.impl.LocalFileSpanReceiver

  $ hadoop trace -list -host 192.168.56.2:50020
  ID  CLASS
  1   org.apache.htrace.impl.LocalFileSpanReceiver
```

命令`hadoop trace -remove`从服务器删除span接收器，`-remove`选项将span接受者的id作为参数。

```bash
 $ hadoop trace -remove 1 -host 192.168.56.2:9000
 Removed trace span receiver 1
```

命令`hadoop trace -add`向服务器添加span接受器。需要指定span接收器的类名作为`-class`选项的参数。需要通过`-Cke=value`选项将配置和span接收器关联。

```bash
  $ hadoop trace -add -class LocalFileSpanReceiver -Chadoop.htrace.local-file-span-receiver.path=/tmp/htrace.out -host 192.168.56.2:9000
  Added trace span receiver 2 with configuration hadoop.htrace.local-file-span-receiver.path = /tmp/htrace.out

  $ hadoop trace -list -host 192.168.56.2:9000
  ID  CLASS
  2   org.apache.htrace.impl.LocalFileSpanReceiver
```

### 通过HTrace API开始追踪span

为了追踪，你需要使用tracing span包装追踪逻辑，就像下面展示的。

当最总span运行时，追踪信息通过RPC请求传到服务器。

另外，你需要每次过程都初始化`SpanReceiver`。

```java
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.tracing.SpanReceiverHost;
import org.apache.htrace.Sampler;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceScope;

//...

    SpanReceiverHost.getInstance(new HdfsConfiguration());

//...

    TraceScope ts = Trace.startSpan("Gets", Sampler.ALWAYS);
    try {
      ... // traced logic
    } finally {
      if (ts != null) ts.close();
    }

```

### 追踪的简单代码

`TracingFsShell.java`展示了Fsshell包装器在调用HDFS的shell命令之前开始追踪span。

```java
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.tracing.SpanReceiverHost;
import org.apache.hadoop.util.ToolRunner;
import org.apache.htrace.Sampler;
import org.apache.htrace.Trace;
import org.apache.htrace.TraceScope;

public class TracingFsShell {
  public static void main(String argv[]) throws Exception {
    Configuration conf = new Configuration();
    FsShell shell = new FsShell();
    conf.setQuietMode(false);
    shell.setConf(conf);
    SpanReceiverHost.getInstance(conf);
    int res = 0;
    TraceScope ts = null;
    try {
      ts = Trace.startSpan("FsShell", Sampler.ALWAYS);
      res = ToolRunner.run(shell, argv);
    } finally {
      shell.close();
      if (ts != null) ts.close();
    }
    System.exit(res);
  }
}
```

可以像下面这样编译并执行代码：

```bash
$ javac -cp `hadoop classpath` TracingFsShell.java
$ java -cp .:`hadoop classpath` TracingFsShell -ls /
```

