---
layout:  post
title: ServiceLoader 初级应用
category: 技术
tags: Java
keywords: 
description: 记录对ServiceLoader的基本使用。

---


### 背景

偶然的机会从[牛人博客](https://yq.aliyun.com/articles/39067?spm=5176.8091938.0.0.w4LkpB)发现了该ServiceLoader的存在，以前也遇到过通过接口查找实现类的问题，了解到的办法都是遍历指定包下的所有类然后过滤出指定接口的实现类。现发现java原生就对根据接口查找实现类的功能，现特将实践代码记录如下。

### 实战

#### 编写接口

该接口位于`cn.followtry.dubbo.api`包下，全限定名为`cn.followtry.dubbo.api.UserService`

```java
package cn.followtry.dubbo.api;

import cn.followtry.dubbo.bean.User;

public interface UserService {

  User getUserById(String id);
}
```

定义该接口，下文中会找出实现该接口的实现类。

#### 配置Maven的pom文件

添加如下代码


```xml
<dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>1.16.8<version>
</dependency>
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
            <!--使得所有的resources下的资源都构建在class路径下-->
            <resource>
                  <directory>src/main/resources</directory>
                  <filtering>true</filtering>
            </resource>
      </resources>
</build>
```


#### 编写实现类

实现`cn.followtry.dubbo.api.UserService`的类名分别为`cn.followtry.dubbo.impl.CustomUserServiceImpl`,`cn.followtry.dubbo.impl.DefaultUserServiceImpl`。

实现类如下：

```java
package cn.followtry.dubbo.impl;

import cn.followtry.dubbo.api.UserService;
import cn.followtry.dubbo.bean.User;

public class CustomUserServiceImpl implements UserService {
  public CustomUserServiceImpl() {
    System.out.println("cn.followtry.dubbo.api.UserService.CustomUserServiceImpl.CustomUserServiceImpl()");
  }

  @Override
  public User getUserById(String id) {
    return new User() {
      //初始化块，该代码相当于匿名继承类User
      {
        setId(id);
        setName("custom-user-1");
      }
    };
  }
}

```

```java
package cn.followtry.dubbo.impl;

import cn.followtry.dubbo.api.UserService;
import cn.followtry.dubbo.bean.User;

public class DefaultUserServiceImpl implements UserService {
  public DefaultUserServiceImpl() {
    System.out.println("cn.followtry.dubbo.impl.DefaultUserServiceImpl.DefaultUserServiceImpl()");
  }

  @Override
  public User getUserById(String id) {
    return new User() {
      {
        setId(id);
        setName("default-user-1");
      }
    };
  }
}

```

辅助类

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

该项目使用了lombok，关于lombok的使用自行百度或Google。


#### 创建资源文件

在`src/mian/resources`下创建`META-INF`，在其下创建`services`，在其下创建以接口全限定名命名的文件（如:`cn.followtry.dubbo.api.UserService`）在该文件内填写实现该接口的类全限定名，每个类名一行。

名为`META-INF/services`下的`cn.followtry.dubbo.api.UserService`的文件

```java
cn.followtry.dubbo.impl.DefaultUserServiceImpl
cn.followtry.dubbo.impl.CustomUserServiceImpl
```

#### 编写测试类

```java

package cn.followtry.dubbo.impl.test;

import cn.followtry.dubbo.api.UserService;
import cn.followtry.dubbo.bean.User;
import java.util.ServiceLoader;
import org.junit.Test;

/**
 * 通过接口，查找实现了接口的类
 * Created by followtry on 2017/4/22 0022.
 */
public class UserServiceTest {

  @Test
  public void getSubClassByInterface() {
    ServiceLoader<UserService> serviceLoader = ServiceLoader.load(UserService.class);
    if (serviceLoader != null) {
      for (UserService userService : serviceLoader) {
        User userById = userService.getUserById("ssssss");
        System.out.println(userById);
      }
    }
  }
}


```

#### 执行结果


执行测试类会得到以下结果，说明通过配置的资源文件可以找到指定的接口的实现类，但也只能找到资源文件中指定的实现类。

```
cn.followtry.dubbo.impl.DefaultUserServiceImpl.DefaultUserServiceImpl()
User(name=default-user-1, id=ssssss)
cn.followtry.dubbo.api.UserService.CustomUserServiceImpl.CustomUserServiceImpl()
User(name=custom-user-1, id=ssssss)
```


### 总结

该文章只是对ServiceLoader的简单实践，不涉及原理和内部实现以及高级应用，初级接触，慢慢深入。

接下来有时间还会对该工具类进行深入的实践和原理分析。
