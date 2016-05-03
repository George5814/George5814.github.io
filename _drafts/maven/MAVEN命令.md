## maven 命令

[TOC]

### 1. mvn clean compile	 [清理并编译]

### 2. mvn clean test	 [清理并测试，会先编译]

### 3. mvn clean package	 [清理并打包，会先测试]

### 4. mvn clean install	[清理并安装，会先打包]


### 5.mvn archetype:generate  [自动生成项目骨架]

### 6.mvn dependency:list [当前项目已经解析的依赖信息]

### 7.mvn dependency:tree [依赖树] 

### 8.mvn dependency:analyze [分析当前项目的依赖]

### 9.mvn clean  deploy [maven 将项目构建输出的构建部署到对应的远程仓库]

需要配置POM.xml文件

```xml
<project>
	<distributionManagement>
		<!-- release repository -->
		<repository>
			<!-- 远程仓库的唯一标识 -->
			<id></id>
			<!-- 可读信息 -->
			<name></name>
			<!-- 仓库地址 -->
			<url></url>
		</repository>
		<!-- snapshot repository -->
		<snapshotRepository>
			<id></id>
			<name></name>
			<url></url>
		</snapshotRepository>
	</distributionManagement>
</project>
```
	 

