---
layout: post
title: mybatis-generator反向生成
category: 技术
tags:  Mybatis
keywords: 
description: update:2018-02-01
---

{:toc}


## 创建mysql数据库及表

手动创建数据库，utf-8编码

导出创建表的sql

```sql
/*
Navicat MySQL Data Transfer

Source Server         : 172.20.19.200-test
Source Server Version : 50629
Source Host           : 172.20.19.200:3306
Source Database       : test2

Target Server Type    : MYSQL
Target Server Version : 50629
File Encoding         : 65001

Date: 2016-07-21 15:50:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for esm_palmyy_dept_relation
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '当前表的主键',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `desc` varchar(255) NOT NULL COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

## 在maven项目中配置Mybatis-generator插件

### 将`generatorConfig.xml`放在`src/main/resources`下。

`generatorConfig.xml`的内容为：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN" "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
	<!-- mysql驱动jar包的位置,如果在pom.xml文件的插件mybatis-generator-maven-plugin中已经配置了依赖，则该处的classPathEntry可以省略 -->
    <classPathEntry location="D:\dev_tools\dev_libs\.m2\repository\mysql\mysql-connector-java\5.1.37\mysql-connector-java-5.1.37.jar" />
    <context id="DB2Tables" targetRuntime="MyBatis3">
        <!-- 去除自动生成的注释 -->
        <commentGenerator>
            <property name="suppressAllComments" value="true" />
        </commentGenerator>
        
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
        	 connectionURL="jdbc:mysql://172.20.19.200:3306/test" userId="test" password="test">
        </jdbcConnection>

        <javaTypeResolver>
        	<!-- 是否强制转换Decimal或number域为java的BigDecimal -->
            <property name="forceBigDecimals" value="false" />
        </javaTypeResolver>

		<!-- java domain 位置,targetProject可以写fs位置 -->
        <javaModelGenerator targetPackage="com.mytest.palmyy.dao.model"
            targetProject="src/main/java">
            <property name="enableSubPackages" value="true" />
            <!-- setter方法是否过滤空字符 -->
            <property name="trimStrings" value="false" />
        </javaModelGenerator>

		<!-- 生成的映射xml位置 -->
        <sqlMapGenerator targetPackage="com.mytest.palmyy.dao"
            targetProject="src/main/resources">
            <property name="enableSubPackages" value="true" />
        </sqlMapGenerator>

		<!-- 生成映射mapper接口位置 -->
       <javaClientGenerator type="XMLMAPPER"
            targetPackage="com.mytest.palmyy.dao"
            targetProject="src/main/java">
            <property name="enableSubPackages" value="true" />
        </javaClientGenerator>
        
        <!-- 需要自动逆向生成代码的表 -->
        <table tableName="esm_palmyy_dept_relation"  domainObjectName="epDeptRelation" 
        	enableCountByExample="false"
            enableUpdateByExample="false" 
            enableDeleteByExample="false"
            enableSelectByExample="false" 
            selectByExampleQueryId="false" >
            
            <property name="useActualColumnNames" value="false" />
            <columnOverride column="LONG_VARCHAR_FIELD" jdbcType="VARCHAR" />
        </table>
    </context>
</generatorConfiguration>
```

如上配置文件所示，Mybatis自动生成的一般配置就完成了。

**注意：配置文件中标签必须按照顺序来编写，否则在生成代码时会报错。** 

- 原因

按顺序排序是由文件<http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd>定义的，其中有内容为

```DTD
<!-- 按property，plugin，commentGenerator ...等顺序编写子标签 -->
<!ELEMENT context (property*, plugin*, commentGenerator?, jdbcConnection, javaTypeResolver?,
                         javaModelGenerator, sqlMapGenerator?, javaClientGenerator?, table+)>
```

这句话意思是context标签下的元素必须按照以上顺序编写，而如上的设置方式是根据DTD文件定义设置的。

dtd的修饰符号：

![dtd的修饰符号](//raw.githubusercontent.com/George5814/blog-pic/master/image/mybatis/dtd-definition.jpg)


- 错误内容：

    XML Parser Error on line 23: 元素类型为 "context" 匹配 "(property*,plugin*,commentGenerator?,(connectionFactory \|jdbcConnection),javaTypeResolver?,javaModelGenerator,sqlMapGenerator?,javaClientGenerator?,table+)"。

- 标签的配置顺序如下

```xml
<context>
    <commentGenerator/>
    <property/>
    <jdbcConnection/>或者<connectionFactory/>
    <javaTypeResolver/>
    <javaModelGenerator/>
    <sqlMapGenerator/>
    <javaClientGenerator/>
    <table/>
</context>
```





### 在`pom.xml`中添加Mybatis生成插件的依赖

```xml
<dependencies>
	<!-- mybatis反向生成插件,生成时打开，完成后关闭该依赖 -->
	<dependency>
		<groupId>org.mybatis.generator</groupId>
		<artifactId>mybatis-generator-maven-plugin</artifactId>
		<version>1.3.2</version>
	</dependency>
</dependencies>
  
<!-- mybatis反向生成插件,生成时打开，完成后关闭该依赖 -->
<build>
	<plugins>
		<plugin>
			<groupId>org.mybatis.generator</groupId>
			<artifactId>mybatis-generator-maven-plugin</artifactId>
			<version>1.3.2</version>
             <configuration>
                    <verbose>true</verbose>
                    <overwrite>true</overwrite>
                    <configurationFile>src/main/resources/generatorConfig.xml</configurationFile>
                </configuration>
                <dependencies>
                    <!-- jdbc 依赖 -->
                    <dependency>
                        <groupId>mysql</groupId>
                        <artifactId>mysql-connector-java</artifactId>
                        <version>5.1.36</version>
                    </dependency>
                </dependencies>
		</plugin>
	</plugins>
</build>
```

## 生成代码

在项目的根目录（即`pom.xml`文件所在目录），通过执行maven命令`mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate`在指定位置生成java代码和`*mapper.xml`sql映射文件。

一个`generatorConfig.xml`配置文件可以配置多个`<context>`标签，但必须设置id属性，并且设置唯一id值，在生成代码的命令中，可以通过指定context集合（逗号分隔）对指定的context的配置生成代码。命令为`mvn mybatis-generator:generate -Dmybatis.generator.contexts=contextId1,contextId2`
