#!/bin/sh
#删除文件的方法
function deleteFile(){
	COUNT=1
	while [ ${COUNT} -le ${NUM}  ]
	do 
		echo "********删除Hadoop${COUNT}上的临时文件和日志记录**********"
		#获取远程机器上的磁盘状态
		ssh -t -p 22 hadoop${COUNT} "rm -rf /usr/local/hadoop/tmp/*;rm -rf /tmp/*"
		#ssh -t -p 22 hadoop${COUNT} "rm -rf /tmp/*"
		COUNT=$((${COUNT}+1))
	done
	
}
DEL=$1
NUM=3
if [ "${DEL}" -gt "0" ] ;then

	deleteFile
	echo "********======删除临时文件结束======***********"
elif test !${DEL};then
	echo "警告：$0 num 1  #删除临时文件\n"
fi

