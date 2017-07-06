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

在项目经历了从简单到复杂、从单模块到多模块后。随着代码量提高，有时会需要[将某个或某些模块从原项目中分离出来，作为独立项目进行运作]({% post_url 2017-06-14-git-split-submodule %}){:title="独立项目进行运作"  :target="_blank"}。而被移出去的模块可能需要从原项目中删除，本文就简单讲解从原项目中移除模块的过程。

## 适用人群

本文内容适合对git有一定使用经验者。


## 前提

- 多模块项目：父模块A下有三个子模块(B,C,D)

![多模块项目](http://omsz9j1wp.bkt.clouddn.com/image/git/git-ad.png)

- git工具（2.x）我的是v2.8.1

## 实战

使用命令`git filter-branch --tree-filter "rm -rf remove-dir-name" --prune-empty -- --all`在本地删除指定目录`remove-dir-name`的提交记录。

使用命令`git push -f origin branch-name`将分支强推到远程仓库。