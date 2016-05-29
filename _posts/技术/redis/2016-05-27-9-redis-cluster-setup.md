---
layout: post
title: redis 9：redis实例集群安装
category: 技术
tags:  Redis
keywords: 
description:  在VMware虚拟机中安装redis集群安装，CentOS6.4系统。同一台机器上不同redis实例集群以及不同机器上redis实例集群
---

{:toc}

[Redis 集群规范](http://www.redis.cn/topics/cluster-spec.html "Redis 集群规范"){:target="_blank"}

[Redis Cluster Specification](http://redis.io/topics/cluster-spec "Redis Cluster Specification"){:target="_blank"}

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


### 4.集群操作

#### 4.1 添加新的节点

|实例名称        |  端口号 |所在位置                     |
|-------    |--------|------------------|
|redis      |    6379|h2s1:/usr/local/redis|

`./redis-trib.rb add-node h2s1:6379 127.0.0.1:6379`：将节点`h2s1:6379`添加到所在集群`127.0.0.1:6379`。

**注：**

- `redis-trib.rb`默认是在`redis-3.2.0/src`下

- 新节点`h2s1:6379`不能有数据

- 新节点`h2s1:6379`实例必须已经启动

- 默认添加为主节点


![redis集群添加新的节点](/public/pic/redis/redis-server-cluster-newnode-2.png "redis集群添加新的节点")

添加节点为slave节点，所属的master节点自动选择：

`redis-trib.rb add-node --slave new-node-host:port cluster-node-ip:port `

![redis集群添加新的节点](/public/pic/redis/redis-server-cluster-newnode-3.png "redis集群添加新的slave节点")

添加节点为slave节点，指定所属的master节点：

`redis-trib.rb add-node --slave --master-id masterid   new-node-host:port cluster-node-ip:port `

![redis集群添加新的节点](/public/pic/redis/redis-server-cluster-newnode-4.png "redis集群添加新的slave节点")

#### 4.2 删除节点

`./redis-trib.rb  del-node h2s1:6379 16c85b6ec209b703b90d81019606c1c021ab0a1e`:删除节点所在集群任一host:port, nodeId:即被删除节点在集群中id。

节点id可以通过检查节点的命令`./redis-trib.rb  check host:port`查看

被删除的节点会被下线，而不仅仅是移除集群。如果要重新加入，需要重新启动该节点。而且该节点的appendonly.aof和nodes.conf要删除，启动实例时重新生成。

![redis集群删除节点](/public/pic/redis/redis-server-cluster-delnode.png "redis集群删除节点")

#### 4.3 检查节点

`./redis-trib.rb  check  h2s1:6379`:检查节点，很长的一段字符串就是节点的id,如示例图中的`1dda781d73f38b1abed11beba610b155387e2a8d`

![redis集群检查节点](/public/pic/redis/redis-server-cluster-check-node.png "redis集群检查节点")

#### 4.4  查看节点信息

`./redis-trib.rb  info  h2s1:6379`:查看指定节点信息，包括有多少key，多少槽，有多少的从节点以及每个槽上平均有多少key

![redis集群查看节点信息](/public/pic/redis/redis-server-cluster-info-node.png "redis集群检查节点")

#### 4.5 修复集群

`./redis-trib.rb  fix   127.0.0.1:6379`:修复`127.0.0.1:6379`所在集群

![redis集群修复](/public/pic/redis/redis-server-cluster-fix-node.png "redis集群修复")

#### 4.6 集群简单操作

可以看出，我从任何一个节点都可以访问到集群其他节点的数据

![redis集群简单操作](/public/pic/redis/redis-server-oper-1.png "redis集群简单操作")


#### 4.7 集群重新分片

` ./redis-trib.rb  reshard    127.0.0.1:6379`:host和port指定节点所在集群

![redis集群reshard](/public/pic/redis/redis-server-cluster-reshard-node-1.png "redis集群reshard")

`Source node #1:`我选择的all

![redis集群reshard](/public/pic/redis/redis-server-cluster-reshard-node-2.png "redis集群reshard")

询问是否继续reshard计划：我选的的`yes`

执行移动操作：

![redis集群reshard](/public/pic/redis/redis-server-cluster-reshard-node-3.png "redis集群reshard")

已经成功移动完成

#### 4.8 集群重新平衡

`./redis-trib.rb  rebalance     h2s1:6379`:因集群中三个主节点的槽数量差别较大，因此重新分配槽的数量以达到集群的平衡。

![redis集群重新平衡](/public/pic/redis/redis-server-cluster-rebalance-node.png "redis集群重新平衡")


#### 4.9 集群执行命令

`./redis-trib.rb  call 127.0.0.1:6379  set name "jingzz"`:在集群上执行set和get命令

即在6379端口的节点上的命令被存储在了6579节点上。

![redis集群执行命令](/public/pic/redis/redis-server-cluster-call-cmd.png "redis集群执行命令")


**注意点：**

1. redis默认配置如下：

```
bind 127.0.0.1 
protected-mode yes
```

如果想要连接外部host，需要注释掉bind或指定ip，将protected-mode（保护模式设置为no），重启集群。

![redis集群调整配置](/public/pic/redis/redis-server-cluster-note-pointer.png "redis集群调整配置")