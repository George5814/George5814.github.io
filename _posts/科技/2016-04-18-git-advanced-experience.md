## git进阶使用经验
[TOC]
本经验如未明确说明，默认的命令操作位置为git bash，平台为windows7，操作位置为/c/user/{userHome}
### 1. 同一git客户端，配置不同git url的用户名和密码
#### 1.1使用ssh-keygen 命名创建新的id\_rsa和id\_rsa.pub
```shell
ssh-keygen -t rsa
```
会要求输入id\_isa 的位置(输入新位置，避免不覆盖原来的id_rsa):.ssh/id\_rsa\_github。此处以github为例
一路enter，创建好了私钥和公钥文件。
#### 1.2 将id\_rsa\_github.pub的公钥内容复制到github上
打开github网站 **|** 点击头像 **|** 设置 **|** SSH keys **|** 右侧Add ssh key **|** 将id\_rsa\_github.pub的内容复制到key中 **|** 点 Add key **|** 添加完成后显示key的概要信息

#### 1.3 配置config文件
在当前目录{userHome}的.shh目录下新建config文件，将一下内容复制到config中，并做对应修改
```bash
#原有gitlab的位置
Host 172.16.50.51:8080
#主机名：可以是ip或域名
HostName 172.16.50.51
User test
#提交到对应的Host服务器时需要的私钥文件
IdentityFile ~/.ssh/id_rsa

 

#第二个 git.oschina
Host git.oschina.net
HostName git.oschina.net
User test
IdentityFile ~/.ssh/id_rsa_github

#第三个 github
Host github.com
HostName github.com
User test
IdentityFile ~/.ssh/id_rsa_github
```

#### 1.4 让ssh识别新的私钥
私钥新建后，默认ssh是不识别的，需要手动添加。执行命令
```bash
ssh-add ~/.ssh/id\_rsa\_github
```
如果出现Could not open a connection to your authentication agent 错误后执行
```bash
#使用bash命令解析器
ssh-agent bash
```
再次执行上面的ssh-add命令。

**在以后的clone或者add remote时，需要将config文件中的host替换为git@remote-address中的remote-address。**

#### 1.5 git客户端记住用户名和密码
在/c/user/{userHome}/下的".gitconfig"文件中添加
```shell
[credential]
	helper = store
```

使用git bash在/c/user/{userHome}/下新建文件".git-credentials",前面有“.”（句点）。在里面添加内容格式化如下：
```http
<!-- 先检查协议时http还是https -->
https://{username}:{pwd}@github.com
```
**注意：** 只要在git服务器(如：github)上添加了公钥，.git-credentials在你第一次输入完用户名和密码后会自动建立的。

### 2.同一个git项目提交到多个远程仓库（gitLab）
修改项目中.git目录下的config文件
```bash
[remote "origin"]
	url = https://git.oschina.net/xxx/yyy.git
	url = https://github.com/xxx/yyy.git
```
添加哪个仓库，就将哪个仓库的git链接添加到如上代码位置，同时记得将公钥添加到新仓库中，否则每次提交的都需要输入用户名和密码的哦！


### 3. tortoiseGit配置外部比较工具
	
	开始 | 所有程序 | TortoiseGit | settings 也可以右键选择settings | Diff viewer | 右侧上下都选择External，选择自定义的比较工具即可
	
	
























