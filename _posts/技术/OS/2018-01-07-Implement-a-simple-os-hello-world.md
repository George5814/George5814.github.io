---
layout: post
title: 在虚拟机里显示Hello World
category: 技术	
tags:  OS
keywords: 
description: 一个操作系统的实现（于渊），写简单的只打印红色Hello，world的操作系统。
---
 
{:toc} 

刚开始学于渊的《一个操作系统的实现》，动手写一个可以显示Hello，World的20行代码的操作系统，其实只是个引导盘。

当计算机电源被打开后，它会先进行加电自检（POST），然后寻找启动盘，如果是选择从软盘启动，计算机就会查找软盘的0面0磁道1扇区，如果发现它以0xAA55结束，则BIOS认为它是一个引导扇区，然后BIOS就会把引导扇区的前512字节的引导代码拷贝到内存的0000:7c00处并将控制器彻底交给这段引导代码。

因书中说明要使用到软盘，但是在人工智能盛行的2018年哪里还有软盘，所以我尝试下虚拟机上模拟软盘运行，并最后得到的自己想要的结果。
现在将自己的操作实践记下，为自己和读者提供参考。

### 前提

本次操作是在Mac电脑上执行的，所以其他系统可能会有些不同。

- 系统：MacOSSierra 10.12.6 
- 汇编程序：NASM 0.98.40 （compiled on Apr 10 2017）
- 虚拟机：VirtualBox 5.1.30
- 代码位置:/Users/myname/OS/src/chapter1/a

### 先写引导

创建文件名为boos.asm的引导文件，代码如下：

```ASM
org 07c00h ;告诉编译器加载到07c00h处

    mov ax, cs
    mov ds, ax
    mov es, ax

    call DispStr ;调用显示字符串函数

    jmp $; 无限循环

DispStr:
    mov ax, BootMessge
    mov bp, ax
    mov cx, 16
    mov ax, 01301h
    mov bx, 000ch
    mov dl, 0
    int 10h
    ret
BootMessge: db "hello world!"
times 510 - ($ - $$) db 0
dw 0aa55h
```

### 制作镜像文件

- 编译代码生成boot.bin文件
    - nasm boot.asm -o boot.bin
- 制作一个包含boot.bin的镜像文件boot.img
    - dd if=boot.bin of=boot.img bs=512 count=1

做好了大小为512B的包含如上引导代码的镜像文件boot.img。

### 安装VirtualBox最新版

可以在百度软件中心下载VirtualBox的mac版本，下载后直接点击安装即可。

创建虚拟机

![VirtualBox](http://omsz9j1wp.bkt.clouddn.com/image/OS/MyOS1.png)

![VirtualBox](http://omsz9j1wp.bkt.clouddn.com/image/OS/MyOS2.png)

启动顺序中只选择软驱，将启动EFI选项去掉

![VirtualBox](http://omsz9j1wp.bkt.clouddn.com/image/OS/MyOS3.png)

选择软盘控制器并添加软盘，将引导程序添加到虚拟软盘中。

![VirtualBox](http://omsz9j1wp.bkt.clouddn.com/image/OS/MyOS4.png)

点击OK完成设置，然后点启动，就可以看到Hello，World了。

![VirtualBox](http://omsz9j1wp.bkt.clouddn.com/image/OS/MyOS6.png)


### 参考文档

> [用VMWare运行简单的引导代码](https://www.cnblogs.com/chengxuyuancc/archive/2013/04/22/3036361.html)
