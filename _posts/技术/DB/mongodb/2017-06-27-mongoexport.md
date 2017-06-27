---
layout: post
title: Mongodb 学习之shell命令操作（3）
category: 技术
tags: MongoDB
keywords: 
description: Mongo导出数据操作
---

### 导出指定列数据到csv文件

```bash
mongoexport -d esn-palmyy -c log_person --type=csv -f date,level,message.params.name,message.params.job_number,message.params.join_date,message.params.email,message.params.mobile,message.detailInfo.msg --query '{date:{$gte:1498492800000}}'   -o person.csv 
```

- `mongoexport`为mongo自带的导出命令。

- -d 指定要导出的数据库（如：esn-palmyy）

- -c 指定要导出的collection（如：log_person）

- -type=csv 指定导出类型为csv格式

- -f 指定要导出的列，以逗号分隔，既可以指定一级列，也可以指定对象内的列或者对象内的对象的列。

- -query 在`''`中指定查询条件，只导出符合条件的记录。

- -o 指定要导出的文件位置（如：person.csv为导出到当前目录下的person.csv文件）


