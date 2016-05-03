## eclipse相关配置
[TOC]
### 1.配置问题
#### 1.1 通过配置文件修改jdk的路径
	Eclipse的eclipse目录下的eclipse.ini配置文件，会有一个“-vm”选项，将该选项及其下面的配置路径删除掉后，启动Eclipse就会自己去系统的环境变量中寻找jdk的位置

#### 1.2 项目因误操作删除maven依赖包后重新转为maven项目
	右击项目 -> configure ->convert to maven project 即可恢复为maven项目

#### 1.3 maven webapp项目找不到springmvc映射路径
	maven目录结构src/main/java 在src/main/webapp之前前，否则controller找不到(这是什么狗屁逻辑)

#### 1.4 解决xml配置文件无提示问题
	Eclipse菜单栏 | window | preference | xml | xml catalog | add  | catalogEntry | key Type:uri | key：添加上xml文件中的url路径 ;location:与key相同| OK

#### 1.5 部署web应用：
	Tomcat version 6.0 only supports J2EE 1.2, 1.3, 1.4, and Java EE 5 Web modules
	解决办法：
		项目右键 | preperties | Project Facets | 右侧 Dynamic Web Module 、java、JavaServer Faces、JAX-RS的勾都去掉| apply | 重新打开
		项目右键 | preperties | Project Facets | 右侧 Dynamic Web Module 改为3.0或以下 、java 改为1.6+、JavaServer Faces、JAX-RS重新打上勾 | apply  刷新一下即可
	需要重新设置下source：
		项目右键 | preperties | java Build Path | Source |Add Folder | src 下的main,resource 和test下的java | OK | source中原来有的去掉，留下三项
		project/src/main/java
		project/src/main/resources
		project/src/test/java 
		| apply |OK	

#### 1.6 安装属性文件编辑插件
	在线更新：http://propedit.sourceforge.jp/eclipse/updates/
	
#### 1.7 项目部署文件
	{project}/.Settings/org.eclipse.wst.common.component 文件，里面包含web项目将哪些文件部署到服务器，引用哪些包 
	{project}/.Settings/org.eclipse.wst.common.project.facet.core.xml 文件存放的project Facets选项的配置


#### 1.8 设置spket 
	Window -> Preferences -> Spket -> JavaScript Profiles -> New,输入“jQuery”点击OK； 选择“jQuery” 并点击“Add Library”然后在下拉条中选取“jQuery”； 选择 “jQuery”并点击“Add File”，然后选中你下载的jQuery.js 文件；设成Default; 
#### 1.9 设置js打开方式
	这一步很重要,不设置的话,也不会有jQuery的智能提示。
	Window -> Preferences ->General-> Editors-> File Associations-> 选择*.js,将Spket JavaScript Editor设为Default。 
#### 1.10 新建一个js文件，jQuery自动提示！！

#### 1.11 修改默认的注释字体大小而不改变代码
 
	window ->preferences->General ->Appearance -> Color and Fonts ->Basic ->Text Font ->edit中将字体改为Courier New ;如果字体改的不像样子,则恢复系统字体再改
 
#### 1.12 显示行号
- windows | preferences | general | editors | text editors | show line number
	
- 在行最前的空白部分右击选择：show  line numbers

#### 1.23 设置编码 
	windows | preferences | general | editors | text editors | spelling |  Encoding  

















