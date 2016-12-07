---
layout: post
title: 搭建JStorm集群
category: 技术
tags:  JStorm
keywords: 
description: 官方文档地址：http://jstorm.io/quickstart_cn/Deploy/
---

{:toc}

## 搭建jstorm集群环境

###  预置环境

- zookeeper集群
	
	三个节点：`h2m1`,`h2s1`,`h2s2`。

- jdk1.7及以上的64位版本

### 检查预置环境配置

- 运行`hostname -i`,检查是否配置了内网ip地址而非127.0.0.1,如果没有配置需要配置ip；
- 运行`java -version`,检查jdk版本是否在1.7及以上。

### 安装jstorm

**假设以jstorm-2.1.1.zip为例**

```bash
# 解压安装包
unzip jstorm-2.1.1.zip
# 配置全局环境变量 
vi /etc/profile
# 配置jstorm的根目录，比如:/usr/local/jstorm211
export JSTORM_HOME=/XXXXX/XXXX
export PATH=$PATH:$JSTORM_HOME/bin

# 使得环境变量配置生效
source /etc/profile
```

### 修改jstorm的配置

基本配置项：

- storm.zookeeper.servers: 表示zookeeper 的地址，
- storm.zookeeper.root: 表示JStorm在zookeeper中的根目录，当多个JStorm共享一个zookeeper时，需要设置该选项，默认即为“/jstorm”
- nimbus.host: 表示nimbus的地址， 填写ip
- storm.local.dir: 表示JStorm临时数据存放目录，需要保证JStorm程序对该目录有写权限


示例配置：

```yaml
storm.zookeeper.servers:
    - "h2m1,h2s1,h2s2"
storm.zookeeper.root: "/jstorm"
nimbus.host: "h2m1,h2s1,h2s2"
storm.local.dir: "%JSTORM_HOME%/data"
```

### 扩展节点

执行远程复制命令构建集群节点

```
scp -rp /usr/local/jstorm211/  root@h2s1:/usr/local/;
scp -rp /usr/local/jstorm211/  root@h2s2:/usr/local/;
```

### 启动jstorm集群

- 在每个nimbus 节点上执行 `nohup jstorm nimbus &`, 查看$JSTORM_HOME/logs/nimbus.log检查有无错误
- 在每个supervisor节点上执行 `nohup jstorm supervisor &`, 查看$JSTORM_HOME/logs/supervisor.log检查有无错误

## 配置jstorm的web界面监控环境

### 预置环境

- tomcat8
	tomcat7及以上版本，笔者使用的是tomcat8

### 配置环境

**在h2s2上配置web监控环境**

假设tomcat8的安装包为`apache-tomcat-8.5.8.tar.gz`，下载位置为`/root`目录

```bash
#获取tomcat安装包
wget http://mirrors.koehn.com/apache/tomcat/tomcat-8/v8.5.8/bin/apache-tomcat-8.5.8.tar.gz
#解压tomcat
tar -xzvf /root/apache-tomcat-8.5.8.tar.gz
#解压后的目录更名
mv apache-tomcat-8.5.8 tomcat858
#将目录移动到指定位置
mv tomcat858/ /usr/local/
#在当前用户(root)的home目录下创建`.jstorm`目录
mkdir ~/.jstorm
#将jstorm集群的配置复制到当前用户的`.jstorm`目录中
cp /usr/local/jstorm211/conf/storm.yaml ~/.jstorm/
# 将web监控的war包复制到tomcat的webapps下
cp /usr/local/jstrom211/jstorm-ui-2.1.1.war /usr/local/tomcat858/webapps/
cd /usr/local/tomcat858/;
unzip ./webapps/jstorm-ui-2.1.1.war -d jstorm-ui-2.1.1
#建立软连接，使得对ROOT的访问直接转到指定目录
ln -s jstorm-ui-2.1.1 ROOT
```

### 启动tomcat并验证

- 启动：
	`/usr/local/tomcat858/bin/startup.sh`

- 验证：
	`h2s2:8080`,前提是请求的机器的hosts中配置了h2s2的ip和主机名的绑定