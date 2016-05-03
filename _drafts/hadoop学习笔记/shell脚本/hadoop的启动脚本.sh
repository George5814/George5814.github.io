########startDFS.sh############
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
                ssh -t -p 22 hadoop${NUM} "/usr/local/zk/bin/zkServer.sh start" #向远程
机器发送命令
        NUM=$[${NUM}+1]
        done
        ssh -t -p 22 hadoop1 "/usr/local/hbase/bin/start-hbase.sh" #向远程机器发送命令
else
echo "error"

fi #结束if命令


########stopDFS.sh############
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
                ssh -t -p 22 hadoop${NUM} "/usr/local/zk/bin/zkServer.sh stop" #向远程>机器发送命令
                NUM=$[${NUM}+1]
        done
        echo "shutdwon hadoop cluster"
        stop-all.sh
else
        echo "error"

fi #结束if命令

########查看hadoop的状态###########
#!/bin/sh
#查看hadoop的运行状态
function hadoopState(){
        COUNT=1
        while [ ${COUNT} -le ${NUM} ]
        do
                echo "查看hadoop${COUNT}上hadoop的运行状态"
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
echo "=====查看结束===="

                                               