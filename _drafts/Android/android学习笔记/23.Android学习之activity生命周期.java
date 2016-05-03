/***********-- activity的生命周期 --***********/
整个生命周期的方法是系统自动调用的
@Override
protected void onCreate(Bundle savedInstanceState) {
	//完整生命周期的开始，可以添加预读写功能
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
}

@Override
protected void onStart() {
	//可视生命周期的开始，UI界面可见时调用，可用于刷新界面
	//比如，判断网络是否连接可用时，可以在这里判断
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

}

@Override
protected void onResume() {
	//前台生命周期的开始，获得焦点时调用

}

@Override
protected void  onPause()  {
	//前台生命周期的结束，失去焦点时调用

}
@Override
protected void onStop() {
	//可视生命周期的结束，UI界面不可见时调用

}
@Override
protected void onDestroy() {
	//完整生命周期的结束，可以添加保存功能

}

/*********-- 弹出对话框 --**************/
配置xml配置文件
 <activity
	android:theme="@android:style/Theme.Dialog"
	android:name="com.skd.activity.ThirdActivity"
	>
</activity>

在类中调用该配置项对应的类


/********-- 弹出提示框 --**********/
//创建对话框的构造器
AlertDialog.Builder  builder=new Builder(MainActivity.this);
builder.setTitle("提示:");
builder.setMessage("当前网络不可用");

builder.setPositiveButton("确定", new OnClickListener() {
	@Override
	public void onClick(DialogInterface dialog, int which) {
		Intent intent = new Intent();
		intent.setClassName("com.android.settings", "com.android.settings.HWSettings");
		startActivity(intent);
	}
});
builder.create().show();


