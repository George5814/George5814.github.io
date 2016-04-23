---
layout: post
title: 从零开始搭建spring-springmvc-mybatis-mysql和dubbo项目
category: 技术
tags: java
keywords: 
description: 
---

{:toc}

## 一.准备开发环境 

### 1.工具软件

	JDK1.7+，Eclipse J2EE 最新版，mysql5.6，maven3.x，tomcat7+等必须软件
	
### 2.搭建maven项目开发环境

###	**[Eclipse从零配置Maven环境 笔记](http://blog.csdn.net/kisssun0608/article/details/45437621)**

	现在搭建好的空的maven项目是这样的：
	
![这里写图片描述](http://img.blog.csdn.net/20160123184047072)

## 二.添加项目配置

### 1.配置项目的pom文件

内容如下：

```XML
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>cn.jingzz.test</groupId>
  <artifactId>Demo</artifactId>
  <version>0.0.1</version>
  
	<properties>
		<spring.version>4.2.1.RELEASE</spring.version>
		<mongo.version>1.8.0.RELEASE</mongo.version>
		<mybatis.version>3.2.6</mybatis.version>
		<mybatis-spring-version>1.2.0</mybatis-spring-version>
		<mysql.version>5.1.29</mysql.version>
		<ojdbc14.version>10.2.0.4.0</ojdbc14.version>

		<servlet-api.version>2.5</servlet-api.version>
		<jsp-api.version>2.2</jsp-api.version>
		<jstl.version>1.2</jstl.version>

		<fastjson.version>1.1.39</fastjson.version>

		<commons-dbcp2.version>2.0</commons-dbcp2.version>
		<commons-pool2.version>2.2</commons-pool2.version>
		<commons-logging.version>1.1.3</commons-logging.version>
		<commons-io.version>2.4</commons-io.version>
		<commons-lang3.version>3.3.1</commons-lang3.version>
		<commons-fileupload.version>1.2.1</commons-fileupload.version>

		<log4j.version>1.2.17</log4j.version>
		<slf4j.version>1.7.6</slf4j.version>
		<dom4j.version>1.6.1</dom4j.version>
		<jfreechart.version>1.0.17</jfreechart.version>
		<shiro.version>1.2.3</shiro.version>

		<aspectjweaver.version>1.7.4</aspectjweaver.version>
		<bonecp.version>0.8.0.RELEASE</bonecp.version>
		<druid.version>1.0.4</druid.version>
		<c3p0.version>0.9.2.1</c3p0.version>
		<testng.version>6.8.8</testng.version>
		<junit.version>4.11</junit.version>
		<tapestry.version>5.3.7</tapestry.version>

		<asm.version>4.2</asm.version>
		<cglib.version>3.1</cglib.version>
		
		<javassist_version>3.15.0-GA</javassist_version>
		<netty_version>3.7.0.Final</netty_version>
		<mina_version>1.1.7</mina_version>
		<grizzly_version>2.1.4</grizzly_version>
		<httpclient_version>4.2.1</httpclient_version>
		<fastjson_version>1.1.39</fastjson_version>
		<bsf_version>3.1</bsf_version>
		<sorcerer_version>0.8</sorcerer_version>
		<zookeeper_version>3.4.6</zookeeper_version>
		<zkclient_version>0.1</zkclient_version>
		<curator_version>2.5.0</curator_version>
		<jedis_version>2.1.0</jedis_version>
		<xmemcached_version>1.3.6</xmemcached_version>
		<cxf_version>2.6.1</cxf_version>
		<servlet_version>3.1.0</servlet_version>
		<jetty_version>6.1.26</jetty_version>
		<validation_version>1.0.0.GA</validation_version>
		<jcache_version>0.4</jcache_version>
		<sca_version>2.0-M5.1</sca_version>
		<guice_version>3.0</guice_version>
		<cglib_version>2.2</cglib_version>
		<webx_version>3.0.8</webx_version>
        <jackson_version>2.3.3</jackson_version>
        <commons_pool_version>1.6</commons_pool_version>
		<dubbo.version>2.8.5</dubbo.version>
        

		<!-- Plugin的属性定义 -->
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<jdk.version>1.7</jdk.version>
	</properties>

	<dependencies>
		<!-- spring相关 -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-core</artifactId>
			<version>${spring.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-beans</artifactId>
			<version>${spring.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context</artifactId>
			<version>${spring.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-jdbc</artifactId>
			<version>${spring.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-web</artifactId>
			<version>${spring.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-webmvc</artifactId>
			<version>${spring.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-aop</artifactId>
			<version>${spring.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-tx</artifactId>
			<version>${spring.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-orm</artifactId>
			<version>${spring.version}</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context-support</artifactId>
			<version>${spring.version}</version>
		</dependency>
		
		<!-- mongo -->
		<dependency>
			<groupId>org.mongodb</groupId>
			<artifactId>mongo-java-driver</artifactId>
			<version>2.13.0</version>
		</dependency>		
		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-commons</artifactId>
			<version>1.11.0.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-mongodb</artifactId>
			<version>${mongo.version}</version>
		</dependency>

		<!-- mybaits -->
		<dependency>
			<groupId>org.mybatis</groupId>
			<artifactId>mybatis</artifactId>
			<version>${mybatis.version}</version>
		</dependency>
		<dependency>
			<groupId>org.mybatis</groupId>
			<artifactId>mybatis-spring</artifactId>
			<version>${mybatis-spring-version}</version>
		</dependency>
		
		<dependency>
			<groupId>com.mchange</groupId>
			<artifactId>c3p0</artifactId>
			<version>${c3p0.version}</version>
		</dependency>
		
		<!-- JDBC驱动 -->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>${mysql.version}</version>
		</dependency>

		<!-- json库 -->

		<!-- 日志库 -->
		<dependency>
			<groupId>commons-logging</groupId>
			<artifactId>commons-logging</artifactId>
			<version>${commons-logging.version}</version>
		</dependency>
		<dependency>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
			<version>${log4j.version}</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
			<version>${slf4j.version}</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>${slf4j.version}</version>
		</dependency>


		<!-- 依赖的WEB类库 -->
		<!-- -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>servlet-api</artifactId>
			<version>${servlet-api.version}</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.servlet.jsp</groupId>
			<artifactId>jsp-api</artifactId>
			<version>${jsp-api.version}</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>jstl</artifactId>
			<version>${jstl.version}</version>
			<scope>compile</scope>
		</dependency>

		<!--⑤ 依赖的测试类库 -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-test</artifactId>
			<version>${spring.version}</version>
		</dependency>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>${junit.version}</version>
		</dependency>
		<dependency>
			<groupId>org.testng</groupId>
			<artifactId>testng</artifactId>
			<version>${testng.version}</version>
		</dependency>


		<!-- 依赖的公共类库 -->
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-dbcp2</artifactId>
			<version>${commons-dbcp2.version}</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-pool2</artifactId>
			<version>${commons-pool2.version}</version>
		</dependency>
		<dependency>
			<groupId>commons-io</groupId>
			<artifactId>commons-io</artifactId>
			<version>${commons-io.version}</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-lang3</artifactId>
			<version>${commons-lang3.version}</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-proxy</artifactId>
			<version>1.0</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-jexl</artifactId>
			<version>2.1.1</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-exec</artifactId>
			<version>1.2</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-compress</artifactId>
			<version>1.8</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-daemon</artifactId>
			<version>1.0.9</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-email</artifactId>
			<version>1.3.2</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-math</artifactId>
			<version>2.2</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-collections4</artifactId>
			<version>4.0</version>
		</dependency>
		<dependency>
			<groupId>org.kuali.commons</groupId>
			<artifactId>commons-beanutils</artifactId>
			<version>1.8.3-kuali-4</version>
		</dependency>
		<dependency>
			<groupId>commons-dbutils</groupId>
			<artifactId>commons-dbutils</artifactId>
			<version>1.5</version>
		</dependency>

		<!-- 缓存 -->
		<!-- <dependency> <groupId>net.sf.ehcache</groupId> <artifactId>ehcache</artifactId> 
			<version>2.8.1</version> </dependency> <dependency> <groupId>com.googlecode.xmemcached</groupId> 
			<artifactId>xmemcached</artifactId> <version>1.4.3</version> </dependency> 
			<dependency> <groupId>opensymphony</groupId> <artifactId>oscache</artifactId> 
			<version>2.4.1</version> </dependency> -->

		<!-- httpcomponents -->
		<!-- <dependency> <groupId>org.apache.httpcomponents</groupId> <artifactId>httpclient</artifactId> 
			<version>4.3.2</version> </dependency> <dependency> <groupId>org.apache.httpcomponents</groupId> 
			<artifactId>httpcomponents-client</artifactId> <version>4.3.2</version> </dependency> -->
		<dependency>
			<groupId>org.apache.httpcomponents</groupId>
			<artifactId>httpcore</artifactId>
			<version>4.3.2</version>
		</dependency>
		<dependency>
			<groupId>org.apache.httpcomponents</groupId>
			<artifactId>httpmime</artifactId>
			<version>4.3.3</version>
		</dependency>
		<dependency>
			<groupId>org.apache.httpcomponents</groupId>
			<artifactId>httpcore-nio</artifactId>
			<version>4.3.2</version>
		</dependency>
<!-- 		<dependency>
			<groupId>org.apache.httpcomponents</groupId>
			<artifactId>httpclient-android</artifactId>
			<version>4.3.3</version>
		</dependency> -->

		<!-- 其他 -->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>${fastjson.version}</version>
		</dependency>
		<dependency>
			<groupId>org.aspectj</groupId>
			<artifactId>aspectjweaver</artifactId>
			<version>${aspectjweaver.version}</version>
		</dependency>
		<dependency>
			<groupId>dom4j</groupId>
			<artifactId>dom4j</artifactId>
			<version>${dom4j.version}</version>
		</dependency>
<!-- 		<dependency>
			<groupId>com.belerweb</groupId>
			<artifactId>pinyin4j</artifactId>
			<version>2.5.0</version>
		</dependency> -->
<!-- 		<dependency>
			<groupId>com.darwinsys</groupId>
			<artifactId>hirondelle-date4j</artifactId>
			<version>1.5.1</version>
		</dependency> -->
		<dependency>
			<groupId>net.sourceforge.htmlunit</groupId>
			<artifactId>htmlunit</artifactId>
			<version>2.14</version>
		</dependency>
		<dependency>
			<groupId>net.sourceforge.jwebunit</groupId>
			<artifactId>jwebunit-htmlunit-plugin</artifactId>
			<version>3.2</version>
		</dependency>
<!-- 		<dependency>
			<groupId>net.sourceforge.javacsv</groupId>
			<artifactId>javacsv</artifactId>
			<version>2.0</version>
		</dependency> -->
<!-- 		<dependency>
			<groupId>org.jsoup</groupId>
			<artifactId>jsoup</artifactId>
			<version>1.7.3</version>
		</dependency> -->

    	<dependency>
			<groupId>xml-apis</groupId>
			<artifactId>xml-apis</artifactId>
			<version>1.4.01</version>
		</dependency>

<!-- 		<dependency>
			<groupId>com.thoughtworks.xstream</groupId>
			<artifactId>xstream</artifactId>
			<version>1.4.7</version>
		</dependency> -->
		
		<!-- dubbo -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>dubbo</artifactId>
            <version>${dubbo.version}</version>
        </dependency>

        <dependency>
            <groupId>org.javassist</groupId>
            <artifactId>javassist</artifactId>
            <version>${javassist_version}</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty</artifactId>
            <version>${netty_version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.mina</groupId>
            <artifactId>mina-core</artifactId>
            <version>${mina_version}</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish.grizzly</groupId>
            <artifactId>grizzly-core</artifactId>
            <version>${grizzly_version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>${httpclient_version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>${fastjson_version}</version>
        </dependency>
<!--         <dependency>
            <groupId>com.thoughtworks.xstream</groupId>
            <artifactId>xstream</artifactId>
            <version>${xstream_version}</version>
        </dependency> -->
        <dependency>
            <groupId>org.apache.bsf</groupId>
            <artifactId>bsf-api</artifactId>
            <version>${bsf_version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>${zookeeper_version}</version>
        </dependency>
        <dependency>
            <groupId>com.github.sgroschupf</groupId>
            <artifactId>zkclient</artifactId>
            <version>${zkclient_version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>${curator_version}</version>
        </dependency>
        <dependency>
            <groupId>com.googlecode.xmemcached</groupId>
            <artifactId>xmemcached</artifactId>
            <version>${xmemcached_version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-frontend-simple</artifactId>
            <version>${cxf_version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-rt-transports-http</artifactId>
            <version>${cxf_version}</version>
        </dependency>
<!--         <dependency>
            <groupId>org.apache.thrift</groupId>
            <artifactId>libthrift</artifactId>
            <version>${thrift_version}</version>
        </dependency>
        <dependency>
            <groupId>com.caucho</groupId>
            <artifactId>hessian</artifactId>
            <version>4.0.7</version>
        </dependency> -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>${servlet_version}</version>
        </dependency>
<!--         <dependency>
            <groupId>org.mortbay.jetty</groupId>
            <artifactId>jetty</artifactId>
            <version>${jetty_version}</version>
        </dependency> -->

<!--         <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>${jedis_version}</version>
        </dependency> -->
        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
             <version>${validation_version}</version>
        </dependency>
<!--         <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>${hibernate_validator_version}</version>
        </dependency> -->
        <dependency>
            <groupId>javax.cache</groupId>
            <artifactId>cache-api</artifactId>
            <version>${jcache_version}</version>
        </dependency>

        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jaxrs</artifactId>
            <version>3.0.7.Final</version>
        </dependency>

        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-client</artifactId>
            <version>3.0.7.Final</version>
        </dependency>

        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-netty</artifactId>
            <version>3.0.7.Final</version>
        </dependency>

        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jdk-http</artifactId>
            <version>3.0.7.Final</version>
        </dependency>

        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jackson-provider</artifactId>
            <version>3.0.7.Final</version>
        </dependency>

        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jaxb-provider</artifactId>
            <version>3.0.7.Final</version>
        </dependency>


        <dependency>
            <groupId>com.esotericsoftware.kryo</groupId>
            <artifactId>kryo</artifactId>
            <version>2.24.0</version>
        </dependency>
        <dependency>
            <groupId>de.javakaffee</groupId>
            <artifactId>kryo-serializers</artifactId>
            <version>0.26</version>
        </dependency>
        <dependency>
            <groupId>de.ruedigermoeller</groupId>
            <artifactId>fst</artifactId>
            <version>1.55</version>
        </dependency>
        
        <dependency>
			<groupId>commons-fileupload</groupId>
			<artifactId>commons-fileupload</artifactId>
			<version>${commons-fileupload.version}</version>
		</dependency>
		
		<!-- <dependency>
			<groupId>com.xxx</groupId>
			<artifactId>stellar</artifactId>
			<version>1.0</version>
		</dependency> -->
		
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>3.8.1</version>
			<scope>test</scope>
		</dependency>
    
	<dependency>
		<groupId>org.apache.kafka</groupId>
		<artifactId>kafka_2.10</artifactId>
		<version>0.9.0.0</version>
	</dependency>

	<dependency>
		<groupId>com.xxx.yyy.sns.im</groupId>
		<artifactId>im_phosvr_api</artifactId>
		<version>0.0.1-SNAPSHOT</version>
	</dependency>
	</dependencies>

	<build>
		<sourceDirectory>src/main/java</sourceDirectory>
		<plugins>
			<plugin>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.1</version>
				<configuration>
					<source>1.7</source>
					<target>1.7</target>
				</configuration>
			</plugin>
			<plugin>
				<artifactId>maven-war-plugin</artifactId>
				<version>2.4</version>
				<configuration>
					<warSourceDirectory>src/main/webapp</warSourceDirectory>
					<failOnMissingWebXml>false</failOnMissingWebXml>
				</configuration>
			</plugin>
		</plugins>
		<resources>
			<resource>
				<directory>src/main/resources/META-INF</directory>
				<filtering>true</filtering>
				<targetPath>META-INF</targetPath>
			</resource>
			<!-- 复制非class文件 -->
			<resource>
				<directory>src/main/resources</directory>
				<filtering>true</filtering>
				<includes>
					<include>**/*.xml</include>
					<include>**/*.properties</include>
				</includes>
				<targetPath>${project.build.directory}/classes</targetPath>
			</resource>
			<resource>
				<directory>resource</directory>
				<filtering>true</filtering>
				<includes>
					<include>*.xml</include>
				</includes>
				<targetPath>${project.build.directory}/classes</targetPath>
			</resource>
			<resource>
				<directory>resource</directory>
				<filtering>true</filtering>
				<includes>
					<include>*.properties</include>
				</includes>
				<targetPath>${project.build.directory}/classes</targetPath>
			</resource>
		</resources>
	</build>
</project>
```

### 2.配置web项目的最根本文件web.xml

内容如下：

```XML
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd" version="3.0">
  <display-name>demo</display-name>
  <!-- 加载 spring的配置文件-->
  <context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/classes/applicationContext.xml</param-value>
  </context-param>
  <!-- 加载 日志配置文件-->
  <context-param>
    <param-name>log4jConfigLocation</param-name>
    <param-value>/WEB-INF/classes/log4j.properties</param-value>
  </context-param>
  <!-- 配置springmvc的拦截器
  	因为设置的springmvc的配置文件名为WEB-INF/xxx-servlet.xml,因此可以不用配置在web.xml中
  -->
  <servlet>
    <servlet-name>springMVC</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>springMVC</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
  <listener>
    <listener-class> org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>
  <filter>
    <filter-name>encodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
      <param-name>encoding</param-name>
      <param-value>UTF-8</param-value>
    </init-param>
    <init-param>
      <param-name>forceEncoding</param-name>
      <param-value>true</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <filter-name>encodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
  <error-page>
    <error-code>404</error-code>
    <location>/jsps/error_404.jsp</location>
  </error-page>
</web-app>
```

### 3.根据web.xml的加载创建并配置spring，log配置文件

其他配置文件一般放置在src/main/resources下
spring配置文件：applicationContext.xml
日志配置文件：log4j.properties

### 4.根据applicationContext.xml的加载创建并配置jdbc.properties,conf.properties等属性文件和applicationMybatis.xml，dubbo-consumer.xml等配置文件

jdbc配置文件：

```bash
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/mybatis_test?characterEncoding=utf-8
jdbc.username=root
jdbc.password=xxx
``` 

其他自定义配置文件：

mybatis配置文件：

**添加了druid数据库及监控**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
	xmlns:aop="http://www.springframework.org/schema/aop" xmlns:context="http://www.springframework.org/schema/context"
	xmlns:mvc="http://www.springframework.org/schema/mvc" xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="
		http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
		http://www.springframework.org/schema/context
		http://www.springframework.org/schema/context/spring-context-3.0.xsd
		http://www.springframework.org/schema/aop
		http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
		http://www.springframework.org/schema/mvc  
		http://www.springframework.org/schema/mvc/spring-mvc-3.0.xsd
		http://www.springframework.org/schema/task
		http://www.springframework.org/schema/task/spring-task-3.0.xsd>
		http://www.springframework.org/schema/tx
		http://www.springframework.org/schema/tx/spring-tx-3.0.xsd">

	<context:property-placeholder location="classpath:config/jdbc.properties" />

	<!-- 配置数据源 -->
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

	<!-- 数据源监控 -->
	<bean id="log-filter" class="com.alibaba.druid.filter.logging.Slf4jLogFilter">
		<property name="statementExecutableSqlLogEnable" value="true" />
	</bean>

	<!-- 防SQL注入攻击 -->
	<bean id="wall-filter" class="com.alibaba.druid.wall.WallFilter">
	</bean>

	<!-- 数据源监控 -->
	<bean id="stat-filter" class="com.alibaba.druid.filter.stat.StatFilter">
		<!-- 合并相同SQL的统计 -->
		<property name="mergeSql" value="false" />
		<!-- 显示慢 SQL -->
		<property name="slowSqlMillis" value="1000" />
		<property name="logSlowSql" value="true" />
	</bean>

	<!-- spring bean 和数据源关联监控 -->
	<bean id="druid-stat-interceptor"
		class="com.alibaba.druid.support.spring.stat.DruidStatInterceptor">
	</bean>
	<bean id="druid-stat-pointcut" class="org.springframework.aop.support.JdkRegexpMethodPointcut"
		scope="prototype">
		<property name="patterns">
			<list>
				<value>com.xxx.worktime.service.*.*Service.*</value>
				<!-- <value>com.xxx.worktime.dao.*</value> -->
			</list>
		</property>
	</bean>

	<aop:config>
		<aop:advisor advice-ref="druid-stat-interceptor"
			pointcut-ref="druid-stat-pointcut" />
	</aop:config>

	<!-- mybatis 配置 -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<property name="mapperLocations"
			value="classpath*:com/xxx/worktime/data/**/mapper/*.xml,classpath:yyy/bo/**/mappers/*.xml
			,classpath:yyy/iform/**/mappers/*.xml" />
	</bean>

	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
		<property name="basePackage" value="com.xxx.worktime.data.**.mapper,yyy.bo.**.mappers,yyy.iform.**.mappers" />
		<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
	</bean>

	<!-- 配置事务管理器 -->
	<bean id="transactionManager"
		class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource" />
	</bean>

	<!-- 开启事务控制的注解支持 -->
	<tx:annotation-driven transaction-manager="transactionManager" />


</beans>

```

dubbo-consumer配置文件：


```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
	http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <dubbo:application name="imworkbench" owner="sns" organization="xxx"/>
	
	<!-- 注册中心 -->
    <dubbo:registry address="zookeeper://172.20.13.62:2181" file="D:\dev_tools\dev_libs\cache\.dubbo\consumer.62.cache"/>
	
	<!-- 监测协议 -->
    <dubbo:monitor protocol="registry"/>

    <dubbo:protocol name="dubbo" port="8882" contextpath="services" />
    
    <!--  
    	该行配置将远程的service实例化为etpAccountService实例，并在Controller或者其他service中直接@Resource( name = "etpAccountService")注解方式调用即可。
    	不用关心是否分布式，可以和使用本地service一样使用
    -->
	 <!-- <dubbo:reference id="etpAccountService" interface="com.xxx.yyy.sns.im.phosvr.service.api.IEtpAccountService" /> -->
	 <dubbo:reference id="accountHandleService" interface="com.xxx.yyy.sns.im.phosvr.service.api.IAccountHandleService" />
	 <dubbo:reference id="payHandleService" interface="com.xxx.yyy.sns.im.phosvr.service.api.IPayHandleService" />
</beans>
```

dubbo-provider配置文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
	http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <dubbo:application name="im-phosvr" owner="sns" organization="xxx"/>
	
	<!-- service provider 本地缓存的service provider列表 文件位置-->
    <!-- <dubbo:registry address="zookeeper://172.20.13.43:2181" file="D:\dev_tools\dev_libs\cache\.dubbo\provider.62.cache"/> -->
    <dubbo:registry address="zookeeper://localhost:2181" file="D:\dev_tools\dev_libs\cache\.dubbo\provider.62.cache"/>
	
	<!-- 监测协议 -->
   <!--  <dubbo:monitor protocol="registry"/> -->

    <dubbo:protocol name="dubbo" port="8882" contextpath="services" />
    
    <!-- 实现服务 -->
    <!--spring已经注解扫描了基础包，即不用再次手写实例bean了(bean的实例化与dubbo:service的先后位置无关) -->
    <!-- <bean id="etpAccountService" class="com.xxx.yyy.sns.im.phosvr.service.imp.EtpAccountServiceImpl" /> -->
    
    <!-- 远程服务提供者配置 声明需要暴露的服务接口,ref值为@service("")中的值-->
    <dubbo:service interface="com.xxx.yyy.sns.im.phosvr.service.api.IAccountService" connections="1" executes="300" ref="accountService"/>
    <dubbo:service interface="com.xxx.yyy.sns.im.phosvr.service.api.IChargeService" connections="1" executes="300" ref="chargeService"/>
    
    <!-- 暴露管理控制台所需服务接口  start-->
    <dubbo:service interface="com.xxx.yyy.sns.im.phosvr.service.api.IAccountHandleService" connections="1" executes="300" ref="accountHandleService"/>
    <dubbo:service interface="com.xxx.yyy.sns.im.phosvr.service.api.IPayHandleService" connections="1" executes="300" ref="payHandleService"/>
    <!-- 暴露管理控制台所需服务接口  end-->
    
</beans>
```

log4j配置文件：

```shell
log4j.rootLogger=INFO, stdout
#Console
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %5p %c{1}:%L - %m%n
#LogFile
log4j.appender.file=org.apache.log4j.FileAppender
log4j.appender.file.File=../logs/jklog.log
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %5p %c{1}:%L - %m%n

#Daily LogFile
log4j.appender.dayfile=org.apache.log4j.DailyRollingFileAppender 
log4j.appender.dayfile.File=../logs/jklog.log
log4j.appender.dayfile.DatePattern='.'yyyy-MM-dd'.log' 
log4j.appender.dayfile.Append=true 
log4j.appender.dayfile.layout=org.apache.log4j.PatternLayout
log4j.appender.dayfile.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %5p %c{1}:%L - %m%n

log4j.appender.logindb.layout=org.apache.log4j.PatternLayout
log4j.logger.org.apache=INFO
log4j.logger.com.mchange=INFO
log4j.logger.org.springframework=INFO
log4j.logger.org.apache.ibatis=INFO
log4j.logger.org.mybatis.spring=INFO
com.xxx.sns.imworkbench.dao.mybatis=INFO
```

### 5.配置springmvc配置文件

	因为没有在web.xml中加载，因此使用默认springmvc的设置名称和位置，为WEB-INF/springMVC-servlet.xml文件。

springmvc配置文件内容：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans 
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd 
    http://www.springframework.org/schema/mvc 
    http://www.springframework.org/schema/mvc/spring-mvc-3.0.xsd 
    http://www.springframework.org/schema/context 
    http://www.springframework.org/schema/context/spring-context-3.0.xsd">


	<!-- 自动将被注解的controller和service等加载-->	
	<context:component-scan base-package="com.xxx.yyy.sns.im.phosvr" />
	
	<!-- springmvc注解驱动 -->
	<mvc:annotation-driven></mvc:annotation-driven> 
</beans>
```




