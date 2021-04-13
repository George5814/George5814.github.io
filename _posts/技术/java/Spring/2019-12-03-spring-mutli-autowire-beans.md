---
layout: post
title:  Spring学习 - 自动装配同一个接口的多个实现类
category: 技术
tags: Spring Java
keywords: Spring Java
description: 介绍 spring 如何将接口的多个实现类装配进 List 和 Map 中
date: 2019-12-03
author: followtry
published: true
---


## 介绍

在平时使用 Spring 的 DI 机制时，几乎都是在某个类中引用接口的单个实现。但是Spring 如何将多个实现类同时注入进`List<Interface>` 中呢。好吧，不说废话了，直接上代码吧。


原来的引用方式

```java
public interface HelloService {
    void sayHello();
}

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private HelloService  helloService;
}
```



## Spring 高级功能

还是以`HelloService`接口为例，实现多个实例。

### HelloService接口

```java
public interface HelloService {

    void sayHello();
}
```

### HelloService实现类

分别实例化`猫`、`狗`、`猪`的三个与世界打招呼的实现类。

```java
@Service("Cat")
@Order(10)
public class CatHelloService implements HelloService {
    @Override
    public void sayHello() {
        System.out.println("I'm a Cat,Hello world!");
    }
}

@Service
@Order(3)
public class DogHelloService implements HelloService {
    @Override
    public void sayHello() {
        System.out.println("I'm a Dog,Hello world!");
    }
}

@Service
@Order(1)
public class PigHelloService implements HelloService {
    @Override
    public void sayHello() {
        System.out.println("I'm a pig,Hello world!");
    }
}
```

通过`@Service("Cat")`指定实现类的 bean 名称，通过`@Order(3)`来指定实现类的顺序。

### 引用HelloService的测试类

```java
public interface TestService {

    void sayHello();
}

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private List<HelloService> multiServiceList;

    @Autowired
    private Map<String, HelloService> multiServiceMap;

    @Override
    public void sayHello() {
        System.out.println("--------------list--------------");
        for (HelloService multiService : multiServiceList) {
            multiService.sayHello();
        }

        System.out.println("--------------map--------------");
        for (Map.Entry<String, HelloService> entry : multiServiceMap.entrySet()) {
            System.out.println("key=" + entry.getKey());
            entry.getValue().sayHello();
        }
    }
}
```

在`TestServiceImpl`该类中，通过 List和 Map 两种方式引入了`HelloService`的实现类注入，期望是可以按照我们的预期将`HelloService`的所有类都注入。


### 测试主类

初始化 Spring 并调用`TestService`的方法

```java
public class TestMain {
    public static void main(String[] args) {
        GenericApplicationContext applicationContext = new AnnotationConfigApplicationContext("cn.followtry.boot");
        TestService testService = applicationContext.getBean(TestService.class);
        testService.sayHello();
        System.out.println("结束");
    }
}
```

### 执行结果

```
--------------list--------------
I'm a pig,Hello world!
I'm a Dog,Hello world!
I'm a Cat,Hello world!
--------------map--------------
key=Cat
I'm a Cat,Hello world!
key=dogHelloService
I'm a Dog,Hello world!
key=pigHelloService
I'm a pig,Hello world!
结束
```

通过打印结果可以发现，
1. Spring帮我们将接口的所有实现类都注入进了`TestService`的`List<HelloService>`,并且是按照我们通过`@Order`指定的顺序，如果不指定Service 名称，会以默认名称的首字母排序。
2. Spring 也将接口的所有实现类都注入进了`TestService`的`Map<String, HelloService>`，Key为 Bean 的名称，value 为对应的实现类。因为Map具体类型为`LinkedHashMap`，没有按照我们指定的顺序执行。

**对于List，Map和单个实例的注入的具体实现是在`org.springframework.beans.factory.support.DefaultListableBeanFactory#resolveMultipleBeans`方法内。**

