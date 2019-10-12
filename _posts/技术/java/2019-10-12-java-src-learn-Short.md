---
layout:  post
title: java源码学习之Short
category: 技术
tags: Java
keywords: 
description: 阅读源码分析SHort类的实现

---
## 继承体系

继承自：

java.lang.Number，主要是定义各个数值类型间的转换方法

实现接口：

java.io.Serializable

java.lang.Comparable，所有数值类型都实现该接口，可对值进行比较

## 内部数据结构
基本类型：short

final 修饰的常量

## 常量值设置
最大值: 215-1

最小值： -215

BIT 大小为：16

占用字节数为：2

## 核心方法和类

### 类 ShortCache

**类似于 IntegerCache，缓存了从-128 ~ 127的数值，但是high 不可更改。**

## 方法

1. parseShort

依赖于Integer.parseInt，并对取值范围进行限制。

1. valueOf

先从ShortCache判断数值是否存在，存在返回缓存的数值，否则返回新的 Short 对象。

1. toString

使用的Integer.toString方法

1. hashCode

返回 value 本身。

1. equals

先判断类型，然后比较 value

## 继承方法
1. byteValue

(byte)value

1. shortValue

value;

1. intValue

(int)value

1. longValue

(long)value;

1. floatValue

(float)value;

1. doubleValue

(double)value;

1. compareTo

因为对传入的参数没有限制和检查，如果传入参数为 null，会导致 NPE

```java
    /**
     * Compares two {@code short} values numerically.
     * The value returned is identical to what would be returned by:
     * <pre>
     *    Short.valueOf(x).compareTo(Short.valueOf(y))
     * </pre>
     *
     * @param  x the first {@code short} to compare
     * @param  y the second {@code short} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @since 1.7
     */
    public static int compare(short x, short y) {
        return x - y;
    }

```

正常情况下两个 short 值相减，为 0 则相等。大于 0 则，第一个参数大。小于 0 则第二个参数大。


## 工具方法

1. decode

依赖于Integer.decode并对取值范围进行限定。