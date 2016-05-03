/**********--- 常见适配器 ---**************/
arrayAdapter 适配器

private static String[] names={"功能1","功能2","功能3","功能4","功能5","功能2","功能3","功能4","功能5","功能2","功能3","功能4","功能5"};

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	lvShow=(ListView)findViewById(R.id.lv_show);
	lvShow.setAdapter(new ArrayAdapter<String>(this, R.layout.list_view, R.id.tv_item, names));
}

list_view.xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
	android:orientation="horizontal"
	>
	<ImageView 
	    android:layout_width="wrap_content"
	    android:layout_height="wrap_content"
	    android:src="@drawable/ic_launcher"
	    />
    
    
    <TextView
        android:id="@+id/tv_item"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
	></TextView>
</LinearLayout>

sampleAdapter 适配器
private ListView lvItems;
	
private static List<Map<String, Object>> data=new ArrayList<Map<String,Object>>();

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	Map<String, Object> map1=new HashMap<String, Object>();
	map1.put("name", "我是第一个功能");
	map1.put("iconid", R.drawable.ic_menu_allfriends);
	Map<String, Object> map2=new HashMap<String, Object>();
	map2.put("name", "我是第三个功能");
	map2.put("iconid", R.drawable.ic_menu_agenda);
	Map<String, Object> map3=new HashMap<String, Object>();
	map3.put("name", "我是第四个功能");
	map3.put("iconid", R.drawable.ic_menu_always_landscape_portrait);
	
	data.add(map1);
	data.add(map2);
	data.add(map3);
	data.add(map4);
	data.add(map5);
	data.add(map6);
	String[] from={"iconid","name"};//map中对应的kay
	int[] to={R.id.imgv_item,R.id.tv_item};//map参数绑定的布局文件中的控件对象
	lvItems=(ListView)findViewById(R.id.lv_items);
	lvItems.setAdapter(new SimpleAdapter(this, data, R.layout.list_item, from, to));
}


