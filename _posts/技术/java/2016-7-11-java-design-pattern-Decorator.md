---
layout:  post
title: (转)浅析 Decorator 模式，兼谈 CDI Decorator 注解
category: 技术
tags: Java
keywords: 
description: 本文将传统的 Decorator 设计模式比作毛胚房的装修，形象直观的介绍了 Decorator 设计模式的用法和注意事项，并进一步介绍了 CDI 容器对 Decorator 设计模式的支持，帮助广大 Java 开发者熟悉 CDI 的组件式开发。
---

{:toc}


### 从毛胚房的装修说起

每个人拿到属于自己的毛胚房时都兴奋不已，左看看右量量，筹划着装修的桩桩件件，憧憬着未来的幸福家园。每个人都希望装修完全按照自己的设想走，无论风格还是造价预算。可是装修这个活并不好干，同样的毛胚房有很多种装修方案，装修过程中也不可避免发生计划不如变化快的情形，往往最终的效果和最初的设想并不一致，这就是生活的实际。
软件开发的某个阶段和装修房子像极了！系统的基本功能实现后（原型跑起来），相当于毛胚房建造完成，界面不美观，功能也不够完善，毛胚嘛！在接下来的软件“装修”过程中，Decorator 模式将发挥重大的作用。众所周知，我们实施房屋装修工程有两个基本条件和约束：第一，必须有个毛胚房，否则还装修什么啊！第二，不能拆除承重结构。软件的装修也是如此，基本功能和流程框架不会做大的改动，否则还不如推倒重来了。Decorator 设计模式正如房屋的装修，是在毛胚房的基础上层层 wrapper（包装），先刷刷墙面漆，再铺铺木地板，再购置家具布置一下等等。这一道道的装修工序，可以看作是对毛胚房层层包装，最后的装修效果，甚至让你忘记了毛胚房的样子！因此，Decorator 设计模式也被称为 Wrapper 设计模式。

---

### Decorator 设计模式的特点

Decorator 设计模式正如毛胚房的装修，不会改变原毛胚房的基本框架，只是增加新的外观、功能等，且随着时间的推移，可以不断的实施装修工程：增加新的家具、根据心情换换新鲜的墙纸等等。在面向对象的程序设计中，扩展系统的原有功能也可以采用继承、组合的方式。继承也不会改变毛胚房（父类），但是由于装修工程的复杂和很多不可预测的改变，比如不同墙纸和地板样式的组合数量简直无法想想，难道我们要为每一种组合都定义一个子类吗？显然这是不现实的，即通过继承的方式来应对未来的功能和外观改变通常是吃力不讨好的事情。组合的方式也不可取，因为这要求不断的修改父类的结构，相当于对毛胚房大动干戈，房屋的可维护性和可靠性就大大降低了。

让我们回顾一下设计模式的重要原则：Classes should be open for extenstion, but closed for modification。Decorator 设计模式很好的诠释了这个原则。

---

### Decorator 设计模式的 Java 实例

以房屋装修为例，依据 Decorator 设计模式的原则设计类图如下：

**图 1.Decorator 模式类图**

![Decorator模式类图](/public/pic/java/decorator-001.png)

Room.java 是房屋的接口类，定义了房子的基本功能：

**清单 1.Room.java**

```java
package cn.edu.sdut.r314;
public interface Room {
 public String showRoom();
}
```

BlankRoom.java 就是毛胚房了。毛胚房也是房子啊，因此实现了 Room 接口的基本功能：

**清单 2.BlankRoom.java**

```java
package cn.edu.sdut.r314;

// 毛胚房，这是我们装修工程的基础
public class BlankRoom implements Room {

	@Override
 public String showRoom() {
 return "毛胚房";
 }
}
```

RoomDecorator 是所有 Decorator 类的父类，由于 RoomDecorator 通常不实现具体功能，因此设计为抽象类。

**清单 3.RoomDecorator.java**

```java
package cn.edu.sdut.r314;

// 装修工程的模板
abstract public class RoomDecorator implements Room {
 /*
  * wrapper 的具体体现，每个独立的装修工序都是在上一个装修工序的基础上进行的，装修就是这样层层包装完成的
  */ 
 protected Room roomToBeDecorated;

 public RoomDecorator(Room roomToBeDecorated) {
 this.roomToBeDecorated = roomToBeDecorated;
 }

 @Override
 public String showRoom() {
 // 委托（delegate）
 return roomToBeDecorated.showRoom();
 }
}
```

PaintedDecorator 类是 RoomDecorator 的子类，实现了房屋粉刷功能：

**清单 4.PaintedDecorator.java**

```java
package cn.edu.sdut.r314;

public class PaintedDecorator extends RoomDecorator {

 public PaintedDecorator(Room roomToBeDecorated) {
 super(roomToBeDecorated);
 }

 public String showRoom(){
 doPainting();
 return super.showRoom() + "刷墙漆"; 
 }

 // 刷墙漆
 private void doPainting(){}

}
```


FlooredDecorator 类是 RoomDecorator 的子类，实现了铺地板的功能：

**清单 5.FlooredDecorator.java**

```java
package cn.edu.sdut.r314;

public class FlooredDecorator extends RoomDecorator {
 public FlooredDecorator(Room roomToBeDecorated) {
 super(roomToBeDecorated);
 }


 public String showRoom(){
 doFlooring();
 return super.showRoom() + "铺地板";
 }

 // 铺地板
 private void doFlooring(){}
}
```

最后，写一个测试类，看看 Decorator 的神奇效果：

**清单 6.TestClient.java**

```java
package cn.edu.sdut.r314;

public class TestClient {
 public static void main(String[] args) {
 // 毛胚房
 Room blankRoom = new BlankRoom(); 

 // 刷了墙的毛胚房
 Room paintedRoom = new PaintedDecorator(new BlankRoom()); 

 // 先刷墙再铺地板的毛胚房
 // 注意到连续的 new 操作，这就是 wrapper，最内层的一般是毛胚房
 Room paintedAndFlooredRoom = new FlooredDecorator(new 
PaintedDecorator(new BlankRoom()));

 // 先铺地板再刷墙的毛胚房
 Room flooredAndPaintedRoom = new PaintedDecorator(new 
FlooredDecorator(new BlankRoom()));

 System.out.println(blankRoom.showRoom());
 System.out.println(paintedRoom.showRoom());
 System.out.println(paintedAndFlooredRoom.showRoom());
 System.out.println(flooredAndPaintedRoom.showRoom());
 }

}
```

执行结果:

**图 2.TestClient 执行结果**

![执行结果](/public/pic/java/decorator-002.png)

可以看出，我们可以随意组合 RoomDecorator 子类形成不同的装修风格和装修工序，灵活，简便，这就是 Decorator 设计模式的魅力所在！在上面的 TestClient 代码中，尤其要注意是如何通过层层封装（wrapper）的方式创建 paintedAndFlooredRoom 等对象的。

---


### CDI 对 Decorator 设计模式的支持


通过上面的例子我们可以看出，Decorator 设计模式虽然降低了需求变更对软件开发的影响，但是通过层层包装，即层层 new 操作创建对象的方式不够优雅。CDI 容器可以管理组件的生命周期，在大部分情况下我们无须通过 new 操作创建所需要的对象。CDI 中的 Decorator/Delegate 注解很大程度上简化了 Decorator 设计模式的代码编写量，比如实现上面相同的功能，借助于 CDI，就无须 RoomDecorator 这个抽象类了，所有的 Decorator 类直接实现 Room 接口并使用注解声明为 Decorator 即可，比如 PaintedDecorator 类：


**清单 7.PaintedDecorator.java**

```java
package cn.edu.sdut.r314;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

@Decorator
public class PaintedDecorator implements Room {

 @Inject
 @Delegate
 Room roomToBeDecorated;

 public String showRoom(){
 doPainting();
 return roomToBeDecorated.showRoom() + "刷墙漆"; 
 }

 // 刷墙漆
 private void doPainting(){}
}
```

RoomController 中是这样使用 Decorator 类的：

**清单 8.RoomController.java**

```java
package cn.edu.sdut.r314;

import javax.inject.Inject;
import javax.inject.Named;

@Named("room")
public class RoomController {

 @Inject
 Room room;

 public String showRoom(){
 return room.showRoom();
 }
}
```

一切看起来简单多了，秘密就在于 CDI 的 beans.xml 文件，CDI 会根据 beans.xml 文件中对 Decorator 的声明顺序加载并构造相应的 Decorator 对象，完全复现了传统方式的 Decorator 模式中通过 new 操作层层包装“毛胚房”的过程。

**清单 9.beans.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>

<beans xmlns="http://java.sun.com/xml/ns/javaee" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="
 http://java.sun.com/xml/ns/javaee
 http://java.sun.com/xml/ns/javaee/beans_1_0.xsd">

 <!-- To activate CDI decorator, it must be specified below -->
 <decorators>
 <class>cn.edu.sdut.r314.PaintedDecorator</class>
 <class>cn.edu.sdut.r314.FlooredDecorator</class>
 </decorators>
</beans>
```

---

### Java IO 中的 Decorator 设计模式

在 Java IO API 中，其实大量的使用了 Decorator 模式。试想一下，不同的输入输出源，对输入输出数据的不同处理方式和不同流程，Decorator 模式正是大显身手的时候。基础的输入输出类就好比是毛胚房，比如下面的用法：

BufferedInputStream bis = new BufferedInputStream(new FileInputStream("filename"));

从图 3 中可以更清楚的了解 Java IO 是如何使用 Decorator 模式的。

**图 3.Java IO 中的 Decorator 模式**

![执行结果](/public/pic/java/decorator-003.png)


####　基于传统方案实现一个 Java IO 的 Decorator


根据图 2 Java Io 的 Decorator 模式，我们可以实现一个 Java IO 的 Decorator，比如读入一个文件，将每个字母的 ASCII 码都后移一位，代码如下：

**清单 10.EncodeInputStream.java**

```java
package cn.edu.sdut.r314; 
 
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EncodeInputStream extends FilterInputStream {
 protected EncodeInputStream(InputStream in) {
 super(in);
 } 

 public int read() throws IOException {
 int c = super.read();
 return c + 1;
 } 
}
```

再写一个简单的测试文件：

**清单 11.InputTest.java**

```java
package cn.edu.sdut.r314; 

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputTest {
 public static void main(String[] args) {
 int c;
 try {
 InputStream in = new EncodeInputStream(new 
BufferedInputStream(InputTest.class.getResourceAsStream("test.txt")));
 while ((c = in.read()) >= 0) {
 System.out.print((char) c); 
 }
 in.close();
 } catch (IOException e) {
 e.printStackTrace();
 }
 } 
}

```

执行后的输出结果是（test.txt 文件的内容是 hello,world!）：ifmmp!xpsme"

嗯，简单的加密技术:-)

#### 基于 CDI 实现一个 Java IO 的 Decorator

Java IO 的 Decorator 在 CDI 环境下和传统环境下写法上没有多大区别，只是一般在 CDI 环境下自己写的 Decorator 可以当作组件使用，即可以通过 Decorator 的类型而不是名称查询组件而已，不再赘述。

---

### 结束语

Decorator 设计模式简单而精巧，它其实是 Unix 哲学的体现：每一个应用程序都尽力做好自己，然后通过应用程序之间的协作完成更复杂的任务，正如 shell 的管道符的作用。从复杂应用程序框架设计的角度看，Decorator 设计模式也降低了模块之间的耦合度，而 CDI 更进了一步，借助于容器和类型安全的组件模型，简化了 Decorator 模式的应用，同时消除了某些潜在的运行时异常，也就是说，CDI 之上的 Decorator 设计模式能够帮助构建更加安全的复杂应用。

