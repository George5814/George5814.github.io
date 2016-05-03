/***********-- 屏幕转换 --*************/
只会横向显示
 android:screenOrientation="landscape"

 只会竖向显示
  android:screenOrientation="portrait"

 在屏幕横向或竖向变化时，关掉软键盘
  android:configChanges="orientation|keyboardHidden"

  横竖屏切换时，保证activity不被销毁，重新创建。比如视频播放等
  4.0之后的系统
  android:configChanges="orientation|keyboardHidden|screenSize
  4.0之前的系统
  android:configChanges="orientation|keyboardHidden"

  /*******-- 代码中判断屏幕转换 --**********/
  Point outSize=new Point();;
getWindowManager().getDefaultDisplay().getSize(outSize);
int x=outSize.x;
int y=outSize.y;

判断x和y的大小就可以判断是横屏还是竖屏

