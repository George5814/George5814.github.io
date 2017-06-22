---
layout: post
title: 搭建本地Jekyll+Markdown+Github的开发环境
category: 技术
tags: Git
keywords: 
description: 搭建使用Github page作为博客的本地运行环境
---

{:toc}

### 1.安装git、tortoisegit，注册github账号，添加ssh公钥证书。

当前这些步骤保证能通过git向github提交和修改项目

**注意：**

	本教程不提供该部分的内容，不懂的自行Google或百度

### 2.安装ruby环境 

[Ruby官网下载](http://rubyinstaller.org/){:target="_blank"}

[Ruby网盘下载](http://pan.baidu.com/s/1miguKXi){:target="_blank"} 密码：7f0l 

#### 2.1 安装ruby

**双击安装文件**

![安装选项](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-1.png)

**点击install**

![安装ruby](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-2.png)

#### 2.2 将DevKit-xxx.exe 双击解压到`C`盘根目录下

- 解压DevKit：

![解压DevKit](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-3.png)

- 查看解压位置：

![查看解压位置](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-4.png)
 
- 初始化ruby，在`C:\devKit`下生成config.yml文件


![生成config.yml文件](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-5.png)

![查看config.yml文件](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-6.png)

- 在config.yml文件中配置ruby的主目录位置

![修改config.yml文件](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-7.png)

- 安装devkit.rb 

![安装devkit.rb ](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-8.png)

#### 2.3 安装python

因为有些插件如`pygments`会用到python，因此安装python运行环境。

[python下载地址](https://www.python.org/downloads/){:taret="_blank"} 推荐使用2.x版本

#### 2.4 更换gem的源为taobao

`gem sources -l` 查看源列表

`gem sources -a url` 增加新的源

`gem sources -r url` 删除指定的源

示例如图:

![示例如图](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-9.png)

出现错误是因为没有https的证书，需要将证书放在自定义位置，如`C:\RailsInstaller`,并设置环境变量
`SSL_CERT_FILE=C:\RailsInstaller\cacert.pem`

![设置SSL_CERT_FILE环境变量](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-10.png)


`cacert.pem`[下载位置](https://curl.haxx.se/ca/cacert.pem){:target="_blank"}，保存在`C:\RailsInstaller`目录下，文件名为`cacert.pem`。

`cacert.pem`[网盘下载位置](http://pan.baidu.com/s/1pLSrGlt){:target="_blank"};

`cacert.pem`[当前下载位置](/public/file/cacert.pem){:target="_blank"};

查看证书是否已经添加进环境变量

![查看证书是否已经添加进环境变量](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-11.png)

验证taobao源已经添加成功

![验证taobao源已经添加成功](http://omsz9j1wp.bkt.clouddn.com/image/git/ruby-setup-12.png)

**接下来就可以安装jekyll及其他插件了。**

### 3.安装Jekyll 

jekyll安装:`gem install jekyll`

![jekyll安装](http://omsz9j1wp.bkt.clouddn.com/image/git/jekyll-setup-1.png)


jekyll-paginate安装:`gem install jekyll-paginate`

![jekyll-paginate安装](http://omsz9j1wp.bkt.clouddn.com/image/git/jekyll-setup-2.png)

### 4.启动本地环境

Jekyll 启动：`jekyll s`


![jekyll-paginate安装](http://omsz9j1wp.bkt.clouddn.com/image/git/jekyll-setup-3.png)


运行截图：

![jekyll本地环境运行截图](http://omsz9j1wp.bkt.clouddn.com/image/git/jekyll-setup-4.png)


