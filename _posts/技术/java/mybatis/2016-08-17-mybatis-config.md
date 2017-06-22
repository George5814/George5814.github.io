---
layout: post
title: mybatis集成进spring
category: 技术
tags:  Mybatis
keywords: 
description: 在spring中使用mybatis作为持久层框架，并通过mybatis-spring将mybatis集成进spring中。
---

{:toc}


## 添加mybatis和mybatis-spring的依赖

添加spring的依赖就不说了，此处只显式设置mybatis的相关依赖,在`POM.xml`中。

```xml
<properties>
	<mybatis.version>3.3.0</mybatis.version>
	<mybatis-spring.version>1.2.2</mybatis-spring.version>
</properties>
<dependencies>
	<!-- Mybatis -->
	<dependency>
	    <groupId>org.mybatis</groupId>
	    <artifactId>mybatis</artifactId>
	    <version>${mybatis.version}</version>
	</dependency>
	<dependency>
		<groupId>org.mybatis</groupId>
		<artifactId>mybatis-spring</artifactId>
		<version>${mybatis-spring.version}</version>
	</dependency>

</dependencies>
```

## 添加mybatis的配置

该配置中使用的类都是在mybatis-spring的包中的，在其内部引用的mybatis的实现，来达到与spring的集成。

```xml
<!-- mybatis 配置 -->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
	<property name="dataSource" ref="dataSource" />
	<property name="mapperLocations"
		value="classpath*:/com/yonyou/esn/palmyy/dao/*.xml" />
</bean>

<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
	<property name="basePackage" value="com.yonyou.esn.palmyy.dao" />
	<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
</bean>
```

数据源使用阿里的**druid**

```xml

<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource"
	init-method="init" destroy-method="close">
	<!-- 驱动程序 -->
	<property name="driverClassName" value="${jdbc.driver}" />
	<!-- 基本属性 url、user、password -->
	<property name="url" value="${jdbc.url}" />
	<property name="username" value="${jdbc.username}" />
	<property name="password" value="${jdbc.password}" />

	<!-- 配置初始化大小、最小、最大 -->
	<property name="initialSize" value="1" />
	<property name="minIdle" value="3" />
	<property name="maxActive" value="50" />

	<!-- 配置获取连接等待超时的时间 -->
	<property name="maxWait" value="60000" />

	<!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
	<property name="timeBetweenEvictionRunsMillis" value="60000" />

	<!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
	<property name="minEvictableIdleTimeMillis" value="300000" />

	<property name="validationQuery" value="SELECT 'x'" />
	<property name="testWhileIdle" value="true" />
	<property name="testOnBorrow" value="false" />
	<property name="testOnReturn" value="false" />

	<!-- 打开PSCache，并且指定每个连接上PSCache的大小 -->
	<!-- 如果用Oracle，则把poolPreparedStatements配置为true，mysql可以配置为false。分库分表较多的数据库，建议配置为false。 -->
	<property name="poolPreparedStatements" value="false" />
	<property name="maxPoolPreparedStatementPerConnectionSize"
		value="20" />
	
	<!-- 定期将监控数据输出到日志，值小于等于 0 时忽略 -->
	<property name="timeBetweenLogStatsMillis" value="-1" />
	
	<!-- 配置监控统计拦截的filters -->
	<!-- <property name="filters" value="slf4j,wall,stat" /> -->

	<property name="proxyFilters">
		<list>
			<ref bean="log-filter" />

			<!-- 防SQL注入攻击检查不包含在统计时间内 -->
			<ref bean="wall-filter" />

			<ref bean="stat-filter" />
		</list>
	</property>

	<!-- 合并多个DruidDataSource的监控数据 -->
	<property name="useGlobalDataSourceStat" value="true" />
</bean>

```

当然了，这些配置都需要放置在包含有spring命名空间的配置<beans>内部

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
	xsi:schemaLocation="
		http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans.xsd>
		
</beans>
```

然后在spring的主配置文件中，将该配置文件引入即可`<import resource="classpath*:datasource.xml" />`
