---
layout: post
title: Linux 基本命令操作
category: 技术
tags: Linux
keywords: 
description:  不定时更新
---

{:toc}

### 修改主机名

```bash
## 长期修改，重启后有效
vim /etc/sysconfig/network 

#临时修改
hostname xxx 
```

### 使得修改的环境变量生效

```bash
## 使得系统环境变量生效
source /etc/profile

## 使得root用户下的环境变量生效
source /root/.bash_profile

```

### unzip 解压到指定目录

```bash
unzip -d /path sourceFile
```

### 解压tar文件

```bash
tar -xvf package-name.tar
```

### 查看指定模糊名称的进程

```bash
ps -f | grep name
```

### 强制杀死进程

```bash
kill -9 pid
```

###	正确的关机方法


```bash
who         #查看目前有谁在线
netstat -a  #查看网络的联机状态
ps -aux     #查看后台执行的程序
```


惯用的关机命令：

```bash
shutdown -h now     #立刻关机
shutdown -h 20:25   #晚上8点25分关机
shutdown -h +10     #过十分钟后关机
shutdown -r now     #立刻重启
shutdown -r +30 ‘The system will be reboot’    #再过30分钟关机，并显示后面的消息给所有在线用户
shutdown -k now ‘The system will be reboot’    #仅发出警告，系统并不会真正关机
```

### 切换视图界面

- 手工切换：`init 3`，进入纯文本模式；`init 5`，进入图形界面模式

- 快捷键：`ctrl+alt+(F1~F6)`，运行文本模式的控制台，但图形界面还是在运行中（占用内存）

