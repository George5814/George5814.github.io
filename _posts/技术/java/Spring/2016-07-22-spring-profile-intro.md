---
layout: post
title: Spring的profile机制介绍
category: 技术
tags:  Spring
keywords: 
description: Spring的profile机制介绍
---

{:toc}

## 目标

因spring在使用中，比如连接数据库，缓存等属性配置在不同的环境中有所不同，而且开发人员可能再本地开发环境，测试环境甚至是生产环境间切换。尤其是本地开发环境和测试环境切换频繁。
导致每次切换都需要更改配置文件，时间久了次数多了，不仅配置有可能会出错，而且开发人员也会因琐碎的无重复工作而苦不堪言。因此，本文将简单介绍spring的profile机制，可以对不同的环境做不同的配置。
然后只需要为不同的环境打包不同的配置即可。

## 前提

该文档是以项目开发为导向，基于maven项目讲解。因此在使用前需要的软件：`Eclipse`,`tomcat 8`,`maven 3.3.3`,`mysql 5.6`等。


## 在maven项目中配置profile

### 添加配置文件`datasource-profile.xml`

该配置文件中仅包含**profile**机制的配置。
内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
	http://www.springframework.org/schema/beans/spring-beans.xsd 
	http://www.springframework.org/schema/context 
	http://www.springframework.org/schema/context/spring-context.xsd
	">

	<!-- 开发环境 -->
	<beans profile="development">
		<context:property-placeholder
			ignore-unresolvable="true"
			location="classpath*:/env/default.properties, classpath*:/env/development.properties" />
	</beans>

	<!-- 测试环境 -->
	<beans profile="testing">
		<context:property-placeholder
			ignore-unresolvable="true"
			location="classpath*:/env/default.properties, classpath*:/env/testing.properties" />
	</beans>

	<!-- 生产环境 -->
	<beans profile="produce">
		<context:property-placeholder
			ignore-unresolvable="true"
			location="classpath*:/env/default.properties, classpath*:/env/produce.properties" />
	</beans>
</beans>
```

该方式是只加载每个环境不同的配置，比如上文提到的数据库，缓存等的配置信息。
展现下`/env/default.properties`配置文件中的内容：

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://172.20.19.200:3306/esn_palmyy_plugin?useUnicode=true&characterEncoding=UTF-8
jdbc.username=esn_palmyy
jdbc.password=esn_palmyy
```

以上只是简单的数据库连接的配置。其他配置文件中的内容与其相同，但该机制中需要将不同配置文件中修改为不同的参数值。

![各环境的配置文件](http://omsz9j1wp.bkt.clouddn.com/image/spring/spring-profile-1.png)

在spring的主配置文件`applicationContext.xml`中使用`<import resource="classpath*:datasource-profile.xml" />`引入该配置，这样spring的profile的配置就简单的完成了。
**但是怎样在web项目启动时怎么指定使用的profile呢？请往下看。**

###  在Eclipse中启动web项目时应用`profile`机制

要在web项目中使用profile机制，需要在`web.xml`文件中添加几行配置。

```
<context-param>
    <param-name>spring.profiles.active</param-name>
    <param-value>development</param-value>
</context-param>
```

从属性名称上可以看出来该属性是指定web项目启动时激活使用的profile，value值即`datasource-profile.xml`中配置的profile属性值。
如果该参数不配置，那么不会加载任何profile下的内容，因此会导致使用到对应`*.properties`文件内占位符的其他配置会因为找不到替代值而报错。

虽然Eclipse中可以使用了，但如果使用mvn命令打包，每次更换环境都需要修改配置，好麻烦。能不能自动化一些呢？嗯可以，往下看。

### mvn命令打包时应用`profile`机制

既然mvn打包要指定`profile`，那就需要改`pom.xml`文件了。

在`pom.xml`中添加`<profiles>`内的所有内容：

```xml
<project>

<build>
		<plugins>
			<plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <configuration>
                	<!-- 设置war包名称 -->
                    <warName>open-api</warName>
                    <!-- 激活spring profile -->
                    <webResources>
                        <resource>
                            <filtering>true</filtering>
                            <directory>src/main/webapp</directory>
                            <includes>
                                <include>**/web.xml</include>
                            </includes>
                        </resource>
                    </webResources>
                    <warSourceDirectory>src/main/webapp</warSourceDirectory>
                    <webXml>src/main/webapp/WEB-INF/web.xml</webXml>
                </configuration>
			</plugin>
		</plugins>
	</build>

	<profiles>
		<!-- 测试环境 -->
		<profile>
			<id>testing</id>
			<activation>
				<!-- 在不指定profile情况下，默认使用的profile -->
				<activeByDefault>true</activeByDefault>
			</activation>
			<properties>
				<profiles.activation>testing</profiles.activation>
			</properties>
			<build>
				<plugins>
					<plugin>
						<groupId>org.apache.tomcat.maven</groupId>
					    <artifactId>tomcat-maven-plugin</artifactId>
					    <version>2.2</version>
						<configuration>
							<url></url>
							<path></path>
							<server></server>
							<warFile></warFile>
						</configuration>
					</plugin>
				</plugins>
			</build>
		</profile>
		
		<!-- 开发环境 -->
		<profile>
			<id>development</id>
			<properties>
				<profiles.activation>development</profiles.activation>
			</properties>
			<build>
				<plugins>
					<plugin>
						<groupId>org.apache.tomcat.maven</groupId>
					    <artifactId>tomcat-maven-plugin</artifactId>
					    <version>2.2</version>
						<configuration>
							<url></url>
							<path></path>
							<server></server>
							<warFile></warFile>
						</configuration>
					</plugin>
				</plugins>
			</build>
		</profile>
		
		<!-- 生产环境 -->
		<profile>
			<id>produce</id>
			<properties>
				<profiles.activation>produce</profiles.activation>
			</properties>
			<build>
				<plugins>
					<plugin>
						<groupId>org.apache.tomcat.maven</groupId>
					    <artifactId>tomcat-maven-plugin</artifactId>
					    <version>2.2</version>
						<configuration>
							<url></url>
							<path></path>
							<server></server>
							<warFile></warFile>
						</configuration>
					</plugin>
				</plugins>
			</build>
		</profile>
	</profiles>
</project>
```

`<build>`内的打包插件是激活spring的profile，没有该配置即使配置了`<profiles>`内的内容也不会生效。
而`<profiles>`内的每个`<profile>`对应上文配置的一个profile。

在项目根目录(pom.xml所在目录)下使用maven命令`mvn clean package`打包,当打包成功后，打开`target/project-name-version/WEB-INF/web.xml`文件，发现`spring.profiles.active`属性的值仍然是`development`。
怎么会没有改变呢？哦，原来是因为写的固定值，而不是占位符，没办法替换。

那将`web.xml`中的`spring.profiles.active`的值设置为占位符`${profiles.activation}`。

```xml
<context-param>
    <param-name>spring.profiles.active</param-name>
    <param-value>${profiles.activation}</param-value>
</context-param>
```

再次执行maven命令`mvn clean package`,执行成功后查看`web.xml`会发现`${profiles.activation}`位置已经被`testing`（pom.xml中设置默认使用的profile）替换了。

那我要想指定其他的profile呢？使用命令`mvn clean package -P profile-name`,`profile-name`是你指定的存在的profile名称，比如`produce`。执行成功后再次查看`${profiles.activation}`已经被指定的`produce`替换了。说明启动应用就会使用profile为`produce`的配置了。


哎，怎么在Eclipse中不能启动web项目了，难道还要将`web.xml`中的配置改回去吗，这样改来改去太麻烦了？别急，往下看。

### 解决mvn打包和Eclipse启动web项目报错问题

既然mvn打包没问题了，那就专注的看Eclipse中启动项目的问题，而我们又不想每次都改`web.xml`的`spring.profiles.active`,怎么办呢？嗯... 有了

在Eclipse中配置变量值，不就可以将占位符`${profiles.activation}`替换掉了嘛，太聪明了。操作如下：

项目右击  --> Run As --> Run configurations --> 左侧：选择要启动该项目的tomcat； 右侧：点击Environment，点击New，新增name和value，name就是上文中说的占位符内的变量`profiles.activation`,value填写你想指定的profile名称，如development。 -> 点OK -> 然后点Run。
这样就可以在tomcat中成功启动项目了，并且不影响maven中的打包。

**下次启动就不用配置了，直接部署项目启动tomcat就可以了。**













