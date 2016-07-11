---
layout: post
title: 6.hadoop-2.7.2官网文档翻译-Hadoop的兼容性
category: 技术
tags:  Hadoop
keywords: 
description: Hadoop的兼容性，官网地址：http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/Compatibility.html
---

{:toc}


### 目的

该文档主要讲述Hadoop项目的兼容目标。影响着Hadoop开发者、下游项目和最终用户的Hadoop版本间不同类型的兼容性会被列举。

对于每一中类型的兼容性，我们：

- 描述对下游项目或最终用户的影响

- 在适用的情况下，在允许不兼容的更改时，呼吁Hadoop开发者采用的政策

### 兼容类型

#### JAVA API（java的接口）

Hadoop的接口和类被注解用来描述其受众和稳定性，以保持与以前版本的兼容性。

详细请看[Hadoop 接口类别](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/InterfaceClassification.html)

- InterfaceAudience：捕获与其的观众，可能值为`Public`（对于最终用户和扩展项目）,`LimitedPrivate `（对于Hadoop组件和有密切关系的项目像`YARN`,`MapReduce`，`HBase`等等）和`Private`（对内部组件使用）

- InterfaceStability：描述那种类型的接口改变是被允许的。可能的值为`Stable`,`Evolving`,`Unstable`,`Deprecated`。

**使用案例**

- `Public-Stable`的API兼容性在确保最终用户程序和下游项目继续可以无修改的工作的情况下是必需的。

- `LimitedPrivate-Stable`的API兼容性在允许在小版本中升级单个组件时必需的。

- `Private-Stable`的API兼容性在滚动升级时是必须的。

**策略**

- `Public-Stable`的API必须在从至少一个主要版本中移除之前主要版本时被标记为过期。

- `LimitedPrivate-Stable`的API可以在主要版本间改变，但不能在一个重大的版本内。

- `Private-Stable`的API可以在主要版本间改变，但不能在一个重大的版本内。

- 没有被注解的类，隐式的为`Private`.没被注解的类成功继承了封闭类的注解。

- 注意：从原始文件生成的API需要为滚动升级做兼容。请看`wire-compatibility`片段了解更详细信息。
API和有线通信的兼容性策略需要携手解决


#### Semantic compatibility（语义通用）

Hadoop致力于确保API的行为保持一致的版本，虽然正确性的改变可能会导致行为的改变。

测试和java文档指定API的行为。更严格的指定一些API的通用性正在进行中。






