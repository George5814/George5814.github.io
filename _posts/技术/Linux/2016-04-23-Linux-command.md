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
