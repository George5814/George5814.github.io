---
layout:  post
title: java源码学习之Enum
category: 技术
tags: Java
keywords: 
description: 阅读源码分析Enum枚举的实现

---

## Enum的API规范出处

[JSR 161](https://www.jcp.org/en/jsr/detail?id=161)


## Enum的优点

1. 编译时的类型安全；
1. 性能与int常量可比；
1. 类型系统为每个Enum类型提供了一个命名空间，这样不必每个常量设置前缀；
1. 类型安全的常量不会编译进客户端，因此可以在不重新编译的前提下添加、重排序、甚至删除常量,因为导出常量的属性在枚举类型和它的客户端之前提供了一个隔离层:常量值并没有被编译到客户端代码中，而是在int枚举模式中；
1. 通过`toString`可以将枚举转换为可打印的字符串，打印值信息（在堆栈中可以看到name或value）；
1. Enum可以被用于集合中（如作为HashMap的key）；
1. 可以为一个Enum类添加任意的属性和方法；
1. Enum类型可以实现任意的接口。

另外：

- Enum类型可以用于switch结构；

```java
EType eType=EType.ONE;
switch (eType){
      case ONE:
            break;
      case TWO:
            break;
      case THREE:
            break;
      default:
}
```

- Enum类型简单可读。

一个Enum类型对于每个被命名的enum常量都有public并且类型安全的成员。所有的Enum类都有高效的`toString`、`hashCode`和`equals`等方法，但没有`Cloneable`方法。实现了`Serializable`和`Comparable`接口。除了`toString`外的所有方法都是final类型的。因为比较关系序列化和比较，所以要保证他们是正确的。

自定义的属性可以添加到enum类，也可以添加到单个常量中。虽然通常在有经验的程序员中使用，但大大提高了该特性的功能。

属性是用来识别class文件中的enum类和enum常量。

Enum是所有枚举类的父类，并且提议实现了专用用途的Set和Map，位于`java.util`包中，称为`EnumSet `和`EnumMap`，其所有的实例或者key是简单的枚举类的实例。

枚举类完美兼容标准集合的实现,特殊用途的实现仅仅为了提高性能。

所有的枚举类声明默认都是final类型的，除非它们包含常量指定的类主体，枚举成员类是隐式的static。

不管使用什么修饰符，显式实例化枚举类都是非法的。

构造方法不能显式调用父类构造方法，会报编译器错误。如下：

```java
enum EType{
      ONE,TWO,THREE;
      EType(){
            //该调用方式不允许，是由编译器自动处理的。
            super();
      }

}
```
不过同一枚举类中的一个构造方法可以调用另一个，而且所有的构造方法潜在都是private修饰的，而且只能被private修饰。

```java
enum EType{
      ONE,TWO,THREE;
      EType(){
            //这样的调用是被允许的
            this(3);
      }
      EType(int i){}
}
```

#### 参数

每个枚举常量可以有参数，该参数是在常量创建的时候通过构造方法设置的，而且构造方法的默认和创建正常的类是相似的，未声明构造方法会生成默认无参构造方法，声明了构造方法就会用已经声明的方法。


#### 枚举声明外的方法

枚举声明不包含那些与自动生成的成员冲突的方法:包括(`VALUES`、`family()`、` readObject(ObjectInputStream)`、`writeObject(ObjectOutputStream)`)，相似的，枚举声明也不包含那些在`java.util.Enum`中定义为final的方法，包括(`equals(Object)`、`hashCode()`、`clone()`、`compareTo(Object)`、`readResolve()`)，同时也不包含`ordinal`和`name`

### 序列化

枚举的序列化形式是包含常量的名字,如果反序列化时，序列化名字中没有正确类型的常量,则反序列化会报`InvalidObjectException`异常。枚举常量的反序列化将不会被重新排序,添加枚举常量,或删除未使用的枚举常量。

### 语义

枚举声明声明的枚举类和普通类在可访问的修饰符方面有相同的可见性。任何枚举中的方法声明的可见性也和普通类的相同.特定于常量的类主体定义在enum中定义内部类来扩展封闭的enum类。所以该类中的实例方法如果覆盖了修饰符为可访问则可以被外界访问。但是静态方法和属性不能被指定常量所在类之外的类主体访问。

除了继承自Enum类的成员，枚举类还有一个为每个声明的枚举常量声明“自我类型”的`public static final`属性。

Enum不能被new实例化，也不能被克隆并且完全控制序列化和反序列化过程，这确保了在以上字段之外没有实例可用。因为每个值就是一个实例，所以在使用`equal`的地方可以使用`==`操作符来确定两个对象是引用了同一个枚举常量。

枚举类自动生成如下属性：

```java
public static List<this enum class> VALUES;
public final List<this enum class> family();
public static <this enum class> valueOf(String name);
```

### 注意事项

- 如果一个枚举具有普遍适用性，它就应该成为顶层类；如果只是被用于某个顶层类中，那么它就应该成为该顶层类的成员类。   

- 如果在枚举类型中覆盖`toString`方法，要考虑编写`fromString`方法,将定制的字符串表示法变回相应的枚举。

- 如果多个枚举常量同时共享相同的香味，则考虑策略枚举。

- **永远不要根据枚举的序数导出与它关联的值，而是要将其存在实例域中。**

- 特定于常量的方法实现：即在枚举中声明抽象方法，然后在特定于常量的类主体中用具体的方法覆盖每个常量的抽象方法。  

示例:

```java
enum EType{
      ONE{
            void sayHello() {System.out.println("I'm one");}
      },
      TWO{
            void sayHello() {System.out.println("I'm two");}
      },
      THREE{
            void sayHello() {System.out.println("I'm three");}
      };

      Object VALUES;

      EType(){
            this(3);
      }

      EType(int i){
            this.VALUES = i;
      }
      abstract void sayHello();
}
```





## 参考文献

>JCP 的Enum规范

<https://www.jcp.org/en/jsr/detail?id=161>  
<https://jcp.org/aboutJava/communityprocess/jsr/tiger/enum.html>

>JDK1.8Enum源码

>Effective Java（第二版） 第六章
      
