/*************--- contentProvider ----*************/
1.建立继承 ContentProvider 的类
2.AndroidManifest.xml配置文件中写
application下
<provider 
	android:name="com.skd.sqlite.db.PersonDBPricider"  //操作的类
	android:authorities="com.skd.sqlite.db.PersonProvider"> //暴露的接口
</provider>
