### 1. 问题:Launch error: Failed to connect to remote VM
	解决办法： 
		修改端口号，有可能端口号被占用。window -> preferences -> android -> DDMS 右侧端口浩修改下
	另一种方法:
		可能是jdk切换为1.6 

### 2. Adb connection Error:远程主机强迫关闭了一个现有的连接。 
	原因：端口可能被占用，豌豆荚、qq、360手机助手等的adb服务抢占端口，并可能强制kill掉调试程序的adb。
		也可能是多个开发工具中的adb，服务冲突原因。
	解决方法：
		如果软件自带关闭adb服务功能(如qq中的而将安卓连接提醒功能关掉[取消打钩])，或者将暂时用不到的adb.exe 程序重命名，使得该服务无法启动。
	