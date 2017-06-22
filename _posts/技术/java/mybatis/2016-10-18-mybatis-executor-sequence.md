---
layout: post
title: mybatis操作主体流程
category: 技术
tags:  Mybatis
keywords: 
description: 
---

{:toc}



## 一 mybatis操作流程

- 定位配置文件的位置
- 构建sessionFactory
- 获取数据库操作会话
- 建立数据库连接并执行数据库操作


### 1.1 定位配置文件位置

使用`org.apache.ibatis.io.Resources.getResourceAsReader(String)`定位mybatis配置文件的位置，获得文件的输入流。


```
sequenceDiagram

Reader->>Resources:getResourceAsReader()
Resources->>InputStreamReader:getResourceAsReader()
InputStreamReader->>InputStream:''
InputStream->>Resources:getResourceAsStream()
Resources->>ClassLoaderWrapper:getResourceAsStream()
ClassLoaderWrapper->>ClassLoader:getResourceAsStream()
ClassLoader->>InputStream:''
InputStream->>InputStreamReader:getResourceAsReader
InputStreamReader->>Reader:new InputStreamReader()
```

### 1.2构建SqlSessionFactory

构建SqlSessionFactory的时序图,使用`SqlSessionFactoryBuilder`创建`SqlSessionFactory`对象

```
sequenceDiagram

SqlSessionFactoryBuilder->>XMLConfigBuilder: build(reader)

XMLConfigBuilder->>XPathParser: XMLConfigBuilder(XPathParser)

XPathParser->>DocumentBuilder:createDocument()
DocumentBuilder->>DOMParser:parse()
DOMParser->>Document:getDocument()
Document->>XMLConfigBuilder:XMLConfigBuilder()
XMLConfigBuilder->>Configuration:parse()
Configuration->>Configuration:parseConfiguration()
Configuration->>Configuration:parseConfiguration()
SqlSessionFactoryBuilder->>SqlSessionFactory:build(Configuration)
```

根据时序图可以看得出，创建sessionFactory需要

- 将读入的配置文件流解析为Document对象
- 将Document对象解析为Configuration对象
- 使用Configuration对象，通过build（）方法完成对SqlSessionFactory对象的创建。


### 1.3 获取数据库操作的会话

```
sequenceDiagram

SqlSessionFactory->>DefaultSqlSessionFactory:openSession()
DefaultSqlSessionFactory->>DefaultSqlSessionFactory:openSessionFromDataSource()
DefaultSqlSessionFactory->>SqlSession:new DefaultSqlSession()
```

### 1.4 获取接口实例


```
sequenceDiagram

SqlSession->>DefaultSqlSession:getMapper()
DefaultSqlSession->>Configuration:getMapper()
Configuration->>MapperRegistry:getMapper()
MapperRegistry->>Proxy:newInstance()
Proxy->>UserMapper:Proxy.newProxyInstance()
```

```
sequenceDiagram

UserMapper->>MapperProxy:getUserById()
MapperProxy->>MapperMethod:execute()
MapperMethod->>DefaultSqlSession:selectOne()
DefaultSqlSession->>DefaultSqlSession:selectList()
DefaultSqlSession->>CachingExecutor:query()
CachingExecutor->>SimpleExecutor:query()
SimpleExecutor->>BaseExecutor:queryFromDatabase()
BaseExecutor->>SimpleExecutor:doQuery()
SimpleExecutor->>SimpleExecutor:prepareStatement()
SimpleExecutor->>BaseExecutor:getConnection()
SimpleExecutor->>PreparedStatementHandler:prepare()
SimpleExecutor->>PreparedStatementHandler:query()
PreparedStatementHandler->>PreparedStatement:execute()
PreparedStatement->>DefaultResultSetHandler:handleResultSets()
DefaultResultSetHandler->>UserMapper:''

```

在执行具体的
`org.apache.ibatis.executor.SimpleExecutor.prepareStatement(StatementHandler, Log)`处连接数据库，检查是否能连接成功


**数据库连接时序图**

```sequence
sequenceDiagram

SimpleExecutor->>SimpleExecutor:getConnection()
SimpleExecutor->>BaseExecutor:getConnection()
BaseExecutor->>JdbcTransaction:getConnection()
JdbcTransaction->>JdbcTransaction:openConnection()
JdbcTransaction->>DataSource:getConnection()
DataSource->>DataSource:popConnection()
DataSource->>DataSource:new PooledConnection()
DataSource->>UnpooledDataSource:doGetConnection()
UnpooledDataSource->>DriverManager:getConnection()
DriverManager->>DriverInfo:connect()
DriverInfo->>BaseExecutor:newInstance()
BaseExecutor->>SimpleExecutor:''

```




