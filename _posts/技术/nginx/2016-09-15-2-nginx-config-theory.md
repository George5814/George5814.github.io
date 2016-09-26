---
layout: post
title: nginx 2：Nginx模块配置理论及实战
category: 技术
tags: Nginx
keywords: 
description: Nginx模块配置理论及实战
---

{:toc}

## Nginx模块配置理论知识

nginx由许多模块组成，这些模块可在配置文件中配置。

nginx配置指定可分为：

- 简单配置指指令

- 区块配置指令

### 简单配置指令 

由“名称”和“参数”组成，“名称”和“参数”以空格分隔，指定的最后以英文分号";"为结尾。

### 区块配置指令

与简单配置指令有相同的结构。区块指令不以分号结尾，使用花括号"{}"将一组配置包含于其中。
如果区块指定内含有其他指令，该区块指令被称为"context"。例如events,http,server,location等等。

所有的context外的指令，被认为处于"main context"中。

- events和http指令用于main context中

- server指令用于 http中

- location指令用于server中

- 配置文件注释行以#开始

- nginx提供静态内容服务

在http指令块中可以包含多个server，每个用友不同的立案庭端口和server name


## Nginx基础实战

在nginx配置文件`/usr/local/nginx/conf/nginx.conf`中的`http{}`中添加如下server块指令。

```nginx

 # 测试用的server配置
server {
    listen       8000;# 监听端口
    listen       test.jingzz.cn; # 监听的域名
    #server_name  somename  alias  another.alias;
    charset utf-8; # 设置字符编码
    root   /var/www/test/html; # 内容所在的物理路径的根目录，需要放置在location之外
    location /test/ {#/test

        index  index.html index.htm;# 默认指定的首页
    }   
} 
```

因为是在测试，不需要DNS查找域名，在本机的hosts文件中添加上域名和ip的映射关系即可。
比如，将test.jingzz.cn映射为本机地址127.0.0.1或内网地址或外网地址。

```
192.168.31.101   test.jingzz.cn
```

在本机上创建上文root中指定的物理路径和文件名

`mkdir /var/www/test/html # 创建访问的实际目录`

`echo "这是通过nginx访问的首页！" > /var/www/test/html/index.html`

在访问该测试服务test.jingzz.cn的机器上也要在hosts中配置ip和域名映射。

配置完成后，`/usr/local/nginx/sbin/nginx`启动，或者已启动情况下，通过`/usr/local/nginx/sbin/nginx -s reload`重新加载配置文件。

然后在浏览器中访问：http://test.jingzz.cn:8000。

正常情况下就可以访问到index.html中的内容了。
