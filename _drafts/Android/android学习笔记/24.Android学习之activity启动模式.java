/*********-- activity的启动模式 --***********/
activity：与用户交互的界面 UI
service ：后台长期运行的组件
receiver: 接收系统自带的广播，发送接收自定义的广播事件
content provider：把一个应用程序私有的数据库，暴露给别的应用


应用程序 与 进程
应用程序是一组组件的结合
进程 运行这些组件的载体

任务栈：task stack ->  只针对activity 而言
一个重要作用：用来维护用户的界面

/*****-- activity的启动模式 --********/
 设置activity的启动模式
任务栈顶之下不能连续存在多个同一activity的实例
 android:launchMode="singleTop"

任务栈中每个界面只存在一例，当添加activity界面到taskstack中时，如果栈
中存在该activity实例，就将该实例上面的所有activity实例都清除出栈，来显示，
如果没有就新创建
android:launchMode="singleTask"

每个activity创建一个任务栈，每个任务栈中只有一个activity的实例，如果存在当前点击的activity的任务栈，就将
该栈移到最前面的位置，如果不存在就创建该activity的任务栈
极端：在整个手机系统中只会存在同一个activity实例，不管是几个外部应用程序调用
android:launchMode="singleInstance"

不管栈中是否存在该activity，每次点击都在栈顶生成该aactivity的实例，
即任务栈中可以连续或不连续的存在多个同一activity的实例
android:launchMode="standard"











