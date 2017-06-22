---
layout: post
title: 用私有构造器或枚举类型强化Singleton属性
category: 技术
tags: Java
keywords: 
description: Singleton指仅仅被实例化一次的类
---

{:toc}


## 三种实例化方式


### 1.通过实例化公有的静态final域`SingletonBean.INSTANCE`

单例的实现代码

```java
public class SingletonBean {
	
	public static final SingletonBean INSTANCE = new SingletonBean();
	
	private SingletonBean(){};
	
	public void showInfo(){
		System.out.println("SingletonBean.showInfo()");
	}
}
```
	
因为该方式定义了构造方法，所以通过一定的手段，可以通过反射机制调用私有构造方法，实现创建多个对象。
	
```java
//附上反射调用私有构造方法构造对象的例子
Class<?> clazz = Class.forName(SingletonBean.class.getName());
Constructor<?>[] constructors = clazz.getDeclaredConstructors();
constructors[0].setAccessible(true);
SingletonBean singletonBean = (SingletonBean)constructors[0].newInstance(null);
System.out.println("反射获取的对象："+singletonBean.toString());
```
	
为了抵御该攻击，可以修改构造方法，在创建第二个对象时抛出异常。
	
```java
private SingletonBean(){
	if (INSTANCE != null) {
		throw new ConcurrentModificationException("不允许创建多个对象");
	}
};
```

结果对比图：

两个紫色框中分别是正常的单例对象和反射获取的对象通过toString方法显示的内容。
而下图报出的异常是因为在构造方法中加入了实例的非空校验，阻止了反射机制创建新的单例实例的行为。

![结果对比图](http://omsz9j1wp.bkt.clouddn.com/image/java/singleton-001.png) 


### 2.通过静态方法获取Singleton对象

单例的实现代码

```java
public class SingletonBean {
	
	public static SingletonBean instance;
	
	private SingletonBean(){};
	
	public static SingletonBean getInstance(){
		if (instance == null) {
			synchronized (SingletonBean.class) {
				if (instance == null) {
					instance = new SingletonBean();
				}
			}
		}
		return instance;
	}
}
```

该实现也可以实现正常访问代码时的单例，但和上一种实现方式具有同样的缺陷。

**注意：**

> 如果在该类的声明上“ implements Serializable”的字样，那么该类就不再是Singleton了。
> 无论使用的是默认的序列化方式还是自定义的序列化方式都没有关系；也跟它是否提供了显示的readObject方法无关。
> 任何一个readObject方法，不管是显式的还是默认的，都会返回一个新建的实例。该新建的实例不同于该类初始化时创建的实例。  

> 来自 Effective java  <第77条>


为了维护并保证Singleton，必须声明所有的实例都是瞬时（transient）的，并创建一个readResolve方法。否则每次反序列化一个实例时，都会创建一个新的实例。


### 3.最优的实现单例的方式 --- 枚举实现单例

使用枚举实现单例的优点：

- 自由序列化（无偿提供序列化机制）

- 保证只有一个实例（绝对的防止多次实例化，即使在面对复杂的序列化或者反射攻击的时候）


```java
public enum SingletonEnum {
	INSTANCE;
	
	private String name;
	
	private SingletonEnum(){};
	
	public void showName(String name){
		this.name = name;
		System.out.println("your name is "+ this.name);
	}
}
```

如果试图通过反射实例化新的枚举值会报异常,截图中显示的是比对产生的对象的hashCode

```java
Class<?> clazz = Class.forName(SingletonEnum.class.getName());
Constructor<?>[] constructors = clazz.getDeclaredConstructors();
constructors[0].setAccessible(true);
SingletonEnum singletonBean = (SingletonEnum)constructors[0].newInstance(null);
System.out.println("反射获取的枚举对象："+singletonBean.hashCode());
```

![试图通过反射实例化新的枚举值会报异常](http://omsz9j1wp.bkt.clouddn.com/image/java/reflect-enum-errorinfo.png) 

