---
layout: post
title: git进阶经验- git提交 log 的模板配置
category: 技术
tags: Git
keywords: git template commit log
modify: 2019-11-12
description: 为了 RD 在开发时更方便的使用 git commit 的模板，既可以明晰本次提交的目的，又可以方便便捷的使用模板而无需时刻记着模板的格式规范。
published: true
---

# git commit 规范

[Commit message 和 Change log 编写指南](http://www.ruanyifeng.com/blog/2016/01/commit_message_change_log.html)

# git commit 模板配置

## 背景

为了 RD 在开发时更方便的使用 git commit 的模板，既可以明晰本次提交的目的，又可以方便便捷的使用模板而无需时刻记着模板的格式规范。

## 目标

提高 RD 使用模板提交 commit log 的意愿

## 方案

### terminal 方式 commit log

在项目目录下建立模板文件。如  `.gitcommit_template`。

```s

type（scope ）: subject

body

BREAKING CHANGE:

CLOSE:


# TYPE:
#		feat : 新功能
#		fix ： 修补 bug
#		docs: 仅文档的改动
#		style：代码格式的改动，不影响代码运行的变动。
#		refactor：重构，即不是新增功能，也不是修改bug的代码变动
#		test：	增加测试
#		chore：构建过程或辅助工具的变动
# SCOPE：用于说明 commit 影响的范围，比如数据层、控制层、视图层等等，视项目不同而不同。pegasus项目中可以使用"业务 + 功能模板",看起来更清晰些。
# subject: 摘要，概述。一句话描述本次提交的目的
# Body： 详细描述本次提交。比如：修改了什么，增加了什么，删除了什么等。
# Footer：
#	BREAKING CHANGE:不兼容的改动
#	Closes：关闭issue。如#123, #245, #992。对于#BOS-1273，git 可以直接链接到 https://flow.sankuai.com/browse/BOS-1273
#
```

#### 设置提交模板

模板名为: `.gitcommit_template` , 文件前有"."，表示隐藏文件

```s
git config commit.template .gitcommit_template
```


#### 设置文本编辑器为 vim

```s
git config core.editor vim
```

#### 通过命令行提交代码

```s
git add .

git commit -v
```

会弹出 commit log的模板，以 vim 方式编辑。#开头的为注释，都不会提交到 实际 commit 的 log中，不用删除。

![commit log 模板](//raw.githubusercontent.com/George5814/blog-pic/master/image/git/git-commit-log-1.png)

编辑完后以 `:wq `保存即可

#### 提交本次commit

```s
git push
```

### Intellij Idea 方式 commit log

#### 安装插件

对于 Idea 方式提交代码的 RD，不需要配置模板文件。但需要下载模板插件 `Git Commit Template`

![安装 Git Commit Template 插件](//raw.githubusercontent.com/George5814/blog-pic/master/image/git/git-commit-log-2.png)


#### 提交代码

菜单栏 =》 CVS =》 Git =》 Commit File => 弹出提交更改的操作窗口。

在该窗口中可以使用模板，也可以在 commit 前做其他操作。如下图的右侧选项显示。

![提交代码窗口](//raw.githubusercontent.com/George5814/blog-pic/master/image/git/git-commit-log-3.png)

编辑模板内容并点 OK 后的 log 样式如下：

![编辑好的 commit log 信息](//raw.githubusercontent.com/George5814/blog-pic/master/image/git/git-commit-log-4.png)

然后在做commit 和 push 操作即可。

### SourceTree 方式 commit log

SourceTree也可以配置模板，不过配置在模板内的内容都认为是模板内容，如果添加了以#开头的内容，在SourceTree中不认为该内容为注释。

#### 配置模板内容

打开一个代码仓库，如 pegasus。

菜单栏 =》仓库 =》仓库设置 =》弹窗(提交模板) =》 选择自定义。将在 terminal 方式 commit log  中配置的模板内容配置在该处。然后点确定。

![sourceTree 配置 commit log 模板](//raw.githubusercontent.com/George5814/blog-pic/master/image/git/git-commit-log-5.png)

#### 提交更改

点击提交按钮会自动弹出模板内容信息。在按模板编辑完后，将以#开头的注释内容删掉即可。

![commit log 提交更改](//raw.githubusercontent.com/George5814/blog-pic/master/image/git/git-commit-log-6.png)


> <https://blog.csdn.net/import_sadaharu/article/details/54576748>
> <https://www.cnblogs.com/ctaodream/p/6066694.html>
> 

#### 文本模板

```
feat（scop）：Subject

Body

BREAKING CHANGE:无

CLOSE: 无

#TYPE:
#       feat:新功能
#       fix :修补 bug
#       docs:仅文档的改动
#       style：代码格式的改动，不影响代码运行的变动。
#       refactor：重构，即不是新增功能，也不是修改bug的代码变动
#       test： 增加测试
#       chore:构建过程或辅助工具的变动
#SCOPE：用于说明commit影响的范围，比如数据层、控制层、视图层等等，视项目不同而不同。项目中可以使用“业务+功能模板“，看起来更清晰些,
#Subject:摘要，概述。一句话描述本次提交的目的
#Body：详细描述本次提交。比如：修改了什么，增加了什么，删除了什么等。
#Footer：
#       BREAKING CHANGE:不兼容的改动
#       Closes：关闭issue。如#123, #245, #992O 。 CLOSE: #BOS-1273
```

