---
layout: post
title:  Spring学习--SpringCore包的主要功能介绍.1
category: 技术
tags: Spring Java
keywords: Spring Java
description: spring-core包的主要功能介绍
date: 2021-05-08
author: followtry
published: true
---


### CollectionFactory

CollectionFactory是用来生成相近的或者判断是否是相近集合的工厂类。

主要判断

1. 集合：
	
	- 是否是相近的集合类；创建相近的空集合对象

1. Map：
	
	- 是否是相近的Map类；创建相近的空的Map对象。


主要的指定的类如下

```java
// Standard collection interfaces
approximableCollectionTypes.add(Collection.class);
approximableCollectionTypes.add(List.class);
approximableCollectionTypes.add(Set.class);
approximableCollectionTypes.add(SortedSet.class);
approximableCollectionTypes.add(NavigableSet.class);
approximableMapTypes.add(Map.class);
approximableMapTypes.add(SortedMap.class);
approximableMapTypes.add(NavigableMap.class);

// Common concrete collection classes
approximableCollectionTypes.add(ArrayList.class);
approximableCollectionTypes.add(LinkedList.class);
approximableCollectionTypes.add(HashSet.class);
approximableCollectionTypes.add(LinkedHashSet.class);
approximableCollectionTypes.add(TreeSet.class);
approximableCollectionTypes.add(EnumSet.class);
approximableMapTypes.add(HashMap.class);
approximableMapTypes.add(LinkedHashMap.class);
approximableMapTypes.add(TreeMap.class);
approximableMapTypes.add(EnumMap.class);
```

#### 通过对象实例创建相近的集合

主要逻辑判断依次如下

1. 给定的对象是否是`LinkedList`类型，是的话新建`LinkedList`的对象
1. 给定对象是否是`List`,是的话新建`ArrayList`的对象
1. 给定对象是否是`EnumSet`,是的话克隆一份并清空数据
1. 给定对象是否是`SortedSet`，是的话将对象排序并生成新的`TreeSet`对象
1. 如果以上条件都不符合清空下，生成空的`LinkedHashSet`对象。

```java
public static <E> Collection<E> createApproximateCollection(@Nullable Object collection, int capacity) {
		if (collection instanceof LinkedList) {
			return new LinkedList<>();
		}
		else if (collection instanceof List) {
			return new ArrayList<>(capacity);
		}
		else if (collection instanceof EnumSet) {
			// Cast is necessary for compilation in Eclipse 4.4.1.
			Collection<E> enumSet = (Collection<E>) EnumSet.copyOf((EnumSet) collection);
			enumSet.clear();
			return enumSet;
		}
		else if (collection instanceof SortedSet) {
			return new TreeSet<>(((SortedSet<E>) collection).comparator());
		}
		else {
			return new LinkedHashSet<>(capacity);
		}
	}
```

#### 通过对象Class类型创建集合


主要逻辑判断依次如下

1. 给定类型是接口并且是`Set`或者`Collection`类型，则生成新的`LinkedHashSet`对象
1. 给定类型是`List`，则生成`ArrayList`对象
1. 给定类型如果是`SortedSet`或者`NavigableSet`,则生成新的`TreeSet`对象
1. 如果给定类型来自于`EnumSet`，则创建空的`EnumSet`对象
1. 否则通过反射方式获取给定类型的构造方法，生成新的实例


```java
public static <E> Collection<E> createCollection(Class<?> collectionType, @Nullable Class<?> elementType, int capacity) {
	Assert.notNull(collectionType, "Collection type must not be null");
	if (collectionType.isInterface()) {
		if (Set.class == collectionType || Collection.class == collectionType) {
			return new LinkedHashSet<>(capacity);
		}
		else if (List.class == collectionType) {
			return new ArrayList<>(capacity);
		}
		else if (SortedSet.class == collectionType || NavigableSet.class == collectionType) {
			return new TreeSet<>();
		}
	}
	else if (EnumSet.class.isAssignableFrom(collectionType)) {
		// Cast is necessary for compilation in Eclipse 4.4.1.
		return (Collection<E>) EnumSet.noneOf(asEnumType(elementType));
	}
	else {
		return (Collection<E>) ReflectionUtils.accessibleConstructor(collectionType).newInstance();
	}
}
```


#### 通过对象实例创建相近的Map

该方法还是比较简单的，通过给定的Map实例来判断具体类型，如果是`EnumMap`则生成空的`EnumMap`类型。如果是SortedMap则创建`TreeMap`对象，否则创建`LinkedHashMap`对象

```java
public static <K, V> Map<K, V> createApproximateMap(@Nullable Object map, int capacity) {
		if (map instanceof EnumMap) {
			EnumMap enumMap = new EnumMap((EnumMap) map);
			enumMap.clear();
			return enumMap;
		}
		else if (map instanceof SortedMap) {
			return new TreeMap<>(((SortedMap<K, V>) map).comparator());
		}
		else {
			return new LinkedHashMap<>(capacity);
		}
	}
```

#### 通过Map类型创建对应的Map实例

1. 先判断给定类型是否是接口，然后判断如果是`Map`类型，生成`LinkedHashMap`的空对象。
1. 如果是`SortedMap`或`NavigableMap`类型，生成`TreeMap`空对象
1. 如果是`MultiValueMap`类型，生成`LinkedMultiValueMap`对象
1. 如果是`EnumMap`类型，生成`EnumMap`的空对象
1. 否则反射通过Map的构造方法生成新的空对象。


```java
public static <K, V> Map<K, V> createMap(Class<?> mapType, @Nullable Class<?> keyType, int capacity) {
		Assert.notNull(mapType, "Map type must not be null");
		if (mapType.isInterface()) {
			if (Map.class == mapType) {
				return new LinkedHashMap<>(capacity);
			}
			else if (SortedMap.class == mapType || NavigableMap.class == mapType) {
				return new TreeMap<>();
			}
			else if (MultiValueMap.class == mapType) {
				return new LinkedMultiValueMap();
			}
		}
		else if (EnumMap.class == mapType) {
			return new EnumMap(asEnumType(keyType));
		}
		else {
			
			return (Map<K, V>) ReflectionUtils.accessibleConstructor(mapType).newInstance();
			
		}
	}
```

### 创建可适配为String的Properties。

内部使用SortedProperties来存储，当获取值时，如果值为空则返回null，否则返回`value.toString()`格式

```java
public static Properties createStringAdaptingProperties() {
	return new SortedProperties(false) {
		@Override
		@Nullable
		public String getProperty(String key) {
			Object value = get(key);
			return (value != null ? value.toString() : null);
		}
	};
}
```

## Constants

常量类，该类是将指定的class的标记为`public static final`类型的变量缓存在Map结构中。

可以通过前缀或后缀找到指定的key集合，或者将获取的常量值转化为Object，String和Number。主要是给`PropertyEditors`用的。

## Conventions

主要用来将类名转换为变量名

如
```
com.myapp.Product 转为 product
com.myapp.MyProduct 转为 myProduct
m.myapp.UKProduct 转为 UKProduct
Mono<com.myapp.Product> 转为  productMono
Flux<com.myapp.MyProduct> 转为 myProductFlux
Observable<com.myapp.MyProduct> 转为 myProductObservable
```

如果对于复数的情况，会在以上的参数名后再加上`List`字符串作为后缀。代码里用的不多，就不过多分析了。感兴趣的可以自行研究下。

## DecoratingProxy

是代理类的包装接口，SpringAOP的代理或者自定义的代理。接口方法`getDecoratedClass`是要获取最终的目标里，而不是最近一层目标类。对于多层代理是比较有用的。


## ParameterNameDiscoverer

参数名称发现的接口，该接口可以获得参数的实际名称，如果是真实名称的话会获得真实名称。该接口有三种策略实现`KotlinReflectionParameterNameDiscoverer`,`StandardReflectionParameterNameDiscoverer`,`LocalVariableTableParameterNameDiscoverer`。

1. `KotlinReflectionParameterNameDiscoverer`是给kotlin的类和接口使用的。
1. `StandardReflectionParameterNameDiscoverer`如果需要获取真实名称，需要在编译时使用`-parameters`参数
1. `LocalVariableTableParameterNameDiscoverer`使用ASM库来分析类文件



## ExceptionDepthComparator

可以根据异常抛出的深度对异常进行排序，也可以获取到异常的深度。

## ReactiveAdapter 


反应式适配器，用来将反应式流适配到一个异步或者反映式类型或者反向。还有个名为`ReactiveAdapterRegistry`的注册器，用来判断注册`Reactor`，`RxJava1`,`RxJava2`,`Java 9+ Flow.Publisher`和`kotlin Coroutines`等不同类型的流适配器








