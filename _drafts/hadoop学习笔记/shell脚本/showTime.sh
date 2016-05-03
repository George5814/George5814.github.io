#!/bin/sh
#显示时间
flag=1
while [ ${flag} -le 3 ]
do 
	ssh -t -p 22 hadoop${flag} "date"
	flag=$((${flag}+1))
done 
