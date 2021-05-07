---
layout: post
title:  Spring学习--SpringCore包的结构和主要功能
category: 技术
tags: Spring Java
keywords: Spring Java
description: spring-core包的结构和主要功能
date: 2021-05-07
author: followtry
published: true
---


## 主要功能

Spring-core内都是基础通用的功能，不涉及到Spring的IOC等概念。

主要目录功能包括

1. ASM： 对ASM7.x功能的重新打包，避免应用级的ASM的依赖和任何潜在冲突。
1. cglib：对CGlib核心包的重新打包，仅限于Spring内部使用
1. core.annotation: 注解,元注解，属性覆盖合并注解的核心支持包
1. core.codec: 在字节流和java对账建进行转换的Encoder和Decoder抽象及具体实现
1. core.convert: 不同类型间的转换器
1. core.env: Spring的环境抽象和对分成属性源的支持
1. core.io: 用于处理字节缓冲区实现的通用抽象
1. core.log: 对于一系列Log的代理和包装
1. core.serializer: 序列化和反序列化相关的接口及实现类
1. core.task: 定义了spring的任务执行器的抽象，并提供同步任务执行器`SyncTaskExecutor`和简单的异步任务执行器`SimpleAsyncTaskExecutor`的实现
1. core.type： 类型自省的核心支持包
1. core: 提供用于异常处理和版本检测的基本类，以及其他不特定于框架任何部分的核心帮助程序。
1. lang: 提供语言级别羽翼的通用的注解，在框架代码库中的描述性使用，可以被构建工具或者IDE识别验证。
1. objenesis： spring对objenesis的重新打包，避免与三方包和依赖的潜在冲突
1. utils: spring的基础工具包，主要报错编码，类，集合，数值，问津系统，id生成器，网络IO等的工具类
1. utils.backoff: 一种通用的退避抽象。
1. utils.comparator:比较器及实现类，比如可逆比较器和复合比较器
1. utils.concurrent:  future扩展相关的适配器和回调等类
1. utils.function: Supplier的单例工具类
1. utils.unit: 数据单位转换。B，KB，MB，GB，TB等
1. utils.xml: xml的解析和转换的工具类


### core.serializer 包

接口为`Deserializer`和`Serializer`，分别是反序列化和序列化的策略接口。用于进行Java对象和输入输出流之间的转换

#### Serializer实现类

##### DefaultSerializer 

该类为Serializer接口的默认实现类，功能就是将java对象转为Output流。核心代码如下

```java
//直接使用ObjectOutputStream将java的Obj抓换，当然在转换前需要实现Serializable接口
ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
objectOutputStream.writeObject(object);
objectOutputStream.flush();
```

#### SerializingConverter

该类为`Serializer`的代理类，用来将java Object转换为Byte的字节数组。而使用的转换器就是`DefaultSerializer`

#### Deserializer实现类

##### DefaultDeserializer

该类为`Deserializer`实现类接口的默认实现类，功能就是将InputStream转换为Java Object。在转换前需要先指定classLoader，用来检查要转换的java Object的类的信息。

核心代码为

```java
ObjectInputStream objectInputStream = new ConfigurableObjectInputStream(inputStream, this.classLoader);
try {
  return objectInputStream.readObject();
}
catch (ClassNotFoundException ex) {
  throw new NestedIOException("Failed to deserialize object type", ex);
}
```

#### DeserializingConverter

该类主要用来将字节数组准换为java对象,核心代码如下

```java
public Object convert(byte[] source) {
  ByteArrayInputStream byteStream = new ByteArrayInputStream(source);
  try {
    return this.deserializer.deserialize(byteStream);
  }
  catch (Throwable ex) {
    throw new SerializationFailedException("Failed to deserialize payload. " +
        "Is the byte array a result of corresponding serialization for " +
        this.deserializer.getClass().getSimpleName() + "?", ex);
  }
}
```


### SerializationDelegate

SerializationDelegate是序列化和反序列化比较重要和常用的类，该类实现了`Serializer`和`Deserializer`，可以同时支持序列化和反序列化

### core.log 包是对Log的桥接，不再赘述

### core包下的类

1. AliasRegistry： 别名注册器接口。可以注册别名，删除别名，获取所有别名和判断名称是否为别名。（个人认为放在spring-core中不合适，可以放在spring-beans中。因为spring-core中基本都是工具类，基本没有和Spring的概念相关）

1. AttributeAccessor： 对象属性的访问接口，可以设置属性，访问属性，删除属性和判断属性是否存在。（个人同样认为将该接口移入spring-beans中更合适）

1. AttributeAccessorSupport： 为`AttributeAccessor`的实现类，内部存储结构是个LinkedHashMap，所有属性的操作都是对Map的操作。Map的Key为String，Value为Object。
1. BridgeMethodResolver： 桥接方法的解析器。获取桥接方法的原始方法。判断桥接方法实际上是通过判断方法名、参数的个数以及泛型类型参数来获取的。

核心方法
```java
MethodFilter filter = candidateMethod ->
					isBridgedCandidateFor(candidateMethod, bridgeMethod);
ReflectionUtils.doWithMethods(bridgeMethod.getDeclaringClass(), candidateMethods::add, filter);
if (!candidateMethods.isEmpty()) {
  bridgedMethod = candidateMethods.size() == 1 ?
      candidateMethods.get(0) :
      searchCandidates(candidateMethods, bridgeMethod);
}

private static boolean isBridgedCandidateFor(Method candidateMethod, Method bridgeMethod) {
  return (!candidateMethod.isBridge() && !candidateMethod.equals(bridgeMethod) &&
      candidateMethod.getName().equals(bridgeMethod.getName()) &&
      candidateMethod.getParameterCount() == bridgeMethod.getParameterCount());
}

```

ReflectionUtils的工具方法
```java
public static void doWithMethods(Class<?> clazz, MethodCallback mc, @Nullable MethodFilter mf) {
		// Keep backing up the inheritance hierarchy.
		Method[] methods = getDeclaredMethods(clazz, false);
		for (Method method : methods) {
			if (mf != null && !mf.matches(method)) {
				continue;
			}
			try {
				mc.doWith(method);
			}
			catch (IllegalAccessException ex) {
				throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
			}
		}
		if (clazz.getSuperclass() != null && (mf != USER_DECLARED_METHODS || clazz.getSuperclass() != Object.class)) {
			doWithMethods(clazz.getSuperclass(), mc, mf);
		}
		else if (clazz.isInterface()) {
			for (Class<?> superIfc : clazz.getInterfaces()) {
				doWithMethods(superIfc, mc, mf);
			}
		}
	}

```

同构以上两个方法找出所有的符合桥接候选方法的方法集合。查找桥接方法的原方法，会先从当前类中查找，然后再从父类中，如果父类中还没有的话，会再从所有的实现接口里去找。

后续的源码再开文章分析













