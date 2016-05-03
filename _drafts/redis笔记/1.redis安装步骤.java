/***********---- redis安装笔记 ----*************/
1.使用winscp将windows下的redis的压缩包复制到虚拟机的centos6中，
2.解压redis压缩包，并重命名为redis,移动到/usr/local/下
#tar -xzvf redis-2.8.3.tar.gz 
#cp -r redis-2.8.3 /usr/local/
#mv redis-2.8.3 redis
3.切换工作目录到redis下，打开README文件，按照文件中的步骤安装redis
#cd /usr/local/redis/
#vim README

4.编译redis源码
#make  ##当前系统的位数，64位机器会便以为64位程序
或者#make 32bit #编译为32位程序
或者#make CFLAGS="-m32 -march=native" LDFLAGS="-m32"  #编译为32位程序

没报错就代表编译成功了。

5.启动redis的服务，并使用redis-cli客户端测试使用(当前目录为/usr/local/redis/)
#cd src
##启动redis服务，使用默认配置
#./redis-server &
##启动redis服务，使用自定义的redis配置(例:redis.conf)
#./redis-server /path/to/redis.conf
#可以通过添加正确的参数修改 redis配置文件的内容，临时修改
# ./redis-server --port 9999 --slaveof 127.0.0.1 6379
# ./redis-server /etc/redis/6379.conf --loglevel debug

6.运行redis客户端
[root@hadoop1 redis]# ./src/redis-cli
127.0.0.1:6379> ping
PONG
127.0.0.1:6379> set foo bar
OK
127.0.0.1:6379> get foo
"bar"
127.0.0.1:6379> incr mycounter
(integer) 1
127.0.0.1:6379> incr mycounter
(integer) 2
127.0.0.1:6379> incr mycounter
(integer) 3
127.0.0.1:6379> incr mycounter
(integer) 4
127.0.0.1:6379>

7.在ubuntu和Debian生产环境中安装redis二进制。
[root@hadoop1 redis]# make install 
安装redis到系统上，也不会配置初始化的脚本和配置文件，如果只是玩一玩redis，
这一步不是必须的，但是如果安装在一个生产中的环境中，那么可以使用以下脚本安装
[root@hadoop1 redis]#cd utils
#./install_server 
该脚本会提问少量的问题，并且会做好redis随系统作为daemon进程启动的每件事。
[root@hadoop1 utils]# ./install_server.sh
Welcome to the redis service installer
This script will help you easily set up a running redis server


Please select the redis port for this instance: [6379]
Selecting default: 6379
Please select the redis config file name [/etc/redis/6379.conf]
Selected default - /etc/redis/6379.conf
Please select the redis log file name [/var/log/redis_6379.log]
Selected default - /var/log/redis_6379.log
Please select the data directory for this instance [/var/lib/redis/6379]
Selected default - /var/lib/redis/6379
Please select the redis executable path [/usr/local/bin/redis-server]
s#^port [0-9]{4}$#port 6379#;s#^logfile .+$#logfile /var/log/redis_6379.log#;s#^dir .+$#dir /var/lib/redis/6379#;s#^pidfile .+$#pidfile /var/run/redis_6379.pid#;s#^daemonize no$#daemonize yes#;
Copied /tmp/6379.conf => /etc/init.d/redis_6379
Installing service...
./install_server.sh: line 178: update-rc.d: command not found
 exists, process is already running or crashed
Installation successful!

可以通过/etc/init.d/redis_<portnumber> start/stop来启动和关闭redis服务




