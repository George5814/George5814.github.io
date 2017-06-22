---
layout: post
title: Eclipse配置
category: 技术
tags: 工具配置
keywords: 
description: 不定期更新
---

{:toc}


## 1. Eclipse分配到不同的工作集中

 1. Package Explorer   filter   ->    Top Level Elements   ->   Working sets
 1. Package Explorer 右键 new  ->  other  ->  java  ->   java working set 
 1. 输入 working set 的 name时 把你的项目add / add all 到新建的 working set 中
 1. Selected Working Set，选中你刚新建的那个working se即可


## 2.Eclipse自定义视图

菜单栏 window  ->  perspective  ->  custom perspective

##  3. 通过配置文件修改jdk的路径

	Eclipse的eclipse目录下的eclipse.ini配置文件，会有一个“-vm”选项，将该选项及其下面的配置路径删除掉后，启动Eclipse就会自己去系统的环境变量中寻找jdk的位置

##  4. 项目因误操作删除maven依赖包后重新转为maven项目

	右击项目 -> configure ->convert to maven project 即可恢复为maven项目

##  5. maven webapp项目找不到springmvc映射路径
	maven目录结构src/main/java 在src/main/webapp之前前，否则controller找不到(这是什么狗屁逻辑)

##  6. 解决xml配置文件无提示问题
	Eclipse菜单栏  ->  window  ->  preference  ->  xml  ->  xml catalog  ->  add   ->  catalogEntry  ->  key Type:uri  ->  key：添加上xml文件中的url路径 ;location:与key相同 ->  OK

##  7. 部署web应用：

Tomcat version 6.0 only supports J2EE 1.2, 1.3, 1.4, and Java EE 5 Web modules
	
解决办法：

 1. 项目右键  ->  preperties  ->  Project Facets  ->  右侧 Dynamic Web Module 、java、JavaServer Faces、JAX-RS的勾都去掉 ->  apply  ->  重新打开
 1. 项目右键  ->  preperties  ->  Project Facets  ->  右侧 Dynamic Web Module 改为3.0或以下 、java 改为1.6+、JavaServer Faces、JAX-RS重新打上勾  ->  apply  刷新一下即可

需要重新设置下source：

- 项目右键  ->  preperties  ->  java Build Path  ->  Source  -> Add Folder  ->  src 下的main,resource 和test下的java  ->  OK  ->  source中原来有的去掉，留下三项
`project/src/main/java`,`project/src/main/resources`,`project/src/test/java` ->  apply  -> OK	

##  8. 安装属性文件编辑插件

	在线更新：http://propedit.sourceforge.jp/eclipse/updates/
	
##  9. 项目部署文件

	{project}/.Settings/org.eclipse.wst.common.component 文件，里面包含web项目将哪些文件部署到服务器，引用哪些包 
	
	{project}/.Settings/org.eclipse.wst.common.project.facet.core.xml 文件存放的project Facets选项的配置


##  10. 设置spket
 
Window -> Preferences -> Spket -> JavaScript Profiles -> New,输入“jQuery”点击OK； 选择“jQuery” 并点击“Add Library”然后在下拉条中选取“jQuery”； 选择 “jQuery”并点击“Add File”，然后选中你下载的jQuery.js 文件；设成Default;

##  11. 设置js打开方式


**这一步很重要,不设置的话,也不会有jQuery的智能提示。**


Window -> Preferences ->General-> Editors-> File Associations-> 选择*.js,将Spket JavaScript Editor设为Default。 



##  12.  修改默认的注释字体大小而不改变代码
 

window ->preferences->General ->Appearance -> Color and Fonts ->Basic ->Text Font ->edit中将字体改为Courier New ;

如果字体改的不像样子,则恢复系统字体再改
 
##  13.  显示行号

- windows  ->  preferences  ->  general  ->  editors  ->  text editors  ->  show line number
	
- 在行最前的空白部分右击选择：show  line numbers

##  14.  设置编码 


windows  ->  preferences  ->  general  ->  editors  ->  text editors  ->  spelling  ->   Encoding  


## 15. 一个项目在Eclipse中启动两次

可能是因为配置了Eclipse中context的名称与项目名称不同导致

![Eclipse中配置的context名称为esn-palmyy](http://omsz9j1wp.bkt.clouddn.com/image/other/eclipse-tomcat-1.png) 

因项目名称为**esn-palmyy-plugin**，而context中设置为**esn-palmyy**。

故而一套代码会在tomcat中展现为两个项目部署的样子

![tomcat的管理台显示的项目名称](http://omsz9j1wp.bkt.clouddn.com/image/other/eclipse-tomcat-2.png)

因此，在启动时会根据这两个名字**esn-palmyy-plugin**和**esn-palmyy**将同一套代码部署两遍。

**解决办法**：在Eclipse中，如上图所示的位置，将**esn-palmyy**修改为**esn-palmyy-plugin**。

而该修改是修改的在Eclipse中配置的tomcat的`server.xml`的值

![tomcat的server.xml的配置](http://omsz9j1wp.bkt.clouddn.com/image/other/eclipse-tomcat-3.png) 

该值的修改也可以在tomcat目录下的`server.xml`中看到，但当清除掉当前项目**esn-palmyy-plugin**后，该配置会自动删除。


## 16.在eclipse中执行maven的update后部署中丢失了maven的lib库



这是因为M2E-WTP版本过低导致的eclipse的一个bug，更新到M2E-WTP1.2.0版本就可以解决问题了。

**1.2.0版本中的一个显著特点就是保持部署装配时的配置**

eclipse中直接安装新软件的链接:<http://download.eclipse.org/m2e-wtp/releases/mars/1.2>
