/*********** redis transaction命令操作 **********/
1.
DISCARD -
summary: Discard all commands issued after MULTI
since: 2.0.0

EXEC -
summary: Execute all commands issued after MULTI
since: 1.2.0

MULTI -
summary: Mark the start of a transaction block
since: 1.2.0

UNWATCH -
summary: Forget about all watched keys
since: 2.2.0

WATCH key [key ...]
summary: Watch the given keys to determine execution of the MULTI/EXEC block
since: 2.2.0

/************** redis connection命令操作 ***********/
1.验证服务器密码
AUTH password
summary: Authenticate to the server
since: 1.0.0

2.显示指定的message信息
ECHO message
summary: Echo the given string
since: 1.0.0

3.判断服务是否ping通
PING -
summary: Ping the server
since: 1.0.0
4.关闭连接并退出
QUIT -
summary: Close the connection
since: 1.0.0

5.选择redis下的数据库
SELECT index
summary: Change the selected database for the current connection
since: 1.0.0


