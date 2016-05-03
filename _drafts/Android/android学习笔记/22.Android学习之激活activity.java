/************-- 跳转到另一个activity --*************/
先新建类继承activity，OtherScreenActivity
新建布局文件，activity_two.xml
在配置文件AndroidManifest.xml中注册该activity，
通过Intent 实现activity之间的跳转
Intent intent=new Intent()
intent.setCLassName(this,"包名.类名");
startActivity(intent);

activity中的 配置Android图标的设置，其他activity一般不设置
<intent-filter>
	<action android:name="android.intent.action.MAIN" />
	<category android:name="android.intent.category.LAUNCHER" />
</intent-filter>

修改application的名字和图标，activity的名字和图标
当存在activity的名字和图标时，在桌面上显示的是activity的名字和图标而不是
application的图标和名字，每个activity的名字和图标可以自定义设置
在Androidmanifest.xml文件中

/*********************************************/
2.显示意图激活系统应用
Intent intent = new Intent();
intent.setClassName("com.android.gallery3d", "com.android.gallery3d.app.Gallery");
startActivity(intent);
可以通过logcat中打印的activityManager的cmp后的包名和类名调用
cmp=com.android.gallery3d/.app.Gallery

/******************** 检查用户的网络 *************/
ConnectivityManager  systemService = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
NetworkInfo networkInfo = systemService.getActiveNetworkInfo();
if (networkInfo!=null&&networkInfo.isConnected()) {
	Toast.makeText(this, "网络可用", Toast.LENGTH_SHORT).show();
}else {
	Toast.makeText(this, "网络不可用，跳转到网络设置", Toast.LENGTH_SHORT).show();
	Intent intent = new Intent();
	intent.setClassName("com.android.settings", "com.android.settings.HWSettings");
	startActivity(intent);
}

/***********-- 隐式意图	激活activity --****************/
隐式意图调用浏览器
public void btClick(View view){
	Intent intent = new Intent();
	intent.setAction(Intent.ACTION_VIEW);
	intent.setData(Uri.parse("http://3g.qq.com"));
	startActivity(intent);
	
}

/***************  activity的隐式配置 ********************/
意图过滤器，动作关联
<intent-filter>
	<action android:name="android.intent.action.MAIN" />
	<category android:name="android.intent.category.LAUNCHER" />
</intent-filter>

同一个应用程序中，自己激活自己的东西，推荐使用显式意图，效率高

激活别人的应用或者不同应用间，或者希望自己的某个页面被别人激活，推荐使用隐式意图
1.查看系统里的所有activity，看是否有满足条件的activity
2.有：判断有几个，一个就之间启动，多个就列表形式选择
3.如果没有，应用程序异常终止，activity not found Exception

