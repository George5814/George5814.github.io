---
layout: post
title: redis安装笔记-redis实例集群安装
category: 技术
tags:  Redis
keywords: 
description:  在VMware虚拟机中安装redis集群安装，CentOS6.4系统。同一台机器上不同redis实例集群
---

{:toc}


### 1. 下载/编译/安装`redis3.2.0`版本

#### 1.1 下载`redis3.2.0`并解压到指定目录

- [下载位置](http://download.redis.io/releases/redis-3.2.0.tar.gz "redis3.2.0"){:target="_blank"}

- 使用winscp复制到centos的`/root`中

- 解压到`/usr/local/`目录下：`tar -xzvf /root/redis-3.2.0.tar.gz -C /usr/local/`

#### 1.2 编译`redis3.2.0`源码

阅读`/usr/local/redis-3.2.0`下的README.md

**最一般的步骤：**

- 编译项目：`make`

- 安装执行文件到`/usr/local/redis/bin`：`make PREFIX=/usr/local/redis install`

- 按照生产环境要求，将redis作为服务安装

在`/usr/local/redis-3.2.0/utils`下执行`./install_server.sh `

![redis服务安装](/public/pic/redis/redis-server-setup.png)

上图中，红框中是自定义的端口和路径，紫色框中是`make install`步骤指定的执行文件的路径，蓝框中是要安装的位置的确认信息。

![redis服务安装](/public/pic/redis/redis-server-setup-success.png "redis服务安装成功")

出现上图所示即安装成功

### 2. 修改配置文件，启动多个redis实例

要求:

	启动六个redis实例，并将实例组成redis集群，三个主节点，三个从节点

集群配置最小选项

```
port 7000
cluster-enabled yes 
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
```

配置要求

|实例名称       |  端口号 |所在位置                     |
|-------    |--------|------------------|
|redis01    |    6379|/usr/local/redis01|
|redis02    |    6479|/usr/local/redis02|
|redis03    |    6579|/usr/local/redis03|
|redis04    |    6679|/usr/local/redis04|
|redis05    |    6779|/usr/local/redis05|
|redis06    |    6879|/usr/local/redis06|


#### 2.1 将当前redis复制六份

`cp -rp /usr/local/redis /usr/local/redis0x`

#### 2.2 修改每一份的配置文件

以redis01为例(其他相同):

`vim /usr/local/redis01/conf/redis.conf`

- 使用vim命令`:$s/7000/6379/g` # 将端口即与端口有关的文件名中的7000改为6379

- 使用vim命令`/usr/local/redis`查找所有的`usr/local/redis`并修改为`usr/local/redis01`

- 使用vim命令`:wq`,保存修改后退出。

#### 2.3 启动实例

`/usr/local/redis01/bin/redis-server /usr/local/redis01/conf/redis.conf`

#### 2.4 检查实例是否存在

`ps -ef | grep redis ` 如果存在端口为6379即代表实例启动成功

![redis启动实例](/public/pic/redis/redis-server-instance-success.png "redis实例启动成功")

在`/usr/local/redis01/data/redis/`会生成文件`appendonly.aof`和`nodes.conf`

#### 2.5 其他redis0x仿照`2.2`，`2.3`，`2.4`进行配置

所有redis实例启动示意图（我这截图是7个实例）

![redis实例示意图](/public/pic/redis/redis-server-instance-show.png "redis实例示意图")


### 3.安装预装环境和redis集群

#### 3.1. 安装ruby 

`yum install ruby`


#### 3.2. 安装rubygems 	

 `yum install rubygems`

未安装时报如下错误：

```
./redis-trib.rb:24:in `require': no such file to load -- rubygems (LoadError)
        from ./redis-trib.rb:24
```

#### 3.3. 安装redis 

`gem install redis`

未安装时报如下错误：

```
/usr/lib/ruby/site_ruby/1.8/rubygems/custom_require.rb:31:in `gem_original_require': no such file to load -- redis (LoadError)
        from /usr/lib/ruby/site_ruby/1.8/rubygems/custom_require.rb:31:in `require'
        from ./redis-trib.rb:25
```


#### 3.4. 在多个实例上创建集群

创建新的集群，每个主节点的从节点为一个

```
/usr/local/redis-3.2.0/src/redis-trib.rb create  --replicas 1  127.0.0.1:6379  127.0.0.1:6479  127.0.0.1:6579  127.0.0.1:6679  127.0.0.1:6779  127.0.0.1:6879  127.0.0.1:6979 
```

中间输入一次`yes`

![redis实例集群示意图](/public/pic/redis/redis-setup-cluster.png "redis实例集群示意图")

这表示集群中的 16384 个槽都有至少一个主节点在处理， 集群运作正常。