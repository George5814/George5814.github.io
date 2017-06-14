---
layout: post
title: git进阶经验-从多模块项目中分理子模块
category: 技术
tags: Git
keywords: 
description: 将子模块从原项目中分理出来作为独立项目，同时能保持子模块的提交记录。
---

{:toc}

## 背景

在项目经历了从简单到复杂、从单模块到多模块后。随着代码量提高，有时会需要将某个或某些模块从原项目中分离出来，作为独立项目进行运作。
本文就是以此为出发点，完成子模块到独立项目的操作。

## 适用人群

本文内容适合对git有一定使用经验者。


## 前提

- 多模块项目：父模块A下有三个子模块(B,C,D)

![多模块项目](http://omsz9j1wp.bkt.clouddn.com/image/git/git-ad.png)

- git工具（2.x）我的是v2.8.1

## 实战

**以上文截图中的git多模块项目为例**

1. 切换到git项目A的根目录：`cd /F/develop-code-2/A/`

1. 将指定的子模块B(子模块所在目录B)抽取出来命令为`rename-b`分支:`git subtree split -P ./A/B -b 
rename-b`

	![多模块项目](http://omsz9j1wp.bkt.clouddn.com/image/git/git-ad-2.png)

	原提交记录

	![多模块项目](http://omsz9j1wp.bkt.clouddn.com/image/git/git-ad-3.png) 

	拆分后B目录提交记录，B目录下的所有内容都存在git项目根目录下
	
	![多模块项目](http://omsz9j1wp.bkt.clouddn.com/image/git/git-ad-4.png) 
	

1. 创建新的目录，初始化新的git项目B：`mkdir /F/develop-code-2/B`

1. 初始化该目录为git项目根目录：`git init`,该目录下会产生`.git`目录

1. 拉取git项目A的`rename-b`分支：`git pull ../A rename-b`

	从截图中可以看到，已经成功将B目录抽取为独立项目了。

	![多模块项目](http://omsz9j1wp.bkt.clouddn.com/image/git/git-ad-5.png) 

1. 现在可以修改`${B-root}/.git/config`文件

	添加红框中的内容就可以实现向远端服务器提交代码了。

	![多模块项目](http://omsz9j1wp.bkt.clouddn.com/image/git/git-ad-6.png) 


## 总结

该文图文并茂的展示了将一个子模块(子目录)从复杂的git项目中分离为独立项目的过程，简单易懂。









