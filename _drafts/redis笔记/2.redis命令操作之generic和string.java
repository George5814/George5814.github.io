/********  redis 一般命令操作 ************/
redis是键值对的内存数据库，属于Nosql范畴
登录redis自带的客户端
#redis-cli
127.0.0.1:6379>
1.设置redis 键值对
> set foo bar  #设置键foo的值为"bar"
OK #设置成功

2.获得redis 键值对
> get foo #根据键获得值
"bar" #返回的结果

3.删除键值对 
127.0.0.1:6379> del foo foo2 foo3 #删除键值对，可删除多个
(integer) 3 #删除成功的键值对的个数

4.返回空
127.0.0.1:6379> get foo #如果键值对不存在，返回nil
(nil)

5.判断键值对是否存在，返回0：不存在，返回1：存在；只能查询一个键。

127.0.0.1:6379> EXISTS foo
(integer) 0
127.0.0.1:6379> set foo bar
OK
127.0.0.1:6379> EXISTS foo
(integer) 1
127.0.0.1:6379> EXISTS foo foo2 foo3
(error) ERR wrong number of arguments for 'exists' command

6.设置键值对有效期
127.0.0.1:6379> EXPIRE foo 32 #设置foo键有效期为32秒
(integer) 1

7.根据键的表达式，查找符合表达式的键
127.0.0.1:6379> keys foo*
1) "foo3"
2) "foo2"
3) "foo"

8.自动将一个key从一个redis实例传输到另一个	
MIGRATE host port key destination-db timeout
summary: Atomically transfer a key from a Redis instance to another one.
since: 2.6.0

9.将一个key移到另一个数据库
MOVE key db
summary: Move a key to another database
since: 1.0.0

10.检查内部的redis对象
OBJECT subcommand [arguments [arguments ...]]
summary: Inspect the internals of Redis objects
since: 2.2.3

11.从key中移除过期时间
PERSIST key
summary: Remove the expiration from a key
since: 2.2.0

127.0.0.1:6379> set foo bar
OK
127.0.0.1:6379> EXPIRE foo 32
(integer) 1
127.0.0.1:6379> PERSIST foo
(integer) 1

12.获取键值对的存活时间(微秒)
PTTL key
summary: Get the time to live for a key in milliseconds
since: 2.6.0

127.0.0.1:6379> EXPIRE foo 32
(integer) 1
127.0.0.1:6379> pttl foo
(integer) 27944
13.获取键值对存活时间(秒)
TTL key
summary: Get the time to live for a key
since: 1.0.0


13.设置微秒级别的键值对存活时间
PEXPIRE key milliseconds
summary: Set a key's time to live in milliseconds
since: 2.6.0

14.设置微秒级别的unix形式的时间，键值对存活时间
PEXPIREAT key milliseconds-timestamp
summary: Set the expiration for a key as a UNIX timestamp specified in milliseconds
since: 2.6.0

15.随机获取一个键
RANDOMKEY -
summary: Return a random key from the keyspace
since: 1.0.0

127.0.0.1:6379> RANDOMKEY
"foo2"
16.重命名键
RENAME key newkey
summary: Rename a key
since: 1.0.0

127.0.0.1:6379> rename foo12 foo13
OK

17.重命名键，新键不存在时
RENAMENX key newkey
summary: Rename a key, only if the new key does not exist
since: 1.0.0

127.0.0.1:6379> RENAMENX foo13 foo3 #foo3已经存在执行不成功
(integer) 0

18.备份键值对
DUMP key
summary: Return a serialized version of the value stored at the specified key.
since: 2.6.0
127.0.0.1:6379> dump foo
"\x00\x03bar\x06\x00pS!\xe0\x1b3\xc1\x84"

19.还原键值对
RESTORE key ttl serialized-value
summary: Create a key using the provided serialized value, previously obtained using DUMP.
since: 2.6.0

20.获取键的类型
TYPE key
summary: Determine the type stored at key
since: 1.0.0
127.0.0.1:6379> type foo3
string

21.按键排序
SORT key [BY pattern] [LIMIT offset count] [GET pattern [GET pattern ...]] [ASC|DESC] [ALPHA] [STORE destination]
summary: Sort the elements in a list, set or sorted set
since: 1.0.0

/****** string类型的命令操作 ********/
1.将值添加到键的值得后面
APPEND key value
summary: Append a value to a key
since: 2.0.0
127.0.0.1:6379> append foo3 barbar
(integer) 10
127.0.0.1:6379> get foo3
"bar3barbar"

2.
BITCOUNT key [start] [end]
summary: Count set bits in a string
since: 2.6.0
127.0.0.1:6379> bitcount foo3
(integer) 34
127.0.0.1:6379> bitcount foo13
(integer) 16

3."不懂"
BITOP operation destkey key [key ...]
summary: Perform bitwise operations between strings
since: 2.6.0

4.将值减小1(键对应的值必须为int)
DECR key
summary: Decrement the integer value of a key by one
since: 1.0.0

127.0.0.1:6379> set num1 13
OK
127.0.0.1:6379> get num1
"13"
127.0.0.1:6379> decr num1
(integer) 12
127.0.0.1:6379> get num1
"12"

5.将值减小指定大小(键对应的值必须为int)
DECRBY key decrement
summary: Decrement the integer value of a key by the given number
since: 1.0.0
127.0.0.1:6379> get num1
"12"
127.0.0.1:6379> DECRBY num1 6
(integer) 6
127.0.0.1:6379> get num1
"6"

6.为键重新复制，并返回之前的旧值
GETSET key value
summary: Set the string value of a key and return its old value
since: 1.0.0

7. 
GETBIT key offset
summary: Returns the bit value at offset in the string value stored at key
since: 2.2.0

8.
GETRANGE key start end
summary: Get a substring of the string stored at a key
since: 2.4.0

9.为key设置string值，并返回原先的值
GETSET key value
summary: Set the string value of a key and return its old value
since: 1.0.0

127.0.0.1:6379> get foo13
"bar12nametest"
127.0.0.1:6379> getset foo13 test
"bar12nametest"

10. 将value类型为int的值加1
INCR key
summary: Increment the integer value of a key by one
since: 1.0.0
127.0.0.1:6379> set num 10
OK
127.0.0.1:6379> get num
"10"
127.0.0.1:6379> INCR num
(integer) 11

11. 将value类型为int的值加指定的数
INCRBY key increment
summary: Increment the integer value of a key by the given amount
since: 1.0.0
127.0.0.1:6379> get num
"11"
127.0.0.1:6379> INCRBY num 10
(integer) 21
127.0.0.1:6379> get num
"21"

12.将value类型为float的值加上指定的数
INCRBYFLOAT key increment
summary: Increment the float value of a key by the given amount
since: 2.6.0

127.0.0.1:6379> set flo 11.2
OK
127.0.0.1:6379> get flo
"11.2"
127.0.0.1:6379> INCRBYFLOAT flo 12.0
"23.2"

13.获取多个key的value
MGET key [key ...]
summary: Get the values of all the given keys
since: 1.0.0
127.0.0.1:6379> MGET foo2 foo3 foo4
1) "bar2"
2) "bar3barbar"
3) (nil)


14.设置多个key的value
MSET key value [key value ...]
summary: Set multiple keys to multiple values
since: 1.0.1
127.0.0.1:6379> MSET foo2 bar22 foo3 bar33 foo4 bar44
OK
127.0.0.1:6379> MGET foo2 foo3 foo4
1) "bar22"
2) "bar33"
3) "bar44"

15.当所有的key都不存在时，设置value
MSETNX key value [key value ...]
summary: Set multiple keys to multiple values, only if none of the keys exist
since: 1.0.1
127.0.0.1:6379> MSETNX foo2 bar23 foo5 bar55 ##因为foo2键已经存在
(integer) 0

16.设置键的微秒生存时间和值
PSETEX key milliseconds value
summary: Set the value and expiration in milliseconds of a key
since: 2.6.0

17.设置键的秒生存时间和值
SETEX key seconds value
summary: Set the value and expiration of a key
since: 2.0.0


18.设置值
SET key value [EX seconds] [PX milliseconds] [NX|XX]
summary: Set the string value of a key
since: 1.0.0

19.
SETBIT key offset value
summary: Sets or clears the bit at offset in the string value stored at key
since: 2.2.0

20.在键不存在时，设置值
SETNX key value
summary: Set the value of a key, only if the key does not exist
since: 1.0.0

21.从offset位置写入新的值内容
SETRANGE key offset value
summary: Overwrite part of a string at key starting at the specified offset
since: 2.2.0
127.0.0.1:6379> set foo5 jing
OK
127.0.0.1:6379> get foo5
"jing"
127.0.0.1:6379> SETRANGE foo5 3 test
(integer) 7
127.0.0.1:6379> get foo5
"jintest"


22.获取值得长度
STRLEN key
summary: Get the length of the value stored in a key
since: 2.2.0
127.0.0.1:6379> STRLEN foo5
(integer) 4
















