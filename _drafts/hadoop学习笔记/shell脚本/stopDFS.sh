#!/bin/sh
#echo "waring"
#read NAME #等待用户输入并把输入的值付给NAME
NAME=$1 #将脚本第一个参数赋给NAME
#引用变量时加上"{}",是个好习惯,利于shell辨别变量边界
if [ -z ${NAME} ] ; then #默认关闭hadoop
	stop-all.sh
elif [ ${NAME} = "hadoop" ]; then #如果NAME中的值为"jingzhongzhi"，则继续执行if
	echo "shutdown hadoop cluster"
	stop-all.sh
elif [ ${NAME} = "hbase" ] ; then #否则就执行else if
	echo "shutdown hbase on hadoop1"
	ssh -t -p 22 hadoop1 "/usr/local/hbase/bin/stop-hbase.sh" #向远程机器发送命令
	NUM=1
	while [ ${NUM} -lt 4 ]
	do
		echo "shutdown hadoop${NUM}'s zkServer"
		ssh -t -p 22 hadoop${NUM} "/usr/local/zk/bin/zkServer.sh stop" #向远程机器发送命令
		NUM=$[${NUM}+1]
	done
	echo "shutdwon hadoop cluster"
	stop-all.sh
else
	echo "error"

fi #结束if命令
