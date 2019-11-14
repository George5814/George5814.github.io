---
layout:  post
title: Tomcat 配置服务
category: 技术
tags: Tomcat
keywords: 
description: Tomcat配置相关服务
---

{:toc}

## 配置

### 将tomcat安装为后台服务

```cmd
service.bat install [<service_name>] [/user <user_name>]
```

window启动tomcat服务

```cmd
net start <service_name>
```

### 远程调试

修改catalina.sh中的jpda的host:port,然后使用`catalina.sh jpda start` 就可以启用远程调试
