---
layout:  post
title: ServiceLoader内部实现分析
category: 技术
tags: Java
keywords: 
description: 记录对ServiceLoader的内部实现。

---


### 背景


对于技术，要知其然更要知其所以然，在[上一篇文章](http://followtry.cn/2017/04/22/serviceLoader-action-1.html)简单介绍了ServiceLoader的基本使用，完成了**知其然**的阶段，本篇要完成**知其所以然**。完成对其基本实现原理的分析。

### 原理分析

ServiceLoader类是final类型，并且实现了Iterable接口，使得该类可以被迭代，不能被继承。

```java
//关键属性

private static final String PREFIX = "META-INF/services/";

// The class or interface representing the service being loaded
private final Class<S> service;

// The class loader used to locate, load, and instantiate providers
private final ClassLoader loader;

// The access control context taken when the ServiceLoader is created
private final AccessControlContext acc;

// Cached providers, in instantiation order
//会缓存已经实例化的实现类，并且会根据实例化顺序排序
private LinkedHashMap<String,S> providers = new LinkedHashMap<>();

// The current lazy-lookup iterator
//该属性是懒加载实现类的迭代器
private LazyIterator lookupIterator;

```

接口`load(Class<S> service)`和`load(Class<S> service,ClassLoader loader)`都会直接new一个新的ServiceLoader类，在实例化时会调用`reload`方法实例化`LazyIterator`迭代类。

```java
private ServiceLoader(Class<S> svc, ClassLoader cl) {
    service = Objects.requireNonNull(svc, "Service interface cannot be null");
    loader = (cl == null) ? ClassLoader.getSystemClassLoader() : cl;
    acc = (System.getSecurityManager() != null) ? AccessController.getContext() : null;
    reload();
}

public void reload() {
    providers.clear();
    lookupIterator = new LazyIterator(service, loader);
}
```

在该接口调用返回后并去查找并加载指定接口的实现类，这是完成查找前的配置工作。接口实现类的查找是在ServiceLoader迭代时通过调用`next`和`hasNext`方法去完成。接下来分析ServiceLoader的迭代过程。

对ServiceLoader进行Iterator迭代，在调用`hasNext`时，会调用`lookupIterator`的`hasNext`,并调用`hasNextService`方法，而该方法是查找实现类全限定名的关键位置

```java
private boolean hasNextService() {
    if (nextName != null) {
        return true;
    }
    if (configs == null) {
        try {
           //例如：对于接口cn.followtry.dubbo.UserService,fullName=META-INF/services/cn.followtry.dubbo.UserService,这就是为什么要在META-INF下创建要查询接口全限定名命名的文件了。
            String fullName = PREFIX + service.getName();
            //configs为获取到的文件信息
            if (loader == null)
                configs = ClassLoader.getSystemResources(fullName);
            else
                configs = loader.getResources(fullName);
        } catch (IOException x) {
            fail(service, "Error locating configuration files", x);
        }
    }
    while ((pending == null) || !pending.hasNext()) {
        if (!configs.hasMoreElements()) {
            return false;
        }
        //解析出资源文件中的配置的每个实现类的全限定类名
        pending = parse(service, configs.nextElement());
    }
    nextName = pending.next();
    return true;
}

```

在这步判断之后，ServiceLoader内部就已经有了接口对应的实现类的全限定名集合，在接下来调用`next`接口中会调用`nextService`方法。而该方法内部会调用`Class.forName`和`clazz.newInstance()`获取到接口实现类的实例。

`nextService`实现如下：

```java
private S nextService() {
    if (!hasNextService())
        throw new NoSuchElementException();
    String cn = nextName;
    nextName = null;
    Class<?> c = null;
    try {
      //通过获取到的全限定类名获取到Class对象
        c = Class.forName(cn, false, loader);
    } catch (ClassNotFoundException x) {
        fail(service,
              "Provider " + cn + " not found");
    }
    if (!service.isAssignableFrom(c)) {
        fail(service,
              "Provider " + cn  + " not a subtype");
    }
    try {
      //将获取到的子类实例化并将其转为需要的类型（接口类型）
        S p = service.cast(c.newInstance());
        //将已经实例化的实现类添加到缓存中，避免再次查询并实例化
        providers.put(cn, p);
        return p;
    } catch (Throwable x) {
        fail(service,
              "Provider " + cn + " could not be instantiated",
              x);
    }
    throw new Error();          // This cannot happen
}
```

### ServiceLoader缺点分析


- 虽然ServiceLoader也算是使用的延迟加载，但是基本只能通过遍历全部获取，也就是接口的实现类全部加载并实例化一遍。如果你并不想用某些实现类，它也被加载并实例化了，这就造成了浪费。

- 获取某个实现类的方式不够灵活，只能通过Iterator形式获取，不能根据某个参数来获取对应的实现类

### 总结

如上所述完成后就分析完了ServiceLoader对接口实现类的查询，其实可以发现核心实现也没有什么难的地方，无非就一下几点：

- 通过资源文件指定实现了接口的实现类名称；

- 使用java I/O方式查找该资源文件获取到该文件的信息及其内容；

- 将资源文件内部的各个实现类的名称存储在缓存中供接下来使用；

- 使用`Class.forName`通过类名构造类并对其实例化返回给调用方，即可供调用方使用了。
