---
layout:  post
title: java源码学习之AbstractStringBuilder
category: 技术
tags: Java
keywords: 
description: 记录研读AbstractStringBuilder源码的内部实现知识点

---

AbstractStringBuilder拥有的能力在`StringBuilder`和`StringBuffer`中都有。

## 零碎知识点

- StringBuilder和StringBuffer都实现了AbstractStringBuilder

    而具体的除了线程安全外`StringBuilder`和`StringBuffer`都是调用的`AbstractStringBuilder`内部实现。

- AbstractStringBuilder以char数组形式存储数据，默认数组大小为0，但StringBuilder和StringBuffer默认为数组初始化大小为16

- 内部char数组最大值为2^31 - 8

- 每次扩容为原来的2倍+2,即原size为a,新size为2a+2

- 可以将过大的char数组设置缩短为有效存储的大小。（trimToSize方法）

- 可设置长度，如果新长度小于原长度，那么只截取原长度数组新长度的数据作为新数据。如果新长度大于原长度，那多出的部分用null字符填充。

- 如果拼接的字符串为null，则AbstractStringBuilder会直接拼接上“null”字符串

- AbstractStringBuilder