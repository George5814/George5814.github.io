/**********-- 利用广播实现IP拨号 --*****************/
获取设置的IP拨号号码
private SharedPreferences sp;
sp = getSharedPreferences("config",MODE_PRIVATE);
sp.getString(SPKEY, DEFAULT_KEY_VALUE)

设置IP拨号的号码
String number = etPhone.getText().toString().trim();
Editor edit = sp.edit();
edit.putString(SPKEY, number);
edit.commit();


//编写类继承广播拨号
package com.skd.ipdail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class OutCallReceiver extends BroadcastReceiver {

	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("OutCallReceiver.onReceive()  外拨电话");
		String resultData = getResultData();
		
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		String ipNum = sp.getString("config", "");
		String phoneNumber=ipNum+resultData;
		setResultData(phoneNumber);
		System.out.println("设置ip拨号号码:"+phoneNumber);
	}

}

//配置广播拨号
AndroidManifest.xml
<!-- 定义了广播接受者，new出一个收音机，设置action就相当于设置了监听的频道 -->
<receiver 
	android:name="com.skd.ipdail.OutCallReceiver"
	>
	<intent-filter >
		<action android:name="android.intent.action.NEW_OUTGOING_CALL" 	 />
	</intent-filter>
</receiver>

添加广播拨号权限
<uses-permission android:name="android.permission.PROCESS_OUTGOING_CALLS"/>


