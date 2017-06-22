---
layout: post
title: ZooKeeper集群操作脚本
category: 技术
tags: ZooKeeper
keywords: 
description: 比较初级的集群脚本，暂时没有高级功能
---
 
 
{:toc}

### 功能
-  可以一键启动/停用/状态查询/重启 Zookeeper集群的所有机器节点；

-  可以在文件中动态和删除要操作的集群的节点ip，每行一个节点；


### 预备知识

- shell的基础语法结构，包括if，for，ssh命令等操作。
- 交互式shell和非交互式shell区别

### 编写脚本

#### 脚本样例

```bash
#!/bin/bash
#####################################################
#       一键操作zookeeper集群
#####################################################

# 对zookeeper的操作，start,stop,restart,status
OPER=$1

HOSTS_FILE="./zk-nodes"
echo ${OPER}
if [ "${OPER}"  != "start" ] && [ "${OPER}" != "stop"  ] && [ "${OPER}" != "status"  ] && [ "${OPER}" != "restart"  ];
then
        echo "参数只允许【start】【stop】【restart】【status】其中一个"
        exit -1
fi

echo "zookeeper cluster ${OPER} "
for  LINE in `cat ${HOSTS_FILE}`
do
        echo "===============zookeeper node ${OPER} at ${LINE}==================="
		#远程执行命令
        ssh root@${LINE} "cd /usr/local/zk/;nohup ./bin/zkServer.sh ${OPER}"
            
done

echo "===============zookeeper ${OPER} end at all nodes==============="
```

以上脚本需要读取当前目录下的"zk-nodes"文件，该文件每行一个节点主机名或ip地址


![/etc/profile环境变量](http://omsz9j1wp.bkt.clouddn.com/image/zk/zookeeper-script-4.png)

### 出现的问题

![/etc/profile环境变量](http://omsz9j1wp.bkt.clouddn.com/image/zk/zookeeper-script-2.png)



![/etc/profile环境变量](http://omsz9j1wp.bkt.clouddn.com/image/zk/zookeeper-script-3.png)

明明服务已经启动了，但是远程执行查看zk状态的命令就是找不到服务，而且在相应节点上执行`jps`也没有名为"QuorumPeerMain"的进程。

### 问题原因

交互式shell和非交互式shell是有区别的，登录的shell属于交互式shell，而通过ssh执行的命令是通过非交互式shell完成的。

在登录shell里，环境信息需要读取/etc/profile、~ /.bash_profile, ~/.bash_login, and ~/.profile。按顺序读取最先的一个，并执行其中的命令。除非被 --noprofile选项禁止了；

在非登录shell里，环境信息只读取 /etc/bash.bashrc和~/.bashrc

而我在linux的环境中只配置了/etc/profile,

![/etc/profile环境变量](http://omsz9j1wp.bkt.clouddn.com/image/zk/zookeeper-script-1.png)

如果通过脚本批量执行，需要将其复制到`~/.bashrc`文件中。执行`source ~/.bashrc`使得修改生效。这样再次执行脚本就不会包这样的错误了。

