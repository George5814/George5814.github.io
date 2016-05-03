#!/bin/sh
#查看hadoop的运行状态
function hadoopState(){
	COUNT=1
	while [ ${COUNT} -le ${NUM} ]
	do
		echo "**********查看hadoop${COUNT}上hadoop的运行状态********"
		#连接远程主机查看运行状态
		ssh -t -p 22 hadoop${COUNT} "jps"
		COUNT=$((${COUNT}+1))
	done
}

echo "=====查看hadoop的运行状态===="
NUM=$1 #hadoop 集群的数量
echo "您输入的的NUM为${NUM}"
if test !${NUM};then
	NUM=3
	hadoopState
else
	hadoopState
fi
echo "***********=====查看结束====***************"


