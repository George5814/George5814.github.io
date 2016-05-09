/***** redis sorted_set 命令操作 ********/
score 是set排序的值，越大越在前
1.添加 成员到sorted_set中，并设置score
ZADD key score member [score member ...]
summary: Add one or more members to a sorted set, or update its score if it already exists
since: 1.2.0
127.0.0.1:6379> zadd sset1 2 val4
(integer) 1


2. 查询sorted_set的成员数量
ZCARD key
summary: Get the number of members in a sorted set
since: 1.2.0
127.0.0.1:6379> ZCARD sset1
(integer) 5

3.获取score在min和max之间的数量
ZCOUNT key min max
summary: Count the members in a sorted set with scores within the given values
since: 2.0.0
127.0.0.1:6379> ZCOUNT sset1 12 100
(integer) 2
127.0.0.1:6379> ZCOUNT sset1 0 200000
(integer) 5

4.增加sorted_set中成员的score
ZINCRBY key increment member
summary: Increment the score of a member in a sorted set
since: 1.2.0
127.0.0.1:6379> ZINCRBY sset1 30 val1
"52"

5.将key1和key2...中重复的值得结果存到des-key中，numkeys为源key的数量
ZINTERSTORE des-key numkeys key1 [key2 ...] [WEIGHTS weight] [AGGREGATE SUM|MIN|MAX]
summary: Intersect multiple sorted sets and store the resulting sorted set in a new key
since: 2.0.0
127.0.0.1:6379> ZINTERSTORE sset5 2 sset1 sset2
(integer) 3

6.获取sorted_set中index值在start和stop之间的值(包含首末值)
ZRANGE key start stop [WITHSCORES]
summary: Return a range of members in a sorted set, by index
since: 1.2.0

6.获取sorted_set中index值在min 和max之间的值(包含首末值)
ZRANGEBYSCORE key min max [WITHSCORES] [LIMIT offset count]
summary: Return a range of members in a sorted set, by score
since: 1.0.5
127.0.0.1:6379> ZRANGEBYSCORE sset5 6 11
1) "val4"

7.获取成员的索引位置
ZRANK key member
summary: Determine the index of a member in a sorted set
since: 2.0.0
127.0.0.1:6379> ZRANK sset5 val3
(integer) 2
127.0.0.1:6379> ZRANGE sset5 0 10
1) "val4"
2) "val2"
3) "val3"

8.从sorted_set 中删除指定的成员（可多个）
ZREM key member [member ...]
summary: Remove one or more members from a sorted set
since: 1.2.0

9.删除index值在start和stop之间的成员
ZREMRANGEBYRANK key start stop
summary: Remove all members in a sorted set within the given indexes
since: 2.0.0

10.删除score值在min和max之间的成员
ZREMRANGEBYSCORE key min max
summary: Remove all members in a sorted set within the given scores
since: 1.2.0

11.返回指定index在start和stop之间，并按score从高到底低排序
ZREVRANGE key start stop [WITHSCORES]
summary: Return a range of members in a sorted set, by index, with scores ordered from high to low
since: 1.2.0
127.0.0.1:6379> ZREVRANGE sset5 0 2
1) "val3"
2) "val2"
3) "val4"

12.返回指定score在min和max之间，并按score从高到底低排序
ZREVRANGEBYSCORE key max min [WITHSCORES] [LIMIT offset count]
summary: Return a range of members in a sorted set, by score, with scores ordered from high to low
since: 2.2.0

13.获取成员的索引位置，并按score从高到低排序
ZREVRANK key member
summary: Determine the index of a member in a sorted set, with scores ordered from high to low
since: 2.0.0

14.获取指定成员的score
ZSCORE key member
summary: Get the score associated with the given member in a sorted set
since: 1.2.0

127.0.0.1:6379> ZSCORE sset5 val2
"12"

15.连sorted_key显示结果，并将显示结果放到目标key中，numkeys代表源key的数目
ZUNIONSTORE des-key numkeys key1 [key2 ...] [WEIGHTS weight] [AGGREGATE SUM|MIN|MAX]
summary: Add multiple sorted sets and store the resulting sorted set in a new key
since: 2.0.0




