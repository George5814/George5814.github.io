---
layout: post
title: Linux启动简要过程
category: 技术
tags: Linux
keywords: 
description:  
---

{:toc}


### 1.打开电源，机器自检； 

### 2.读取BIOS信息，并依据设置取得第一个可启动设；

### 3.读取并执行该启动设备的MBR中的bootLoader（如果有多个系统，还会根据选择的系统和MBR中的信息跳转到相应的分区）；

### 4.依据bootLoader解压并加载kernel信息，kernel加载后开始检测硬件并加载驱动程序；

### 5.在硬件驱动成功后，kernel会主动调用init进程，而init进程会取得run-level信息；

### 6.init执行/etc/rc.d/rc.sysinit文件来准备软件执行的系统环境(比如网络、时区等)

### 7.init执行run-level各服务的启动(脚本方式)

### 8.init执行/etc/rc.d/rc.local文件(自己的程序如果想在开机时启动可以在这里添加)

### 9.init通过终端模拟程序mingetty启动login进程，进入登录界面等待用户登录
