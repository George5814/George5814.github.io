/************--- ListView ---************/
在页面的布局文件中定义listview，并在后台获取到
获取到service中的数据后
PersonDao dao=new PersonDao(this);
persons = dao.findAll();	//获取到数据列表
listView=(ListView)findViewById(R.id.lv_show); //获取到listview
listView.setAdapter(new MyAdapter());  //继承BaseAdapter，用适配器将数据放在listview中
listView.setOnItemClickListener(new onItemClick()); //设置listview的item点击事件

private class MyAdapter extends BaseAdapter {
	//获取取到数据的总数
	@Override
	public int getCount() {
		return persons.size();
	}

	
	@Override
	public Object getItem(int position) {
		Log.i(TAG, "设置view对象，位置："+position);
		TextView tView=(TextView)listView.getItemAtPosition(position);
		return tView;
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}
	//设置每个item的view并返回给listview的适配器
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(TAG, "返回view对象，位置："+position);
		Person person=persons.get(position);
		
		View view=View.inflate(MainActivity.this, R.layout.list_view, null);
		
		TextView tView=(TextView)view.findViewById(R.id.tv_id);
		//如果括号中的内容是int类型的，系统会当做是R中的id去查找，所以要把int类型转为String
		tView.setText(""+person.getId());
		
		tView=(TextView)view.findViewById(R.id.tv_name);
		tView.setText("姓名:"+person.getName());
		
		tView=(TextView)view.findViewById(R.id.tv_phone);
		tView.setText("手机号:"+person.getPhone());
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StringBuffer sBuffer=new StringBuffer(); 
				TextView view=(TextView)v.findViewById(R.id.tv_name);
				sBuffer.append(view.getText()+"\n");
				view=(TextView)v.findViewById(R.id.tv_phone);
				sBuffer.append(view.getText()+"\n");
				Toast.makeText(MainActivity.this, sBuffer.toString(), Toast.LENGTH_SHORT).show();	
				
			}
		});
		
		
		
		return view;
	}
	
}