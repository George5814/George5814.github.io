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
export PATH=$PATH:$NGINX_HOME/bin
```

使得profile的修改生效：`source /etc/profie`


### 2.操作nginx

#### 2.1 启动基本的nginx

无环境变量：`/usr/local/nginx`，或设置了环境变量`nginx`

#### 2.2 停止nginx服务

无环境变量：`/usr/local/nginx -s stop`，或设置了环境变量`nginx -s stop`