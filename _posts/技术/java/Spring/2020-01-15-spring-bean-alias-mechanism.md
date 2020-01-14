---
layout: post
title:  Spring学习 - bean 的别名注册原理
category: 技术
tags: Spring Java AliasRegistry
keywords: Spring Java AliasRegistry
description: 
date: 2020-01-15
author: followtry
published: true
---


## 背景

在学习Spring源码前已经知道在xml中配置的时候可以注册beanName 和多个别名aliasName，那beanName 和多个aliasName 之间的关系是怎么处理的呢？

## 实现原理

Bean 的别名注册器有定义别名功能的接口`AliasRegistry`。该接口定义了四件事情

1. 注册别名。方法`registerAlias`。
2. 删除别名。方法`removeAlias`。
3. 判断别名。方法`isAlias`。
4. 获取别名。方法`getAliases`。

那对于这四种操作，Spring 中分别是怎么实现呢？

在 Spring 中`org.springframework.core.SimpleAliasRegistry`类实现了`AliasRegistry`接口的所有功能，并以ConcurrentHashMap作为存储 beanName 和 aliasName 之间映射关系存储的数据结构。

### 注册别名

1. 判断alias和 beanName是否相同
   直接判断alias和 bean 是否相等，相等则认为alias其实就是 beanName，将该alias从 Map 中删除。
2. 判断该alias是否已经注册
   如果该alias已经注册，并且beanName 和已注册的 beanName 相等，则认为该组合已经注册，不需要重复注册。
3. 判断alias是否可被覆盖
   如果 alias 已注册，但是 beanName 不同，那么判断该别名的注册是否可以被覆盖（默认是可以覆盖），可以覆盖则直接将已有的 alias 和 beanName 关系覆盖掉。
4. 判断alias和 beanName是否是循环别名
   反向检查 alias 和 beanName 在 map 中是否互为k-v，如果是则抛出异常，别名注册失败。
5. 注册别名 
   在一系列检查都通过后，通过 Map 的 put 方法将alias 和 beanName注册进 map 中，其中 key 为 alias，Map 为 beanName。

**在该别名注册时，一个 bean 可以有多个alias，而每个 alias 也还可以有 alias。**

比如：

实例`HelloService`的 beanName 为`hello`。而别名为`hello1`、`hello2`、`hello3`、`hello11`、`hello12`。
则别名注册器中的注册如下

```java
左侧为 alias，右侧为 alias 或 beanName

<hello1,hello>
<hello2,hello>
<hello3,hello>
<hello11,hello1>
<hello12,hello1>
```

也可以这样

```java
左侧为 alias，右侧为 alias 或 beanName

<hello1,hello>
<hello2,hello>
<hello3,hello>
<hello11,hello>
<hello12,hello>
```

还可以这样

```java
左侧为 alias，右侧为 alias 或 beanName

<hello1,hello>
<hello2,hello1>
<hello3,hello2>
<hello11,hello3>
<hello12,hello11>
```

以上三种方式不管哪一种，通过一个 alias，最终都能找到真正的 beanName。而这种查找，就是`SimpleAliasRegistry`提供的额外实现方法`canonicalName`。


### 删除别名
    直接调用 map 的 remove 方法进行删除。

### 判断别名

    通过 map 的containsKey方法判断别名 alias 作为 key是否存在

### 获取别名

    获取别名就是`canonicalName`方法的反其道而行。通过 beanName 获取所有的 alias。此处使用的遍历 Map 的方式，一旦找到一个 alias，那么再用该 alias 作为 beanName 重新进行一次内层遍历，一次类推。

**此处存在一个问题：如果一个 bean 的别名特别深，那么循环查找就会呈现指数级增长。**

加入 aliasMap中总共有 100 个 bean 的别名信息，但是其中有一个 bean 的别名有 10 个，而10 个别名的格式如下

```java
左侧为 alias，右侧为 alias 或 beanName

<hello1,hello>
<hello2,hello1>
<hello3,hello2>
<hello4,hello3>
<hello5,hello4>
<hello6,hello5>
<hello7,hello6>
<hello8,hello7>
<hello9,hello8>
<hello10,hello9>
```

如果加入注册的 别名总共有100个，则查找 hello 的所有别名得循环1100次。
如果加入注册的别名总共有 1万个，则查找 hello 的所有别名得循环11万次。
如果加入注册的别名总共有 1百万个，则查找 hello 的所有别名得循环1千1百万次。

即循环比较次数是bean 注册数量的 11 倍。好在 Spring 中注册的 bean 不会超过 1w，而且正常情况一个 bean 也不会有这么多的别名，否则就太可怕了。

