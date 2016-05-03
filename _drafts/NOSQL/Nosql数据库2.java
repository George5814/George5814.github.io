名词解释：


ACID：指数据库事务正确执行的四个基本要素的缩写。包含：原子性（Atomicity）、一致性（Consistency）、隔离性（Isolation）、持久性（Durability）。一个支持事务（Transaction）的，必需要具有这四种特性，否则在事务过程（Transaction processing）当中无法保证数据的正确性，

AQL：acceptable qualifying levels 可接受的适格标准

Atom ：atom 是一种基于XML（标准通用标记语言的子集）的文档格式以及基于HTTP的协议

BDB：全称是 “Berkeley DB”

BeansDB(豆瓣)：BeansDB（豆瓣开源数据存储系统），豆瓣公司以New BSD许可证发布了分布式key/value存储系统BeansDB，开发者称BeansDB是简化版的Dynamo

Blob：(binary large object)，二进制大对象，是一个可以存储二进制文件的容器

Blueprints：Blueprints是一组针对属性图数据模型的接口、实现、测试套件，有些类似于JDBC，不同之处在于Blueprints是针对图形数据库的。Blueprints提供了一组通用的接口，允许开发者在他们的图形数据库后端即插即用。

DVCS：台达分布式图像控制系统，突破传统PC架构控制器固有的总线带宽受限、处理速度慢、故障率高等技术瓶颈，采用先进的网络分布式图像处理架构，由RGB处理器、视频处理器、高清处理器、显示处理器、音频处理器、超高分图像处理器SRIS、多通道流媒体处理器IPSS、控制服务器及数据交换机构成。

EMOF

FFR
Gremlin
Hazelcast 
ISAM 
LINQ
MQL

MVCC
NRPC
ODMG ODL
OODBMS
OpenLDAP
P2P
PKI 
QBE
QMBasic
RDF

SOUL
Persistit
RCL
REST
SSD
TCO
ZEO

悲观锁和乐观锁
最终一致
热备用
热备份
======================================

原文地址：http://nosql-databases.org/
非关系数据库世界的终极向导
--------------------------------------------
Nosql定义：下一代数据库大多专注于这几点：非关系型、分布式的、开源并且可以横向扩展。

原意图是成为新式的网络级数据库。这场运动开始于2009年早期，并快速成长。通常的特点，比如无模式、容易的备份支持、简单的API、最终一致/BASE、海量数据等等。因此误导性术语"Nosql"(社区现在大多数翻译为"not only sql")应该被看做是上面等译的一个别名。[基于7个方面、15个有建设性的邮件和一个讨厌的评论]


--------------------------------------------
NOSQL数据库列表【当前有150个】

核心NOSQL系统：【大多源自于Web2.0的需要】

---------------------------------
【宽列存储/列族】
Hadoop/HBase：
http://hadoop.apache.org/
API:java/any writer，
Protocol：any write call.Query Method：MapReduce Java/any exec，
备份：HDFS Replication，
编写语言：java， 
并发：？，
杂项：三本书的链接：
http://www.amazon.com/Hadoop-Definitive-Guide-Tom-White/dp/0596521979/ 
http://www.amazon.com/Pro-Hadoop-Jason-Venner/dp/1430219424/
http://www.amazon.com/Hadoop-Action-Chuck-Lam/dp/1935182196/ (译者注：版本都比较老了，可以找新版本的书籍)

*********
MapR，Hortonworks，Cloudera：
http://www.mapr.com/
http://www.hortonworks.com/
http://www.cloudera.com/
Hadoop发布和专业服务商

*********
Cassandra：
https://cassandra.apache.org/
大规模的可扩展的，分区列存储，无主节点架构，线性规模表现，无单节点失败，多数据中心和云可用区域读写支持。
API/Query Method: CQL 和 Thrift；
备份：对等；
编写语言：java
并发：可调一致性
杂项：内置数据压缩MapReduce支持，第一和第二索引，安全功能
链接:Documentation, PlanetC*, Company.

*********
Hypertable :
http://hypertable.org/
API:Thrift（java,php,perl,python,ruby等等）；
协议：Thrift；
查询方法：HQL，原生Thrift API；
复制：HDFS复制；
并发：MVCC；
一致性模型：完全一致；
杂项：使用C++对Google的BigTable的高性能实现。 » Commercial support

*********
Accumulo ：
http://accumulo.apache.org/
Accumulo 是基于BigTable，建立在Hadoop，ZooKeeper和Thrift之上的。特点是改进了BigTable设计中的基于访问控制的形式，改进压缩和服务端的编程机制可以在数据管理进程中修改键值对在多样的点上。
*********
Amazon SimpleDB：
http://aws.amazon.com/simpledb/
杂项：非开源/AWS的一部分，将优于DynamoDB

*********
Cloudata：
http://www.cloudata.org/
Google的BigTable的克隆版本就像HBase
» Article
http://www.readwriteweb.com/hack/2011/02/open-source-bigtable-cloudata.php

*********
MonetDB：
https://www.monetdb.org/
开始于2002年的列存储先驱

*********
HPCC：
http://www.hpccsystems.com/
来自于LexisNexis数据库，
信息： 
http://www.lexisnexis.com/government/solutions/literature/hpcc-das.pdf
文章：
http://wikibon.org/blog/lexisnexis-hpcc-takes-on-hadoop-as-battle-for-big-data-supremacy-heats-up/

*********
Apache Flink：(原名：Stratosphere)
http://flink.apache.org/
大规模并行和灵活的数据分析平台
API：JAVA和scala；
Query Method:有表现的数据流(扩展：M/R、富UDF、迭代支持);
数据存储：独立(例如：HDFS、S3、和MongoDB)；
编写语言：JAVA；
证书：Apache License V2.0
杂项：很好的整合了Hadoop的栈(HDFS和YARN)，源码在GitHub
https://github.com/apache/flink

*********
IBM Informix 
http://www-01.ibm.com/software/data/informix/
横向扩展和纵向扩展，关系型，分区列存储，文档存储；
API/Query Method:SQL(原生，DRDA, JDBC, ODBC)，mongoDB写监听者，混合模式。
复制：主从，对等，分片和网格操作；
编写语言：C；
并发：行，页，表，数据库锁定；
杂项：支持ACID，内置数据压缩，调度，自动循环存储管理，可扩展，内存加速， ARM v6 up原生端口；
链接： Documentation, IIUG, Company.
http://www-01.ibm.com/support/knowledgecenter/SSGU8G_12.1.0/com.ibm.welcome.doc/welcome.htm
http://www.iiug.com/
http://www.ibm.com/

*********
Splice Machine：
http://www.splicemachine.com/
Splice Machine 是建立在Hadoop，HBase和Derby上的RDBMS(关系数据库管理系统)。使用商用硬件无应用重写的规模化的实时应用。
特点：ACID 事务，ANSI SQL支持，ODBC/JDBC，分布式计算


*********
ConcourseDB
http://concoursedb.com/
分布式的自调整数据库，带有自动索引，版本控制和ACID 事务；
编写语言：JAVA；
API/protocol:Thrift(多语言)；
并发：在时间锁定内的序列化事务；
杂项：当平台富索引在后台执行时，使用缓存存储系统立即提交所有数据到磁盘。

*********
---------------------------------
【文档存储】

Elasticsearch:
http://www.elasticsearch.org/
API:REST和多种语言；
Protocol：REST；
Query Method：via JSON；
复制和分片：自动和配置；
编写语言：JAVA
杂项：模式映射，带任意索引的多租户
公司支持
http://www.elasticsearch.com/，
文章
https://www.found.no/foundation/elasticsearch-as-nosql/


*********
MongoDB：
http://www.mongodb.org/
API：BSON
Protocol：C
Query Method：面向对象的动态语言和MapReduce；
复制：主从和自动分片
编写语言：C++
并发：空间更新；
杂项：索引，GridFS，免费和商业许可证
链接：»Talk
http://www.leadit.us/hands-on-tech/MongoDB-High-Performance-SQL-Free-Database
, » Notes
http://www.paperplanes.de/2010/2/25/notes_on_mongodb.html
, » Company
http://www.10gen.com/

*********
Couchbase Server ：
http://www.couchbase.com/
API：Memcached API+协议(二进制和ASCII)，多语言
protocol：集群配置和管理的Memcached REST接口；
编写语：C/C++、Erlang(集群)；
复制：对等，完全一致
杂项：在操作过程中的透明拓扑变化，memcached缓存桶兼容，商业支持版本；
链接：» Wiki
http://wiki.membase.org/
, » Article
http://www.infoq.com/news/2010/10/membase

*********
CouchDB :
http://couchdb.apache.org/
API:JSON;
Protocol:REST
Query Method : javascript函数的MapReduceR；
备份：Master Master；
编写语言：Erlang
并发：MVCC
链接：
» 3 本CouchDB 书
http://couchdb.apache.org/docs/books.html
, » Couch Lounge (partitioning / clusering)
http://tilgovi.github.com/couchdb-lounge/
, » Dr. Dobbs
http://www.drdobbs.com/java/223100116

*********
RethinkDB ：
http://www.rethinkdb.com/
API：基于protobuf；
Query Method：统一连贯性的语言(incl,joins,子查询，MapReduce和groupedMapReduce)；
复制：带有每个表确认的同步和非同步的主从复制；
分片：基于范围的引导
编写语言：C++；
并发:MVCC
杂项：带有并发增长的垃圾压实机的日志结构化存储引擎


*********
SequoiaDB：
http://www.sequoiadb.com/en/index.php?p=index&j=2
API：BSON
protocol：C
Query Method ：动态的面向对象的语言
复制：主从复制和自我分片
编写语言：C++
杂项：索引，巨大对象存储，事务，自由和商业证书。
基准
http://www.bankmark.de/wp-content/uploads/2014/12/bankmark-20141201-WP-NoSQLBenchmark.pdf
, 代码
https://github.com/SequoiaDB/SequoiaDB
*********
RavenDB：
http://github.com/ravendb/ravendb
.net的解决方案。提供HTTP/JSON接入。LINQ查询和分片支持
杂项：http://www.codeproject.com/KB/cs/RavenDBIntro.aspx

*********
MarkLogic Server ：【免费软件和商业软件】
http://www.marklogic.com/
API;JSON,XML,JAVA
protocol:HTTP,REST
Query Method ：完全文本搜索，XPath，XQuery，Range，Geospatial
编写语言：C++
并发：无共享集群，MVCC
杂项：PB级可扩展性，cloudable，ACID事务，自动分片，故障转移，主从复制，访问控制列表(ACL)的安全机制.
开发社区：
http://developer.marklogic.com/

*********
Clusterpoint Server ：【免费软件和商业软件】
http://www.clusterpoint.com/
API:xml,PHP,JAVA,.NET
protocol：HTTP,REST，原生TCP/IP
Query Method ：完全文本搜索，XML，范围和Xpath查询；
编写语言：C++;
并发：ACID一致，事务，多主节点集群
杂项：PB级可扩展文档存储和全文搜索引擎，消息排名。复制和Cloudable

*********
JSON ODM ：
https://github.com/konsultaner/jsonOdm
纯javascript写的对象文档到JSON文档的映射。使用类似gremlin的DSL(使用mongoDB的API方法)查询集合。但也提供join操作。这集合延伸原生的数组对象，提供整体的ODM一个良好的性能。查询50万的元素时间在一秒内。

*********
NeDB  ：
https://github.com/louischatriot/nedb
使用原生javascript为node.js编写的NOSQL数据库。它的实现一般使用mongoDB 的API的子集，并且是相当快的(在一万个带索引的文档集合上的读速度为每秒25000次)

*********
Terrastore ：
http://code.google.com/p/terrastore/
API：java和http，
Protocol：http
语言：java,
查询：范围查询，谓词
复制：采用一致性hash分区；
一致性：每条记录严格一致；
杂项：基于Terracotta

*********
AmisaDB:
http://www.amisalabs.com/
构建统一的最好的搜索引擎、nosql和NewSql数据库技术。
API：TEST和多语言
Query Method ：SQL
编写语言：C++
并发：MVCC
杂项：ACID 事务，通过hash一致的数据分布，静态和动态模式支持，内存中处理。免费软件和商用证书


*********
JasDB ：
http://www.oberasoftware.com/
使用java写的高性能的轻型开源文档数据库，内存黄总运行，支持Android。
API：JSON,JAVA
Query Method ：REST OData 风格查询语言，java流畅查询API
并发:原子级文档写操作
索引：最终一致索引


*********
RaptorDB：
http://www.codeproject.com/Articles/375413/RaptorDB-the-Document-Store
基于JSON，带有.net 映射功能、自动混合位图索引和LINQ查询过滤的文档存储型数据库

*********
djondb：
http://djondb.com/
API：BSON
protocol：C++
Query Method :动态查询和MapReduce，
驱动：JAVA,C++,PHP
杂项：ACID兼容，Google v8引擎上的完全shell控制，djondb  需要用户提交，非市场。
证书：GPL和商业证书

*********
EJDB ：
http://ejdb.org/
基于tokyocabinet的嵌入式JSON数据库引擎；
API:C/C++，C#(.net,Mono),lua,Ruby,Python,Node.js 绑定，
protocol：原生
编写语言：C
查询语言：类似mongoDB的动态查询
并行：RW锁定，事务
杂项：索引，集合级的RW锁定，集合级的事务，集合JOIN
证书：LGPL

*********
densodb ：
http://www.densodb.net/
densodb是新型的nosql文档数据库。使用C#为。net环境编写。它是简单快速和可靠的。
源码：https://github.com/teamdev/densodb

*********
SisoDB ：
http://www.sisodb.com/ 
基于SQLServer 的文档存储

*********
SDB ：
http://pagenotes.com/wordpress/2011/12/08/sdb/
小型的在线数据库，php和json接口，用php实现

*********
NoSQL embedded db：
https://github.com/petersirka/nosql
为小型web网站或者项目编写的Node.js 异步 nosql嵌入式数据库。
数据库支持：增删改查和支持视图(创建，删除和读)。
使用javascript编写，无依赖，实现小的并发模型

*********
ThruDB：
http://code.google.com/p/thrudb/
使用Thrift 整合 多个后台数据库，就像 BerkeleyDB, Disk, MySQL, S3

*********
iBoxDB：
http://www.iboxdb.com/
事务的嵌入式数据库。可以嵌入到手机，桌面和web应用中。支持磁盘和内存存储
API：java，C# (Android, Mono, Xamarin, Unity3D)
Query Method :类SQL和键值对
编写语言： Java, C#.
复制：主从和主主


*********
BergDB ：
http://www.bergdb.com/
API: Java/.NET
编写语言：java
复制：主/从
证书：AGLP
历史查询，ACID 和模式
并发：STM和持续性数据机构
仅追加存储，加密存储。耐久性的灵活控制。
第二和复合索引。
透明序列的java/.net对象


*********
--------------------------------- 
【键值对/元组存储】
DynamoDB :
http://aws.amazon.com/dynamodb/
基于快速SSD的自动超可扩展的NOSQL数据库。多可用区域。可伸缩的MapReduce整合。备份到S3和其他更多...


*********
Azure Table Storage：
http://msdn.microsoft.com/en-us/library/dd179423.aspx
自由形式的实体类集合(行健，分区键，时间戳)。Blob和队列存储可用，三分冗余。通过REST或者ATOM访问。
*********
Riak：
http://riak.basho.com/
API：JSON,
protocol:REST;
Query Method:MapReduce 期限匹配
缩放：多主节点；
编写语言：Erlang；
并发：最终一致(比通过向时钟的MVCC更强)；

*********
Redis：
http://redis.io/ 
API：大量语言
编写语言：C
并发：内存运行和在指定时间后一步保存在磁盘上，追加模式，多种fsync政策。
复制：主/从
杂项：支持list、set、sorted-set、hash和队列。
清单表：http://masonoise.files.wordpress.com/2010/03/redis-cheatsheet-v1.pdf
great slides：http://blog.simonwillison.net/post/57956858672/redis
admin界面：
http://www.servicestack.net/mythz_blog/?p=381
从零起步：http://blog.mjrusso.com/2010/10/17/redis-from-the-ground-up.html


*********
Aerospike：
http://www.aerospike.com/
快速和Web的规模化数据库。内存和本地flash存储。可预测性能-读写250k/50k TPS平衡，99%在1ms以下；
并发：ACID和可调一致性；
复制：零配置、零停机时间、自动聚类、通过数据中心复制、滚动升级；
编写语言：C；
API：很多
链接：本地Flash和SSD
http://highscalability.com/blog/2012/12/10/switch-your-databases-to-flash-storage-now-or-youre-doing-it.html
，5000美元服务器上百万级别的系统吞吐量
http://highscalability.com/blog/2012/9/10/russ-10-ingredient-recipe-for-making-1-million-tps-on-5k-har.html
，更低的TCO
http://wikibon.org/wiki/v/Data_in_DRAM_is_a_Flash_in_the_Pan
，零停机时间
http://www.aerospike.com/blog/impact-of-real-time-big-data-on-the-business/
，魔力象限
http://www.aerospike.com/only-visionary-in-gartner-mq-2013/


*********
LevelDB：
http://code.google.com/p/leveldb/ 
来自Google的快速批量更新的数据库
编写语言：C++
博客：http://google-opensource.blogspot.com/2011/07/leveldb-fast-persistent-key-value-store.html
 hot Benchmark ：http://leveldb.googlecode.com/svn/trunk/doc/benchmark.html
 ，文章(德语)：http://www.golem.de/1107/85298.html
JAVA访问：https://github.com/fusesource/leveldbjni

*********
Berkeley DB：
http://www.oracle.com/technetwork/database/database-technologies/berkeleydb/overview/index.html
API：多语言
编写语言：C
复制：主/从；
并发：MVCC
证书：Sleepycat；
Berkeley DB  JAVA版：
http://www.oracle.com/technetwork/database/berkeleydb/overview/index-093405.html
	API：java
	编写语言：java
	复制：主/从；
	并发：可序列化事务隔离
	证书：Sleepycat

*********
Oracle NOSQL Database：
http://www.oracle.com/technetwork/database/database-technologies/nosqldb/overview/index.html
Oracle NoSQL Database 是分布式键值对数据库。它被设计通过一组可配置的系统(作为存储节点)来提供高可靠、可扩展和可用数据存储。NOSQL和企业数据被存储为键值对,其基于主键的hash值被写在特定的存储节点上。存储节点会被复制来确保高可用型，节点失败时的快速转移和最佳的负载均衡问题。
API：java和C

*********
GenieDB ：
http://www.geniedb.com/
直接一致性分片键值对存储和最终一致的AP存储使得最终一致性问题降到理论最小。它具有高效的记录合并。
GenieDB 解析SQL和co-exists,可使用SQL RDBMS做可插入JOIN。

*********
BangDB：
http://www.iqlect.com/
API: Get,Put,Delete, 
Protocol: 原生, HTTP
领域：嵌入式，网络和弹性缓存
复制：基于网络覆盖的P2P
编写语言：C++
杂项：鲁棒性，崩溃校验，弹性和把机器呈线性比例关系，Btree/Ehash

*********
Chordless ：
http://sourceforge.net/projects/chordless/
API：java和简单RPC
Protocol: internal
Query Method: M/R 中值对象
可扩展：每个节点是该片命名空间的主节点
编写语言：java
并发：序列化事务隔离


*********
Scalaris：[please help provide more facts]
http://code.google.com/p/scalaris/
编写语言：Elang;
复制：复制的强一致性
并发：非阻塞Paxos算法

*********
Tokyo Cabinet / Tyrant：
http://fallabs.com/tokyocabinet/
链接: nice talk »
http://www.infoq.com/presentations/grigorik-tokyo-cabinet-recipes
, slides »
http://www.scribd.com/doc/12016121/Tokyo-Cabinet-and-Tokyo-Tyrant-Presentation
杂项: Kyoto Cabinet »
http://fallabs.com/kyotocabinet/

*********
Scalien：
http://scalien.com/
API/Protocol: http (text, html, JSON), C, C++, Python, Java, Ruby, PHP,Perl
并发：Paxos算法

*********
Voldemort ：
http://project-voldemort.com/
亚马逊Dynamo键值对存储的开源实现

*********
Dynomite ：
http://wiki.github.com/cliffmoon/dynomite/dynomite-framework
亚马逊Dynamo键值对存储的开源实现。
Erlang语言编写。拥有数据分区，版本化和读修复，并且用户提供的存储引擎提供出就花和查询处理。

*********
KAI：
http://sourceforge.net/projects/kai/
亚马逊Dynamo键值对存储的开源实现
*********
MemcacheDB：
http://memcachedb.org/
API: Memcache protocol (get, set, add, replace等等)；
编写语言：C,C++
数据模型：Blob
杂项 ：他是Memcached增加了BerkleyDB存储层

*********
Faircom C-Tree ：
http://www.faircom.com/nosql
API: C, C++, C#, Java, PHP, Perl,
编写语言：C,C++
杂项：事务日志，C/s模式，嵌入式和基于SQL(非核心)。大约从1979开始
*********
LSM ：
http://www.sqlite.org/src4/artifact/41b08c1d31c156d3916558aad89b7e7ae8a381c5
作为SQLite4一部分的键值对数据库，其声称它比levelDB更快。相比支持自定义比较器，他们有为键设计的推荐数据编码来允许多样化的数据类型的排序。

*********
KitaroDB：
http://www.kitarodb.com/
一个为WP8，Win RT，Win32(x86和x64)和.net设计的快速高效的基于磁盘的数据库。

提供了键值对和多段键访问，
API：C#，VB，C++，C和Html5/javascript
纯C编写的高性能和低占用数据库。支持最大2G记录大小的同步和异步操作。


*********
HamsterDB：
http://hamsterdb.com/
ACID 规则(嵌入式解决方案)，自由锁架构(冲突上而非阻塞的事务失败)，事务日志和失败恢复(重写日志)。支持内存运行，可以用作非持久化的缓存。B+树支持。
源码(Tony Bain)：http://blog.tonybain.com/

*********
STSdb :
http://stsdb.com/
API:C#
编写语言：C#
嵌入式解决方案，生成 XTable<TKey,TRecord>的实现，
ACID事务，快照和表版本化，共享记录，垂直数据压缩，自定义压缩，混合成的和自定义的主键，可用的文件系统部署，多卷上工作，PB级的可扩展性，LINQ

*********
Tarantool/Box：
https://github.com/mailru/tarantool
API: C, Perl, PHP, Python, Java and Ruby.
编写语言：Objective C
Protocol：异步二进制，memcached, text (Lua控制台)
数据模型：无量纲的元组集合，主键和第二主键索引；
并发：内存中自由锁，磁盘上(写前日志)一致性
复制：主/从,可配置的
其他：调用Lua 存储过程


*********
Maxtable ：
http://code.google.com/p/maxtable/
API: C, 
Query Method: MQL, 原生 API,
复制：DFS复制
一致性：严格一致
编写语言：C

*********
quasardb：
https://www.quasardb.net/
非常高性能的关联数据库，高可扩展性
API：C, C++, Java, Python and (limited) RESTful 
Protocol: binary 
Query method: key-value, 迭代
复制:分布式
编写语言：C++ 11/Assembly
并发：ACID
杂项：内置数据压缩，原生支持FreeBSD、linux和windows
证书：社区

*********
Pincaster ：
http://github.com/jedisct1/Pincaster
为geolocalized应用编写
并发：内存中运行，支持异步写到磁盘
API:HTTP/json
编写语言：C
证书：BSD

*********
RaptorDB：
http://www.codeproject.com/KB/database/RaptorDB.aspx
一个带有优化的B+树和murmur哈希的纯键值对存储数据库(在不久的将来它也可能是JSon文档数据库就像mongodb和couchdb)。

*********
TIBCO Active Spaces ：
https://ssl.tibcommunity.com/blogs/activespaces
对等的内存中分布式数据表格，实现可扩展了元组空间的概念。
有SQL查询和ACID(=> NewSQL)


*********
allegro-C :
http://www.allegro-c.de/
键值对概念.每条记录有多个键。多个关键值，分层记录。关系型，在sameDB中的不同记录类型。B*树索引，全可配置的，完全脚本语言，多用户ACID，web界面(PHP, Perl, ActionScript)和windows的客户端

*********
nessDB：
https://github.com/shuttler/nessDB
一个快速的键值对数据库(使用LSM树存储引擎)
API：redis Protocol(SET,MSET,GET,MGET,DE 等等)
编写语言:ANSI C


*********
HyperDex :
http://hyperdex.org/
分布式搜索的键值对存储，快速(延迟和吞吐量)，可扩扎，一致性，容错，使用超空间哈希
API：C,C++,Python

*********
SharedHashFile :
https://github.com/simonhf/sharedhashfile
快速，开源，共享内存，多继承，哈希表。采用内分片减少锁的争用
编写语言：C

*********
Symas LMDB：
http://symas.com/mdb/ 
Symas为OpenLDAP项目开发的超快，超紧凑型的键值对的嵌入式数据存储。它使用内存映射文件，因此他拥有纯内存数据库的读性能的同时仍然提供彼岸准的基于磁盘数据库的持久性，并且仅由虚拟地址空间的大小限制(而非物理RAM大小限制)。

*********
Sophia ：
http://sphia.org/
Sophia 是一个新式的可嵌入的键值对数据库，被设计用于高负载环境。
它拥有唯一的架构，其作为原有算法约束，与基础数据结构流行的日志文件关联的反复研究的结构被创建。比如LSM树。
作为一个小型的C编写的，BSD证书的库的实现。

*********
PickleDB ：
http://pythonhosted.org/pickleDB/
为python对象系列化且受redis启发的键值对存储

*********
Mnesia 
http://www.erlang.org/doc/apps/mnesia/index.html
(ErlangDB »)
http://www.infoq.com/news/2007/08/mnesia
*********
Hibari ：
http://hibari.sourceforge.net/
Hibari是一个高可靠的，强一致性的，耐用的分布式键值对数据存储


*********
OpenLDAP ：
http://highlandsun.com/hyc/mdb/
键值对存储，B+树，闪电般快速读取和快速批量加载。和内存数据库一样速度的内存映射文件的持久化存储。无调整配置要求。完全ACID支持。MVC，无读者锁。微代码。使用C语言写成，x86-64机器上的目标代码小于32kB，因容易从基于Berkeley代码迁移而建模与BerkeleyDB API 。以LevelDB，Kyoto Cabinet, SQLite3为基准。并且BerkeleyDB 是可用的，加上全页和演示滑动

*********
Genomu：
http://genomu.com/
高可用，并发性的基于事件的带有事务和因果一致性的键值对数据库。
Protocol: MsgPack, 
API: Erlang, Elixir, Node.js. 
编写语言: Elixir, Github-Repo
*********
BinaryRage：
https://github.com/mchidk/BinaryRage
BinaryRage 被设计为一个轻型的，.net无依赖的超快键值对存储。在一个蹩脚的电脑上测试过每秒向磁盘写超过20万复杂的对象。无配置，无生疏的驱动连接，无服务，无设置-简单参考DLL和在一分钟之内就能开始使用它。


*********
Elliptics 
http://www.ioremap.net/projects/elliptics/
Github Page »
https://github.com/reverbrain/elliptics
*********
DBreeze ：为.NET 3.0在MONO中设计的专业，开源，nosql(嵌入键值对存储)，事务，ACID兼容，多线程，对象数据库管理系统。有C#编写


*********
RocksDB ：
http://rocksdb.org/
API: C++.
C++编写，FaceBook提高levelDB的速度超过内存数据吞吐量而设计，嵌入式解决方案。

*********
TreodeDB ：
http://https//github.com/Treode/store
API: Scala
Scala编写，
复制：读写复制
分片：复制队列数组上的哈希键
并发：Optimistic +多版本并发控制
提供多层原子级写操作。
公开的API支持HTTP Etags

*********
BoltDB：
https://github.com/boltdb/bolt
使用go编写的键值对数据库
*********
其他数据库：
[Scality »
http://www.scality.com/
, KaTree » 
http://www.katree.net/
TomP2P »
http://tomp2p.net/
, Kumofs » 
http://github.com/etolabo/kumofs
, TreapDB »
http://code.google.com/p/treapdb/
, Wallet » 
https://github.com/YaroslavGaponov/wallet
, NoSQLz »
http://nosqlz.com/
, NMDB, luxio, actord, keyspace, flare, schema-free, RAMCloud]

[SubRecord, Mo8onDb, Dovetaildb]

*********
---------------------------------------------
【图数据库 http://www.graph-database.org/ 】
Neo4J ：
http://www.neo4j.org/ 
API：多语言，
Protocol：java嵌入式/REST
Query Method : SparQL,原生java api jRuby
复制：讲点mysql风格的主/从复制
编写语言：java
并发：非阻塞读写锁节点/关系直到提交
杂项：ACID
可能： 视频 »
http://www.infoq.com/presentations/emil-eifrem-neo4j
, 好的博客 »
http://blog.neo4j.org/



*********
Infinite Graph ：
http://www.infinitegraph.com/
API: Java,
Protocol: 直接语言结合
Query Method ：Graph Navigation API, Predicate Language Qualification,
编写语言：java(核心是C++)
数据模型：标记的有向多重图
并发：更新子图锁，非阻塞并发
*********
Sparksee：
http://www.sparsity-technologies.com/dex.php
API: Java, .NET, C++, Python, Objective-C,
Blueprints 接口 
Protocol: Embedded
Query Method: Graph Navigation API, Predicate Language Qualification + Gremlin
编写语言：C++
数据模型：标记的有向属性图
支持并发
杂项：ACID，免费社区版高达100万对象
链接：Intro »
http://www.slideshare.net/SparsityTechnologies/sparksee-overview
, Technical Overview »
http://www.slideshare.net/SparsityTechnologies/sparksee-technology-overview

*********
TITAN:
https://github.com/thinkaurelius/titan/wiki
API: Java, Blueprints, Gremlin, Python, Clojure 
Protocol: Thrift, RexPro(Binary), Rexster(HTTP/REST)
Query Method: Gremlin, SPARQL
编写语言：java
数据模型：标记的属性图，定向，多图邻接表
并发：ACID 可调 C
复制：多主节点
证书：apache 2
可插拔后台：Cassandra, HBase, MapR M7 Tables, BDB, Persistit, Hazelcast 
链接：TITAN用户组https://groups.google.com/forum/#!forum/aureliusgraphs
*********
InfoGrid :
http://infogrid.org/
API:JAVA.HTTP/REST
Protocol: as API + XPRISO, OpenID, RSS, Atom, JSON,java 嵌入式
Query Method:web用户接口， RSS, Atom, JSON 输出, Java 原生
复制：对等
编写语言：java
并发：并发读写锁在同一Meshbase
杂项：报告http://www.slideshare.net/infogrid/info-grid-core-ideas

*********
HyperGraphDB :
http://www.kobrix.com/hgdb.jsp
API: Java
编写语言：java
Query Method :java 或者P2P
复制：P2P
并发：STM
杂项：开源、尤其是AI和语义网

*********
GraphBase ：
http://graphbase.net/
基于子图的API，查询语言，工具和事务。嵌入式java，远程代理 java或者TEST。分布式存储和处理。读写所有节点。权限和约束框架。对象存储，顶点嵌入代理。支持多图模式。java语言编写

*********
Trinity ：
http://research.microsoft.com/en-us/projects/trinity/
API: C#, Protocol: C#语言绑定
Query Method: Graph Navigation API,
复制：主节点的P2P
编写语言：C#
并发：事务更新的在线查询模式，在批处理模式下喜爱的非阻塞读
杂项：分布式内存存储，并行图计算平台(微软研究项目)


*********
AllegroGraph：
http://www.franz.com/agraph/
API: Java, Python, Ruby, C#, Perl, Clojure, Lisp
Protocol: REST, 
Query Method: SPARQL 和 Prolog,
库：Social Networking Analytics 和 GeoSpatial
编写语言：Common Lisp
链接：学习中心http://www.franz.com/agraph/support/learning/
视频http://www.franz.com/agraph/services/conferences_seminars/

*********
BrightstarDB： 
http://www.brightstardb.com/
原生的,.net的代码第一实体框架语义Web数据库。LINQ和OData支持
API：C#
Protocol：SPARQL HTTP, C#
Query Method: LINQ, SPARQL
编写语言：C#

*********
Bigdata ：
http://www.systap.com/bigdata.htm
API: Java, Jini service discovery
并发：非常高(MVCC)
编写语言：java
杂项：GPL+社区证书，
数据：RDF数据接口，动态密钥范围分片指标
杂项：博客http://www.bigdata.com/blog
(并行数据库，高可用架构，历史上不朽的数据库视图)

*********
Meronymy ：
http://www.meronymy.com/
RDF 企业数据库管理系统。它支持跨平台还可以在多数编程语言上使用。主要特点：高性能，通过ACID保证数据库事务，带ACL的安全保证，SPARQL和SPARUL,ODBC和JDBC驱动 。RDFs http://en.wikipedia.org/wiki/Meronymy_SPARQL_Database_Server


*********
WhiteDB：
http://whitedb.org/
WhiteDB 是一个快速轻量型的图/n个元组共享内存数据库库，使用C专注于速度、可移植性和简单易用而编写。可在linux和windows下使用，双证书：GPLV3和一个免费的非商业牌照

*********
OpenLink Virtuoso ：
http://www.openlinksw.com/
混合DBMS模式：关系，文档和图

*********
VertexDB：
http://www.dekorte.com/projects/opensource/vertexdb/
*********
FlockDB 
http://github.com/twitter/flockdb
by twitter » 
http://gigaom.com/2010/04/12/twitter-open-sources-the-home-of-its-social-graph/
»
http://github.com/twitter/flockdb
*********
weaver
http://weaver.systems/
可扩展，一致性和快速

*********
BrightstarDB：
http://www.brightstardb.com/
*********
Execom IOG：
http://iog.codeplex.com/
*********
Fallen8 ：
http://www.fallen-8.com/
Github »
https://github.com/cosh/fallen-8
*********
其他相关：
[Java Universal Network / Graph Framework, OpenRDF / Sesame, Filament, OWLim, NetworkX, iGraph, Jena]
SPARQL实现列表 链接http://www.w3.org/wiki/SparqlImplementations


*********
【多模式数据库】
ArangoDB ：
http://www.arangodb.org/ 
API: REST, Graph Blueprints, C#, D, Ruby, Python, Java, PHP, Go, Python 等等
数据模型：K/V, JSON & graphs with shapes
Protocol: 使用JSON的HTTP
 Query Method : 声明的AQL，示例查询，map/reduce, key/value
 复制：主从
 分片：自动和可配置的
 编写语言： C/C++/Javascript(V8 集成)
并发：MVCC,可调的
杂项：存储过程(Ruby 和javascript)，
许多指数如secondary, fulltext, geo, hash, Skip-list, bit-array, n-gram, capped collections

*********
OrientDB ：
http://www.orientechnologies.com/

语言：java
模式：拥有对象数据库，文档数据库，图数据库和key-value数据库
编写语言：java
Query Method ：原生和SQL
杂项：快速、轻巧，AICD级恢复

*********
Datomic :
http://www.datomic.com/ 
API: 许多JVM语言, 
Protocol: 原生 + REST,
Query Method: 数据记录和自定义扩展
可扩展：通过底层数据库(in-mem, DynamoDB, Riak, CouchBase, Infinispan)的弹性扩展
编写语言：Clojure
并发：ACID
杂项：智能缓存，非限制读扩展性，全文搜索，cardinality，双向图遍历，喜好 Clojure + Storm

*********
FoundationDB :
http://www.foundationdb.com/
整齐的键值对存储带有多键ACID事务，复制存储和容错，基于无共享，分布式架构
API:Python, Ruby, Node, Java, C
编写语言：FLOW，C++
数据模型：元组层，数组，表，SQL，图，文档，时间序列和地理空间。
*********
FatDB：
http://fatcloud.com/net_nosql_database.html 
紧密整合sqlserver的.net解决方案
API:C#
protocol:Protobuf or Raw Binary
Query Method: LINQ
复制：所有对等网络，多一致性策略
编写语言：C#和.net
并发：多策略
证书：免费社区版和商业版
杂项：双向sqlserver同步，整合文件管理系统，异步工作队列，统一路由，容错，Hosting agnostic (in-house, AWS, Azure等等)
链接：自由下载http://www.fatcloud.com/download.html
*********
AlchemyDB ：
http://code.google.com/p/alchemydatabase/
图数据库+RDBMS+键值存储+文档存储。AlchemyDB是一个低延时高吞吐量的NewSQL关系数据库嵌入到nosql数据库reids中。广泛的数据存储端的脚本是通过嵌入lua提供。收购并整合进


*********
CortexDB ：
http://cortex-ag.com/cortexdoku/cms.php?i=206df578da20202020212024
CortexDB 是动态无schema多model数据库提供了几乎现在已经nosql数据库类型(k-v存储，文档存储，图存储，多值存储，列存储)的所有优点和动态重新组织在连续操作，管理分析和事务数据为敏捷软件配置，改变FLY上的需求，自服务和低占用

*********
====================================
接下来的部分包含NOSQL系统
大多不是因为Web2.0的需要而开始，但值得一看的注明的非关系型解决方案

*********
-------------------------------------
【对象数据库】http://odbms.org/
Versant ：
http://www.versant.com/ 
Protocol： Java, C#, C++, Python.
图式：语言类模式(易变)
模式：总是一致和最终一致
复制：同步容错复制和异步对等复制
并发：基于锁的对象和optimistic 
可扩展性：在运行时可添加物理节点到集群中并在没有应用代码的节点间迁移对象。
杂项：通过并行类SQL查询的MapReduce访问逻辑节点组


*********
db4o ：
http://db4o.com/
API: Java, C#, .Net 语言
protocol：语言
Query Method：QBE，Soda,原生查询，LINQ(.net)
复制：db4o到db4o 和dRS到关系数据库
编写语言：java
并发：ACID序列
杂项：嵌入库


*********
Objectivity ：
http://www.objectivity.com/
 API: Languages: Java, C#, C++, Python, Smalltalk, SQL access through ODBC
Schema ：原生语言类模型，引用、互操作的访问所有绑定语言的直接支持；
64位唯一对象ID支持多个EB字节
平台：32位和64位的windows、linux、mac、OSX、类Unix
模型：总是一致性(ACID)
并发：集群上对象水平的锁
可扩展性：唯一的分布式架构，客户端和服务端的动态添加和删除，云环境读操作。
复制：带有容错访问对等分区的同步复制


*********
GemStone/S：
http://gemtalksystems.com/
API: Java, C, C++, Smalltalk 
Schema: 语言类模型(language class model) 
平台：Linux, AIX, Solaris, Mac OSX, Windows clients 
模式：重视一致性(ACID)
复制：每个节点的共享页缓存，热备份失败切换
并发：基于锁的对象和optimistic
可扩展：任意数量节点
杂项：通过GemConnect的SQL

*********
Starcounter：
http://www.starcounter.com/
API: C# (.NET 语言),
Schema: 原生语言类模型
Query Method ：SQL
并发：完全ACID兼容
存储：带有磁盘上事务安全的内存存储
可靠性：完全检查点恢复
杂项：VMDBMS-因最大性能和易用而整合DBMS和虚拟机，


*********
Perst ：
http://www.mcobject.com/perst
API: Java,Java ME,C#,Mono
Query Method：通过Perst集合，QBE，原生查询，LINQ和原生全文搜索，JSQL的面向对象

复制：异步和同步(主从)
编写语言：java C#
缓存：对象缓存(LRU,弱和强)，页工具，内存数据库
并发：Pessimistic+optimistic (MVCC) + async or sync (ACID) 
索引类型：多种树模型和时间序列
杂项：嵌入式库，加密，自动恢复，原生全文搜索，在线和离线备份

*********
VelocityDB ：
http://www.velocitydb.com/
100%纯C#编写
并发：ACID/事务,悲观/乐观锁
杂项：紧凑数据，B树索引，LINQ查询，64位对象标示符支持多个百万级数据库和高性能。大约400K作用的简单DLL部署

*********
HSS Database ：
http://highspeed-solutions.net/db.aspx
100%纯C#编写
3.0版(高速解决方案数据库)是一个基于客户端，零配置，零shema演变，ACID/事务，LINQ查询，微软.net4/4.5平台DBMS，win8，wp7/7.5/8,Silverlight5,IPhone 的MonoTouch和Android的 MonoTouch
*********
ZODB ：
http://zodb.org/ 
API: Python, 
Protocol: Internal, ZEO, 
Query Method: 直接对象访问, zope.catalog, gocept.objectquery, 
复制: ZEO, ZEORAID, RelStorage
编写语言: Python, C 
并发: MVCC,
证书: Zope Public License (OSI approved) 
杂项: 在1998年用于生产
*********
Magma ：
http://wiki.squeak.org/squeak/2665
Smalltalk 数据库，乐观锁和事务等等。

*********
NEO :
http://www.neoppod.org/
API: Python - ZODB "Storage"接口
Protocol: native,
Query Method: transactional key-value, Replication: native
编写语言：Python
并发:MVCC(内置)
证书：GPLv2
杂项：负载均衡、容错、热扩展

*********
siaqodb:
http://siaqodb.com/ 
当前运行在.NET, Mono, Silverlight,Windows Phone 7, MonoTouch, MonoAndroid, CompactFramework平台的对象数据库引擎。实现了同步框架提供者。并且合一和sqlserver同步；
Query Method ：LINQ

*********
Sterling：
http://sterling.codeplex.com/
为.net设置且支持Silverlight 和WP7的轻型的面向对象的数据库。特征是内存中的关键字、索引和触发器，并且支持压缩和数据加密

*********
Morantex ：
http://www.morantex.com/
在数据池中存储.net类。追求速度。整合了sqlserver和支持LINQ

*********
EyeDB ：
http://www.eyedb.org/
EyeDB 是一个LGPL 的OODBMS（面向对象的关系数据库管理系统），提供一个先进的对象模型(继承，集合，数组，方法，触发器，约束和自反性)。提供了一个基于ODMG ODL的对象定义语言，一个为C++和java设置的基于ODMG OQL编程接口的对象查询和操纵语言

*********
FramerD ：
http://www.framerd.org/
一个设计用来支持操纵和知识共享的面向对象的数据库。使用语义网，框架系统和许多智能代理应用来优化密集指针的数据结构。
编写语言：ANSI C

*********
Ninja Database Pro ：
http://www.kellermansoftware.com/p-43-ninja-net-database-pro.aspx
Ninja Database Pro是一个.net平台上ACID兼容关系对象数据库，支持事务，索引，加密和压缩。
当前运行在.net平台的桌面应用中，Silverlight 应用和wp应用中。


*********
NDatabase：
http://ndatabase.wix.com/home
API: C#, .Net, Mono, Windows Phone 7, Silverlight,
Protocol: language,
Query Method: Soda, LINQ (.NET)
编写语言：C#
杂项：嵌入库，索引，触发器，处理循环引用。Linqpad支持，Northwind示例，重构，内存数据库，事务支持(ACID)等等；
文档：
http://ndatabase.net/

*********
PicoLisp：
http://www.picolisp.com/
语言和对象数据库，可以被视为一个数据库开发框架。
Schema: native language class model with relations + various indexes.
查询: language build in + a small Prolog like DSL Pilog
并发：同步+锁
复制：分布式和容错未实现每个默认但是可以使用原生功能实现
编写语言：C(32位)和assembly(64位)

*********
acid-state ：
http://acid-state.seize.it/
API: Haskell,
Query Method: 函数式编程
编写语言：HASKell
并发：ACID, GHC concurrent runtime
杂项：内存数据库，基于磁盘的日志，支持远程访问；
wiki : 
http://acid-state.seize.it/
Docs :
http://hackage.haskell.org/packages/archive/acid-state/0.8.3/doc/html/Data-Acid.html

*********
ObjectDB :
http://www.objectdb.com/
API: Java (JPA / JDO) 
Query method: JPA JPQL, JDO JDOQL
复制：主从
编写语言：纯java
缓存：对象缓存，数据缓存，页缓存，查询结果缓存，查询计划缓存
并发：对象级锁(悲观锁和乐观锁)
索引类型：B树，single，路径和集合
杂项：在2004年用于生产，嵌入模式，C/S模式，自恢复，在线备份

*********
CoreObject ：
http://www.coreobject.org/
CoreObject 是版本控制的面向对象数据库，支持强大的撤销，语义融合和实时协同编辑
MIT证书，API：OBjC
schema：类EMOF
并发：ACID
复制：差动同步
杂项：基于对象图差别、选择性还原、通过版本控制文件的引用、触发器、时态索引和完整性检查的DVCS

*********
其他数据库
[ StupidDB »
http://www.aztec-project.org/
, KiokuDB » 
http://www.iinteractive.com/kiokudb/
(Perl solution), Durus »
http://www.mems-exchange.org/software/durus/
]

*********
--------------------------------------
【网格和云数据库解决方案】

*********
Oracle Coherence ：
http://www.oracle.com/technetwork/middleware/coherence/overview/index.html
Oracle Coherence 提供了分布式、复制、多数据中心、分层(离堆/SSD)和近(客户端)缓存。它也提供了分布式处理、查询、事件和MapReduce、session管理和数据库更新缓存扩展功能。
可选支持网格架构的发开模式

*********
GigaSpaces :
http://www.gigaspaces.com/
流行的基于空间网格解决方案

*********
GemFire ：
http://www.vmware.com/products/application-platform/vfabric-gemfire
GemFire 听过了内存全局分布式数据管理和动态可扩展、超高性能和颗粒级控制支持最苛刻的程序。
与Spring框架的集成。开发者可以快速，简单的为应用提供复杂的数据管理。简单的横向扩展，通过网络切换造成的数据延迟和磁盘I/O甚至可以在应用增长时被避免


*********
Infinispan ：
http://www.jboss.org/infinispan.html
可扩展，高可用(HA)的数据网格平爱，开源，java编写

*********
Queplix ：
http://www.queplix.com/
nosql数据整合环境，可以整合关系型，对象型，大数据-很简单的nosql而且不用sql

*********
Hazelcast ：
http://www.hazelcast.com/
Hazelcast 是一个内存数据网格，提供了分布式数据，使用java和apache2开源证书的动态可扩展性。
它使用java提供了在包含hashmap、队列、锁、topics的单一jar文件的分布式数据结构，并且提供了一个允许你简单编程这些数据机构就像纯java对象的可执行服务，同时受益于对称多处理和跨集群共享很高的摄取数据流和很高的事务负载弹性内存


*********
Crate Data ：
https://crate.io/
一个费共享，面向文档的集群数据库。通过sql访问和内置BLOB支持。使用集群状态实现和弹性搜索节点发现；
证书：APache2.0
Query Method:SQL
客户端：HTTP (REST), Python, Java (JDBC or native), Ruby, JS, Erlang, 
复制和分片：自动和可配置的
编写语言：java
GIthub项目：https://github.com/crate/crate
文档：https://crate.io/docs/

*********
其他数据库
[GridGain
http://www.gridgain.com/
, ScaleOut Software
http://www.scaleoutsoftware.com/
, Galaxy
http://puniverse.github.com/galaxy/

, Joafip
http://joafip.sourceforge.net/
, Coherence, eXtremeScale]


*********
--------------------------------------
【XML 数据库】
EMC Documentum xDB ：[社区系统]
http://www.emc.com/products/detail/software/documentum-xdb.htm

API: Java, XQuery, 
Protocol: WebDAV, web services, 
Query method: XQuery, XPath, XPointer
复制：懒主复制(主/复制)
编写语言：java
并发：带锁的并发读和写操作
事务 isolation。
杂项：完全事务持久DOM；版本化；索引类型；源数据和非xml数据支持；
非限制的横向扩展。
开发者网站：http://developer.emc.com/xmltech

*********
eXist ：
http://exist-db.org/
API: XQuery, 
XML:DB API, DOM, SAX, 
Protocols: HTTP/REST, WebDAV, SOAP, XML-RPC, Atom, 
Query Method: XQuery, 
编写语言：java
并发：并发读，加锁并发写；
杂项：实体web应用可以使用XQuery编写，使用XSLT	,XHTML,CSS和javascript(为了ajax函数)。
在1.4之后添加了基于Apache Lucene新的全文搜索索引，一个轻型的URL重写和MVC框架和支持XProc

*********
Sedna：
http://modis.ispras.ru/sedna/
杂项：ACID事务，安全，指数，热备份，灵活xml处理工具包括w3c 的XQuery实现。触发器整合XQuery带有全文搜索工具和节点级别更新语言


*********
BaseX ：
http://basex.org/
BaseX 是一个快速的，强大的，轻量级的xml数据库系统并且拥有高一致性支持最新W3C更新和全文推荐的XPath、XQuery处理器。C/S架构，ACID事务支持，用户管理，日志记录，开源，BSD证书。使用java编写，运行在BOX外


*********
Qizx ：
http://www.xmlmind.com/qizx/
社区和开源版本
API：java
protocol：HTTP，REST
Query Method ：XQuery,XQuery全文，XQuery更新
编写语言：java
并发：并发读写
杂项：TB级别扩展，强调查询速度

*********
Berkeley DB XML :
http://www.oracle.com/database/berkeley-db/xml/index.html
API:多语言
编写语言：C++
Query Method：XQuery，
复制：主从
并发：MVCC
证书：sleepycat

*********
其他数据库
[ Xindice 
http://xml.apache.org/xindice/
Tamino 
http://www.softwareag.com/de/products/wm/tamino/
]

*********
------------------------------------------
【多维数据库】

*********
Globals: 
http://globalsdb.org/
通过系统内置的多位数组，Node.js，基于API(java/.net)的数组和基于java的文档API


*********
Intersystems Cache:
http://www.intersystems.com/
后关系型系统，多维数组API，对象API，关系支持(完全SQL,JDBC和ODBC等等)和文档API是在2012.2月版新加的。
windwos、linux和openVMS支持-


*********
GT.M:
http://fis-gtm.com/
API: M, C, Python, Perl, 
Protocol: native, inprocess C
杂项：M/DB for SimpleDB compatible HTTP
http://www.mgateway.com/mdb.html
MDB：X for xml
特点：占用空间小。TB级可扩展，unicode支持，数据库加密，安全，ACID事务(单点)，最终一致(复制)
证书：AGPL v3在linux上
链接：http://www.slideshare.net/robtweed/gtm-a-tried-and-tested-schemaless-database

*********
SciDB ：
http://scidb.org/
为科学家用的数组数据模型
paper
http://scidb.org/Documents/SciDB-VLDB09-paper.pdf
, » poster
http://scidb.org/Documents/SciDB-VLDB09-poster.pdf
, » HiScaBlog
http://highscalability.com/blog/2010/4/29/product-scidb-a-science-oriented-dbms-at-100-petabytes.html


*********
MiniM DB:
http://www.minimdb.com/
多维数组
API: M, C, Pascal, Perl, .NET, ActiveX, Java, WEB
支持windows和linux

*********
rasdaman: 
http://www.rasdaman.org/
简述：Rasdaman 是一个应用科学数据库，允许通过sql风格查询语言存储和检索无大小限制的多维光栅数据（数组）
API：C++/java
编写语言：C++
Query Method ：类SQL查询语言-rasql，通过wcps、WCS、WPS以及OGC标准
链接：http://www.rasdaman.com/

*********
DaggerDB：
.net,C#编写的新的实时分析数据库，兼容ACID，流长的.net查询API，C/S或者进程内，内存中和持久性模式
*********
其他数据库
[ EGTM: GT.M for Erlang
http://github.com/ztmr/egtm
, "IODB: EGTM-powered ObjectDB for Erlang 
http://www.idea.cz/technology
]
*********
-------------------------------------------
【多值数据库】
U2(UniVerse, UniData):多值数据库
http://www.rocketsoftware.com/u2
数据结构：多值、支持嵌套实体，虚拟元数据
API：Basic，intercall，socker,.net和java API
IDE: Native, Record Oriented, 
可扩展性：自动表空间分配
protocol：Client Server, SOA，Terminal Line, X-OFF/X-ON, 
编写语言：C
Query Method ：原生mvQuery，检索和UniQuery和SQL；
复制：热备用
并发：记录和文件锁(细粒度和粗粒度)

*********
OpenInsight ：
http://www.revelation.com/index.php/features
API: Basic+, .Net, COM, Socket, ODBC, Protocol: TCP/IP, Named Pipes, Telnet, VT100. HTTP/S 
Query Method: RList, SQL & XPath
编写语言：原生4GL,C,C++,BASIC+,.NET,java
复制：热备用
并发：表或者行锁，基于可选事务，提交，回滚
数据机构：关系或者多值，支持嵌入实体可扩展性；动态行或者表大小


*********
TigerLogic PICK：(D3, mvBase, mvEnterprise)
http://www.tigerlogic.com/tigerlogic/pick/database/pickdatabase.jsp
数据结构：鼎泰多位PICK数据模型，多值，字典驱动
API：.net,JAVA,PHP,C++
protocol：C/S
编写语言：C
Query Method ：AQL，SQL，OBDC，Pick/Basic
复制：热备份，FFR，事务日志，实时复制
并发：行级锁
连通性：OSFI，ODBC，web-services，web-enabled
安全：文件级128位AES加密


*********
Reality ：[Northgate IS]
http://www.northgate-reality.com/
原始的多值数据集数据库，虚拟机，调查和快速开发环境，delivers超效率，可扩展性和弹性。为web扩展，内置自动调整，故障保护等等。互操作性包括web service，java类，xml,ActiveX,Socket,C.对于那些不得不与SQL世界互操作的地，ODBC/JDBC和双向透明SQL数据访问。

*********
OpenQM ：
http://www.openqm.com/
支持内嵌数据。完全自动表空间分配。通过任务锁，文件锁和共享/独占记录锁并发控制。不区分大小写选项，次键指数。完整数据复制。QMBasic 编程语言开快速开发。面向对象编程整合进QMBasic。
QMClient 链接自VB，PB，delph,PureBasic, ASP, PHP, C 等等。扩展多值查询语言


*********
Model 204 Database ：
http://www.rocketsoftware.com/m204
运行在IBD大型机(IBM z/OS, z/VM, zVSE)的高性能DBM，+SQL接口并且内嵌实体支持
API：原生4GL,SQL,Host Language (COBOL, Assembler, PL1)API,ODBC, JDBC, .net, Websphere MQ, 
Socket可扩展性：自动表空间分配，64位支持
编写语言：IBM汇编程序，C
Query Method ：SOUL,SQL,RCL(客户端的原生语言调用)
并发：记录和文件级锁
链接： TN3270, Telnet, Http

*********
Tieto TRIP：
http://www.tieto.com/services/information-management/enterprise-content-management-services/multimedia-content-management-trip
混合数据库/带多值，文档，关系，xml和图型的数据库特点的查询引擎系统，在1985年适用于生产用于高性能搜索和检索解决方案。全文搜索，文本分类，近似查询，结构排序，实时facets，Unicode，中文字段等等
平台： Windows, Linux, AIX 和 Solaris
API: .NET, Java and C/C++
Query methods: native (CCL), SQL 子集, XPath. Commercial.

*********
ESENT：
http://msdn.microsoft.com/en-us/library/windows/desktop/gg269259%28v=EXCHG.10%29.aspx
微软所开发,ISAM 存储技术，使用索引或游标导航访问，非规范化模式。稀疏列、多值列和稀疏且富索引的宽表。
C#和Delphi启动。
多个MS产品交易的后台
*********
jBASE ：
http://www.jbase.com/index.html
http://en.wikipedia.org/wiki/InfinityDB

*********
-------------------------
【事件源】
Event Store：
http://geteventstore.com/
--------------------------
【网络模型】
Vyhodb：
http://www.vyhodb.com/
面向服务，无模式，网络数据模型的DBMS
客户端应用调用Vyhodb服务的方法。
使用java编写和内部部署vyhodb
Vyhodb服务读和修改存储数据
API：java
protocol：RSI(远程服务调用)
ACID完全支持
赋值：异步主从
杂项：在线备份
证书：专有
*********
-------------------------
【其他nosql相关数据库】
IBM Lotus/Domino ：
http://www-01.ibm.com/software/lotus/
类型：文档存储
API：Java, HTTP, IIOP, C API, REST Web Services, DXL
语言：Java, JavaScript, LotusScript, C, @Formulas
protocol：HTTP, NRPC
复制：主/主
编写语言：C
并发：最终一致
缩放：复制集群

*********
eXtremeDB ：
http://www.mcobject.com/extremedbfamily.shtml
类型：内存数据库
编写语言：C
API：C/C++，SQL,JNI,c#(.NET),JDBC
复制：异步和同步(主从)、集群
可扩展性：64位和MVCC


*********
RDM Embedded ：
http://www.raima.com/products/rdme/
API：C++，Navigational C和兼容ACID，多核，支持磁盘和内存的嵌入式解决方案
分布式能力，在线热备份，支持所有主要平台。支持B树和hash索引。
复制：主从
并发：MVCC
C/S：内置中/在过程中

*********
ISIS Family：
http://www.unesco.org/webworld/isis/isis.htm
半结构化数据库
http://bvsmodelo.bvsalud.org/php/level.php?lang=en&component=31

*********
Moonshadow :
http://www.moonshadowmobile.com/data-visualization/big-data-visualizer/
Nosql,内存中，平面文件，基于云。
API接口，小数据空间占用和分畅快的数据检索。存储2亿记录和200属性在仅仅10G。每个CPU核心每秒检索1.5亿记录。通常用于图上的可视化大数据
编写语言：C

*********
VaultDB ：
http://www.rediosoft.com/
下一代NoSQL加密文档存储，多容器/组加密
并发：指数，ACID事务，复制和PKI 管理，支持PHP和其他。
编写语言：C++。
商业和免费版本
API：JSON

*********
Prevayler ：
http://www.prevayler.org/
java RAM数据结构日志处理

*********
Yserial ：
http://yserial.sourceforge.net/
Python包装在sqlite3上

*********
-------------------------------------
【未分类和为解决分类】
Btrieve (Pervasive软件开发)：
键/索引/元组数据库。
使用Using Pages. 
http://www.wordiq.com/definition/Btrieve
» (faq »
http://www.rothgeb.net/wordpress-2.7.1/?page_id=64
)

*********
KirbyBase ：
https://rubygems.org/gems/KirbyBase
Ruby编写。
Github地址：https://github.com/gurugeek/KirbyBase

*********
Tokutek:
http://tokutek.com/
*********
Recutils:
http://www.gnu.org/software/recutils/
包含记录和域的的文本文件的GNU工具
手册：http://www.gnu.org/software/recutils/manual/index.html

*********
FileDB:
http://www.eztools-software.com/tools/filedb/
主要针对Silverlight/Windows Phon开发，但是对任何在一个简单的本地数据库是必需的、轻型(小于50k).net应用也很棒。每个文件存储一张表，包括索引，WP7和Silverlight和.NET,的编译版本。
可快速免费用于应用
*********
CodernityDB ：
http://labs.codernity.com/codernitydb/
Python编写

*********
illuminate Correlation Database »
http://www.datainnovationsgroup.com/
, FluidDB (Column Oriented DB) »
http://doc.fluidinfo.com/fluidDB/
,Fleet DB »
http://fleetdb.org/
, Btrieve, Twisted Storage »
http://twistedstorage.com/
, Java-Chronicle »
https://github.com/peter-lawrey/Java-Chronicle
, Ringo, Sherpa, tin, Dryad, SkyNet, 
Disco可能是最古老的NoSQL数据库(和MUMPS和IBM的IMS和IDM一起[1968,1964])

通用数据库系统：http://documentation.softwareag.com/adabas/ada814mfr/adamf/concepts/cfadais.htm

IBM的VSAM也是一个很好的备选 
