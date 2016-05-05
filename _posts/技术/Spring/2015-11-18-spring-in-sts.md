---
layout: post
title: Spring源码导入Eclipse中
category: 技术
tags:  Spring
keywords: 
description: 
---

{:toc}

### 1.下载STS最新版，并解压到你自己的指定目录

### 2.下载安装git

### 3.将git上的代码克隆到本地  


[https://github.com/spring-projects/spring-framework.git](https://github.com/spring-projects/spring-framework.git)


### 4.Win+R / cmd / 打开命令行窗口

### 5.切换到spring-framework所在目录

### 6.执行目录下的import-into-eclipse，windows下执行以.bat结尾的脚本

![以.bat结尾的脚本](/public/img/posts/spring-in-sts-1.png)


### 7.输入回车会执行如下图

![如下图](/public/img/posts/spring-in-sts-2.png)


这一步推荐IDE工具使用STS(SpringSource Tool Suite ),因为很多需要的插件该工具都集成了，其次使用Eclipse，但需要AJDT插件(AspectJ Development Tools)[aspectj 开发工具]。

可以边下载边执行该脚本，不冲突。因为该脚本的执行现在还只是在spring-framework源码中进行的，不涉及IDE工具。回车继续执行…


### 8.开始执行第一步：

![第一步](/public/img/posts/spring-in-sts-3.png)


这一步是生成子项目的元数据[metadata],spring-framework目录下的每一个以spring-开头的目录都是spring的一个子模块，也是一个子项目，他们的集合才是spring。因此现在要执行`gradlew cleanEclipse :spring-oxm:compileTestJava eclipse –x :eclipse `命令生成元数据。
 
gradlew是一个自动化构建工具[详细信息自己搜索]，通过执行spring-framework目录下的gradlew脚本，来自动化生成每个子项目的元数据，该工具是在spring-framework中自带的，因此不用另行下载gradlew了。

### 9.可以看一下import-into-eclipse的bat脚本文件，内有注释内容帮助理解该命令的含义

![import-into-eclipse的bat脚本文件](/public/img/posts/spring-in-sts-4.png)


该命令每次执行都是清空已经存在的元数据，并生成OXM测试类避免在将源码导入Eclipse时出现错误，该命令会为所有的子项目生成元数据但会跳过根项目。参数`-x :eclipse`就是用来绕过生成根项目元数据的。

也就是说会清理当前项目并重新编译所有子项目。

### 10.这是windows下使用gradlew的部分脚本【可以忽略，感兴趣的了解】

![gradlew的部分脚本](/public/img/posts/spring-in-sts-5.png)


![gradlew的部分脚本](/public/img/posts/spring-in-sts-6.png)



 

### 11.这是通过命令行自动去下载并解压需要的gradlew的配置文件[spring-framework/gradle/wrapper/gradle-wrapper.properties]

![下载并解压需要的gradlew的配置文件](/public/img/posts/spring-in-sts-7.png)


gradle安装的路径，如果指定了GRADLE_USER_HOME则是在你指定目录下的wrapper/dists下。如果没有指定，默认是在`C:\Users\yourname\.gradle\wrapper\dists\gradle-2.5-bin\7mk8vyobxfh3eazpg3pi2y9mv\`

![gradle安装的路径](/public/img/posts/spring-in-sts-8.png)



解压后的和压缩包都在这里，目录7m***9mv是随机的。

### 12.直接回车执行命令

`gradlew cleanEclipse :spring-oxm:compileTestJava eclipse –x :eclipse`，该步骤是花费时间最长的一步，它会去指定地址去下载需要的工具和jar包，而且中途有可能会因为下载时间过长提示下载失败，不用担心，再次执行import-into-eclipse即可，直到执行成功为止。



执行途中的截图

![gradlew cleanEclipse :spring-oxm:compileTestJava eclipse –x :eclipse](/public/img/posts/spring-in-sts-9.png)



### 13.第一步执行成功，会执行第二步，该步骤是将子项目导入到STS或者Eclipse中。

![截图](/public/img/posts/spring-in-sts-10.png)


### 14.按照Eclipse中平时导入的步骤操作即可

菜单File / Import / General  /  Existing Projects into Workspace

![导入](/public/img/posts/spring-in-sts-11.png)


全选后选择finish，等待spring子项目导入即可。STS自动编译完后会出现一个错误

![等待spring子项目导入](/public/img/posts/spring-in-sts-12.png)


![等待spring子项目导入](/public/img/posts/spring-in-sts-13.png)


这是因为该部分包含Groovy编译代码，需要安装Groovy的编译器。

在STS中，操作如图：


![安装Groovy的编译器](/public/img/posts/spring-in-sts-17.png)


![安装Groovy的编译器](/public/img/posts/spring-in-sts-18.png)

选上3位置的插件后，右下角Install安装插件。
 

### 15.回到命令行，回车

![回车](/public/img/posts/spring-in-sts-14.png)


这一步是生成根项目的元数据，因为Eclipse不允许分层导入项目，只能跳过第一步在该步骤生成根项目元数据，然后在下一步导入了。

### 16.导入根项目，步骤跟导入子项目相同

![导入根项目](/public/img/posts/spring-in-sts-15.png)


### 17.步骤5可做可不做

![步骤5可做可不做](/public/img/posts/spring-in-sts-16.png)


### 18.如果是要提交代码的大牛，也不用参考我写的这步骤了。如果只是看看源码，这些现在也就够了。