/*********redis hash命令操作 **********/
1.删除一个或多个hash域
HDEL key field [field ...]
summary: Delete one or more hash fields
since: 2.0.0
127.0.0.1:6379> HDEL hash1 name
(integer) 1


2.判断一个域是否存在
HEXISTS key field
summary: Determine if a hash field exists
since: 2.0.0
127.0.0.1:6379> HEXISTS hash1 name
(integer) 0 #不存在返回0
127.0.0.1:6379> HEXISTS hash1 name2
(integer) 1 #存在返回1


3.获取一个hash域
HGET key field
summary: Get the value of a hash field
since: 2.0.0
127.0.0.1:6379> HGET hash1 name2
"name22"

4.获取hash中所有的域和值
HGETALL key
summary: Get all the fields and values in a hash
since: 2.0.0
127.0.0.1:6379> HGETALL hash1
1) "name"  #field名
2) "jing"  #value值
3) "name2" #field名
4) "jing2" #value值
5) "name3" #field名
6) "jing3" #value值



5.增加域的值(如果是int型)，增加increment
HINCRBY key field increment
summary: Increment the integer value of a hash field by the given number
since: 2.0.0

6.增加域的值(float型),增加increment
HINCRBYFLOAT key field increment
summary: Increment the float value of a hash field by the given amount
since: 2.6.0
127.0.0.1:6379> HSET hash1 num1 10
(integer) 1
127.0.0.1:6379> HINCRBY hash1 num1 2
(integer) 12

7.获取hash的所有域
HKEYS key
summary: Get all the fields in a hash
since: 2.0.0
127.0.0.1:6379> HKEYS hash1
1) "name2"
2) "name3"
3) "num1"


8.获取hash中域的数据
HLEN key
summary: Get the number of fields in a hash
since: 2.0.0
127.0.0.1:6379> HLEN hash1
(integer) 3



9.获取所有给定域的值
HMGET key field [field ...]
summary: Get the values of all the given hash fields
since: 2.0.0
127.0.0.1:6379> HMGET hash1 name2 num1
1) "name22"
2) "12"


10.设置多个hash域对应多个hash值
HMSET key field value [field value ...]
summary: Set multiple hash fields to multiple values
since: 2.0.0
127.0.0.1:6379> HMSET hash1 num2 12 name4 jing4 num3 33
OK
127.0.0.1:6379> HGETALL hash1
 1) "name2"
 2) "name22"
 3) "name3"
 4) "name33"
 5) "num1"
 6) "12"
 7) "num2"
 8) "12"
 9) "name4"
10) "jing4"
11) "num3"
12) "33"


11.设置hash域的字符串值
HSET key field value
summary: Set the string value of a hash field
since: 2.0.0

12.只有当该域不存在时，设置hash的域的值
HSETNX key field value
summary: Set the value of a hash field, only if the field does not exist
since: 2.0.0

13.获取hash的所有值
HVALS key
summary: Get all the values in a hash
since: 2.0.0

127.0.0.1:6379> HVALS hash1
1) "name22"
2) "name33"
3) "12"
4) "12"
5) "jing4"
6) "33"
