/****************************************/
android 开发步骤：
1.在res/layout/activity_main.xml写界面和修改默认的空间参数
2.在mainactivity中的onCreate方法中添加空间添加点击事件
3.修改应用所需要的权限
/****************************************/
创建点击事件的四种方式：
	1."匿名内部类"
	2."命名内部类"
	3.让调用的类，如mainactivity实现OnClickListener接口(常用)
	button.onClickListenre(this);
	然后在onClicklistener（view v）中判断是哪个控件的点击事件
	4.在布局文件中绑定点击方法，方法名可以自定义

/***********---拨号器调用方法：---**************/

private void callPhone(){
		String phoneNumber = et_num_local.getText().toString().trim();
		if (TextUtils.isEmpty(phoneNumber)) {
			/**
			 * 吐司，类似于js的alert
			 */
			Toast toast = Toast.makeText(MainActivity.this, "号码不能为空", Toast.LENGTH_LONG);
			/**
			 * 显示出来
			 */
			toast.show();
		}
		/**
		 * 意图，即想要做什么事
		 */
		Intent intent=new Intent();
		intent.setAction(intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+phoneNumber));
		startActivity(intent);
	}

/************---短信发送器调用方法：---************/

String phone = phoneNum.getText().toString().trim();
		String msgContent = content.getText().toString().trim();
		
		SmsManager smsManager=SmsManager.getDefault();
		//sentIntent 代表短信是否发送出去
		//deliveryIntent 代表短信是否送达
		smsManager.sendTextMessage(phone, null, msgContent, null, null);
}

/*********---弹窗显示---********/

/**
 * 吐司，类似于js的alert
 */
Toast toast = Toast.makeText(MainActivity.this, "号码不能为空", Toast.LENGTH_LONG);
/**
 * 显示出来
 */
toast.show();

/****************************************/

/****************************************/

/****************************************/

/****************************************/

/****************************************/

/****************************************/

