---
layout: post
title: mac上命令行操作
category: 技术
tags: Mac
keywords: 
description:  不定时更新
---

### lsof 

- 查找端口占用：`lsof -i tcp:port`,将port替换为指定端口，如8080。


### kill 

- 强制kill掉进程:`kill -9 PID`,pid替换为要kill的进程id。