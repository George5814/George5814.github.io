---
layout: post
title: Hive编程指南之一
category: 技术
tags:  Hive
keywords: 
description:   hive使用sql语法，简化学习坡度。
---


## Hive QL详解

表的操作语句，数据类型不区分大小写,但SerDe和属性名区分大小写，注释是单引号的字符串

### DDL操作

#### 1)	创建表

语法：

```
Create [external] table [if not exists]  table_name
[(
Col_name data_type [comment col_comment]
)] [comment table_comment];
```

1.	如果需要存储为纯文本文件，使用`STORED AS TEXTFILE`
1.	如果数据需要压缩,使用`STORED AS SEQUENCEFILE`
1.	`INPUTFORMAT`和`OUTPUTFORMAT`定义一个与inputformat和outputformat类相对应的名字作为字符串
1.	 Hive支持带有分区(partition)的表，创建的时候使用PARTITIONED BY语句，可有多个分区，每个分区一个目录。
1.	 表和分区都能对某列CLUSTERED BY操作，将若干个列放在一个桶中。
1.	可以利用SORT BY列存储数据，提高查询性能


**示例：**

**重点：**

1. 不区分大小写
1. 每次创建表都会在hive库的`TBLS`表中创建一条表的描述，并在HDFS的`/user/hive/warehouse`下创建对应表名的目录。


- 例1：创建普通表

```
CREATE TABLE page2 (
	pageId INT,
	pageUrl string,
	userId BIGINT,
	reg_url string,
	ip string COMMENT 'id address'
) COMMENT 'this is the page1 table';
```

- 例2：添加表分区,并用制表符区分同一行的不同字段

```
CREATE TABLE page3 (
	pageId INT,
	pageUrl string,
	userId BIGINT,
	reg_url string,
	ip string COMMENT 'id address'
) COMMENT 'this is the page1 table' 
partitioned BY (dt string, country string) 
ROW format delimited 
FIELDS TERMINATED BY '\001' 
stored AS sequencefile;
```


- 例3：添加聚类存储,将列按照userid进行分区并划分到不同的桶中,
按照pageid值得大小进行排序存储。这样允许用户通过userid属性高效的对集群列进行采样。

```
CREATE TABLE page7 (
	pageId INT,
	pageUrl string,
	userid BIGINT,
	referrer_url string,
	ip string COMMENT "IP address of the user"
) COMMENT 'this is the page1 table' 
PARTITIONED BY (dt STRING, country STRING) 
CLUSTERED BY (userid) SORTED BY (pageid) INTO 32 BUCKETS 
ROW FORMAT DELIMITED 
FIELDS TERMINATED BY '\001' 
COLLECTION ITEMS TERMINATED BY '\002' 
MAP KEYS TERMINATED BY '\003' 
STORED AS SEQUENCEFILE;
```

- 例4：通过LOCATION，指定存储路径

指定位置为:`hdfs://h2m1:8220/user/hive/warehouse/external_cus`,会自动在指定为值创建指定目录`external_cus`

**外部表与其他表都存储在`TBLS`表中，字段信息存储在`COLUMNS_V2`表中，只是TBL_Type字段设置为EXTERNAL_TABLE，其他为MANAGED_TABLE**


```
CREATE external TABLE page_view (
	viewTime INT,
	userId BIGINT,
	page_url string,
	ref_url string,
	ip string COMMENT "ip addr",
	country string
) COMMENT "this is the staging page view table" 
ROW format delimited 
FIELDS TERMINATED BY '\054' 
stored AS textfile 
location 'hdfs://h2m1:8220/user/hive/warehouse/external_cus';
```

#### 2)	删除表

语法：`Drop table table_name;`

用于删除表的元数据和数据，如果配置trash，数据删除到Trash/Current目录，元数据完全丢失。
当删除external定义的表时，表中的数据不会从文件系统中删除

**示例：**

- 例1：`drop table p7;`

#### 3)	修改表

**示例：**

- 例1：重命名表

`alter table page7 rename to p7;`

- 例2：改变列名、类型、位置、注释

将列名userid改为uid，数据类型为string

`alter table page1 change column pageid pId string  comment "pageId";`

将列名uid改为userid，数据类型为string，并置于ip后

`alter table page1 change column uid userid  string comment 'user id' after ip ;`


- 例3：增加\更新列

为表page1增加name和remark列，类型都为string。

`alter table page2 add columns (name string  comment "user name" ,remark string comment "remark");`

使用指定的列修改表，其他没有指定的列就会丢失了。

`alter table page2 replace columns (name string  comment "user name" ,other string comment "remark");`

- 例5：改变表文件格式和组织(只修改表的物理存储属性)

```
Alter table table_name set FILEFORMAT file_format
Alter table table_name CLUSTERED BY (col_name,col_name,…)
[SORTED BY (col_name,…)] INTO num_buckets BUCKETS
```

这些命令只能修改hvie的元数据，不能重组或格式化现有的数据，用户应根据实际数据的分布设置符合元数据的定义。

#### 4)	表分区操作语句

查询时会对整个表扫描，为了只对关心的部分数据进行扫描，hive引入分区、相当于简单的索引功能
创建时指定模式、字段指定虚拟列，表中不存在、可指定多级结构，相当于对目录嵌套、在创建完成后使用之前还需通过alter table 语句添加具体的分区目录才能使用

Hive表分区的命令主要包括创建分区、增加分区和删除分区

- 例1：增加分区
Alter table table_name add partition_spec [LOCATION  ' location1' ]  partition_spec [LOCATION  ' location1' ]
Partition_spec:
	: PARTITION (partition_col=partition_col_value, partition_col=partition_col_value,…)
分区名是字符串时加引号。

```
ALTER TABLE page3 ADD PARTITION (
	dt = '2016-12-22',
	country = 'cn'
) location 'viewfs://hadoop-cluster-jingzz/user/hive/warehouse/partition';
```

- 例2：删除分区

Alter table page_view drop PATITION (de='2015-2-4',country='us')
PS：1.当没有声明表模式的时候不能为表指定具体的分区
2.分区名不能与表属性名重复

#### 5)	创建、删除视图

`Create view [if not exists] view_name [(col_name [comment col_comment],…)]
As select`


视图是只读的，不能用于LOAD\INSERT\ALTER	的目标
视图可能包含order by或limit子句，如果一个引用了视图的查询也包含了这些子句，那么在执行这些子句时，首先查看视图子句，然后返回结果按视图中语句执行
删除视图：
Drop view view_name;
6)	创建、删除函数
Create temporary function function_name;
该语句创建了一个由类名实现的函数，在hive中可以持续使用该函数查询，也可以使用Hive类路径中的任何类。用户可以执行add files 语句将函数类添加到类路径中
Drop temporary function function_name;
7)	展示描述语句
1)	显示表
Show tables identifier_with_wildcards
2)	显示分区
Show partitions table_name;
3)	显示表、分区扩展
Show table external [in | from databases_name] like identifier_with_wildcards [partition(partition_desc)]
4)	显示函数
Show functions;
5)	描述表、列
Describe [extended] table_name [dot col_name]
Describe [extended] table_name [dot col_name([DOT field_name] | [DOT ‘$elem$’] | [DOT $key$’]|[DOT ‘$value$’])*] 
通常只用于调试，不用于平时使用中。
[DOT ‘$elem$’] 数组元素
[DOT $key$’]  图的主键
[DOT ‘$value$’])  图的属性

6)	描述分区
Describe [extended] table_name partition_spec;
数据操作（DML）
1)	向数据表中加载文件
不会对数据进行任何转换，Load只是将数据复制或移动到Hive表对应的位置
语法：
Load data [LOCAL] INPATH 'filepath' [OVERWRITE]
		INTO TABLE table_name
[PARTITION (partcol1=val1,part2=val2,…)]
 PS:1.指定LOCAL,load命令会查找本地文件系统的filepath,
2.filepath:支持相对路径、绝对路径、完整URI，目标可以是表或分区;
如果表包含分区，必须指定每个分区名;
Filepath可以引用文件或目录。
3.没有指定LOCAL，filepath 指向URI，Hive会直接使用URI，没有schema或authority，Hive会使用hadoop配置文件中定义的schema或authority，fs.default.name  指定nameNode的URI；
如果是相对路径，Hive会相对/user/解释。
4.使用OVERWRITE,目标表中的内容会被删除，并将filepath指向的文件\目录中的内容添加到表(或分区)中，如果目标表(或分区)中有文件，且与filepath中文件名冲突，现有文件会被替换。
''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
2)	将查询结果插入Hive表中
语法：
INSERT OVERWRITE TABLE table_name [PARTITION (partcol1=val1,partcol2=val2,…)]
Select_statement1  from from_statement
Hive extension (multiple inserts)
FROM from_statement
INSERT OVERWRITE TABLE table_name1   [PARTITION (partcol1=val1,partcol2=val2,…)]
select_statement1
[INSERT OVERWRITE TABLE table_name2   [PARTITION (partcol1=val1,partcol2=val2,…)]]
Hive extension (dynamic partition inserts):
INSERT OVERWRITE TABLE tableName PARTITION (partcol1=[val1], partcol2=[val2],…)
select_statement1  from from_statement
插入可以针对一个表或分区操作，如果对表进行了划分，在插入时要指定划分列的属性值确定分区。每个select 语句的结果会被写入选择的表或分区中，OVERWRITE 关键字会强制将输出结果写入，输出格式和序列化方式由表的元数据决定。在Hive进行多表插入可以减少数据扫描的次数。
3)	将查询的结果写入到文件系统
INSERT OVERWRITE [LOCAL] DIRECTORY directory1 SELECT … FROM ….
Hive extension (multiple inserts)
FROM from_statement
INSERT OVERWRITE [LOCAL] DIRECTORY directory1 select_statement1
[INSERT OVERWRITE [LOCAL] DIRECTORY directory1 select_statement2]…
目录可以是完整的URI,如果使用LOCAL，Hive会将数据写入到本地文件系统中。
PS：数据写入文件系统时会进行文本序列化，并且每列^A区分，换行表示一行数据结束。如果任一列不是原始类型，这些列会被序列化为json格式
Sql 操作(标准的select语句)
语法：
SELECT [ALL | DISTINCT] select_expr,select_expr,…
FROM table_reference
[WHERE where_condition]
[GROUP BY col_list]
[ CLUSTER BY col_list 
| [DISTRIBUTE BY col_list] [SORT BY col_list]
]
[LIMIT number]

PS:
1.	table_reference 指明查询的输入，可以是表、视图或子查询
select * from t1 
2.	WHERE 
Where_condition 是一个布尔表达式。跟sql中的格式一样
3.	ALL和DISTINCT 
ALL 和DISTINCT 可以定义重复的行是否返回，默认为ALL。
4.	LIMIT
LIMIT 可以控制输出的记录数，随机选取检索结果中相应数目输出：
Select * from t1 limit 5 输出最多5条记录
5.	使用正则表达式
Select ‘(ds|hr)?+.+’ from sales
6.	基于区分的查询
如果一个表是使用PARTITIONED BY 语句生成的，查询可以对输入进行’剪枝’，只对表的相关部分扫描。Hive现只where中指定分区剪枝。
例1：如果表page_view按照date列的值进行分区，查询检索日期 2010-03-01  --- 2010-03-31。
Select page_view.* from page_view where page_view.date >= ‘2010-03-01’ and page_view.date <= ‘2010-03-31’
7.	HAVING 
Hive 不支持having 语句，但可以使用子查询实现
8.	Group by和JOIN的作用与SQL相同。


Hive网络(WEB UI)接口
(1)	分离查询的执行
命令行下，要执行多个查询就要打开多个终端，通过网络接口，可以同时执行多个查询，网络接口可以在网络服务器上管理会话(session)
(2)	远程执行Hive WEB UI，需要在Hive服务端配置文件中配置IP地址和端口号
 
(3)	启动hive网络接口服务
#hive  --service hwi  
(4)	通过浏览器访问：http://masterIP:port/hwi 本例中HTTP://192.168.100.11:9999/hwi 
单击Browse Schema 可以查看当前hive的数据库
Hive查询：首先创建一个session会话，可通过list session连接查看所有的session。
当hive重启后，session信息将全部丢失，会话与认证关联。
在list session处点击action项进行查询操作。
