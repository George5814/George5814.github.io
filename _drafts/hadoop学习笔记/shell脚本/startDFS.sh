#!/bin/sh
#echo "waring"
#read NAME #等待用户输入并把输入的值付给NAME
NAME=$1 #将脚本第一个参数赋给NAME

#引用变量时加上"{}",是个好习惯,利于shell辨别变量边界
if [  -z ${NAME} ]  ;then #默认如果为空，hadoop start
	echo "1.start hadoop on ${HOSTNAME}"
	start-all.sh
elif [ "${NAME}" = "hadoop" ];then #如果NAME中的值为"hadoop"，则继续执行if
	echo "2.start hadoop on ${HOSTNAME}"
	start-all.sh
elif [ "${NAME}" = "hbase" ] ;then #否则就执行else if
	start-all.sh
	#for loop in 1 2 3
	NUM=1
	while [ ${NUM} -lt 4 ]
	do
		echo "start zkServer on hadoop${NUM}"
		ssh -t -p 22 hadoop${NUM} "/usr/local/zk/bin/zkServer.sh start" #向远程机器发送命令
	NUM=$[${NUM}+1]
	done
	ssh -t -p 22 hadoop1 "/usr/local/hbase/bin/start-hbase.sh" #向远程机器发送命令
else
echo "error"

fi #结束if命令
