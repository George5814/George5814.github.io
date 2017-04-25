---
layout: post
title: Mongodb 学习之shell命令操作（二）
category: 技术
tags: MongoDB
keywords: 
description: 
---


## 一.服务器端命令

可以只使用mongod命令按照默认配置启动（比如：db dir 是/data/db，port是27017）。
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-1.png)

![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-2.png)
 
介绍命令参数

### 1	–v，--verbose  显示详细的日志信息
 
![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-3.png)

![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-4.png)
 
### 2	--quiet 安静输出日志信息
客户端shell执行命令时，不管正确与否，在服务端不再显示日志信息。

![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-5.png)
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-5-1.png)

 
### 3	–port arg 指定端口，客户端访问时也要指定相同的参数访问。

![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-6.png)
 
### 4	–bind_ip arg  指定绑定的IP地址，其他地址无法访问该服务。默认为all
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-7.png)

![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-8.png)
 
### 5	–ipv6 开启IPv6的支持，默认不开启   

.

### 6 –maxConns arg 设置mongodb的最大连接数

默认最大连接数是100万
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-9.png)

 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-10.png)

 
### 7	--logpath arg 设置日志的路径

将日志写入到指定的文件(如：out.log)，代替标准输出
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-11.png)

### 8	--syslog 将日志写入到系统的日志设备来代替写入到指定文件的标准输出   

.

### 9	--logappend 将日志追加到日志文件中   

.

### 10	--logRotate arg 设置日志轮转的行为（rename | reopen）  
 
![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-12.png)

![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-13.png)
 
### 11	–timeStampFormat arg 设置时间格式化（ctime,iso8601-utc,iso8601-local 三选一，不过一般选择iso8601-local符合我们的使用习惯）
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-14.png)

### 12	 --pidfilepath  arg pidfile的全路径，不设置pidfile不会被创建   

.

 
### 13	 --keyFile arg  集群认证的私有key   
 
.

### 14	 --setParameter  arg 设置配置参数   
 
.

### 15	 --httpinterface   开启http访问接口 。使用--rest 更好一些    
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-15.png)

  ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-16.png)
 
显示http访问页面结果，
 
  ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-17.png)
 
### 16	--rest  开启简单的rest API    
 
.

### 17	--clusterAuthMode arg 集群权限的权限模式，包括keyFile,sendKeyFile,sendX509,x509
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-18.png)

  ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-19.png)
 
 
### 18	--nounixsocket 禁用unix socket监听 
 
  ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-20.png)
 
### 19	–unixSocketPrefix arg  替换unix domain socket的默认目录(/tmp)
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-21.png)

 
### 20	--filePermissions arg  设置socket文件权限(默认为0700)   
 
.

### 21	--fork fork服务进程    
 
.

### 22	--auth 开启安全运行   
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-22.png)
 
### 23	--noauth 开启非安全运行，与—auth相反
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-23.png)
 
### 24	--jsonp 允许jsonp通过http访问   
 
.

### 25	--profile arg 0=off 1=slow, 2=all   
 
.

### 26	--sysinfo 显示部分诊断系统信息   
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-24.png)
 
### 27	--noIndexBuildRetry 被shutdown中断后，不再进行任何的索引构建重试
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-25.png)
 
### 28	--noscripting        禁用脚本引擎   
 
.

### 29	--notablescan       禁止表扫描   
 
.

### 30	--shutdown         停掉一个运行的服务   
 
 ![mongo-shell](http://omsz9j1wp.bkt.clouddn.com/image/mongodb/mongodb-shell-26.png)

```shell
Replication options:
31.	--oplogSize arg        用于副本日志的大小，默认为磁盘空间的5%，大了更好。

Master/slave options： 
32.	--master             主节点模式
33.	--slave               从节点模式
34.	--autoresync          如果从节点的数据过时则自动重新同步  
35.	--source arg           when slave: specify master as<server:port>
36.	--only arg             when slave: specify a single database to replicate
37.	--slavedelay arg        当支持主节点操作从节点时，延迟（单位：秒）将被使用。
Replica set options:
38.	--replSet arg                         arg is <setname>[/<optionalseedhostlist>]
39.	  --replIndexPrefetch arg              specify index prefetching behavior (if secondary) [none|_id_only|all]
40.	  --enableMajorityReadConcern           enables majority readConcern

Sharding options:
41.	  --configsvr                   declare this is a config db of a  cluster; default port 27019; default dir /data/configdb
42.	  --configsvrMode arg       Controls what config server protocol is in use. When set to "sccc" keeps server in legacy SyncClusterConnection mode even when the service is running as a replSet
43.	  --shardsvr                 declare this is a shard db of a cluster; default port 27018
SSL options:
44.	  --sslOnNormalPorts                    use ssl on configured ports
45.	  --sslMode arg                         set the SSL operation mode (disabled|allowSSL|preferSSL|requireSSL )
46.	  --sslPEMKeyFile arg                   PEM file for ssl
47.	  --sslPEMKeyPassword arg               PEM file password
48.	  --sslClusterFile arg                  Key file for internal SSL authentication
49.	  --sslClusterPassword arg              Internal authentication key file password
50.	  --sslCAFile arg                       Certificate Authority file for SSL
51.	  --sslCRLFile arg                      Certificate Revocation List file for SSL
52.	  --sslDisabledProtocols arg            Comma separated list of TLS protocols  to disable [TLS1_0,TLS1_1,TLS1_2]
53.	  --sslWeakCertificateValidation        allow client to connect without presenting a certificate
54.	  –sslAllowConnectionsWithoutCertificates  allow client to connect without presenting a certificate
55.	--sslAllowInvalidHostnames   Allow server certificates to provide  non-matching hostnames
56.	  --sslAllowInvalidCertificates         allow connections to servers with invalid certificates
57.	  --sslFIPSMode                         activate FIPS 140-2 mode at startup

Storage options（存储操作）:
58.	  --storageEngine arg         what storage engine to use – defaults to wiredTiger if no data files present
59.	  --dbpath arg                          directory for datafiles - defaults to /data/db
60.	  --directoryperdb                      each database will be stored in a separate directory
61.	  --noprealloc                          disable data file preallocation – will  often hurt performance
62.	  --nssize arg (=16)                    .ns file size (in MB) for new databases
63.	  --quota                               limits each database to a certain  number of files (8 default)
64.	  --quotaFiles arg                      number of files allowed per db, implies  --quota
65.	--smallfiles                          use a smaller default file size
66.	  --syncdelay arg (=60)                 seconds between disk syncs (0=never, but not recommended)
67.	  --upgrade                             upgrade db if needed
68.	  --repair                              run repair on all dbs
69.	  --repairpath arg                      root directory for repair files - defaults to dbpath
70.	  --journal                             enable journaling
71.	  --nojournal                           disable journaling (journaling is on by default for 64 bit)
72.	  --journalOptions arg                  journal diagnostic options
73.	  --journalCommitInterval arg           how often to group/batch commit (ms)

WiredTiger options（WiredTiger存储引擎操作）:
74.	  --wiredTigerCacheSizeGB arg           maximum amount of memory to allocate for cache; defaults to 1/2 of physical RAM
75.	--wiredTigerStatisticsLogDelaySecs arg (=0)  seconds to wait between each write to a statistics file in the dbpath; 0 means do not log statistics
76.	  --wiredTigerJournalCompressor arg (=snappy)  use a compressor for log records [none|snappy|zlib]
77.	  --wiredTigerDirectoryForIndexes        Put indexes and data in different  directories
78.	  --wiredTigerCollectionBlockCompressor  arg (=snappy)  block compression algorithm for  collection data [none|snappy|zlib]
79.	  --wiredTigerIndexPrefixCompression arg (=1)  use prefix compression on row-store  leaf pages
```

## 二．	客户端命令

```
语法：mongo [options] [db address] [file names (ending in .js)]
数据库地址格式:
  foo                   foo database on local machine
  192.169.0.5/foo         foo database on 192.168.0.5 machine
  192.169.0.5:9999/foo        foo database on 192.168.0.5 machine on port 9999
	
Options:
  --shell                             run the shell after executing files
  --nodb                              don't connect to mongod on startup - no
                                      'db address' arg expected
  --norc                              will not run the ".mongorc.js" file on
                                      start up
  --quiet                             be less chatty
  --port arg                          port to connect to
  --host arg                          server to connect to
  --eval arg                          evaluate javascript
  -h [ --help ]                       show this usage information
  --version                           show version information
  --verbose                           increase verbosity
  --ipv6                              enable IPv6 support (disabled by default)
  --disableJavaScriptJIT              disable the Javascript Just In Time
                                      compiler
  --enableJavaScriptProtection        disable automatic JavaScript function
                                      marshalling
  --ssl                               use SSL for all connections
  --sslCAFile arg                     Certificate Authority file for SSL
  --sslPEMKeyFile arg                 PEM certificate/key file for SSL
  --sslPEMKeyPassword arg             password for key in PEM file for SSL
  --sslCRLFile arg                    Certificate Revocation List file for SSL
  --sslAllowInvalidHostnames          allow connections to servers with
                                      non-matching hostnames
  --sslAllowInvalidCertificates       allow connections to servers with invalid
                                      certificates
  --sslFIPSMode                       activate FIPS 140-2 mode at startup

Authentication Options:
  -u [ --username ] arg               username for authentication
  -p [ --password ] arg               password for authentication
  --authenticationDatabase arg        user source (defaults to dbname)
  --authenticationMechanism arg       authentication mechanism
  --gssapiServiceName arg (=mongodb)  Service name to use when authenticating
                                      using GSSAPI/Kerberos
  --gssapiHostName arg                Remote host name to use for purpose of
                                      GSSAPI/Kerberos authentication
file names: a list of files to run. files have to end in .js and will exit afte
r unless --shell is specified
```