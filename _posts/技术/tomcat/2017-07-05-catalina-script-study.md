---
layout:  post
title: Tomcat的catalina.sh脚本研读
category: 技术
tags: Tomcat
keywords: 
description: 阅读学习tomcat的启动停止脚本
---

## catalina.sh执行步骤

1. 检查特定类型系统
    
    （CYGWIN， Darwin，OS400）

1. 检查执行脚本为软连接问题

1. 设置标准的环境变量

    先设置`CATALINA_HOME`，然后设置`CATALINA_BASE`。如果`CATALINA_BASE`不存在，则将其设置为`CATALINA_HOME`。

1. 检查`CATALINA_HOME`和`CATALINA_BASE`不包含冒号(:)，因为冒号在`classpath`中是分隔符。

1. 确保任何的用户定义的`classpath`在tomcat启动前被使用，在需要的情况下，只允许在`setenv.sh`脚本中指定。

1. 对于`Cygwin`,确保在任何文件创建前使用unix格式的路径。

1. 对于`OS400`, （略过）

1. 获取标准的java环境变量

1. 在classpath中添加额外的jar包(`bootstrap`)

1. 在classpath中添加额外的`tomcat-juli.jar`包

1. 对于`Cygwin`,在运行java前将路径替换为window方式

1. 设置日志管理配置文件和日志管理类

1. 执行请求命令

    - 判断jpda命令及变量设置（不与其他条件并列）

        包括：JPDA_TRANSPORT、JPDA_ADDRESS、JPDA_SUSPEND、JPDA_OPTS

    - 判断debug命令及变量设置

        并设置debug启动`org.apache.catalina.startup.Bootstrap "$@" start`

    - 判断run命令及变量设置并设置启动

    - 判断start命令及变量设置并设置启动

    - 判断stop命令及变量设置并设置启动

    - 判断configtest命令及变量设置

    - 判断version命令并打印显示版本

    - 在所有命令都不匹配情况下，显示提示信息。