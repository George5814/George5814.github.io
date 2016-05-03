#!/bin/sh
#检查Hadoop集群上的磁盘使用量

function diskState(){
	COUNT=1
	while [ ${COUNT} -le ${NUM}  ]
	do 
		echo "********检查Hadoop${COUNT}上的磁盘使用量**********"
		#获取远程机器上的磁盘状态
		ssh -t -p 22 hadoop${COUNT} "df -h"
		COUNT=$((${COUNT}+1))
	done
}


NUM=$1 #hadoop 集群的数量
echo "Hadoop集群数量${NUM}"
if [ "${NUM}" -le "0" ] ;then 
	echo "警告:$0 num "
else
	diskState
	echo "********======磁盘检查结束======***********"
fi


