---
layout: post
title: Docker常用命令
category: 技术
tags:  Docker
keywords: 
description: 介绍Docker常用命令
---

**个人新的博客地址:<https://segmentfault.com/blog/followtry>**

[Docker检查](https://docs.docker.com/docker-for-mac/)

## 查看信息

    $docker --version #查看版本
    $docker-compose --version #查看版本
    $docker-machine --version #查看版本
    $docker version #查看client和server端版本，并可以查看是否开启体验功能
    $docker info #查看 docker 的详细信息

## 检查    

    $docker ps # 查看当前正在运行的image实例
    $docker container ls # 查看当前正在运行的 image 实例，与 docker ps 相同
    $docker ps -a #查看所有镜像实例 同 docker container ls -a
    $docker run hello-world #验证docker是否在运行中
    $docker inspect <task or container>   检查任务或容器
    

## 镜像操作

    $docker search <image-name> $搜索仓库中的指定镜像
    $docker build -t <image-name> . #使用当前目录下的Dockerfile构建镜像
    $docker commit <new-image-name> # 使用当前容器构建新的镜像
    $docker images #列出所有镜像
    $docker image ls -a  显示机器上所有的镜像
    $docker image rm <image id>      删除指定的镜像
    $docker image rm $(docker image ls -a -q)  删除所有的镜像
    $docker rmi [image-id/image-name] #删除指定的镜像，如docker rmi nginx
    $docker tag <image> <username>/<repository>:<tag> #为自定义的镜像打上tag。如：$docker tag hellopython followtry/demo:latest
    $docker push <username>/<repository>:<tag> #将自定义的镜像发布到仓库。如：docker push followtry/demo:latest
        上传后访问地址：https://cloud.docker.com/swarm/followtry/repository/docker/followtry/demo/general
    $docker pull <username>/<repository> #pull自定义的上传上去的镜像。如：$docker pull followtry/demo
    $docker create <image id> # 创建一个镜像的容器，未运行状态
    $docker run username/repository:tag #创建并运行仓库的镜像，相当于 `docker create`和`docker start`两种操作
    $docker run -i -t -h test-docker --name test registry-hulk.sankuai.com/sankuai/centos:6  /bin/bash #以shell 交互方式运行容器，并host 命名为 test-docker,容器命名为 test
 
## 容器操作
    
    $docker container ls #列出所有运行中的容器
    $docker container ls -a #列出所有容器，包括未运行的
    docker container ls -q     #只列出运行的容器的id集合
    $docker container stop <hash>  # 优雅停用指定的容器
    $docker container kill <hash>  #强制关闭指定的容器
    $docker container rm <hash>    #删除指定的容器
    $docker container rm $(docker container ls -a -q)  #删除所有的容器
    $docker run -d -p 8080:80 --name webserver --restart=always nginx # 运行nginx镜像实例，-d：后台，-p:绑定端口8080到docker的80。--name 指定容器名称为webserver,--restart： 自动重启容器
    $docker stop <containerid/container-name> #停止容器webserver
    $docker kill <containerid/container-name> # 强制停止容器webserver
    $docker start <containerid/container-name> #启动容器webserver
    $docker restart <containerid/container-name> #重启容器webserver
    $docker port <containerid/container-name> #查看指定容器的端口映射
    $docker logs -f -t <containerid/container-name> #查看指定容器的操作日志信息并跟踪最新的日志。-t: 打印操作时间
    $docker top <containerid/container-name>  #查看容器的进程
    $docker inspect <containerid/container-name> #检查容器的详细底层信息
    $docker rm <containerid/container-name> #删除容器,只能删除已停止运行的容器
    $docker attach <containerid/container-name> #将操作附着在刚启动的容器上
    $docker top <containerid/container-name> # 查看容器内运行的进程
    $docker exec -i -t  <containerid/container-name> /bin/bash #在已经后台启动的容器上打开一个模拟的交互式的shell 终端

## dockerfile 指令

基于 dockerfile 使用 `docker build` 指定构建新的镜像

1. FROM
   指定一个已经存在的基础镜像
2. MAINTAINER
   镜像作者及作者的邮件地址
3. RUN
   在当前镜像产生的容器中运行指定的命令
4. RUN
5. RUN
6. EXPOSE
   告诉 docker 该容器内的应用程序将会使用容器的指定端口。但是docker 运行新容器时不会自动打开该端口。需要造运行 docker run 时指定。
7. 
   
## Docker操作

    $/Applications/Docker.app/Contents/MacOS/Docker --uninstall #docker卸载
    
## 用户和组

    $docker login #登录
    
## service指令

    $docker service ls  #列出与应用关联的所有运行的service
    $docker service ps <service>  #列出和应用关联的所有任务

## stack指令

    $docker stack ls  #列出stack或app列表
    $docker stack deploy -c <composefile> <appname>  # 运行指定的compose文件
    $docker stack rm <appname>  #删除一个或多个stack
    $docker stack services <stack1>#列出stack1中的服务
    
## swarm指令

    $docker swarm ca  #显示root的ca
    $docker swarm init #swarm初始化
    $docker swarm join --token SWMTKN-1-1qazipp4hbndidnfzsm8psks33tdgvvcgn0ids6uv41u68w9f8-aq1lw7di2g4wx6buypra1j6qz 192.168.65.2:2377 #执行该命令，将当前节点添加到swarm中管理
    $docker swarm join-token [OPTIONS] (worker|manager) #管理允许加入的token
    $docker swarm leave --force  #从管理器中强制移除单个swarm节点
    $docker swarm unlock-key #swarm集群的解锁key
    $docker swarm unlock #解锁集群,key=SWMKEY-1-8jKcZ7SJb2+aHibYuJ1RevmuEAtbx8q0LY+crOx+QRU
    $docker swarm update   #更新swarm,内有自动lock，cert过期周期，分发心跳周期，历史任务限制等功能
    
## node指令

    $docker node demote <NODEID/HOSTNAME> #对NODEID进行降级
    $docker node inspect <NODEID/HOSTNAME> #节点NODEID的详细信息
    $docker node ls #列出节点列表
    $docker node promote node | ... #提升一到多个节点为swarm的管理者
    $docker node ps #列出节点上正在运行的任务，默认是当前节点
    $docker node rm node | ... #swarm中移除一个或多个节点
    $docker node update <NODEID/HOSTNAME> #更新指定节点
    
        
    
    
    
    
    
