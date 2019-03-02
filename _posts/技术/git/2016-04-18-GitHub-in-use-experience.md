---
layout: post
title: github使用经验
category: 技术
tags: Git
keywords: 
description: 
---

## 1. fork分支pull原分支上的更新commit
	例如：要把AA更新的commit代码pull到BB代码区

	GitHub BB仓库| New pull request | base fork BB  head fork AA | create pull request | 填写pull request 备注  | create pull request| BB代码区 Merge pull request | BB代码区 Confirm merge |合并完成
	
## 2.删除github上的分支 
	当前项目为BB，当前为master分支，要删除gh-pages分支
	
	GitHub BB仓库 | Code 标签 | branches | Your branches | 右侧的红色垃圾桶按钮 | 点击后直接删除，没有提示，但可以restore（恢复，仅限在一定时间内）
