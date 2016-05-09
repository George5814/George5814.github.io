/******** redis set命令操作 *****/
1.添加一个或多个成员到set
SADD key member [member ...]
summary: Add one or more members to a set
since: 1.0.0
127.0.0.1:6379> sadd set1 val1 val2 val3
(integer) 3

2.获取一个set的成员数量
SCARD key
summary: Get the number of members in a set
since: 1.0.0
127.0.0.1:6379> scard set1
(integer) 3

3.查询在第一个set中但不在后续set中的成员，并返回
SDIFF key [key ...]
summary: Subtract multiple sets
since: 1.0.0
127.0.0.1:6379> SMEMBERS set1
1) "val3"
2) "val4"
3) "val5"
4) "val2"
5) "val1"
127.0.0.1:6379> SMEMBERS set2
1) "val3"
2) "val4"
3) "val2"
127.0.0.1:6379> SDIFF set2 set1
(empty list or set)
127.0.0.1:6379> SDIFF set1 set2
1) "val5"
2) "val1"

4.查询key1中的成员但不在key2等后续key中的成员，并将获得的结果放在des-key中
SDIFFSTORE des-key key1 [key2 ...]
summary: Subtract multiple sets and store the resulting set in a key
since: 1.0.0
127.0.0.1:6379> SDIFFSTORE set3 set1 set2
(integer) 2
127.0.0.1:6379> SMEMBERS set3
1) "val5"
2) "val1"

5.查询set中的重复项
SINTER key [key ...]
summary: Intersect multiple sets
since: 1.0.0
127.0.0.1:6379> sinter set1 set2
1) "val3"
2) "val4"
3) "val2"
127.0.0.1:6379> sinter set2 set1
1) "val3"
2) "val4"
3) "val2"

6.查询set中的重复项，并放在目标set中
SINTERSTORE des-key key [key ...]
summary: Intersect multiple sets and store the resulting set in a key
since: 1.0.0
127.0.0.1:6379> SINTERSTORE set4 set1 set2
(integer) 3
127.0.0.1:6379> SMEMBERS set4
1) "val3"
2) "val2"
3) "val4"

7.查询指定的成员是否在指定的key中(在返回1，不在返回0)
SISMEMBER key member
summary: Determine if a given value is a member of a set
since: 1.0.0

8.查询set中所有的成员
SMEMBERS key
summary: Get all the members in a set
since: 1.0.0
127.0.0.1:6379> SMEMBERS set4
1) "val3"
2) "val2"
3) "val4"

9.将set中的一个成员从set移到另一个set
SMOVE source destination member
summary: Move a member from one set to another
since: 1.0.0
127.0.0.1:6379> smove set4 set3 val3
(integer) 1
127.0.0.1:6379> SMEMBERS set4
1) "val2"
2) "val4"
127.0.0.1:6379> SMEMBERS set3
1) "val3"
2) "val5"
3) "val1"

10.移除并返回随机的成员
SPOP key
summary: Remove and return a random member from a set
since: 1.0.0
127.0.0.1:6379> SMEMBERS set3
1) "val3"
2) "val5"
3) "val1"
127.0.0.1:6379> spop set3
"val1"
127.0.0.1:6379> SMEMBERS set3
1) "val3"
2) "val5"

11.获取一个或多个随机成员(count指代获取的数目)
SRANDMEMBER key [count]
summary: Get one or multiple random members from a set
since: 1.0.0
127.0.0.1:6379> SRANDMEMBER set3 2
1) "val3"
2) "val5"

12.移除set中的一个或多个成员
SREM key member [member ...]
summary: Remove one or more members from a set
since: 1.0.0
127.0.0.1:6379> srem set3 val3 val4
(integer) 1  #只删除了val3,val4不存在所以返回1

13.添加多个sets，显示多个sets的结果，不重复
SUNION key [key ...]
summary: Add multiple sets
since: 1.0.0

127.0.0.1:6379> SUNION set3 set4 set1
1) "val3"
2) "val5"
3) "val4"
4) "val2"
5) "val1"

14.连接多个set结果，并将结果存在目标set中
SUNIONSTORE dest-key key [key ...]
summary: Add multiple sets and store the resulting set in a key
since: 1.0.0

127.0.0.1:6379> SUNIONstore  set6  set3 set4 set1
(integer) 5


