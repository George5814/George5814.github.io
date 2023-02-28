---
layout: post
title:  Spring AOT示例应用
category: 技术
tags: Java
keywords: Java Spring Graalvm Native
description:  用SpringBoot3.0.3和Graalvm实现native image默认的应用开发和部署
date: 2023-02-25
modified_date: 2023-02-25
author: followtry
published: true
istop: true
---


## 环境准备

操作系统： `mac boot pro MacOS Monterey 12.5.1`


CPU: `英特尔I7`

### 安装`java17` 

1. 从Oracle下载java17对应版本，并安装在Mac系统中
2. 设置环境变量便于快速切换shell的环境。以当前用户的zsh为例,当前用户home下的`.zshrc`文件中增加内容

```zsh
# 指定java17的home目录
export JAVA_17_HOME='/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/
Home'
# 快速将JAVA的HOME指定为java17的home目录，系统上安装多jdk版本时好用
alias java17="export JAVA_HOME=$JAVA_17_HOME"
# 设置maven别名，在使用maven命令时先设置当前shell的java环境
alias mvn17='java17;mvn '
```

在IDE中开发代码直接指定目录设置项目的JDK版本为java17即可，建议使用最新版本的`IDEA`

### 安装`Graalvm`

1. 下载对应系统对应JDK版本的Graalvm，下载页面地址： <https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-22.3.1>
1. 设置Graalvm的home目录
    ```shell
    # 将Graalvm的home路径添加到系统变量中
    export GRAALVM_HOME='/Users/{userName}/{path}/graalvm-ce-java17-amd64/Contents/Home'
    # 将graalvm的bin目录添加到系统path中，可以直接使用bin下的命令，不再需要完整的路径
    export PATH=$GRAALVM_HOME/bin:$PATH
    ```
1. 安装`native-image`
    ```shell
    $gu install native-image
    # 或者使用命令全路径
    $GRAALVM_HOME/bin/gu install native-image
    ```

## 最简示例代码

项目的代码目录如下：

![项目代码目录]({{ site.baseurl }}/img/spring/first-spring-aot-1.jpg)

### POM.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
    <!-- 继承自Springboot的parent，因此内置了native的profile及plugin等信息 -->
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.0.3</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>cn.followtry.app</groupId>
	<artifactId>spring-image-demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>spring-image-demo</name>
	<description>测试Spring的native image</description>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>

		<dependency>
			<groupId>io.micrometer</groupId>
			<artifactId>micrometer-registry-prometheus</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
	<build>
		<plugins>
        <!-- 构建原生镜像的插件 -->
			<plugin>
				<groupId>org.graalvm.buildtools</groupId>
				<artifactId>native-maven-plugin</artifactId>
				<configuration>
					<imageName>followtry-image</imageName>
					<buildArgs>
                    <!-- 开发时可使用，加快构建速度，部署时需要去掉 -->
						<buildArg>-Ob</buildArg>
					</buildArgs>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
</project>
```

### jAVA代码

应用启动入口类：

```java
package cn.followtry.app.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FollowtryImageApplication {
    public static void main(String[] args) {
        SpringApplication.run(FollowtryImageApplication.class, args);
    }
}
```

测试用的Service

```java
package cn.followtry.app.demo.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author followtry
 * @since 2023/2/24 16:55
 */
@Service
public class HelloService {

    private static final Logger log = LoggerFactory.getLogger(HelloService.class);

    @PostConstruct
    public void init() {
        System.out.println("HelloService.init");
        log.info("HelloService.init");
    }

    public String sayHello(String name) {
        String msg = "hello," + name;
        log.info(msg);
        return msg;
    }
}
```

controller如下：

```java
package cn.followtry.app.demo.web;

import cn.followtry.app.demo.service.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author followtry
 * @since 2023/2/25 20:18
 */
@RestController
@RequestMapping("test")
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("hello")
    public String hello(String name) {
        return helloService.sayHello(name);
    }
}
```

为了支持java17的编译，需要对maven添加编译参数.如目录`.mvn`下的`jvm.config`

内容如下：

```properties
--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED
--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED
--add-opens jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
--add-opens jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.math=ALL-UNNAMED
--add-opens java.base/java.util=ALL-UNNAMED
--add-opens java.base/sun.net.www.protocol.https=ALL-UNNAMED
--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED
--add-opens=java.base/java.nio=ALL-UNNAMED
-Dio.netty.tryReflectionSetAccessible=true
```

可以看出，应用代码基本没什么特别之处，但就这样的普通代码就可以最终被编译成本地可执行的镜像文件。

## 编译打包

### 原jar包的打包方式

需执行命令`mvn17 clean package`(mvn17来自于文章初始部分自定义的alias)，编译后的jar文件`spring-image-demo-0.0.1-SNAPSHOT.jar`大小为20M，且打包耗时`3.96`，如图

![jar文件大小]({{ site.baseurl }}/img/spring/first-spring-aot-2.jpg)

执行命令`java -jar ./target/spring-image-demo-0.0.1-SNAPSHOT.jar`启动java应用，从图中可以看出应用启动完耗时`2.38`秒，接口`/test/hello?name=zhangsan`可以正常访问。

![字节码jar包启动耗时]({{ site.baseurl }}/img/spring/first-spring-aot-3.jpg)

### native image打包方式

执行命令`mvn17 clean native:compile -Pnative`,经历步骤日志的关键信息包括

```properties
Scanning for projects...
maven-clean-plugin:3.2.0:clean
# native 编译前先执行类可达性分析，将需要编译的类重新生成元数据信息
native-maven-plugin:0.9.20:compile 
native-maven-plugin:0.9.20:add-reachability-metadata
maven-resources-plugin:3.3.0:resources
maven-compiler-plugin:3.10.1:compile
maven-resources-plugin:3.3.0:testResources
maven-compiler-plugin:3.10.1:testCompile
maven-surefire-plugin:2.22.2:test
spring-boot-maven-plugin:3.0.3:process-aot
maven-jar-plugin:3.3.0:jar
spring-boot-maven-plugin:3.0.3:repackage
# 需要的类元数据信息重新生成完后，开始执行Native编译

native-maven-plugin:0.9.20:compile
    GraalVM Native Image: Generating 'followtry-image' (executable)...
    [1/7] Initializing...   cost 11.4s
    [2/7] Performing analysis   cost 53.2s
    [3/7] Building universe...  cost 5.4s
    [4/7] Parsing methods...    cost 5.5s
    [5/7] Inlining methods...   cost 2.3s
    [6/7] Compiling methods..   cost 21.1s
    [7/7] Creating image...     cost 9.1s
Finished generating 'followtry-image' in 1m 56s.
Total time:  02:07 min
```

经过本地镜像编译后，生成的`followtry-image`可执行文件大小为`68M`,字节码编译后的jar包大小为`20M`。如图：
![文件大小]({{ site.baseurl }}/img/spring/first-spring-aot-4.jpg)

应用启动信息：

![应用启动信息]({{ site.baseurl }}/img/spring/first-spring-aot-5.jpg)

## 两种方式的对比信息

||原Jar方式|Native Image方式|对比倍数
|---|---|---|---
|编译时间|3.96s|127s| Native编译慢32倍
|启动时间|2.38s|0.13s| Native启动时间快18倍
|编译后大小|20M|68M| Native包是Jar包的3.4倍

如文章(<http://george5814.github.io/2023-02-24/spring-aot.html>)中所说，SpringAOT在执行后会生成Java类对应的BeanDefinition的class信息，该步骤是在`process-aot`时完成的。将打好的jar包解压后可以看到如图中增加的几种字节码文件，该文件即为将注解解析后生成的类编译而成，是为了在graalvm执行native 编译时类一定存在。

![jar包中新增的字节码类信息]({{ site.baseurl }}/img/spring/first-spring-aot-6.jpg)

另一个比较关键的是在`META-INF`中生成的反射、资源等的配置文件。已反射的配置文件`reflect-config.json`为例，如下图中示例，可看出SpringBoot的maven插件已经自定找到反射类信息并将其作为配置进行生成。

![反射类配置文件内容]({{ site.baseurl }}/img/spring/first-spring-aot-7.jpg)

## 结语

本文主要讲解了从环境安装，代码编写，编译启动，打包方式对比等方面简单介绍了SpringBoot3.0 在native image的入门使用，其中的AOT原理机制解析待后续文章继续输出。




## 参考文章

> https://www.baeldung.com/spring-native-intro#overview-1
> https://graalvm.github.io/native-build-tools/latest/graalvm-setup.html
> https://github.com/graalvm/graalvm-demos/tree/master/spring-native-image
