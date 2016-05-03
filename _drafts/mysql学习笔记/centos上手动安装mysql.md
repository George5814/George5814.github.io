## Cent OS上手动安装mysql
### 1. 将虚拟机中的centos的ip配置为手动，并可以和宿主机联通
### 2. 使用winscp将mysql-5.5-x.tar.gz 复制到linux的root目录下
#### 2.1 解压文件
	#tar -xzvf  mysql-5.5-x.tar.gz

#### 2.2 更名：
	#mv mysql-5.5-x.tar.gz mysql
### 3. 配置预安装环境
#### 3.1 添加用户和组
	#groupadd mysql
	#useradd -r -g mysql mysql

#### 3.2 安装cmake工具
	#yum install gcc #先安装编译工具集gcc
	#yum install cmake
#### 3.3 确保以下所需系统软件包已经被安装
	通过 rpm -qa | grep name 的方式验证以下软件包是否已全部安装。
	gcc* gcc-c++* autoconf* automake* zlib* libxml* "ncurses-devel" libgcrypt* libtool*
	curses在RH和centos下为ncurses-devel，在Ubuntu下为libncurses5-dev 
#### 3.4 执行"cmake .",如果出现找不到curses的lib，centos下
	#yum  install ncurses-devel
	#删除mysql下的CMakeCache.txt文件
	#cmake .
#### 3.5 编译并安装
	#make
	#make install
### 4. 安装后设置
#### 4.1 配置
	#cd /usr/local/mysql
	#chown -R mysql .
	#chgrp -R mysql .
	####scripts/mysql_install_db --user=mysql #有错
	#./bin/mysqld --skip-grant &
	#chown -R root .
	#chown -R mysql data
	##### Next command is optional
	#cp support-files/my-medium.cnf /etc/my.cnf
	#bin/mysqld_safe --user=mysql &
	##### Next command is optional
	#cp support-files/mysql.server /etc/init.d/mysql.server

**PS:如果出现./mysqld: unknown option '--ndbcluster'**，就修改配置文件
```bash
	[client]
	socket=/var/lib/mysql/mysql.sock
	
	[mysqld]
	max_connections=100
	datadir=/var/lib/mysql
	socket=/var/lib/mysql/mysql.sock
	user=mysql
	#ndbcluster  #修改后为注释掉
	#ndb-connectstring=192.168.100.11   #修改后为注释掉
	# Disabling symbolic-links is recommended to prevent assorted security risks
	symbolic-links=0
	
	[mysqld_safe]
	log-error=/var/log/mysqld.log
	pid-file=/var/run/mysqld/mysqld.pid
	
	[mysql_cluster]
	#ndb-connectstring=192.168.100.11   #修改后为注释掉
```
#### 4.2 启动服务 
	#/usr/local/mysql/bin/mysqld --user=mysql &  #启动mysqld 服务
	#/usr/local/mysql/bin/mysql -u mysql  #启动mysql命令行客户端
#### 4.3 将mysql目录添加到环境变量中
	#vim /etc/profile
	#export MYSQL_HOME=/usr/local/mysql
	#export PATH=.:MYSQL_HOME/bin:$PATH
	
	#source /etc/profile
### 5.配置mysql主从服务器（未完成）
#### 5.1 首先确保主从服务器上的Mysql版本相同
#### 5.2 在主服务器上,设置一个从数据库的账户,使用REPLICATION SLAVE赋予权限
#### 5.3 赋予从服务器权限
	#mysql
	mysql>  GRANT REPLICATION SLAVE ON *.* TO 'root'@'192.168.48.142';## IDENTIFIED BY 'mysql'
	Query OK, 0 rows affected (0.01 sec)

