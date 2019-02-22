---
layout: post
title: Polysh的安装使用
category: 技术
tags: Linux
keywords: 
description: 
---

## 登录Jumper
自己的Jumper账号为（mis账号@jumper.domain.com），系统已经给分配了一个初始密码，可以自己修改密码。
在终端输入：ssh mis账号@jumper.domain.com
输入密码，以及系统发给的验证码。

## 安装Polysh
PS:使用jumper先登录st机器，然后再进行安装（在终端输入：ssh hostname02 登录st机器)
然后输入下面的语句安装Polysh： 

`wget 'http://guichaz.free.fr/polysh/files/polysh-0.4.tar.gz' && pip install --user polysh-0.4.tar.gz && echo 'export PATH=$PATH:$(python -c "import site; print site.USER_BASE")/bin' >> .bash_profile && source .bash_profile`

 

上面的语句执行完后要对polysh解压。

## 设置Jumper登录密码

Jumper登陆使用Polysh时需要输入jumper密码，目前Polysh只支持文件的形式获取密码，需要现在用户目录下面创建存储密码文件： 
`cd ~；vim jp_passwd`

jp_passwd文件内写入个人jumper密码（只写密码即可），并设置500权限：`chmod 500 jp_passwd`

## 直连机器使用脚本

在用户目录下建立polysh.sh脚本文件，内容如下：

其中第一行为你想要连接的机器，可以根据需要自己修改。第二行中的miszhanghao为自己的mis号。

```
#!/usr/bin/env bash
targets='dx-hostname<01-02> yf-hostname<01-02> yf-hostname yf-hostname dx-hostname<01-05> yf-hostname<01-05>'
polysh --password-file=/home/{你的mis}/jp_passwd $targets
```

polysh.sh文件建立之后，为其加上可执行权限：`chmod +x polysh.sh`

输入 `./polysh.sh`执行脚本，设置完毕。