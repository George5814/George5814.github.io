---
layout: post
title: java各版本新特性
category: 技术
tags: Java
keywords: 
description: 描述整理从java8-java13各版本新特性
---


{:toc}

## java13

1. Dynamic CDS Archives  动态 CDS 归档
   
2. ZGC: Uncommit Unused Memory  ZGC 可以将未使用的堆内存返回给操作系统
   
3. Reimplement the Legacy Socket API 重新实现了 Socket 接口的逻辑。原来的老逻辑还存在。通过指定-Djdk.net.usePlainSocketImp 参数
   
4. **Switch Expressions (Preview) 增强了 Java12 引入的 Switch 表达式，可以使用yield关键字返回结果**
   
5. **Text Blocks (Preview)  文本框，避免对多行文本字符的转义。类似于 grovvy 中的三引号括起来的内容。** 
   

更多特性请看<http://openjdk.java.net/projects/jdk/13/>

## java12

1. Shenandoah: A Low-Pause-Time Garbage Collector (Experimental) 名为Shenandoah的低暂停的垃圾回收器
   
2. Microbenchmark Suite 微基准套件
   
3. **Switch Expressions (Preview)  switch 表达式，可以 将 switch 结果赋值给变量**
   
4. **JVM Constants API  JVM 常量 API。JEP 334引入了一个API，用于建模关键类文件和运行时artifacts，例如常量池。 此API将包括ClassDesc，MethodTypeDesc，MethodHandleDesc和DynamicConstantDesc等类。此 API 对于操作类和方法的工具很有帮助。**
   
5. One AArch64 Port, Not Two  移除多余ARM64实现，将两个保留为 1 个
   
6. Default CDS Archives  默认CDS归档。1.改进开箱即用的启动时间;2.摆脱使用`-Xshare：dump`
   
7. Abortable Mixed Collections for G1  G1的可中断 mixed GC。
   
8. Promptly Return Unused Committed Memory from G1  G1归还不使用的内存给操作系统

更多特性请看<http://openjdk.java.net/projects/jdk/12/>

## java11

1. Nest-Based Access Control  Class类新增了getNestHost，getNestMembers方法
   
2. Dynamic Class-File Constants  jvm规范里头对Constant pool新增一类CONSTANT_Dynamic
   
3. **Improve Aarch64 Intrinsics  对于AArch64处理器改进现有的string、array相关函数，并新实现java.lang.Math的sin、cos、log方法**
   
4. Epsilon: A No-Op Garbage Collector  名为Epsilon的垃圾收集器.该收集器不做任何垃圾回收，可用于性能测试、短生命周期的任务等，使用-XX:+UseEpsilonGC开启. 
   
5. **Remove the Java EE and CORBA Modules 将java9标记废弃的Java EE及CORBA模块移除掉，具体如下：（1）xml相关的，java.xml.ws, java.xml.bind，java.xml.ws，java.xml.ws.annotation，jdk.xml.bind，jdk.xml.ws被移除，只剩下java.xml，java.xml.crypto,jdk.xml.dom这几个模块；（2）java.corba，java.se.ee，java.activation，java.transaction被移除，但是java11新增一个java.transaction.xa模块**
   
6. **HTTP Client (Standard)  在java9及10被标记incubator的模块jdk.incubator.httpclient，在java11被标记为正式，改为java.net.http模块。**
   
7. Local-Variable Syntax for Lambda Parameters  允许lambda表达式使用var变量
   
8. Key Agreement with Curve25519 and Curve448  使用RFC 7748中描述的Curve25519和Curve448实现key agreement
   
9. Unicode 10  升级现有的API，支持Unicode10.0.0
   
10. Flight Recorder  Flight Recorder以前是商业版的特性，在java11当中开源出来，它可以导出事件到文件中，之后可以用Java Mission Control来分析。可以在应用启动时配置java -XX:StartFlightRecording，或者在应用启动之后，使用jcmd来录制
   
11. ChaCha20 and Poly1305 Cryptographic Algorithms  实现 RFC 7539的ChaCha20 and ChaCha20-Poly1305加密算法
   
12. **Launch Single-File Source-Code Programs  有了这个特性，可以直接java HelloWorld.java来执行java文件了，无需先javac编译为class文件然后再java执行class文件，两步合成一步**
   
13. **Low-Overhead Heap Profiling 通过JVMTI的SampledObjectAlloc回调提供了一个开销低的heap分析方式**
   
14. Transport Layer Security (TLS) 1.3  支持RFC 8446中的TLS 1.3版本
   
15. ZGC: A Scalable Low-Latency Garbage Collector 引入名为 ZGC 的低延时的垃圾回收器
   (Experimental)
   
1. Deprecate the Nashorn JavaScript Engine 标记废弃Nashorn引擎
   
1. Deprecate the Pack200 Tools and API 标记废弃  pack200以及unpack200工具

更多特性请看<http://openjdk.java.net/projects/jdk/11/>

## java10

1. **Local-Variable Type Inference 局部变量类型腿短**
   
1. Consolidate the JDK Forest into a Single Repository 将Forest整合到单一仓库中
   
1. **Garbage-Collector Interface 引入干净的垃圾回收器接口**
   
1. **Parallel Full GC for G1  G1 的并行 FullGC**
   
1. Application Class-Data Sharing  为了改善启动和占用空间，扩展现有的类数据共享（“ CDS”）功能，以允许将应用程序类放置在共享档案中。单机器多 JVM 时有用。对于App Class Loader和自定义的类加载器也有作用
   
1. Thread-Local Handshakes  介绍一种无需执行全局VM安全点即可在线程上执行回调的方法。 使停止单个线程而不是仅停止所有线程或不停止一个线程既可行又便宜
   
1. Remove the Native-Header Generation Tool (javah) 将 javah 从 jdk 中移除
   
1. Additional Unicode Language-Tag Extensions  其他Unicode语言标签扩展
   
1. Heap Allocation on Alternative Memory Devices  备用存储设备上的堆分配.使HotSpot VM可以在用户指定的备用存储设备（例如NV-DIMM）上分配Java对象堆。
   
1. Experimental Java-Based JIT Compiler  基于Java的实验性JIT编译器.使基于Java的JIT编译器Graal可用作Linux / x64平台上的实验性JIT编译器
   
1. Root Certificates  在JDK中提供一组默认的根证书颁发机构（CA）证书.开源Oracle Java SE Root CA程序中的根证书，以使OpenJDK构建对开发人员更具吸引力，并减少这些构建与Oracle JDK构建之间的差异.
   
1. Time-Based Release Versioning 针对当前和将来的基于时间的发行模型，修改Java SE Platform和JDK的版本字符串方案以及相关的版本控制信息。

更多特性请看<http://openjdk.java.net/projects/jdk/10/>

## java9


1. **Modular System（Jigsaw Project） 模块化**
   
2. **Http2 Client  支持Http2和新的客户端**
   
3. Process API Enhance 进程接口增强
   
4. **Try-With-Resources  来自java7，在java9中有优化。减少finally语句编写**
   
5. **Diamond Operator Extension java7提供的钻石语法扩展到匿名内部类**
   
6. Interface Private Method 接口提供私有方法，为java8中的默认方法或静态方法服务
   
7. **JShell  增强java语言的动态特性，可以像脚本语言一样运行**
   
8. JCMD sub-command 诊断命令，并移除了Jhat
   
9. Optional To Stream 对java8中提供的Optional支持Stream操作
   
10. Immutable Set 增加不可变集合实现。ImmutableCollections
   
11. Unified JVM Logging 统一的JVM日志系统
   
12. Publish-Subscribe Framework Reactive流的消息订阅发布框架，，由Flow提供.增强CompletableFuture的API和其他一些提升
   
13. Variable Handles  禁止应用中使用Unsafe，但提供了invoke包提供类似原子核Unsafe操作
   
14. Мulti-Resolution Image API 多分辨率图像的API
   
15. 轻量级的JSON API 
   
16. G1作为默认的垃圾回收器并移除java8中标记为过期的GC回收器,将CMS标记为过期
   
17. H5风格的javadoc，并可以搜索
   
18. 解析JS的引擎nashorn，实现了ES6指定的功能。<http://openjdk.java.net/jeps/292>
   
19. SHA-3算法，废弃SHA-1算法
   
20. New Version-String Scheme 新的版本字符串方案
   
21. UTF-8 Property Resource Bundles UTF-8的属性资源包，更改ResourceBundle类的默认文件编码，以将属性文件从ISO-8859-1加载到UTF-8。
   
22. java9中支持的Unicode标准到V8
   
23. New HotSpot Build System 替换为的HotSpot构建系统
   
24. 将Applet API标记为过期，开发者只能从Java Web或者安装应用上二选一。在有意向移除的时间点上回将其注解Deprecated标注为forRemoval=true

更多特性请看<http://openjdk.java.net/projects/jdk9/>

## java8
1. Java语言的新特性

    1. Lambda表达式

    1. Functional接口

    1. 接口的默认方法与静态方法

    1. 方法引用 
Class::new,Class:static_method,Class::Method,instance::Method

    1. 重复注解

    1. 更好的类型推测机制

    1. 扩展注解的支持

1. Java编译器的新特性

    1. 参数名字

1. Java 类库的新特性
    1. Optional功能

    1. Stream API

    1. Date/Time API (JSR 310)

    1. JavaScript引擎Nashorn

    1. Base64 

    1. 并行（parallel）数组

    1. 并发（Concurrency） ConcurrentHashMap

1. 新的Java工具

    1. Nashorn引擎: jjs

    1. 类依赖分析器jdeps

1. Java虚拟机（JVM）的新特性
    1. PermGen被替换为metaspace

1. Optimize java.text.DecimalFormat.format 
   1. 优化DecimalFormat
