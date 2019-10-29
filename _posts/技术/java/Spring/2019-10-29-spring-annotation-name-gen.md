---
layout: post
title: Spring学习 - bean的名称的生成方式
category: 技术
tags:  Spring Java
keywords: Spring Java genBeanName
description: Bean 的名称生成过程
modify: 2019-10-29
published: true
---


## spring bean name 生成类

都继承自`BeanNameGenerator`接口,外部通过该接口方法`BeanNameGenerator#generateBeanName`获取生成的 bean 名称

### AnnotationBeanNameGenerator

注解`@Component`,`@Repository`,`@Service`,`@Controller`,`@Named`,`@ManagedBean`标记的类，如果没有指定注解的 value，即为类指定 bean 的名称，那么会通过`AnnotationBeanNameGenerator`来为类生成标准的 bean名称。

`@Controller`,`@Repository`,`@Service` 注解的共性就是都以`@Component`作为其元注解。那么 Spring 在扫描需要注册的 bean 和生成 bean name 的类时就是以找到`@Component`为准。

#### bean名称生成流程

1. 先检查 Bean 的注解上是否有指定不为空字符的字符串名称。如果存在则直接使用指定的名称作为 bean 名称
2. 如果没有指定名称，会调用`buildDefaultBeanName`方法取类的短名生成 Spring 标准的 bean 名称。
3. 
4. `buildDefaultBeanName`会调用`java.beans.Introspector#decapitalize`生成名称。

#### bean 名称生成规则

bean 名称的生成主要是通过判断 bean 名称的第二个字符是否大写来决定的。

1. 类的短名只有一个字符，则将该字符转为小写，作为 bean 的名称。
2. 类的短名有多于一个字符，且**第1 和 2 个字符都大写**。则直接使用类的短名作为 bean 的名称。
3. 类的短名有多于一个字符，第二个字符小写，则将类短名的首字母小写后作为 bean 的名称。


### DefaultBeanNameGenerator

该生成器通过调用 `BeanDefinitionReaderUtils#generateBeanName`来生成 bean 的名称。

#### bean 名称的生成流程

1. 通过传入的参数`BeanDefinition`判断有没有设置bean 的名称。
2. 如果没有，判断该`BeanDefinition`有没有父类名称，有的话bean 名称用`{parentName}$child`来拼接。
3. 若果没有父类，判断有没有工厂bean 的名称，有的话 bean 名称用`{factoryBeanName}$created`来拼接。
4. 如果以上三步都没有，那就只有抛异常了。
5. 如果有了 bean 名称，然后再判断是否是内部类，如果是内部类，则是生成的 bean 名称拼接 16 进制字符。`{genBeanName}#{HexStr}`。
6. 如果非内部类，但是在注册器里已经有该 beanName 了，那在该 name 后加0。`genBeanName}#0`。