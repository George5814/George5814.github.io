/**************************************************************/
NoSQl产生原因：
	NoSQL，泛指非关系型的数据库。随着互联网web2.0网站的兴起，传统的关系数据库在应付web2.0网站，特别是超大规模和高并发的SNS类型的web2.0纯动态网站已经显得力不从心，暴露了很多难以克服的问题，而非关系型的数据库则由于其本身的特点得到了非常迅速的发展。NoSQL数据库的产生就是为了解决大规模数据集合多重数据种类带来的挑战，尤其是大数据应用难题。

/**************************************************************/
NoSQL是什么？
	NoSQL(NoSQL = Not Only SQL )，意即“不仅仅是SQL”，是一项全新的数据库革命性运动，早期就有人提出。NoSQL的拥护者们提倡运用非关系型的数据存储，相对于铺天盖地的关系型数据库运用，这一概念无疑是一种全新的思维的注入。

/**************************************************************/
NoSql的适用场景
	NoSQL数据库在以下的这几种情况下比较适用：1、数据模型比较简单；2、需要灵活性更强的IT系统；3、对数据库性能要求较高；4、不需要高度的数据一致性；5、对于给定key，比较容易映射复杂值的环境。

/**************************************************************/
NoSQL的优点：
	1.易扩展
		NoSQL数据库种类繁多，但是一个共同的特点都是去掉关系数据库的关系型特性。数据之间无关系，这样就非常容易扩展。也无形之间，在架构的层面上带来了可扩展的能力。
	2.大数据量，高性能
		NoSQL数据库都具有非常高的读写性能，尤其在大数据量下，同样表现优秀。这得益于它的无关系性，数据库的结构简单。一般MySQL使用Query Cache，每次表的更新Cache就失效，是一种大粒度的Cache，在针对web2.0的交互频繁的应用，Cache性能不高。而NoSQL的Cache是记录级的，是一种细粒度的Cache，所以NoSQL在这个层面上来说就要性能高很多了。
	3.灵活的数据类型
		NoSQL无需事先为要存储的数据建立字段，随时可以存储自定义的数据格式。而在关系数据库里，增删字段是一件非常麻烦的事情。如果是非常大数据量的表，增加字段简直就是一个噩梦。这点在大数据量的web2.0时代尤其明显。
	4.高可用
		NoSQL在不太影响性能的情况，就可以方便的实现高可用的架构。比如Cassandra，HBase模型，通过复制模型也能实现高可用。

/**************************************************************/
NoSql的缺点：
	1. 不提供对SQL的支持：如果不支持SQL这样的工业标准，将会对用户产生一定的学习和应用迁移成本;
	2. 支持的特性不够丰富：现有产品所提供的功能都比较有限，大多数NoSQL数据库都不支持事务，也不像mysql和Oracle那样能提供各种附加功能，比如BI和报表等;
	3. 现有产品的不够成熟：大多数产品都还处于初创期，和关系型数据库几十年的完善不可同日而语;

/**************************************************************/
NoSql的分类：
	1.key-value存储
		代表：Berkeley DB、Hibari、Leveldb、memcachedb、Redis、Tair、DynamoDB
		特点：可以通过key快速查询到其value。一般来说，存储不管value的格式，照单全收。（Redis包含了其他功能）
		优点：快速查询
		不足：存储数据缺少结构化
		数据模型：一系列键值对
		典型应用：内容缓存、适合混合工作负载并扩展大的数据集

	2.列式存储
		代表：Hbase、Cassandra、Hypertable、GBase、Tera
		特点：顾名思义，是按列存储数据的。最大的特点是方便存储结构化和半结构化数据，方便做数据压缩，对针对某一列或者某几列的查询有非常大的IO优势。
		优点：查找速度快、可扩展性强、更容易进行分布式扩展
		不足：功能相对局限
		数据模型：以列族式存储，将同一列数据存在一起
		典型应用：分布式文件系统

	3.文档存储
		代表：MongoDB、CouchDB、Couchbase、EJDB、MarkLogic、SequoiaDB、SimpleDB
		特点：文档存储一般用类似json的格式存储，存储的内容是文档型的。这样也就有有机会对某些字段建立索引，实现关系数据库的某些功能。
		优点：数据结构要求不严格
		不足：查询性能不高，而且缺乏统一的查询语法
		数据模型：一系列键值对
		典型应用：WEB应用

	4.图存储
		代表：Cayley、Neo4j
		特点：图形关系的最佳存储。使用传统关系数据库来解决的话性能低下，而且设计使用不方便。
		优点：利用图结构相关算法
		不足：需要对整个图做计算才能得出结果，不容易做分布式的集群方案。
		数据模型：图结构
		典型应用：社交网络、推荐系统等，专注于构建关系图谱

	5.对象存储
		代表：db4o、Versant
		特点：通过类似面向对象语言的语法操作数据库，通过对象的方式存取数据。
		优点：高性能、易嵌入、零管理。db4o 的 dRS（db4o Replication System）可实现 db4o 与关系型数据库的双向同步（复制）

	6.xml数据库
		代表：BaseX、sedna
		特点：高效的存储XML数据，并支持XML的内部查询语法，比如XQuery,Xpath。
		优点：提供了高效的 XPath 和 XQuery 的实现，有前端界面.基于 ACID 安全事务、用户和密码管理、事务日志等.更能支持大型XML文档的存储
		不足：在大量数据存储时不如其他类型数据库高效
		数据模型：XML文档

	当前多数将nosql分为前四类
/**************************************************************/
Nosql产品：
	Accumulo	(Key-Value型)
	Arangodb  （文档型）
	Aerospike (Key-Value型)
	BaseX	（XML型）
	BeansDB (简化版的Dynamo)
	Berkeley DB	(Key-Value型)
	Cassandra	(Key-Value型)(列存储)
	Cayley  (Graph型)
	Couchbase (CouchBase = CouchDB + MemBase)（文档型）
	CouchDB	（文档型）
	db4o	(面向对象型)
	DynamoDB	(Key-Value型)
	EJDB	（文档型）
	FatDB	(Key-Value型)
	Flare	(Key-Value型)
	FoundationDB  (Key-Value型)
	ForestDB  (Key-Value型)
	Galaxy (高性能内存数据库网格 )
	GBase 8a (列存储)(国产->南大通用数据技术有限公司)
	HandlerSocket （日本，以mysql插件形式运行）
	HBase	（列式存储）
	Hibari	(Key-Value型)
	HyperLevelDB  （改进自Leveldb）(Key-Value型)
	Hypertable	（列式存储）
	InfiniteGraph	(Graph型)
	iPage	（基于文件）(Key-Value型)（java lib）
	Jackrabbit	（文档型）
	Keyspace  (Key-Value型)
	LedisDB  (基于Leveldb，类似redis)(Key-Value型)
	Leveldb  (Key-Value型)
	LiteDB  (嵌入式)（文档型）
	MarkLogic	（文档型）
	MapDB	(嵌入式Java数据库引擎)
	Maxtable	（高性能、可扩展的、PB级海量数据处理系统）
	Memcached	(Key-Value型)
	memcachedb  (Key-Value型)给memcached分布式缓存服务器添加了Berkeley DB的持久化存储机制和异步主辅复制机制
	Memlink		(Key-Value型)
	MongoDB		（文档型）
	Neo4j	(Graph型)
	nessDB		(Key-Value型)
	Oceanbase （分布式关系数据库）(阿里开发现已经用于淘宝和支付宝)
	Oracle NoSQL Database	(Key-Value型)
	Orient DB	（文档型）
	PouchDB （最便携的CouchDB）（文档型）
	RavenDB （文档型）
	Redis	(Key-Value型)
	RethinkDB	（ JSON 文档型）
	Riak (Key-Value型)
	RocksDB (基于Leveldb)(Key-Value型)
	sedna	（XML型）
	SequoiaDB	（文档型）(国产->第一个商用nosql数据库)
	SimpleDB	（文档型）
	SSDB	(基于Leveldb,替代 Redis)(Key-Value型)
	Tair	(Key-Value型)(国产-> 淘宝开发)
	Tera (使用了bigtable的数据模型)（列式存储）
	tiger	(Key-Value型)
	TomP2P  (Key-Value型)
	Tokyo Cabinet/Tokyo Tyant  (Key-Value型)
	TreapDB  (Key-Value型)	
	UnQLite  (嵌入式)（文档型）(Key-Value型)
	Versant	(面向对象型)
	Voldemort (Key-Value型)
	WhiteDB		(轻量级 NoSQL )
	xuncache	(Key-Value型)

/**************************************************************/

/**************************************************************/

/**************************************************************/

/**************************************************************/

/**************************************************************/

/**************************************************************/

