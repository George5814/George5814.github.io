---
layout:  post
title: Dubbo的初级使用
category: 技术
tags: Java
keywords: 
description: 记录对Dubbo的基本使用。

---


## 1.背景

闲话就不讲了，Dubbo官网[Dubbo.io]已经阐述的很详细了,接下来直接实战应用Dubbo开发分布式服务。我所理解的分布式服务就是调用方和提供方可以不在同一个进程内甚至不在同一台机器，同一网段内，每次服务的调用都会经过网络传输。

## 2.实战

**maven项目模块划分**

以我的demo项目为例：

- 父模块[brief-dubbo]

  一些各模块共用的配置（包括资源配置和POM配置）或代码。

- 接口模块[brief-dubbo-api]

  提供消费方和提供方共用的接口和POJO类，该模块需要被customer和service模块依赖。

- 服务调用方（消费方）模块[brief-dubbo-customer]，以下简称customer模块。

  编写调用服务的代码，不关心服务的内部逻辑，只关心接口来处理自己内部的逻辑。

- 服务提供方模块[brief-dubbo-service]，以下简称service模块。

  编写接口内部实现，为服务调用方提供接口的功能，封装并隐藏内部实现。



### 2.1 brief-dubbo的POM配置

```xml
<properties>
    <dubbo.version>2.5.3</dubbo.version>
    <fastjson.version>1.2.17</fastjson.version>
    <slf4j.version>1.7.2</slf4j.version>
    <junit.version>4.12</junit.version>
    <log4j.version>1.2.17</log4j.version>
    <lombok.version>1.16.8</lombok.version>
    <spring.version>4.3.2.RELEASE</spring.version>
    <zkclient_version>0.1</zkclient_version>
    <zookeeper_version>3.4.9</zookeeper_version>
 </properties>
<dependencies>
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>${lombok.version}</version>
  </dependency>

  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>${spring.version}</version>
  </dependency>
</dependencies>

<dependencies>
  <!--为每个子项目都引入日志依赖-->
  <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>jcl-over-slf4j</artifactId>
      <version>${slf4j.version}</version>
  </dependency>
  <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-api</artifactId>
      <version>${slf4j.version}</version>
  </dependency>
  <dependency>
      <groupId>org.slf4j</groupId>
      <artifactId>slf4j-log4j12</artifactId>
      <version>${slf4j.version}</version>
  </dependency>

  <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>${junit.version}</version>
  </dependency>
</dependencies>
```

### 2.2 brief-dubbo-api模块配置

#### 2.2.1 POM配置

引入customer和service都会用到的依赖包

```xml
<dependencies>
    <!-- spring相关 -->
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <exclusions>
        <exclusion>
          <groupId>org.springframework</groupId>
          <artifactId>spring-web</artifactId>
        </exclusion>
      </exclusions>
  </dependency>
  <!-- fastjson -->
  <dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
  </dependency>
  <!-- dubbo -->
  <dependency> 
      <groupId>com.alibaba</groupId>
      <artifactId>dubbo</artifactId>
      <exclusions>
      <exclusion>
        <groupId>org.springframework</groupId>
  <artifactId>spring-core</artifactId>
      </exclusion>
      <exclusion>
        <groupId>org.springframework</groupId>
  <artifactId>spring</artifactId>
      </exclusion>
      </exclusions>
  </dependency>
  <!-- ZooKeeper及客户端 -->
  <dependency>
      <groupId>com.github.sgroschupf</groupId>
      <artifactId>zkclient</artifactId>
      <exclusions>
        <exclusion>
          <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        </exclusion>
      </exclusions>
  </dependency>
  <dependency>
      <groupId>org.apache.zookeeper</groupId>
      <artifactId>zookeeper</artifactId>
      <exclusions>
        <exclusion>
          <groupId>io.netty</groupId>
        <artifactId>netty</artifactId>
        </exclusion>
        <exclusion>
          <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        </exclusion>
        <exclusion>
          <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        </exclusion>
        <exclusion>
          <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
      </exclusions>
  </dependency>

  <!-- aspectjweaver -->
  <dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
  </dependency>
</dependencies>
```

#### 2.2.2 编写代码

在brief-dubbo-api模块下`src/main/java`目录下创建接口`cn.followtry.dubbo.api.UserService`和POJO类`cn.followtry.dubbo.bean.User`

```java
package cn.followtry.dubbo.api;
import cn.followtry.dubbo.bean.User;
public interface UserService{
  User getUserById(String id);
}
```

```java
package cn.followtry.dubbo.bean;
import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User implements Serializable {
  private static final long serialVersionUID = 3899555909604815507L;
  private String name;
  private String id;
}
```

这样，简单的UserService的API接口就已经定义好了，接下来配置service模块并实现接口逻辑。

### 2.3 service模块配置

#### 2.3.1 POM配置

因前面已经做了工作，所以此处配置较少。

```xml
<packaging>war</packaging>
<dependencies>
    <dependency>
      <groupId>cn.followtry</groupId>
      <artifactId>brief-dubbo-api</artifactId>
      <version>${project.version}</version>
    </dependency>
</dependencies>
<build>
  <!-- 指定构建的资源及构建的位置 -->
  <resources>
    <resource>
      <directory>src/main/java</directory>
      <targetPath>${project.build.directory}</targetPath>
      <includes>
        <include>**/*.java</include>
      </includes>
    </resource>
    <resource>
      <directory>src/main/resources</directory>
      <filtering>true</filtering>
    </resource>
  </resources>
</build>
```

#### 2.3.2 web.xml配置

在`src/main/webapp/WEB-INF`下创建`web.xml`文件并配置如下内容：

```xml
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://java.sun.com/xml/ns/javaee"
         xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
         version="3.0">
  <display-name>developer</display-name>

  <context-param>
    <param-name>contextConfigLocation</param-name>
    <!--Spring的主配置文件-->
    <param-value>
      classpath*:spring/applicationContext.xml
    </param-value>
  </context-param>
  <!--Spring启动类-->
  <listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
  </listener>

  <!--设置全局编码-->
  <filter>
    <filter-name>characterEncodingFilter</filter-name>
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
    <filter-name>characterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
</web-app>
```

#### 2.3.3 资源文件创建

在`src/main/resources`目录下创建`spring/applicationContext.xml`文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

  <context:annotation-config/>
  <!--启用注解扫描指定包及其子包下的类-->
  <context:component-scan base-package="cn.followtry.dubbo"/>
</beans>
```

在`src/main/resources`目录下创建`log4j.properties`日志配置文件

```property
log4j.rootLogger=info, stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
#log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %t %c{2}:%L - %m%n
log4j.appender.stdout.Threshold
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d %p %l - %m%n
```

#### 2.3.4 编写代码

新建类`cn.followtry.dubbo.impl.UserServiceImpl`实现`cn.followtry.dubbo.api.UserService`接口

```java
package cn.followtry.dubbo.impl;

import cn.followtry.dubbo.api.UserService;
import cn.followtry.dubbo.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

  public UserServiceImpl() {
    log.info("cn.followtry.dubbo.impl.UserServiceImpl.UserServiceImpl()");
  }

  @Override
  public User getUserById(String id) {
    //demo code
    User user = new User();
    if (id != null && !"".equals(id)) {
      user.setId(id);
      user.setName("hello world");
      return user;
    }
    user.setName("user is not found");
    return user;
  }
}
```

到这最基本最普通的服务端代码就写好了，但是为什么没有看到Dubbo的配置呢，这样就能部署为分布式服务了吗？当然不能，继续天啊及提供方的Dubbo配置。

#### 2.3.5 服务提供方Dubbo配置（xml配置方式）

首先提一下，[注册中心ZooKeeper集群搭建](http://followtry.cn/2015/04/04/ZooKeeper-setup.html)请看这里。

当前ZooKeeper集群有三个节点`192.168.2.203`,`192.168.2.202`,`192.168.2.201`。

在`src/main/resources/spring`目录下创建`dubbo-provider.xml`文件，用来配置Dubbo服务提供方

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
       http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

       <!--每个标签的具体含义请查看Dubbo官网-->

  <dubbo:application name="dubbo-provider-demo" owner="jingzz" organization="followtry" />

  <!-- 服务注册中心，即ZooKeeper集群；address地址为host:port,中间用逗号分隔-->
  <dubbo:registry protocol="zookeeper" address="192.168.2.203:2181,192.168.2.201:2181,192.168.2.202:2181"/>

  <dubbo:protocol name="dubbo" port="28080" contextpath="services" accesslog="true"/>

  <!-- 暴露服务 -->
  <!--
    spring已经注解扫描了基础包，即不用再次手写实例bean了(bean的实例化与dubbo:service的先后位置无关)
    如果接口实现类没有自定义service name,则使用实现类名称（首字母小写）作为ref，否则使用自定义名称。
    如：对于UserServiceImpl implements UserService，在@Service注解中指定名称userSerivce则使用指定名称userService，否则使用userServiceImpl(这是SpringIOC容器实例化bean时的命名生成规则决定的)。
   -->
  <dubbo:service interface="cn.followtry.dubbo.api.UserService" connections="1" executes="300" ref="userService"/>

</beans>
```

在`applicationContext.xml`中加一句`<import resource="dubbo-provider.xml"/>`,以便引入`dubbo-provider.xml`的配置。

这次Dubbo服务提供方真的可以在注册中心注册了。在Tomcat下部署并启动service模块。部分打印日志如下：

```logger

[DUBBO] Export dubbo service cn.followtry.dubbo.api.UserService to local registry, dubbo version: 2.5.3, current host: 127.0.0.1

[DUBBO] Export dubbo service cn.followtry.dubbo.api.UserService to url dubbo://192.168.31.1:28080/services/cn.followtry.dubbo.api.UserService?accesslog=true&anyhost=true&application=dubbo-provider-demo&dubbo=2.5.3&group=primary&interface=cn.followtry.dubbo.api.UserService&methods=getUserById&organization=followtry&owner=jingzz&pid=213160&revision=0.0.3&side=provider&timestamp=1492965068638, dubbo version: 2.5.3, current host: 127.0.0.1

[DUBBO] Register dubbo service cn.followtry.dubbo.api.UserService url dubbo://192.168.31.1:28080/services/cn.followtry.dubbo.api.UserService?accesslog=true&anyhost=true&application=dubbo-provider-demo&dubbo=2.5.3&group=primary&interface=cn.followtry.dubbo.api.UserService&methods=getUserById&organization=followtry&owner=jingzz&pid=213160&revision=0.0.3&side=provider&timestamp=1492965068638 to registry registry://192.168.2.203:2181/com.alibaba.dubbo.registry.RegistryService?application=dubbo-provider-demo&backup=192.168.2.201:2181,192.168.2.202:2181&dubbo=2.5.3&organization=followtry&owner=jingzz&pid=213160&registry=zookeeper&timestamp=1492965068638, dubbo version: 2.5.3, current host: 127.0.0.1

[DUBBO] Register: dubbo://192.168.31.1:28080/services/cn.followtry.dubbo.api.UserService?accesslog=true&anyhost=true&application=dubbo-provider-demo&dubbo=2.5.3&group=primary&interface=cn.followtry.dubbo.api.UserService&methods=getUserById&organization=followtry&owner=jingzz&pid=213160&revision=0.0.3&side=provider&timestamp=1492965068638, dubbo version: 2.5.3, current host: 127.0.0.1

[DUBBO] Subscribe: provider://192.168.31.1:28080/services/cn.followtry.dubbo.api.UserService?accesslog=true&anyhost=true&application=dubbo-provider-demo&category=configurators&check=false&dubbo=2.5.3&group=primary&interface=cn.followtry.dubbo.api.UserService&methods=getUserById&organization=followtry&owner=jingzz&pid=213160&revision=0.0.3&side=provider&timestamp=1492965068638, dubbo version: 2.5.3, current host: 127.0.0.1

[DUBBO] Notify urls for subscribe url provider://192.168.31.1:28080/services/cn.followtry.dubbo.api.UserService?accesslog=true&anyhost=true&application=dubbo-provider-demo&category=configurators&check=false&dubbo=2.5.3&group=primary&interface=cn.followtry.dubbo.api.UserService&methods=getUserById&organization=followtry&owner=jingzz&pid=213160&revision=0.0.3&side=provider&timestamp=1492965068638, urls: [empty://192.168.31.1:28080/services/cn.followtry.dubbo.api.UserService?accesslog=true&anyhost=true&application=dubbo-provider-demo&category=configurators&check=false&dubbo=2.5.3&group=primary&interface=cn.followtry.dubbo.api.UserService&methods=getUserById&organization=followtry&owner=jingzz&pid=213160&revision=0.0.3&side=provider&timestamp=1492965068638], dubbo version: 2.5.3, current host: 127.0.0.1

[DUBBO] The service ready on spring started. service: cn.followtry.dubbo.api.UserService, dubbo version: 2.5.3, current host: 127.0.0.1

```

这样服务已经暴露给调用方，只等调用方调用即可了。那赶紧编写调用方代码测试下服务是否发布成功吧。

### 2.4 customer模块配置

#### 2.4.1 POM配置


```xml
<packaging>war</packaging>
<dependencies>
    <dependency>
        <groupId>cn.followtry</groupId>
        <artifactId>brief-dubbo-api</artifactId>
        <version>${project.version}</version>
    </dependency>
</dependencies>
<build>
    <!-- 指定构建的资源及构建的位置 -->
    <resources>
        <resource>
            <directory>src/main/java</directory>
            <targetPath>${project.build.directory}</targetPath>
            <includes>
                <include>**/*.java</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```

#### 2.4.1 消费方的资源文件配置

`src/main/resources`创建`applicationContext.xml`文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="
	      http://www.springframework.org/schema/beans
		    http://www.springframework.org/schema/beans/spring-beans.xsd
       	http://www.springframework.org/schema/aop
       	http://www.springframework.org/schema/aop/spring-aop.xsd 
       	http://www.springframework.org/schema/context 
       	http://www.springframework.org/schema/context/spring-context.xsd">

  <context:annotation-config/>
  <context:component-scan base-package="cn.followtry.dubbo"/>
  <!-- 放弃JDK的动态代理，而使用cglib  -->
  <aop:aspectj-autoproxy proxy-target-class="true"/>
  <!--引入消费方的Dubbo配置-->
  <import resource="dubbo-consumer.xml"/>
</beans>
```

日志配置参考服务方的2.3.3段


#### 2.4.2 服务调用方Dubbo配置(xml方式)


`src/main/resources`创建`dubbo-consumer.xml`文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.alibabatech.com/schema/dubbo http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

  <dubbo:application name="customer" owner="jingzz" organization="followtry"/>

  <dubbo:registry protocol="zookeeper" address="192.168.2.203:2181,192.168.2.201:2181,192.168.2.202:2181"/>

  <dubbo:protocol name="dubbo" port="28080" contextpath="services" accepts="10"/>

  <dubbo:reference  id="userService"  interface="cn.followtry.dubbo.api.UserService"/>

</beans>

```

既然配置好了，那就需要写代码调一调测试下服务是否可以被分布式访问了。

#### 2.4.3 编写代码

**调用代码**

```java
package cn.followtry.dubbo.customer;

import cn.followtry.dubbo.api.UserService;
import cn.followtry.dubbo.bean.User;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ServiceHandler {
  @Autowired
  @Qualifier("userService")
  private UserService userService;

  public User getUser(String id) {
    return userService.getUserById(id);
  }
}

```

**测试代码**

```java
package cn.followtry.dubbo.customer.core;

import cn.followtry.dubbo.bean.User;
import cn.followtry.dubbo.customer.ServiceHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:applicationContext.xml")
public class ApplicationBoot {

  @Autowired
  private ServiceHandler serviceHandler;

  @Test
  public void testUser() {
    User jingzz = serviceHandler.getUser("zhangsan");
    System.out.println(jingzz);
  }
}

```

调用后返回结果为:

```
User(name=hello world, id=zhangsan)
```

说明调用正确，服务确实可以远程访问了。那用Spring web方式是否可以访问呢，肯定可以不过限于篇幅。读者只好动手自己试试喽！

问题来了，那如果我有多个服务接口，那么是不是在service和customer模块各添加很多的配置，岂不是配置文件难以控制，有没有可以配置自动扫描指定注解方式替代手工配置方式呢？这个必须有，继续向下看。

### 2.5 优化代码

#### 2.5.1 优化service模块

修改service模块的dubbo配置，将手工注入修改为自动扫描方式。

添加如下配置：

```xml
 <!--
  注解方式指定要暴露的包下的服务。
    内部实现是调用了Spring的ClassPathBeanDefinitionScanner类的scan接口，
    可以和<context:component-scan base-package="cn.followtry.dubbo"/>注解一样自动扫描注册bean。
    而需要暴露的服务调用的注解为@com.alibaba.dubbo.config.annotation.Service
  -->
  <dubbo:annotation package="cn.followtry.dubbo"/>
```

注释掉

```xml
<dubbo:service interface="cn.followtry.dubbo.api.UserService" connections="1" executes="300" ref="userService"/>
```

代码得做相应调整，加上dubbo规定的注解`@com.alibaba.dubbo.config.annotation.Service(interfaceClass = UserService.class)`

重启服务。

#### 2.5.2 优化customer模块

添加

```xml
<!--注解方式实例化对com.alibaba.dubbo.config.annotation.Reference注解的属性-->
  <dubbo:annotation package="cn.followtry.dubbo"/>
```

移除`<dubbo:reference  id="userService"  interface="cn.followtry.dubbo.api.UserService"/>`

代码修改,得调整注解如下

```java

package cn.followtry.dubbo.customer;

import cn.followtry.dubbo.api.UserService;
import cn.followtry.dubbo.bean.User;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

@Service
public class ServiceHandler {

  public User getUser(String id) {
    return userService.getUserById(id);
  }

//  @Autowired
//  @Qualifier("userService")
//使用dubbo定义的直接Reference解释该属性
  @Reference(interfaceClass = UserService.class)
  private UserService userService;
}

```

好的，运行下测试代码，应该是能得出正确结果的。到这里以后写暴露服务就省很多事情了。那么问题又来了，如果一个接口有多个实现的时候我怎么调用呢？嗯，这个嘛，有办法，通过为服务指定group属性，来区分同一接口的不同实现。

##### 2.5.4 解决接口多实现的调用问题

因为多实现的调用会有序列化问题，所以接口`UserService`要继承`Serializable`接口

**服务端**

原实现的`Service`注解的group参数指定为"primary",并新创建类

```java

package cn.followtry.dubbo.impl;

import cn.followtry.dubbo.api.UserService;
import cn.followtry.dubbo.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("customUserService")
//指定group为custom
@com.alibaba.dubbo.config.annotation.Service(interfaceClass = UserService.class,export = true,group = "custom")
@Slf4j
public class CustomUserServiceImpl implements UserService {
  public CustomUserServiceImpl() {
    log.info("cn.followtry.dubbo.api.UserService.CustomUserServiceImpl.CustomUserServiceImpl()");
  }

  @Override
  public User getUserById(String id) {
    return new User() {
      {
        setId(id);
        setName("custom-user-1");
      }
    };
  }
}
```

**消费端**

都不用动配置， 只要在注入属性的地方注解`Reference`添加上相应的`group`属性即可。

调用代码添加

```java
//  @Autowired
@Reference(interfaceClass = UserService.class,group = "custom")
private UserService customUserService;
public User getUser2(String id) {
  return customUserService.getUserById(id);
}
```

测试代码添加调用

```java
User zhangsan2 = serviceHandler.getUser2("zhangsan2");
System.out.println(zhangsan2);
```

返回结果为

```
User(name=hello world, id=zhangsan)
User(name=custom-user-1, id=zhangsan2)
```

## 总结

好了，dubbo的使用就简单的介绍到这里吧。夜里加班了两个班小时才搞完。这只是dubbo的入门，详细使用说明还是需要参考官网文档，极其详细。


