---
layout: post
title: 提高hadoop配置效率的shell脚本
category: 技术
tags: Shell
keywords: 
description:  不定期更新
---

## 协助脚本

**每个脚本都需要执行`chmod 755 xxxx.sh`命令，为脚本赋执行权限。**

### 同步机器上的时间

#### 网络时间同步

从指定的网络服务器（`1.cn.pool.ntp.org`）同步时间

将`h2m1`，`h2s1`,`h2s2`机器上的时区修改为当前时区(上海时区)：`cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime`

脚本文件名为`dateSync.sh`，内容如下：

```sh
#!/usr/bin/env bash

ssh h2m1 "ntpdate 1.cn.pool.ntp.org;clock -w;hwclock -r"
ssh h2s1 "ntpdate 1.cn.pool.ntp.org;clock -w;hwclock -r"
ssh h2s2 "ntpdate 1.cn.pool.ntp.org;clock -w;hwclock -r"

echo "显示当前时间:"
ssh h2m1 "date";
ssh h2s1 "date";
ssh h2s2 "date"; 
```


执行:`./dateSync.sh`


#### 将三台机器上的时间设置为指定时间并显示

脚本文件名为`dateModify.sh`，内容如下：

```sh
#!/usr/bin/env bash

#获取当前时间
d=$1

echo "指定时间为：${d}"

#执行远程操作命令
ssh h2m1 "date -s '${d}';clock -w;hwclock -r"
ssh h2s1 "date -s '${d}';clock -w;hwclock -r"
ssh h2s2 "date -s '${d}';clock -w;hwclock -r"



echo "显示当前时间:"
ssh h2m1 "date";
ssh h2s1 "date";
ssh h2s2 "date";
```

执行示例:`./dateModify.sh  "2016-12-21 9:12:40"  `


### 启动Redis集群

在同一台机器上启动6个redis实例

脚本文件名为`redis-start-cluster.sh`，内容如下：


```sh
#!/bin/sh


##检查redis实例状态
ps -ef | grep redis


echo $0
## 批量删除进程,并过滤掉grep redis 和redis-start-cluster.sh
## 在可执行命令中执行命令``和$(())交叉使用
kill  -9  `ps -ef | grep redis | grep -v "redis-start-cluster" | grep -v "grep redis"  | awk '{print $2}'`

## 启动集群实例
INDEX=1  ##变量需要大写
NUM=6
/usr/local/redis/bin/redis-server /usr/local/redis/conf/redis.conf

while [ ${INDEX} -le ${NUM} ]
do
        cd /usr/local/redis0${INDEX}
        ./bin/redis-server ./conf/redis.conf
        echo "当前redis实例目录"
        pwd 

        INDEX=$((${INDEX}+1))
done



##检查redis实例状态
ps -ef | grep redis
```

执行：`./redis-start-cluster.sh`


### HBase基本操作测试脚本

脚本文件名为`hbase-test-script.sh`，内容如下：


```sh

#!/usr/bin/env bash

tableName = 'test1'
rowKey = 'tes_jingzz'
colFamily = 'cf1'

create tableName,colFamily

put tableName,rowKey,'cf1:c1','value1'
put tableName,rowKey,'cf1:c2','value1'
put tableName,rowKey,'cf1:c3','value1'
put tableName,rowKey,'cf1:c4','value1'
put tableName,rowKey,'cf1:c5','value1'
put tableName,rowKey,'cf1:c6','value1'

get tableName,rowKey,'cf1:c1'


scan tableName

disable tableName

drop tableName

```

执行：`hbase shell ./hbase-test-script.sh`

