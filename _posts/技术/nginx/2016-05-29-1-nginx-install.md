---
layout: post
title: nginx 1：nginx的安装
category: 技术
tags: Nginx
keywords: 
description: Nginx的安装
---

{:toc}

### 1.下载/解压/编译源码`nginx-1.11.0.tar.gz`

#### 1.1 下载`nginx-1.11.0.tar.gz`

```
wget http://nginx.org/download/nginx-1.11.0.tar.gz
```


#### 1.2 解压`nginx-1.11.0.tar.gz`到`/usr/local`

```
tar -xzvf nginx-1.11.0.tar.gz  -C /usr/local/
```

#### 1.3 编译源码并安装

安装`pcre-devel`,否则报错`./configure: error: the HTTP rewrite module requires the PCRE library.`。

```bash
yum -y install pcre-devel
```

安装`zlib-devel`，否则报错`./configure: error: the HTTP gzip module requires the zlib library.`。

```bash
yum install -y zlib-devel
```

```bash
 ./configure --prefix=/usr/local/nginx  #配置nginx安装的指定位置
make
make install #安装到prefix指定位置

```

#### 1.4 将nginx配置到环境变量(可选)

编辑系统环境变量文件：`vim /etc/profile`

添加如下内容：

```
#add nginx env  var
export NGINX_HOME=/usr/local/nginx
export PATH=$PATH:$NGINX_HOME/sbin
```

使得profile的修改生效：`source /etc/profie`


### 2.操作nginx

#### 2.1 启动基本的nginx

无环境变量：`/usr/local/nginx/sbin/nginx`，或设置了环境变量`nginx`

windows 环境下：
`nginx.exe`或`start nginx.exe #该命令会以服务方式启动`

#### 2.2 停止nginx服务

无环境变量：`/usr/local/nginx/sbin/nginx -s stop`，或设置了环境变量`nginx -s stop`

#### 2.3 优雅退出nginx

无环境变量：`/usr/local/nginx/sbin/nginx -s quit`，或设置了环境变量`nginx -s quit`

优雅退出nginx就是指nginx主进程会等待worker进程完成当前用户请求的处理。

#### 2.4 重新加载配置

无环境变量：`/usr/local/nginx/sbin/nginx -s reload `，或设置了环境变量`nginx -s reload `

更改配置后，必须执行重新加载配置的命令或者重启nginx。

在主进程受到reload信号，会检查配置文件语法并尝试加载该配置。如果成功，会启动新的worker进程，并发送消息通知原来的worker进程关闭。
如果失败，主进程会回滚，仍以原来配置工作。当原来的worker进程受到消息要求关闭时，会停止接收新的连接，在处理完当前已经接收到的请求后退出进程。

#### 2.3 重新打开日志文件

无环境变量：`/usr/local/nginx/sbin/nginx -s reopen `，或设置了环境变量`nginx -s reopen `


#### 2.4 查看nginx运行的进程列表

`ps -ax | grep nginx`
