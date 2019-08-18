---
layout: post
title: Docker技术入门与实战 - 使用 dockerfile定制镜像
category: 技术
tags:  Docker
keywords: 
description: 介绍Dockerfile常用命令
---

[Docker检查](https://docs.docker.com/docker-for-mac/)

## dockerfile 指令

### FROM

指定基础镜像。所谓定制镜像，就是在一个基础镜像上进行定制。所以需要通过 `FROM`指定基础镜像，且必须是第一条指令

### RUN

RUN 指令用来执行命令行命令

1. shell格式。run <命令>
2. exec 格式。 run ['可执行文件','参数1','参数2']

每个 RUN 命令执行完后都会提交一个 commit，产生一层。多个 RUN 就会产生多层.


RUN 虽然可以像执行 shell命令那样执行，但在 docker 中运行其 RUN 命令要记得，这是在构建层，而不是在写 shell 脚本

### COPY 

`COPY <上下文中的 源文件path>` `容器内的目标路径或者工作目录的相对路径`



### ADD

与 COPY 性质相同。

不过

1. ADD 的源文件路径可以执行为 URL，docker 引擎会下载该文件并复制到目标目录中。下载文件的权限设置为 600。

对于下载的文件还得重新调整权限，对于压缩文件还得解压缩。不如使用 RUN 实用。不推荐使用。

对于tar 压缩文件，`ADD`指令会自动解压到目标目录。这是最适合`ADD`命令的场景

### CMD

容器启动命令。格式与`RUN`格式相似。

1. shell 格式: CMD <命令>
2. exec格式： CMD ['可执行文件','参数1'，'参数2']
3. 参数列表格式：CMD ['参数1'，'参数2'] 。 CMD 指令就是用于指定默认的容器主进程的启动命令的。

docker 不是虚拟机，没有前台后台的概念。容器中的应用都应该前台运行。在容器中不能使用`systemctl`命令的原因。

**对于容器而言，其启动程序就是容器应用进程。容器就是为主进程而存在的，主进程退出，容器就失去了存在的意义，从而退出了。**

### ENTRYPOINT

格式和`RUN`命令一样。
作用和 CMD 类似，都是在指定容器启动程序和参数

应用场景：

1. 让镜像变得像命令一样可用。
2. 应用运行前的准备工作


### ENV 

设置环境变量

格式:

```
ENV <key> <value>

ENV <Key1>=<value1> <Key2>=<value2> <Key3>=<value3>,
```

在其他位置使用时，使用`$`引用变量。

支持环境变量的指令： `ADD`,`COPY`,`ENV`,`EXPOSE`,`LABEL`,`USER`,`WORKDIR`,`VOLUME`,`STOPSIGNAL`,`ONBUILD`。


### ARG

设置环境变量，但与 `ENV`不同的是，`ENV`的环境变量在容器运行时可用，而`ARG`设置的变量是在容器构建环境中可用，在运行环境中不存在。

格式:

```
ARG <参数名>=[=<默认值>]  
```

该值可以在构建命令`docker build`中使用参数`--build <参数名>=<值>`


### VOLUME

指定匿名卷

格式

```
VOLUME ["路径 1","路径 2"]
VOLUME <"路径">
```

如`VOLUME /data`可以保证容器运行时在向/data 写数据时，不会写入到容器存储层，而是实际保存在卷中。

也可以在执行 `run` 命令时通过`-v`参数指定命名卷`mydata=/data`

### EXPOSE

声明端口，但只会告诉使用者本镜像可以开启哪些服务端口。但是在容器运行时不会自动开启。在容器启动指定随机映射时，可以随机映射`EXPOSE`的端口。

格式

```
EXPOSE <port1> <port2>
```

### WORKDIR

格式

```
WORKDIR <工作目录路径>
```

指定工作目录，dockfile 中的所有的`.`都代表着工作目录。该目录必须提前已经存在，`WORKDIR`指令不会自动创建不存在的目录。

在 docker 中，每个`RUN`命令都是不同的容器。所以两个命令的执行环境不同。不同于 shell 中的命令可以依赖上一条命令执行的结果。

### USER

指定当前用户，该用户切换前需要已经存在，否则无法切换

格式

```
USER <用户名>
```

该指定和`WORKDIR`都会改变环境状态并影响以后的层。

### HEALTHCHECK

检查容器的健康状态

格式

```
HEALTHCHECK [选项] CMD <命令> # 设置容器检查状态的命令
    --interval=<间隔>  #检查时间间隔，默认 30s
    --timeout=<时长>  #检查检查超时时间，默认 30s
    --retries=<次数> #连续失败次数，默认 3 次。超过该次数则容器设置为unhealthy


HEALTHCHECK NONE # 如果基础镜像有检查指定，使用该命令可以屏蔽之
```

该指定只可以出现一次，且如果出现多次，只有最后一次生效。


### ONBUILD

为以当前镜像作为基础镜像构建其他镜像时使用。

格式: `ONBUILD <其他指令>`



## dockerfile例子

```dockerfile

FROM debian:jessie

# 将多个命令合并在一起执行
RUN buildDeps='gcc libc6-dev make' \
    && apt-get update \
    && apt-get install -y $buildDeps \
    && wget -O redis.tar.gz "http://download.redis.io/releases/r
edis-3.2.5.tar.gz" \
    && mkdir -p /usr/src/redis \
    && tar -xzf redis.tar.gz -C /usr/src/redis --strip-component
s=1 \
    && make -C /usr/src/redis \
    && make -C /usr/src/redis install \
    && rm -rf /var/lib/apt/lists/* \
    && rm redis.tar.gz \
    && rm -r /usr/src/redis \
    && apt-get purge -y --auto-remove $buildDeps


```