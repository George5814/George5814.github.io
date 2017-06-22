---
layout:  post
title: Vue安装使用
category: 技术
tags: JS
keywords: 
description: Vue的安装使用，内含对nodejs的安装使用
---


### 1. 安装node


![工具包](http://omsz9j1wp.bkt.clouddn.com/image/js/node-setup-0.png)

地址:<http://nodejs.cn/download/>

![node版本](http://omsz9j1wp.bkt.clouddn.com/image/js/node-setup-1.png)

解压到指定目录，本例中为`D:\dev_tools\node-v6.10.3-win-x64`,将该路径添加到环境变量中。

### 2. 安装淘宝镜像

使用cmd打开并输入`node -v`查看版本。

输入`npm install -g cnpm --registry=https://registry.npm.taobao.org`安装node镜像。

### 3. 安装Vue脚手架


`npm install vue-cli -g`

![Vue脚手架](http://omsz9j1wp.bkt.clouddn.com/image/js/vue-setup-1.png)

### 4.安装webpack

`cnpm install webpack -g`

![安装webpack](http://omsz9j1wp.bkt.clouddn.com/image/js/webpack-setup-1.png)

### 5.根据模板创建项目

切换到自定义的项目所在目录，执行命令`vue init webpack-simple <project-name,no chinese>`

创建期间会询问项目名称，项目描述，作者以及是否使用sass等，可以使用默认也可以自己指定。

创建完成后会有提示命令如下：

![创建项目](http://omsz9j1wp.bkt.clouddn.com/image/js/vue-setup-2.png)

按提示执行命令，执行命令时时间稍长，耐心等待下。

执行`npm run dev`，会默认在本地的8080 端口启用vue的简单示例服务。

### 6. 安装Vue路由模块和网络请求模块

![安装Vue路由模块和网络请求模块](http://omsz9j1wp.bkt.clouddn.com/image/js/vue-setup-3.png)




