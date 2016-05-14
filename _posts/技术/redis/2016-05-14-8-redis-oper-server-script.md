---
layout: post
title: redis server 和 scripting命令操作
category: 技术
tags:  Redis
keywords: 
description: redis之server 和 scripting命令的基本操作
---

{:toc}



## redis server命令操作

### 1.BGREWRITEAOF -


summary: Asynchronously rewrite the append-only file  
since: 1.0.0


### 2.BGSAVE -


summary: Asynchronously save the dataset to disk  
since: 1.0.0


### 3.CLIENT GETNAME -


summary: Get the current connection name  
since: 2.6.9


### 4.CLIENT KILL ip:port


summary: Kill the connection of a client  
since: 2.4.0


### 5.CLIENT LIST -


summary: Get the list of client connections  
since: 2.4.0


### 6.CLIENT SETNAME connection-name


summary: Set the current connection name  
since: 2.6.9


### 7.CONFIG GET parameter


summary: Get the value of a configuration parameter  
since: 2.0.0


### 8.CONFIG RESETSTAT -


summary: Reset the stats returned by INFO  
since: 2.0.0


### 9.CONFIG SET parameter value


summary: Set a configuration parameter to the given value  
since: 2.0.0


### 10.DBSIZE -


summary: Return the number of keys in the selected database  
since: 1.0.0


### 11.DEBUG OBJECT key


summary: Get debugging information about a key  
since: 1.0.0


### 12.DEBUG SEGFAULT -


summary: Make the server crash  
since: 1.0.0


### 13.FLUSHALL -


summary: Remove all keys from all databases  
since: 1.0.0


### 14.FLUSHDB -


summary: Remove all keys from the current database  
since: 1.0.0


### 15.INFO [section]


summary: Get information and statistics about the server  
since: 1.0.0


### 16.LASTSAVE -


summary: Get the UNIX time stamp of the last successful save to disk  
since: 1.0.0


### 17.MONITOR -


summary: Listen for all requests received by the server in real time  
since: 1.0.0


### 18.SAVE -


summary: Synchronously save the dataset to disk  
since: 1.0.0


### 19.SHUTDOWN [NOSAVE] [SAVE]


summary: Synchronously save the dataset to disk and then shut down the server  
since: 1.0.0


### 20.SLAVEOF host port


summary: Make the server a slave of another instance, or promote it as master  
since: 1.0.0


### 21.SLOWLOG subcommand [argument]


summary: Manages the Redis slow queries log  
since: 2.2.12


### 22.SYNC -


summary: Internal command used for replication  
since: 1.0.0


### 23.TIME -


summary: Return the current server time  
since: 2.6.0

## redis scripting 命令操作

### 1.EVAL script numkeys key [key ...] arg [arg ...]


summary: Execute a Lua script server side  
since: 2.6.0


### 2.EVALSHA sha1 numkeys key [key ...] arg [arg ...]


summary: Execute a Lua script server side  
since: 2.6.0


### 3.SCRIPT EXISTS script [script ...]


summary: Check existence of scripts in the script cache.  
since: 2.6.0


### 4.SCRIPT FLUSH -


summary: Remove all the scripts from the script cache.  
since: 2.6.0


### 5.SCRIPT KILL -


summary: Kill the script currently in execution.  
since: 2.6.0


### 6.SCRIPT LOAD script


summary: Load the specified Lua script into the script cache.  
since: 2.6.0


