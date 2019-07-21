---
layout: post
title: Ipv6 方式搭梯子看世界
category: 技巧	
tags:  Other
keywords: 
description: 
---

## Ipv6 方式搭梯子看世界

### 1. 在 VPS 服务商处获取到 Ipv6 地址

先打开<https://my.vultr.com>

ipv6地址如: `2001:19f0:5:673a:5200:02f1:fe30:601b`

![vultr-ipv6](//raw.githubusercontent.com/George5814/blog-pic/master/image/vultr-ipv6.png)

### 2. 设置本地网络支持 ipv6

前置条件：
1. 天津联通
2. 光猫
3. 华为 ws5100 路由器

操作步骤：

1. 将光猫设置为 bridge (桥接模式)，因为ivp6 刚开始使用，有些光猫还不支持下发 ipv6前缀
   
![model-setup](//raw.githubusercontent.com/George5814/blog-pic/master/image/model-setup.png)
route-ipv6.png
2. 路由器设置开启 ipv6 功能，wan 和 lan 的地址及 dns 设置为自动获取配置
   ![route-ipv6](//raw.githubusercontent.com/George5814/blog-pic/master/image/route-ipv6.png)
3. 路由器设置 pppoe 拨号上网，输入宽带账号和密码
   ![pppoe](//raw.githubusercontent.com/George5814/blog-pic/master/image/pppoe.png)
4. 手机或电脑连接路由器的 wifi，查看在 ip 地址一栏中是否有非 fe 开头的 ipv6 地址，如果存在则说明 ipv6 地址配置成功
   ![pc-ipv6](//raw.githubusercontent.com/George5814/blog-pic/master/image/pc-ipv6.png)


如果宽带账号和密码忘记了，拨打 10010 找天津联通要，需要提供注册人的姓名和身份证号。

> 参考文章：
> <https://club.huawei.com/thread-20129291-1-1.html>
> <https://www.znj.com/news/27900.html>

### 3. 搭梯子

vps 上安装梯子。参考<https://blog.upx8.com/2249>


既然已经本地的 ipv6 网络连通了，那么需要按照以下参考文章的教程使用 ipv6 搭梯子了

> 参考文章
> <https://www.polarxiong.com/archives/%E6%90%AD%E5%BB%BAipv6-VPN-%E8%AE%A9ipv4%E4%B8%8Aipv6-%E4%B8%8B%E8%BD%BD%E9%80%9F%E5%BA%A6%E6%8F%90%E5%8D%87%E5%88%B0100M.html>

### 4. 设置 DNS

在阿里云的云解析 DNS 中配置

记录类型：`AAAA`
主机记录：`ss`(二级域名)
记录值：`2001:19f0:5:673a:5200:02f1:fe30:601b`

![dns-1](//raw.githubusercontent.com/George5814/blog-pic/master/image/dns-1.png)


### 5. 客户端连接

![ss-client-setup](//raw.githubusercontent.com/George5814/blog-pic/master/image/ss-client-setup.png)









