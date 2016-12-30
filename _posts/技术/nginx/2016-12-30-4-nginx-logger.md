---
layout: post
title: nginx 4：Nginx日志管理
category: 技术
tags: Nginx
keywords: 
description: Nginx日志方面的配置，不定期更新
---

{:toc}


日志配置示例：

```nginx

http {
	log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for" '
                      'upstream_addr: $upstream_addr '
                      'resp_time: $upstream_response_time '
                      'request_time: $request_time ';

    access_log  logs/access.log  main;
}

```

其中：

- $remote_addr：为发送请求的客户端地址

- $remote_user：为发送请求的用户

- $time_local：本地处理时间

- $request：客户端发送请求，带请求的方法

- $status：http状态码

- $http_user_agent：用户代理

- $http_x_forwarded_for：http请求端的真实ip

- $upstream_addr：转发到后端的ip及端口信息，如tomcat所在服务的ip:port。

- $upstream_response_time：转发相应时间

- $request_time：请求时间


这样配置了以后可以清晰的看到哪个用户发送了请求，发送了什么请求，请求的基本信息，
nginx将请求转发到后端的哪个服务，后端服务地址信息，请求时间和回应时间等信息。
