---
layout: post
title:  Calcite机制学习及 Demo
category: 技术
tags:  Calcite
keywords: Calcite RDBMS
description: 学习 Calcite，自定义后端数据源接入。数据源包括 java 方式和接入 mysql 方式。
date: 2020-03-30
author: followtry
published: true
---

## 为什么需要 Calcite
专有数据系统如 storm，flink，spark，es 等需要优化查询，也需要支持 Sql。各个系统的工程师重复工作，缺乏统一的框架

应用开发人员很多时候会将好几个数据系统集成在一起使用，需要有支持异构数据源的查询优化的系统。

## 起源


![起源](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-1.png)

在 2004 年，lucidEra 和 SQLStream 都在搞 Sql系统

在 2012 年进入 apache 作为孵化项目。

难点： 高质量的数据库需要每年 20 个人的努力和 5 年的时间。

方案： 创建一个社区可以贡献的开源框架，并用来构建自己的DBMS。

设计理念： 

灵活性 =》 关系代数

可扩展、可组合的 =》 Volcano-style planner

容易贡献  =》 java，FP 风格

## 挑战

为了集成不同类型的系统，需要以下两个特性。

足够的灵活性

足够的扩展性

## 核心功能

### 开源友好

开源产品，

背靠 apache 基金会，

基于 java 开发，可以很好的与其他数据处理系统交互。比如 Hadoop 的生态。

### 多种数据模型

提供常规数据处理范式和流式数据处理范式。

Calcite 将流视为按时间戳排序的未持久化的一组记录或事件的集合。

### 灵活的查询优化器

插件化和可扩展

范围从规则到成本模型。 

支持多种计划引擎，可以将执行计划优化为不同的优化引擎处理的阶段，具体引擎选择取决于哪个引擎最适合该阶段。

### 跨系统支持

可以跨多个查询处理系统和数据后来的执行运行和优化查询。

### 可靠性

广泛被使用，被充分的测试

广泛的测试套件，用于验证系统的所有组件，包括查询优化器规则以及与后端数据源的集成

### 支持 Sql 及扩展

支持标准的ANSI标准 Sql 和各种 Sql 方言及扩展。比如 Mysql，orcale，hive，spark 等的 Sql。

## 相关工作

calcite 是 Hadoop 生态里最广泛采用的大数据分析优化器。



## 架构

Calcite包含许多组成 DBMS 的组件，但它忽略了几个关键组件。而正是忽略了这些组件，才使得 Calcite成为在具有一个或多个数据存储位置的应用程序之间以及使用多个数据处理引擎的应用程序之间进行调解的绝佳选择。 它也是构建定制数据处理系统的坚实基础。

忽略的关键组件包括

1. 存储数据
1. 数据处理的算法
1. 存储元数据的存储库


![架构](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-2.png)

1. 核心： 算子表达式 Operator Expressions
1. 扩展：数据存储、算法和 catalog
1. 可选组件： Sql 解析器，JDBC 和 ODBC 的驱动
1. 可扩展性：Planner 重写规则、统计、成本模型、代数、UDF

### 核心组件

#### Sql 解析器和校验器

将 Sql 语句翻译成关系运算符树。

由于Calcite不包含存储层，因此它提供了一种`通过适配器在外部存储引擎中定义表模式和视图的`机制，因此可以在这些引擎之上使用

### 查询优化器（Query Optimizer）

不仅为数据库提供 Sql 优化支持，也为**已经拥有自己的语言解析和解释的系统（自定义的数据处理系统）提供了优化支持**。

一些系统支持SQL查询，但没有或没有有限的查询优化。例如，Hive和Spark都提供了对SQL语言的支持，但它们不包含优化器。 对于这种情况，一旦查询得到优化，Calcite可以将关系表达式转换回SQL。 **此功能使Calcite可以在具有SQL接口但没有优化程序的任何数据管理系统上作为独立系统运行。**

Calcite架构不仅针对优化SQL查询量身定制。数据处理系统通常选择使用自己的解析器作为自己的查询语言，这是很常见的。 Calcite也可以帮助优化这些查询，可以使用内置的关系表达式builder接口。此接口提供了构建关系表达式所需的主要结构。 优化阶段完成后，应用程序可以检索优化的相对表达式，然后可以将其映射回系统的查询处理单元

**优化引擎的组成包括**

1. 规则

1. 元数据提供程序

1. 查询计划执行程序

**需对接的功能**

1. Planner 重写规则

1. 元数据

1. 表达式构建器的实现。

## 查询代数

### 算子（Operator）： 

关系代数是 Calcite 的核心。另外Operator表达最常见的数据操作操作。如filter，project 和 join 等。

Calcite包括满足不同目的的其他运算符，比如能够简洁地表示复杂的操作，或者更有效地识别优化机会。比如 window Operator。

### 特质，特征( Traits )：

Calcite不使用不同的实体来表示逻辑运算符和物理运算符。相反，它**用traits将Operator和相关的物理属性关联**。

这些特征有助于优化器评估不同替代计划的成本，更改Traits值不会更改要求值的逻辑表达式，

Calcite包含了通过的特性比如 Ordering，Groupin，Partitioning。

优化器可以对这些属性进行推理，并利用它们来查找可避免不必要操作的计划。 例如，如果已正确排序了排序运算符的输入（可能是因为这与后端系统中行的顺序相同），则可以删除排序操作。

Calcite 的`calling convention`（调用约定）特性。本质上，特征表示将要执行表达式的数据处理系统。将调用约定包括为特征可以使Calcite实现其优化透明查询的目标，这些查询的执行可能会跨越不同的引擎，即该约定将被视为任何其他物理属性。即条件下推到具体的引擎系统进行操作。

**原始查询 和 通过调用约定特性优化后的查询**

![查询优化](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-3.png)
![查询优化](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-4.png)


举例说明：

考虑关联 Mysql 中的products 表和 splunk 中的 splunk 表。 最初，splunk 表扫描发生在`splunk convention`中，product 扫描发生在`jdbc-mysql convention`中。 这些表必须在其各自的引擎内部进行扫描。 join在 `logic convention`中，这意味着尚未选择任何实现。 此外，原始查询中的SQL查询包含一个过滤器（子句），该过滤器由`adapter-specific`规则插入到splunk中。 一种可能的实现是将Apache Spark用作外部引擎：将Join 转换为`spark convention`，其输入是从jdbc-mysql和splunk到`spark convention`的转换器。 

但是有一个更有效的实现：利用以下事实：Splunk可以通过ODBC在MySQL中执行查找，一个计划器规则通过`splunk-to-spark converter` 推动JOIN，而join 现在在splunk引擎内部运行了。

#### 流式 Sql

![流式 Sql](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-5.png)

### 调用约定（calling convention）

使用一种称为`calling convention`的物理特征（Traits）来识别与特定数据库后端相对应的关系运算符。 这些物理运算符实现每个适配器中基础表的访问路径。当解析查询并将其转换为关系代数表达式时，将为每个表创建一个运算符，表示对该表上数据的扫描。 它是适配器必须实现的最小接口。 如果适配器实现表扫描Operator，则Calcite优化器可以使用客户端Operator（例如排序，过滤和联接）对这些表执行任意SQL查询

## 适配器设计

描述： 适配器是一种模式，用来定义Calcite如何合并各种数据源以进行常规访问

![适配器设计](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-6.png)

一个适配器包括

1. 模型： 定义数据源被访问的物理属性
1. Schema： 模型中数据的定义（格式和布局）。数据本身通过表进行物理访问。
1. Schema工厂： 需要从模型中获取元数据信息并生成模式

Calcite与适配器中定义的表进行接口，以在执行查询时读取数据。 适配器可以定义添加到计划程序的一组规则。 例如，它通常包含一些规则，以将各种类型的逻辑关系表达式转换为适配器约定的相应关系表达式。 架构工厂组件从模型获取元数据信息并生成模式。

为了扩展适配器的功能，Calcite 定义了叫`enumerable calling convention` 。具有`enumerable calling convention`的关系Operator只需通过iterator接口对元组进行操作。这种`calling convention`允许Calcite应用于可能在每个适配器后端不可用的操作符。对于仅触及到数据中一小部分数据的查询，Calcite无法枚举所有元组。 幸运的是，可以使用相同的基于规则的优化器来实现特定于适配器的规则以进行优化。例如，假设查询涉及对表进行过滤和排序。 可以在后端执行过滤的适配器可以实现与LogicFilter匹配的规则，并将其转换为适配器的`calling convention`。 该规则将LogicFilter转换为另一个Filter实例。 这个新的Filter节点具有较低的关联成本，这使得Calcite可以跨适配器优化查询。

**适配器的使用是一种强大的抽象，它不仅可以优化特定后端的查询，而且还可以跨多个后端优化查询。通过`将所有可能的逻辑下推到每个后端`，Calcite能够回答涉及多个后端表的查询响应，然后对结果数据执行Join和聚合。实现适配器可以像提供仅可扫描表的 Operator一样简单，也可以涉及许多高级优化的设计。关系代数中表示的任何表达式都可以使用优化器规则下推到适配器。**

![优化代数](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-7.png)

## 查询处理和优化

Calcite通过将计划程序规则重复应用于关系表达式来优化查询。

使用成本模型来知道查询的处理，并且 Calcite 会尝试生成一种替代的，成本更低的且语义相同的关系表达式来替换原有表达式。

### Planner rules（计划器的规则集）

Calcite 包含几百种的优化规则，并且允许自己的规则及允许特定的重写。

比如 LogicalFilter会被转换为CassandraFilter（paper 中示例），LogicalSort转换为CassandraSort。

规则匹配的灵活性使得即使在复杂语义下，后端系统也能下推算子。

**举例（以 paper 中的例子）**：

原Sql：

```sql
SELECT  products.name ,COUNT(*)
FROM  sales  JOIN  products   USING(productId)
WHERE   sales.discount IS  NOT NULL
GROUP BY  products.name 
ORDER BY COUNT(*)  DESC;
```

优化前和优化后的对比图。

因为`WHERE`条件中，`sales.discount IS  NOT NULL`只作用域 sales表，因此可以将 Filter 算子下推到 sales 表的存储后端执行。这样可以缩小 sales 表数据量，减少无谓数据的 Join 操作。当然，在sales和products存储后端不同时这种优化效果比较明显。在 Calcite 中是通过`FilterIntoJoinRule`规则匹配来实现。

![优化代数](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-7.png)


### Metadata providers（元数据提供程序）

`Metadata providers`负责将信息提供给优化器。尤其是Calcite中的默认元数据提供程序实现包含一些函数，这些函数返回在Operator树中执行子表达式的总成本，行数和该表达式结果的数据大小，以及该表达式可以实现的可执行的最大并行度。反过来，它也可以提供有关计划结构的信息，例如某个树节点下的过滤条件。

**目标**

1. 指导Planner实现降低总体查询计划成本的目标

1. 在应用规则时向规则提供信息

Calcite提供的接口允许数据处理系统将其元数据信息插入框架（**插件化**）。这些数据处理系统可以将已有的函数覆盖或者添加新的函数。但是通常来说，**提供有关其输入数据的统计信息就足够了。比如行数、表大小、列是否唯一值等**。其他的 Calcite 会通过默认实现去做。

Calcite 的 MetaData provider 的插件化是通过[Janino](https://github.com/janino-compiler/janino)在运行时将插件编译和实例化的。还可以缓存 MetaData 的结果，显著提高性能。

### Planner engines（计划器引擎）

主要目标就是触发已提供的规则直到达到既定目标。

Calcite 提供了两种不同的引擎，而新引擎也可以通过插件化的方式加入到框架中。

#### 基于成本的计划引擎

触发输入规则，以降低总体表达成本。使用了动态编程算法，如Volcano来创建并跟踪不同的替代计划。

1. 最初，每个表达式都将与计划器一起注册，并基于表达式属性及其输入进行摘要。

1. 当在表达式e1上触发规则并且该规则生成新的表达式e2时，计划者会将e2添加到等价表达式集中。此外，计划者还会为新表达式生成摘要，并将其与计划者中先前注册的摘要进行比较。

1. 如果找到与属于集合Sb的e3相关的相似摘要，则计划者已找到一个重复副本，因此会将Sa和Sb合并为新的等价集合。

1. 该过程一直持续到计划者达到可配置的固定点为止(这是什么点？)。


它可以

1. 详尽地探索搜索空间，直到对所有表达式应用了规则为止；

1. 当计划成本在上一次计划迭代中未将计划成本提高超过给定阈值δ时，使用基于启发式的方法来停止搜索 。 通过Metadata providers提供允许优化程序决定选择哪个计划的成本函数。 默认成本函数实现结合了给定表达式使用的CPU，IO和内存资源的估计

#### 详尽的计划引擎

它会彻底触发规则，直到它生成不再被任何规则修改的表达式为止。该计划程序对快速执行规则很有用，而无需考虑每个表达式的成本。

用户可以根据自己的具体需求选择使用现有的计划器引擎中的一种，并在系统需求发生变化时直接从一个计划者引擎切换到另一个计划者;或者，用户可以选择生成多阶段优化逻辑，在其中应用不同的规则集 在优化过程的连续阶段中。 重要的是，两个计划器的存在允许Calcite用户通过指导搜索不同的查询计划来减少总体优化时间。

#### Materialized views（物化视图）

加速数据仓库中查询处理的最强大技术之一是对相关摘要或物化视图进行预计算。

依赖Calcite的多个Calcite适配器和项目都有其自己的物化视图概念。每个项目把物化视图暴露给 Calcite，然后，优化器有机会重写传入的查询以使用这些视图而不是原始表。

Calcite提供了两种不同的基于物化视图的重写算法的实现。

方法

1. 基于视图替换。目的是用等价表达式代替物化关系代数树的一部分，该等价表达式利用物化视图，并且算法如下进行
   1. 物化视图上的 scan 算子和物化视图的定义和计划器一起注册。
   2. 触发尝试统一计划中的表达式的转换规则。视图不需要完全匹配要替换的查询中的表达式，因为Calcite中的重写算法可以产生部分重写，其中包括用于计算所需表达式的附加运算符，例如带有残留谓词条件的过滤器。

1. 基于lattices。
   1. 一旦数据源被声明为一个晶格。Calcite将每个实例化表示为一个图块，优化器可以使用该图块来回答传入的查询。 一方面，重写算法在匹配以星形模式组织的数据源上的表达式时特别有效，这在OLAP应用程序中很常见。 另一方面，它比视图替换更具限制性，因为它对基础架构施加了限制。

## 扩展 Calcite

### 半结构化数据

可以将关系型数据和半结构化数据存储在一张表中。支持复杂的类型 ARRAY,MAP或者MULTISET。而且还支持内嵌。

### 流式数据

Calcite基于标准SQL的一组特定于流的扩展，即STREAM扩展，窗口扩展，通过联接中的窗口表达式对流的隐式引用等，为流查询提供了一流的支持。这些扩展是受到连续查询语言的启发，同时还试图与标准SQL有效集成。

主要扩展，流指令告诉系统，用户感兴趣的是传入的记录，而不是现有的记录。在查询流时，如果没有STREAM关键字，该查询将成为常规的关系查询，指示系统应处理已从流中接收到的现有记录，而不是传入记录。

由于流固有的无限制性质，因此使用窗口化来解除阻塞诸如聚合和Join之类的阻塞运算符。 Calcite的Stream扩展程序使用SQL分析功能来表达滑动和级联的窗口聚合

## 产品使用方-- 做背书

<https://github.com/julianhyde/share/blob/master/slides/calcite-sigmod-2018.pdf?raw=true>


![做背书](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-8.png)
![做背书](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-9.png)
![做背书](https://raw.githubusercontent.com/George5814/blog-pic/master/image/calcite/calcite-10.png)

  

## 未来规划

1. 支持作为独立引擎

1. 支持 DDL，实例化视图，索引和约束

1. 改进规划器的设计和可扩展性（模块化，可插入性）

1. 将新的参数方法纳入优化器的设计

1. 支持 Sql 命令的扩展集合。功能和实用程序，包括完全符合OpenGIS（空间）

1. 用于非关系数据源（例如阵列数据库）的新适配器

1. 改进性能分析和检测。


## 接入 Demo

**TODO 后续补充**



## 参考文献

> [Apache Calcite: A Foundational Framework for Optimized Query Processing Over Heterogeneous Data Sources](https://arxiv.org/pdf/1802.10233)
> <https://calcite.apache.org/>