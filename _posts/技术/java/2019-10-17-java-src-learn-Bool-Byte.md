---
layout:  post
title: java源码学习之 Boolean 和 Byte
category: 技术
tags: Java
keywords: bool , byte ,integer ,source code ,java
description: 阅读源码分析Boolean 和 Byte 类的实现
published: true

---
# Bool
## 继承体系

实现接口：

java.io.Serializable

java.lang.Comparable，所有数值类型都实现该接口，可对值进行比较

## 内部数据结构

boolean

final 修饰的常量

## 常量值设置
TRUE: new Boolean(true);

FALSE：new Boolean(false);

占用字节数为：2

## 核心方法和类

## 方法

1. parseBoolean

    忽略大小写后，判断字符串是否等于"true",是则为 true，否则为 false。
    
    ```java
    public static boolean parseBoolean(String s) {
        return ((s != null) && s.equalsIgnoreCase("true"));
    }
    ```

2. valueOf

    对值 value 进行判断，对值返回常量的`TRUE`和 `FALSE`。目测是为了优化性能，减少新产生的对象

3. hashCode

    true则返回1231，false 则返回1237

    ```java
    public static int hashCode(boolean value) {
        return value ? 1231 : 1237;
    }
    ```

    参考链接：<https://stackoverflow.com/questions/3912303/boolean-hashcode>


# Byte
## 继承体系

继承自：

java.lang.Number，主要是定义各个数值类型间的转换方法


实现接口：

java.io.Serializable

java.lang.Comparable，所有数值类型都实现该接口，可对值进行比较

## 内部数据结构

byte

final修饰的常量值

## 常量值设置

最大值： 127
最小值： -128

占用字节数为：1

## 核心方法和类

### 类 ByteCache

因为ByteCache缓存范围和 Byte 的范围正好重合，所有取值都是从ByteCache中获取的。ByteCache缓存了Byte类型的常量 数组。


## 方法

1. parseByte ，decode ，toString 等方法都是对 Integer 的方法的调用和封装。


## 继承方法
1. byteValue

value

1. shortValue

(short)value;

1. intValue

(int)value

1. longValue

(long)value;

1. floatValue

(float)value;

1. doubleValue

(double)value;

## 工具方法

1. decode

依赖于Integer.decode并对取值范围进行限定。