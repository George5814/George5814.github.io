/*******  redis list命令操作 **********/
1.将值追加到列表
RPUSH key value [value ...]
summary: Append one or multiple values to a list
since: 1.0.0
127.0.0.1:6379> RPUSH mylist value1 value2 value3
(integer) 3

2.获取列表的长度
LLEN key
summary: Get the length of a list
since: 1.0.0

127.0.0.1:6379> llen mylist
(integer) 3

3.获取并移除列表中第一个元素
BLPOP key [key ...] timeout
summary: Remove and get the first element in a list, or block until one is available
since: 2.0.0

127.0.0.1:6379> blpop mylist 3
1) "mylist" ##列表key
2) "value1" #列表当前第一个值
127.0.0.1:6379> blpop mylist 3
1) "mylist"
2) "value2"
127.0.0.1:6379> blpop mylist 3
1) "mylist"
2) "value3"
127.0.0.1:6379> blpop mylist 3 列表已经不存在value

(nil)
(3.78s)

4.获取并移除列表中的最后一个元素
BRPOP key [key ...] timeout
summary: Remove and get the last element in a list, or block until one is available
since: 2.0.0
127.0.0.1:6379> brpop list1 3
1) "list1" #列表键名
2) "value3" #列表最后一个值

5.出栈list中的一个value，并放入另一个list中，并返回该值
BRPOPLPUSH source destination timeout
summary: Pop a value from a list, push it to another list and return it; or block until one is available
since: 2.2.0

127.0.0.1:6379> BRPOPLPUSH list1 list2 3
"value2"

6.获取指定位置的value值，返回的是该位置的值，无值或超出边界返回nil
LINDEX key index
summary: Get an element from a list by its index
since: 1.0.0

7.在列表一个元素的之前或之后插入一个元素，返回当前列表的长度
LINSERT key BEFORE|AFTER pivot value
summary: Insert an element before or after another element in a list
since: 2.2.0
127.0.0.1:6379> linsert ml before v2 value2
(integer) 5  在v2之前插入值value2

8.栈顶元素出栈
LPOP key
summary: Remove and get the first element in a list
since: 1.0.0
127.0.0.1:6379> lpop ml
"v1"

9.向list中添加一个或多个value，后加入的值，index在前(将元素压入栈顶)
LPUSH key value [value ...]
summary: Prepend one or multiple values to a list
since: 1.0.0
127.0.0.1:6379> lpush list2 val1 val2 val3 val4 val5
(integer) 6
127.0.0.1:6379> lindex list2 0
"val5"

10.只有当列表存在时，才从栈顶压入元素
LPUSHX key value
summary: Prepend a value to a list, only if the list exists
since: 2.2.0

11.获取指定范围的list的value值
LRANGE key start stop
summary: Get a range of elements from a list
since: 1.0.0

12.从列表中移除元素(当list中存在多个重复的值时，count确定要移除几个value)
LREM key count value
summary: Remove elements from a list
since: 1.0.0

13.通过元素的索引index设置value
LSET key index value
summary: Set the value of an element in a list by its index
since: 1.0.0
127.0.0.1:6379> lset list2 3 namew #修改第三个位置的值
OK

14. 
LTRIM key start stop
summary: Trim a list to the specified range
since: 1.0.0

15.移除并获取列表中的最后一个元素
RPOP key
summary: Remove and get the last element in a list
since: 1.0.0

16.移除列表中的最后一个元素，追加到另一个列表中，并返回该值
RPOPLPUSH source destination
summary: Remove the last element in a list, append it to another list and return it
since: 1.2.0


17.将值追加到列表中，只有当这个列表已经存在
RPUSHX key value
summary: Append a value to a list, only if the list exists
since: 2.2.0









