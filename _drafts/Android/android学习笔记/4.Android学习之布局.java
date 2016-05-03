/***********---在布局文件中定义空间的位置和属性---**************/
<TextView
        android:id="@+id/tv_hello"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/hello_world" />
    
    <Button 
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_toRightOf="@id/tv_hello"
        android:text="按钮" />
/**********************************/
 android:textColor="#4432dece" 其中的"44"代表字体的透明度，其他的是颜色

/*********---px和dp的区别----*****************/
dp == dip 单位像素密度
dp是指的像素密度的比值，能保证在不同分辨率的手机上UI同一
px仅是指像素，在不同屏幕上大小不变，会造成UI变乱
"sp":文字用sp，根据分辨率大小缩放，不会产生锯齿。也可以用dp

/************---线性布局---****************/
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" >
	默认为竖直方向排列

线性布局中可以嵌套相对布局和空间同时存在等
/**********---表格布局---******************/
<TableLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >
    
    <TableRow 
		android:layout_width="wrap_content"
		android:layout_height="wrap_content" >
        <TextView 
			android:layout_weight="1"
			android:layout_height="wrap_content"
			android:text="哈哈"            
            />
        <TextView 
			android:layout_weight="2"
			android:layout_height="wrap_content"
			android:text="嘻嘻"            
            />
    </TableRow>
	<TableRow 
		android:layout_width="wrap_content"
		android:layout_height="wrap_content" >
        <TextView 
			android:layout_weight="2"
			android:layout_height="wrap_content"
			android:text="哈哈"            
            />
        <TextView 
			android:layout_weight="1"
			android:layout_height="wrap_content"
			android:text="嘻嘻"            
            />
    </TableRow>
    <TableRow 
		android:layout_width="wrap_content"
		android:layout_height="wrap_content" >
        <TextView 
			android:layout_weight="1"
			android:layout_height="wrap_content"
			android:text="哈哈"            
            />
        <TextView 
			android:layout_weight="1"
			android:layout_height="wrap_content"
			android:text="嘻嘻"            
            />
        
         <TextView 
			android:layout_weight="1"
			android:layout_height="wrap_content"
			android:text="嘻嘻"            
            />
    </TableRow>
</TableLayout>
适合做办公类的属性和值显示
android:layout_weight="1" 几乎相当于多个控件在每行上的宽度比


/**************---绝对布局---**************/
在游戏中用的相对多一些，其他地方不推荐使用
/**********---帧布局---****************/
播放器常用帧布局

/**********************************/

