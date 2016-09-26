---
layout: post
title: mysql的时间函数
category: 技术
tags:  Mysql
keywords: 
description: 
---

{:toc}

### 获取指定时间的毫秒值

```sql
SELECT UNIX_TIMESTAMP('2016-09-26 00:00:00')*1000;
```

### 获取指定时间的unix时间戳

```sql
SELECT UNIX_TIMESTAMP('2016-09-26 00:00:00');
```

### 获取当前时间的时间戳

```sql
SELECT UNIX_TIMESTAMP(NOW());
```


### 将时间戳转为可视化的日期

```sql
SELECT FROM_UNIXTIME(1474858245000 / 1000);
```

### 将当前时间戳转为可视化的日期

```sql
-- 两者相同
SELECT FROM_UNIXTIME(UNIX_TIMESTAMP(NOW()));

SELECT NOW();
```

### 将当前时间格式化为指定格式

```sql
-- 两者相同
SELECT DATE_FORMAT(NOW(),'%Y-%m-%d');

SELECT DATE_FORMAT(FROM_UNIXTIME(UNIX_TIMESTAMP(NOW())),'%Y-%m-%d');

-- 格式化为英文格式
SELECT DATE_FORMAT(NOW(),'%Y-%M-%D');
```

### Date_format字符串标识

```
DATE_FORMAT(date,format) 
　根据format字符串格式化date值
　(在format字符串中可用标志符:
　%M 月名字(January……December) 
　%W 星期名字(Sunday……Saturday) 
　%D 有英语前缀的月份的日期(1st, 2nd, 3rd, 等等。） 
　%Y 年, 数字, 4 位 
　%y 年, 数字, 2 位 
　%a 缩写的星期名字(Sun……Sat) 
　%d 月份中的天数, 数字(00……31) 
　%e 月份中的天数, 数字(0……31) 
　%m 月, 数字(01……12) 
　%c 月, 数字(1……12) 
　%b 缩写的月份名字(Jan……Dec) 
　%j 一年中的天数(001……366) 
　%H 小时(00……23) 
　%k 小时(0……23) 
　%h 小时(01……12) 
　%I 小时(01……12) 
　%l 小时(1……12) 
　%i 分钟, 数字(00……59) 
　%r 时间,12 小时(hh:mm:ss [AP]M) 
　%T 时间,24 小时(hh:mm:ss) 
　%S 秒(00……59) 
　%s 秒(00……59) 
　%p AM或PM 
　%w 一个星期中的天数(0=Sunday ……6=Saturday ） 
　%U 星期(0……52), 这里星期天是星期的第一天 
　%u 星期(0……52), 这里星期一是星期的第一天 
　%% 字符% )
```

### 获取指定日期是一周的第几天，以周天开始

```sql
-- 输出为2，即周一
select DAYOFWEEK('2016-9-26'); 
```

### 获取指定日期是周几，以周一开始

```sql
-- 两者相同
select WEEKDAY('2016-9-26');

select WEEKDAY('2016-9-26 11:00:00'); 
```


### 指定日期是所在月的第几天

```sql
select DAYOFMONTH('2016-9-26 11:00:00'); 
```

### 指定日期是所在年的第几天

```sql
select DAYOFYEAR('2016-9-26 11:00:00'); 
```

### 指定日期在一年的第几周

```sql
select WEEK('2016-9-26 11:00:00'); 
```

### 指定日期在一年的第几周

```sql
select WEEK('2016-9-26 11:00:00'); 
```

### 指定日期在一年的第几周

```sql
select WEEK('2016-9-26 11:00:00'); 
```

### 指定日期在一年的第几周

```sql
select WEEK('2016-9-26 11:00:00'); 
```

### 指定日期在一年的第几周

```sql
select WEEK('2016-9-26 11:00:00'); 
```

 