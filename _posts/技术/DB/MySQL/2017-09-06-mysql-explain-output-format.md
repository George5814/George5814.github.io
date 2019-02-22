---
layout: post
title: explain输出格式
category: 技术
tags:  Mysql
keywords: 
description: 官方文档地址：https://dev.mysql.com/doc/refman/5.7/en/explain-output.html
---

EXPLAIN语句提供了mysql怎么执行语句的相关信息。EXPLAIN 可以描述select,insert,update,delete,replace等语句的执行信息。[表8.1](#table8-1)

对于select语句使用到的每一张表，explain都会返回一行信息，而且是按照mysql在处理语句时读取的顺序列出这些表。
mysql使用内循环join的方法来解决所有的join操作，这意味着mysql从第一张表中读取一行数据，然后在第二张表中读取匹配的行，然后第三张，以此类推。当所有的表都被执行完时，mysql通过表列表输出选定的列和回溯，直到找到有更多匹配行的表。然后下一行从当前表中读取然后继续执行后面的表。

explain输出会包含分区信息。此外，对于select语句，explain会生成扩展信息，可以在explain之后显示show warnings（请看[Section 8.8.3, “Extended EXPLAIN Output Format”](https://dev.mysql.com/doc/refman/5.7/en/explain-extended.html)）。

> **注意**  
> 在早期mysql版本总，分区和扩展信息是使用`explain partitionyu'fayufaexplain extended`产生的。那些语法因为向后兼容性仍然可用，但是分区和扩展输出默认是不开启的，所以`partitions`和`extended`关键字是多余的并且过期了。他们的使用会导致警告并且在未来的mysql版本中会从`explain`语法中移除。
>
> 你不能在同一个`explain`语句中同时使用`partitions`和`extended`关键字。另外，这两个关键字的任何一个也不能和`format`操作一起使用。

## explain输出列

该节描述了使用`explain`的输出列，后面的章节会介绍`type`和`extra`列额外的信息。

来自`explain`的每个输出行代表一张表的信息。每行包含[表8.1](#table8-1)中的值的概要，并且在该表格后会有更详细的描述。表格的第一列是列明，第二列是提供的与使用`format=json`等价的属性名显示


<a name="table8-1">表8.1</a>



|列名|json名|意义|
|-----|-----|----|
|[id](#id)|select_id|`select`标识符|
|[select_type](#select_type)|NONE|`SELECT`的类型|
|[table](#table)|table_name|输出行的表|
|[partitions](#partitions)|partitions|匹配的分区|
|[type](#type)|access_type|join类型|
|[possible_keys](#possible_keys)|possible_keys|可能选择的索引|
|[key](#key)|key|真实选择的索引|
|[key_len](#key_len)|key_length|选择的key的长度|
|[ref](#ref)|ref|列在索引上的参照|
|[rows](#rows)|rows|被检查行的估值|
|[filtered](#filtered)|filtered|通过表的条件过滤的行的百分比|
|[Extra](#Extra)|None|额外信息|

> **注意**  
> 为null的JSON属性在json格式化的`explain`输出中不显示。

- <a name="id">id</a>（JSON name:select_id）

  `select`的标识符。这是查询中`select`的序列号。如果该行参照其他行的联合结果，该值可以为NULL。这种情况系，`table`列显示像`<union M,N>`这样的值来表示该行的id值是M和N的并集

- <a name="select_type">select_type</a>（JSON 那么:none）

  `select`的类型，可以是下标中显示的任何一个。JSON格式的explain将`select`类型显示为query_block的属性，除非是`SIMPLE`和`PRIMARY`

  |select_type的值|JSON名|意义|
  |---|----|----|
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  ||||
  

- <a name="table">table</a>

- <a name="partitions">partitions</a>

- <a name="type">type</a>

- <a name="possible_keys">possible_keys</a>

- <a name="key">key</a>

- <a name="key_len">key_len</a>

- <a name="ref">ref</a>

- <a name="rows">rows</a>

- <a name="filtered">filtered</a>

- <a name="Extra">Extra</a>



