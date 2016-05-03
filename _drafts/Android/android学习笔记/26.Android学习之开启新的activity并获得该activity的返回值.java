/********** -- 开启新的activity并获得返回结果 --****************/
mainactivity页面
选择联系人
public void selectPeople(View view){
	//Toast.makeText(this, "selectPeople", Toast.LENGTH_LONG).show();
	Intent intent=new Intent(this, ContactSelectACtivity.class);
	//这是关键，要使用可有返回值的方法
	startActivityForResult(intent, requestCode);
	//startActivity(intent);
}

//重写 onActivityResult方法，将获取到的内容放入控件中
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (data!=null) {
		String contact = data.getStringExtra("contact");
		etContact.setText(contact+etContact.getText().toString());
	}
}

选择联系人界面
listView控件通过添加适配器lvContacts.setAdapter(new ContactAdapter());
获取到service中的数据，
设置item的点击事件，并实现该接口的方法
lvContacts.setOnItemClickListener(new ItemListener());
private class ItemListener implements OnItemClickListener{

	//该方法就可以获取到listView的点击控件
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		System.out.println("ContactSelectACtivity.ItemListener.onItemClick()");
		ContactInfo contactInfo = contactInfos.get(position);
		StringBuffer sBuffer=new StringBuffer();
		sBuffer.append(contactInfo.getName());
		sBuffer.append("<");
		sBuffer.append(contactInfo.getPhone());
		sBuffer.append(">,");
		
		//生成返回的内容，并使用setResult方法写入返回值中，
		//此处intent使用的必须是putExtra方法
		//finish将当前点击的界面关闭，返回到之前的页面，并调用之前页面的
		//onActivityResult方法，完成返回值的获取
		Intent data=new Intent();
		data.putExtra("contact", sBuffer.toString());
		setResult(0, data);
		finish();
	}
	
}

/****************-- 请求码和结果码的作用 --*************/
requestCode 主要区分请求来自哪里
在startActivityForResult方法中设置requestCode的值
在 onActivityResult中判断
if (requestCode==1) {
	//一个startActivityForResult(intent, 1);
}else if (requestCode==2) {
	//另一个startActivityForResult(intent, 2);
}

resultCode 在点击页面设置	
Intent data=new Intent();
data.putExtra("contact", sBuffer.toString());
setResult(resultCode, data);
finish();
在 onActivityResult中判断
if (resultCode==1) {
}else if (resultCode==2) {
}

