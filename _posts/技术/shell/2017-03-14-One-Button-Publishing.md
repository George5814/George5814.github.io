---
layout: post
title: 编写一键发布脚本
category: 技术
tags:  Shell
keywords: 
description: 通过编写脚本一键将本地开发的java web程序打包并发布到指定的测试环境
---

## 一键发布脚本编写背景

项目比较复杂，每次打包将代码部署到测试环境时都需要在多个配置文件处做相应的更改。为了简化发布流程，降低不必要的工作量。
特编写脚本，一键执行，让脚本代替人执行繁杂且不复杂的工作。


## 一键发布脚本编写思路

1. 从远程git上pull最新代码，要保证当前分支没有未提交的代码。

1. 使用`pushd`命令切换到需要打包的子项目目录中。`pushd`命令会记录上一个目录位置，使用`popd`可以直接跳转到上次访问的目录。

1. 使用`mvn clean package`（可能还会有其他参数命令）对目标项目打包。

1. 对于需要手动操作的多处更改，都可以交给Linux命令`sed`结合`find`命令一键完成，提高工作效率，降低低技术含量的工作量。

1. 使用`ssh`远程执行命令，停掉tomcat服务。

1. 使用`scp`远程安全复制命令将打好的war包，直接复制到tomcat的webapps下。

1. 使用`ssh`远程执行命令，启动tomcat服务。同时执行命令时加上`nohup`命令，这样可以在ssh远程执行命令关闭后，远程命令可以继续执行。



## 一键发布脚本实例

```shell
git pull
pushd ./iform_parent/iform_parent/
mvn clean package

popd 

pushd ./iform_wb/

mvn clean package

popd

#将以指定字符开头的行替换为指定内容
sed -i  's/^client\.credential\.path.*/client\.credential\.path=\/home\/authfile.txt/g' `find . -path "*target*"   \( -name sdk.properties -o -name application.properties  \)`

#替换ip地址
sed -i  's/^servername.*/servername=172\.20\.9\.40\:8080/g' `find . -path "*target*"   \( -name sdk.properties -o -name application.properties  \)`

#停止服务
ssh -t -p 22 root@172.20.9.40 '/usr/local/tomcat/bin/shutdown.sh;'

#部署ifrom_web包
scp -r ./iform_parent/iform_parent/iform_web/target/iform_web-0.0.1  root@172.20.9.40:/usr/local/tomcat/webapps/iform_web

#部署iform_wb包
scp -r ./iform_wb/target/iform_wb-0.0.1   root@172.20.9.40:/usr/local/tomcat/webapps/iform_wb

#重启服务,加上nohup是为了在ssh退出后不中断命令执行
ssh -t -p 22 root@172.20.9.40 'nohup /usr/local/tomcat/bin/startup.sh'
```

## 总结

该脚本编写比较仓促，有多处需要完善的地方，此处仅作为参考，可以此为基础编写适合于自己应用的发布脚本。


