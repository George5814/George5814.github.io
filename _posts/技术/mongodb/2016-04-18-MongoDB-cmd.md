## MongoDB 命令

[toc]

RDBMS和mongodb的对应术语
|RDBMS|MongoDB|
|---|---|
|数据库|数据库|
|表|集合|
|行|文档|
|列|字段|
|表联合|嵌入文档|
|主键|主键|

### 1.显示所有的数据库

**管理员可用**

只要是管理员，在哪一个数据库下操作都无所谓

	show dbs
	
### 2.显示当前的数据库

	db
	
### 3.切换数据库或创建数据库

如果数据库不存在则创建数据库，存在则直接切换

	use admin ##admin 为指定的数据库名
	
### 4.  删除当前数据库
	
	db.dropDatabase()
	
### 5. 数据库中插入文档
	
db.test.insert(
```json
	{
		name:"test3",
		age:12,
		sex:false
	}
```
)

如果当前字段不存在， 数据库会自动创建。如果当前字段已经没有值了，则不会显示


### 6. 查询已经存在的数据

查询指定条件的文档

	db.test.find({name:"test3"})
	
查询所有文档

	db.test.find()
	
格式化显示查询的所有文档
	
	db.test.find().pretty()
	
**插入文档你也可以使用 db.col.save(document) 命令。如果不指定 _id 字段 save() 方法类似于 insert() 方法。如果指定 _id 字段，则会更新该 _id 的数据。**

### 7.更新文档
{name:"test4"} 相当于 RDBMS中的where name='test4' 

{$set:{name:"test4_modify"}} 相当于RDBMS中的set name='test4_modify'

	db.test.update({name:"test4"},{$set:{name:"test4_modify"}})
	
### 7.1其他更新文档操作

只更新第一条记录：

	db.col.update( { "count" : { $gt : 1 } } , { $set : { "test2" : "OK"} } );

全部更新：

	db.col.update( { "count" : { $gt : 3 } } , { $set : { "test2" : "OK"} },false,true );

只添加第一条：
	
	db.col.update( { "count" : { $gt : 4 } } , { $set : { "test5" : "OK"} },true,false );

全部添加加进去:

	db.col.update( { "count" : { $gt : 5 } } , { $set : { "test5" : "OK"} },true,true );

全部更新：

	db.col.update( { "count" : { $gt : 15 } } , { $inc : { "count" : 1} },false,true );

只更新第一条记录：

	db.col.update( { "count" : { $gt : 10 } } , { $inc : { "count" : 1} },false,false );
	
	
### 8.删除文档

#### 8.1根据id删除文档

	 db.test.remove({_id:ObjectId("54c955492b7c8eb21818bd09")})
	 
#### 8.2 删除当前数据库的所有文档

	db.test.remove({})
	
### 9.$type 操作符

查询String 类型的title

	db.test.find({"title" : {$type : 2}})、
	
### 10.limit()和skip()

limit：限制最大查询数量，skip，跳过查询到的前n条数据

	db.test.find({"title" : {$type : 2}}).skip(1).limit(2)	

### 11.sort()排序
	
	1：升序，-1：降序


将查询出的数据升序排列

	 db.test.find({"title" : {$type : 2}}).sort({likes:1})
	 
### 12.ensureIndex() 创建索引

创建单个字段索引

	db.test.ensureIndex({"title":1}	)

创建多个字段索引（复合索引[RDBMS]）

	db.test.ensureIndex({"title":1,"description":-1})
	
后台创建索引
	
	db.test.ensureIndex({"title":1,"description":-1}, {background: true})


### 13.aggregate()聚合

对应RDBMS中的聚合函数

根据by_user分组查询，并将相同值求和.

	db.test.aggregate([{$group : {_id : "$by_user", num_tutorial : {$sum : 1}}}])
	

### 14.$project、$match、$group 显示指定的字段
相当于select后查询的字段

默认中的_id字段是被包含的，设置1为包含，0为不包含

	db.test.aggregate({$project:{"_id":0,title:1}})

匹配likes>100的记录，跳过前两条记录，并只显示likes和_id两个字段：

 db.test.aggregate(
[
{
	$match:
		{
		likes:{
			$gt:100
		}
	}
},
{
	$project:{
		likes:1
	}
} ,
{
	$skip:2
}
])	

### 15.mongodb副本集

服务端启动名为rs0的mongodb实例：

	 mongod --port 27117 --dbpath "/data/db" --replSet rs0

客户端：	 
	
	mongo -u root -p 123456 --port 27117
	
**端口号需要一致**

启动新的副本集

	> rs.initiate()

将mongod1.net:27017添加到副本集中，mongod1.net为主机地址，27017为端口号

	>rs.add("mongod1.net:27017")
	

### 16.Mongodb备份

-h ：host:port

-d : dbname

-o :备份的位置，目录必须已经存在

	 mongodump -h 192.168.31.100:27117  -d test -o /var/mongodb-dump

	
### 17.Mongodb数据恢复

-h ：host:port

-d : dbname

-directoryperdb :备份的位置

--drop ：恢复的时候，先删除当前数据，然后恢复备份的数据

	>mongorestore -h dbhost -d dbname --directoryperdb dbdirectory 
	
### 18.MongoDB监控

	mongostat -u root -p 123456 --authenticationDatabase=test --port 27117
	 
	 
	mongotop -u root -p 123456 --authenticationDatabase=test --port 27117



