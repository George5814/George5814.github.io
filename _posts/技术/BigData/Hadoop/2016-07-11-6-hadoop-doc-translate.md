---
layout: post
title: 06.hadoop-2.7.2官网文档翻译-Hadoop的兼容性
category: 技术
tags:  Hadoop
keywords: 
description: Hadoop的兼容性，官网地址：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/Compatibility.html
---

{:toc}


### 目的

该文档主要讲述Hadoop项目的兼容目标。影响着Hadoop开发者、下游项目和最终用户的Hadoop版本间不同类型的兼容性会被列举。

对于每一中类型的兼容性，我们：

- 描述对下游项目或最终用户的影响

- 在适用的情况下，在允许不兼容的更改时，呼吁Hadoop开发者采用的政策

### 兼容类型

#### JAVA API（java的接口）

Hadoop的接口和类被注解用来描述其受众和稳定性，以保持与以前版本的兼容性。

详细请看[Hadoop 接口类别](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/InterfaceClassification.html)

- InterfaceAudience：捕获与其的观众，可能值为`Public`（对于最终用户和扩展项目）,`LimitedPrivate `（对于Hadoop组件和有密切关系的项目像`YARN`,`MapReduce`，`HBase`等等）和`Private`（对内部组件使用）

- InterfaceStability：描述那种类型的接口改变是被允许的。可能的值为`Stable`,`Evolving`,`Unstable`,`Deprecated`。

**使用案例**

- `Public-Stable`的API兼容性在确保最终用户程序和下游项目继续可以无修改的工作的情况下是必需的。

- `LimitedPrivate-Stable`的API兼容性在允许在小版本中升级单个组件时必需的。

- `Private-Stable`的API兼容性在滚动升级时是必须的。

**政策**

- `Public-Stable`的API必须在从至少一个主要版本中移除之前主要版本时被标记为过期。

- `LimitedPrivate-Stable`的API可以在主要版本间改变，但不能在一个重大的版本内。

- `Private-Stable`的API可以在主要版本间改变，但不能在一个重大的版本内。

- 没有被注解的类，隐式的为`Private`.没被注解的类成功继承了封闭类的注解。

- 注意：从原始文件生成的API需要为滚动升级做兼容。请看`wire-compatibility`片段了解更详细信息。
API和有线通信的兼容性政策需要携手解决

---

#### Semantic compatibility（语义兼容）


Hadoop致力于确保API的行为保持一致的版本，虽然正确性的改变可能会导致行为的改变。

测试和java文档详细说明了API的行为。更严格的详述一些API的通用性正在进行中并且加强测试套件，以验证是否符合规范，有效的创建一个正式规范的子集的范围，可以很容易的测试。

**政策**

API的行为可能会通过修复不正确的行为被改变，这种变化是伴随着更新现有的buggy测试或者在没有改变之前添加的测试用例。

--- 

#### Wire compatibility（线兼容）

线兼容问题在于Hadoop进程之间的线传输过程。

Hadoop的大多数RPC通信使用的protocol buffers。保持兼容性需要禁止修改如下描述的条件。非RPC通信业也需要考虑，比如使用HTTP向HDFS传输的图像作为快照的一部分或者抓你MapTask的输出。
潜在的通信可以分类如下：

- Client-Server: Hadoop客户端和服务器间的通信（比如HDFS客户端到NameNode协议，或者YARN客户端到ResourceManager协议）。

- Client-Server(Admin):区分一个仅仅使用管理的命令的客户端-服务器协议（比如HAAdmin协议）是值得的。因为这些协议仅仅影响管理者可以容忍的变化，而最终用户（一般使用Client-Server协议）不能。

- Server-Server ： 服务器之间的通信（比如：DataNode和NameNode，NodeManager和ResourceManager之间的协议）。


**使用案例**

- 客户端-服务端兼容允许用户继续使用老的客户端，甚至在升级服务器（集群）到最新版本之后，反之亦然。比如，Hadoop2.1.0的客户端与Hadoop2.3.0集群交互。

- 客户端-服务端兼容也允许 用户在升级服务器集群之前升级客户端。比如，Hadoop2.4。0客户端与Hadoop2.3.0的集群交互。
	允许在完整的集群升级前部署客户端的错误修复。注意：通过新的客户端API或者Shell命令调用的新的集群功能将无法使用。尝试使用还未部署到集群的新的API（包括数据结构中的新域）的YARN应用会抛出link异常


- 客户端-服务端兼容也允许升级某个内部组件而不升级其他组件。比如：将HDFS从2.1.0升级到2.2.0，而不升级MapReduce。

- 服务单-服务端兼容允许你在一个激活的集群中运行不同版本，因此集群可能会滚动方式升级而没有停机时间。


**政策**

- 在一个主要版本中客户端-服务端和服务端-服务端的兼容性都会被保持（不同类别的不同政策也会被考虑）。

- 兼容性在主要版本改变时会被破坏，尽管在主要版本变更时破坏兼容性会有严重的后果，同时应该在Hadoop社区中讨论。

- Hadoop的协议定义在`.proto`(ProtocolBuffers)文件中。客户端-服务端协议和服务端协议的`.proto`文件会被标记为未定状态。
当一个`.proto`文件被标记为稳定态时，意味着改变应该兼容以下列举的内容：

	- 以下改变是兼容的并且允许在任何时间改变：
	
		- 添加一个可选字段，携带由于与旧代码通信导致的字段丢失的代码处理的期望值
		
		- 在service中添加的RPC或者方法
		
		- 向消息添加一个新的可选请求
		
		- 重命名一个域
		
		- 重命名一个 `.proto`文件
		
		- 改变影响代码生成的`.proto`注解（比如：java包名）
		
	- 以下改变时不兼容的并且仅仅可以在主要版本考虑
	
		- 改变RPC/method名
		
		- 改变RPC/method参数类型或返回类型
		
		- 移除一个RPC/method
		
		- 改变消息名
		
		- 以不兼容的方式修改字段类型（比如定义递归）
		
		- 改变请求的可选字段
		
		- 添加或删除一个请求域
		
		- 删除一个可选字段，只要改可选字段有合理的默认值允许删除
		
	- 以下更改时不兼容的，因此永远不被允许
	
		- 改变一个字段的id
		
		- 恢复一个在先前版本中删除的旧字段
		
		- 字段号码是廉价的，改变和重新使用都不是一个好主意
		
		


--- 

####  Java Binary compatibility for end-user applications i.e. Apache Hadoop ABI（对于最终用户程序的java二进制的兼容，比如Hadoop ABI）

在Hadoop版本升级时，最终用户很自然的期望他们的程序在没有任何修改的情况下仍然能够继续工作。这是支持API兼容性，语义兼容性和线兼容性的满意的结果。

然而，Hadoop是一个非常复杂的分布式系统，服务非常广泛的用例。特别指出的是，Hadoop的MapReduce是一个非常，非常宽泛的API；
在这个意义上，最终用户可能会作出刚发你的假设，如在他们的map/reduce任务正在运行时布局本地磁盘，他们任务的环境变量。
这种情况下，它变得非常难以完全指定和支持以及绝对的兼容性。


**使用案例**

- 当指向一个在一个主版本内升级后的Hadoop集群时，现有的MapReduce应用，包括现有的最终用户应用程序和项目包的jar，比如`pig`,`Hive`,`Cascading `等等。
应该在无修改的情况下继续工作。

- 当指向一个在一个主版本内升级后的Hadoop集群时，现有的YARN应用，包括现有的最终用户应用程序和项目包的jar，比如`Tez`等等。
应该在无修改的情况下继续工作。

- 当指向一个在一个主版本内升级后的Hadoop集群时，现有的HDFS数据传递的应用，包括现有的最终用户应用程序和项目包的jar，比如`Flume`等等。
应该在无修改的情况下继续工作。

**政策**

- 现有的MapReduce，YARN和HDFS应用和框架应该在同一个住版本中无修改的工作(比如，Hadoop ABI被支持)。

- 非常小的应用程序可能会受到磁盘布局变化的影响等等。开发者社区努力在一个小版本中将变化减小到最小。更糟糕的情况是，在必要情况下我们将考虑强还原这些打破变化和无效的违规发布的更改

- 尤其是MapReduce应用，开发者社区将会尽我们最大的努力支持跨主版本的二进制兼容。比如，应用使用`org.apache.hadoop.mapred`。

- API 直接兼容跨Hadoop-1.x和Hadoop2.x版本。请看[Hadoop1.x和Hadoop2.x间的MapReduce兼容性](http://hadoop.apache.org/docs/r2.7.2/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduce_Compatibility_Hadoop1_Hadoop2.html){:target="_blank"}


#### REST API

Rest接口的兼容性对应于请求(URL)和对每个请求的响应(内容，其中可能包含其他的URL)。Hadoop的Rest API是特意被客户端跨版本，甚至主版本稳定使用的。

以下是暴露的Rest API：

- [WebHDFS](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-hdfs/WebHDFS.html){:target="_blank"}

- [ResourceManager](http://hadoop.apache.org/docs/r2.7.2/hadoop-yarn/hadoop-yarn-site/ResourceManagerRest.html){:target="_blank"}

- [NodeManager](http://hadoop.apache.org/docs/r2.7.2/hadoop-yarn/hadoop-yarn-site/NodeManagerRest.html){:target="_blank"}

- [MR Application Master](http://hadoop.apache.org/docs/r2.7.2/hadoop-yarn/hadoop-yarn-site/MapredAppMasterRest.html){:target="_blank"}

- [History Server](http://hadoop.apache.org/docs/r2.7.2/hadoop-yarn/hadoop-yarn-site/HistoryServerRest.html){:target="_blank"}

- [Timeline Server v1 REST API](http://hadoop.apache.org/docs/r2.7.2/hadoop-yarn/hadoop-yarn-site/TimelineServer.html){:target="_blank"}

**政策**

在上面的文本中注解的API稳定，保持至少一个主要版本的兼容性。也许在一个主要的版本更新中会被新版本的RestAPI替代为过期。

---

#### Metrics/JMX

Metrics(度量工具)API的兼容性是被java API的兼容性所管理的同时，暴露在Hadoop的实际指标需要兼容的用户能够自动使用它们（脚本等）。

添加额外的度量是兼容的。修改(比如更改单元或测量)或删除存在的度量会破坏兼容性。相似的，对JMX MBean对象名称的改变也会破坏兼容性

**政策**

度量应该在主要版本内保持兼容性。


#### File formats & Metadata（文件格式化和元数据）

用户和系统级别的数据(包括元数据)被以不同的格式存储在文件中，改变元数据或者用来存储数据或元数据的文件格式会导致版本间的不兼容问题。

**用户级文件格式**

改变最终用户用来存储他们数据的格式会妨碍他们在更新的版本 中访问数据，因此保持这些文件格式兼容性是非常重要的。

可以总是增加新的格式以提高现有格式。这些格式的例子包括`har`,`war`,`SequenceFileFormat`等等。

**政策**

- 非向前兼容的用户文件格式更改被限制在主要版本内。当用户文件格式改变，新的版本将读取现有的格式，但是可能写数据的格式与之前的版本不兼容，。
社区更喜欢创建新的格式，该格式必须选择，而不是对现有格式的不兼容的更改。


**系统内部的文件格式**

Hadoop内部数据也存储在文件中，修改他们的格式也会导致不兼容。虽然这种变化并不像用户级别的文件格式具有破坏性，但在兼容性会被打破时的政策是很重要的。

**MapReduce**

MapReduce使用的文件像`I-File`来存储MapReduce指定的数据。

**政策**

MapReduce内部格式像`I-File`,在一个主要版本中维持兼容性。改变这些格式会造成运行任务失败，因此我们应该确保新的客户端可以以兼容的方式从老的服务器获取洗牌数据。

**HDFS 元数据**

HDFS的元数据(image和edit日志)有一个特定的格式。不管是格式还是元数据的改变都会阻止随后的版本读取就得元数据。
这种不兼容的改变可能需要HDFS升级覆盖元数据来使得可以访问。某些改变可能需要不止一个这样的升级。

取决于变化中的不相容程度，会出现下面潜在的情况：

- 自动：image自定升级，不需要明确的升级

- 直接的：image可以升级，但是需要一个明确的版本升级

- 间接地：image可以升级，但是可能需要需要先升级到中间版本

- 不需要升级：该image不能升级


**政策**

- 一个版本的实际必须允许集群回滚到老版本和他的老的磁盘格式。该回滚需要恢复原来的数据，但是不需要恢复已经更新了的数据。

- HDFS元数据改变必须通过更新途径`自动`,`直接`和`间接`任何一种升级。

- 更多详细的基于这种升级的政策也会被考虑。

#### Command Line Interface (CLI)（命令行接口）

Hadoop命令行程序可以用直接通过系统shell或者通过shell脚本。改变命令的路径，删除或者重命名命令行选项，参数顺序或者命令行返回值和数据都会破坏兼容性并且对用户产生不利影响。

**Policy**

在一个主要版本前删除它们或者在随后的主要版本中修改为不兼容状态时的命令行命令要被标记为过期.

#### Web UI

WEB UI ,特别是内容和web页面布局的改变可能会干扰试图筛选网页的信息的尝试。

**政策**

WEB页面并不会被勉强，而且对于他们的不兼容的改变随时都允许。用户预计可以使用Rest API获得任何信息。


#### Hadoop Configuration Files（Hadoop配置文件）

用户使用Hadoop定义的属性给Hadoop配置和提供建议，
并且自定义属性将信息传递给作业。配置属性的兼容性有两种：

- 修改key名称，单位值，Hadoop定义属性的默认值

- 自定义配置数据key应该和Hadoop定义属性的命名空间不冲突。通常，用户应该避免使用Hadoop已经使用的前缀：
`Hadoop`,`io`,`ipc`,`fs`,`net`,`file`,`ftp`,`s3`,`kfs`,`ha`,`dfs`,`mapred`,`mapreduce`,`yarn`。


**政策**

- Hadoop定义的属性至少在主要版本发布之前被删除时标记为过期。不允许修改现有属性的单元。

- Hadoop定义属性的默认值可以在跨主版本和小版本时改变，但是一个小版本内跨点版本是需要保持相同。

- 目前，没有明确的有关政策，当新的前缀被添加或删除和英爱被避免自定义配置属性的前缀列表。然而，如上面提到的，用户应该避免使用Hadoop已经使用的前缀：
`Hadoop`,`io`,`ipc`,`fs`,`net`,`file`,`ftp`,`s3`,`kfs`,`ha`,`dfs`,`mapred`,`mapreduce`,`yarn`。

#### Directory Structure（目录结构）

源码,artifacts ,用户日志，配置文件，输出和job历史都被存储在本地磁盘或者HDFS上。改变这些用户可以访问的文件的目录结构会破坏兼容性，即使在原始路径被保留情况下，通过符号链接（例如：如果路径要被servlet访问，配置是不允许符号链接的）

**政策**

- 源码的布局和artifacts 的构建是任何时候都能改变的，特别是在跨主要版本时。在主要版本内部，开发者会尝试保持目录结构；然而，个别文件会被添加，删除或移动。
为了确保不定停留域代码同步的最好方式是把它们提交到apache的源码树。

- 配置文件，用户目录，job历史的目录结构在同一主版本内跨小版本和点版本时会被保持不变。

#### Java Classpath(java 类路径)

用户创建的应用程序可能会添加所有Hadoop的jar到应用程序的类路径。添加新的依赖或者更新现有依赖版本可能会干扰那些应用程序的类路径。

**政策**

当前，在没有政策时，Hadoop的依赖可以改变。

#### Environment variables（环境变量）

用户和相关的项目经常会利用导出的环境变量(比如`HADOOP_CONF_DIR`),移除或重命名环境变量会导致不兼容

**政策**

当前，在没有政策时，环境变量可以改变。开发者试图限制在对主要版本的修改。


#### Build artifacts

Hadoop使用maven作为项目的管理，并且改变artifacts会影响现有的用户工作流。

**政策**

- 测试artifacts：生成的测试jar严格内部使用，不会再Hadoop意外使用，比如API的注解`@Private`,`@Unstable`

- 构建artifacts：Hadoop客户端的artifacts（maven groupId:artifactId）保持在主要版本内部兼容，其他artifactId可以以不兼容的方式改变。


#### Hardware/Software Requirements(软硬件需求)

为了跟上最先进的硬件，操作系统，JVM和其他软件，Hadoop版本或者他们的一些特定可能会同样需要高版本。对于一个指定的环境，更新Hadoop可能需要更新其他依赖的软件组件。

**政策**

- 硬件:
	
	- 架构：设置没有计划限制Hadoop为指定的架构，但是可能有特定family的优化。
	
	- 最小资源：虽然Hadoop守护进程所需要的最小资源没有抱枕个，但社区尝试在一个小版本被不提高需求。
	
- 操作系统:社区尝试保持在小版本内保持相同的系统需求（系统内核版本）。当前的GNU/Linux和windows是社区官方支持的系统，而Apache的Hadoop是已知的工作相当不错的。其他的系统如Apple的MacOSx和Solaris。

- JVM 的需求在小版本内，跨点版本改变，除非在VJM版本不支持情况下。对于支持的操作系统，主版本和小版本可能需要更新的JVM。

- 其他软件：社区试图保持所要求的最低版本的Hadoop的附加软件。比如：ssh，kerberos 等等。


### 参考

这里有一些相关的jiras与主题相关的页面：

- 当前文档的演进 - [hadoop-9517](https://issues.apache.org/jira/browse/HADOOP-9517){:target="_blank"}

- [Hadoop 1.x 和Hadoop 2.x 间最终用户程序的二进制兼容性](http://hadoop.apache.org/docs/r2.7.2/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduce_Compatibility_Hadoop1_Hadoop2.html){:target="_blank"}

- [接口按接口分类表的注解](https://issues.apache.org/jira/browse/HADOOP-7391){:target="_blank"}
[Hadoop接口分类](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/InterfaceClassification.html){:target="_blank"}

- [Hadoop1.x兼容性](https://issues.apache.org/jira/browse/HADOOP-5071){:target="_blank"}

- 捕获其他版本政策的[Hadoop RoadMap](http://wiki.apache.org/hadoop/Roadmap){:target="_blank"}
