---
layout: post
title: 基于PHPnow搭建Eclipse开发环境
category: 技术	
tags:  PHP
keywords: 
description: 
---

{:toc} 



### 1.准备阶段：

#### a).JDK最新版本

#### b).Eclipse PDT  
官网：[http://www.eclipse.org/pdt/](http://www.eclipse.org/pdt/) 

百度网盘：[http://pan.baidu.com/s/1o66A](http://pan.baidu.com/s/1o66A)

#### c).PHPnow-1.5.6 

官网：[http://servkit.org/ ](http://servkit.org/ )

百度网盘：[http://pan.baidu.com/s/1hq4KzrA](http://pan.baidu.com/s/1hq4KzrA)

#### d).ZendDebugger 

官网：[http://www.zend.com/en/products/studio/downloads](http://www.zend.com/en/products/studio/downloads)

百度网盘：[http://pan.baidu.com/s/1i32a5Op](http://pan.baidu.com/s/1i32a5Op)

![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-1.png) 

将PHPnow-1.5.6和ZendDebugger解压后备用。

### 2.安装JDK和Eclipse PDT，此处不再赘述。

### 3.安装PHPnow-1.5.6

#### a).解压PHPnow-1.5.6到D:\ PHPnow-1.5.6,下文的配置都是以此目录为基础。修改目录请在配置文件中也相应的修改。

#### b).使用PHPnow的脚本文件自动安装：


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-2.png)



#### c).安装中会提示使用init.cmd初始化和设置mysql的root用户密码，全都同意执行。

#### d).安装成功后，截图如下:


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-3.png)



#### e).配置ZendDebugger.dll

打开ZendDebugger目录，将图中红框目录下的文件ZendDebugger.dll复制到D:\ PHPnow-1.5.6。就如同上图中红框的内容。


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-4.png)



#### f) 配置Dummy.php

将ZendDebugger目录下的Dummy.php复制到PHPnow-1.5.6的默认web根目录。


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-5.png)



#### g)修改配置文件

D:\PHPnow-1.5.6\php-5.2.14-Win32 下的php-apache2handler.ini，将文件最后的[zend],修改为 


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-6.png)



这里只是让ZendDebugger生效，默认自带的ZendOptimizer和ZendExtensionManager配置失效，后续有时间解决。

#### h) 重启apache服务器

win+R | services.msc | Apache_pn |左侧的启动


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-7.png)



浏览器访问localhost


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-8.png)


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-9.png)





### 4.配置Eclipse PDT

#### a)Window | Preferences |PHP |PHP Excuteables | 右侧|Add


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-10.png)


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-11.png)





#### b)点Next


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-12.png)



#### c)测试ZendDebugger是否能连接成功


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-13.png)


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-14.png)





如果点击Test不报错就是能连接成功。

### 5.创建PHP project


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-15.png)



直接finish。

### 6.运行或调试项目

#### a)配置如截图


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-16.png)


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-17.png)





#### b) 此处的URL设置要手动配置，而且要把项目名去掉。

#### c)Apply| debug。使用Eclipse内部浏览器访问页面。


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-18.png)



### 7.Apache配置虚拟目录

#### a)配置虚拟目录：


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-19.png)



该配置在D:\PHPnow-1.5.6\Apache-20\conf\extra\httpd-vhosts.conf中。

```
alias / "D:/dev_tools/workspace_php/" 
```

将物理目录设置为”/”，就可以通过项目名+文件在项目中路径的方式访问了，`Http://host/project_name/path/name`.

现在创建项目就可以将位置指定到设置的物理路径(如：`D:/dev_tools/workspace_php/`)中了。


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-20.png)


![](//raw.githubusercontent.com/George5814/blog-pic/master/image/phpnow/phpnow-eclipse-21.png)





接下来就可以专心的开发PHP程序了。祝你好运！！
