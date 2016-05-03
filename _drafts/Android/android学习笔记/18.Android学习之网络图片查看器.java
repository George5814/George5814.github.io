/************-- 网络图片查看器 ---*************/

需要添加用户权限 android.permission.INTERNET
android.os.NetworkOnMainThreadException 2.3与4.x的网络访问区别
在4.x中网络操作不允许在主线程中

ANR  主线程（UI线程）
如果在主线程中等待过久的时间，应用程序就会无响应
为了避免，要在子线程中运行

只有主线程才能更新view对象

1.子线程利用handler发送一条消息。
消息被放在主线程的消息队列中

2.主线程里的looper消息的轮询器
3.如果轮询器发现了新的消息，调用handlermessage的方法处理消息

/***********-- Android的消息机制 --*****************/
在主线程中要是实现Handler(),并重写 handleMessage(Message msg)方法
/*--Toast也是主线程的UI，因此也只能是主线程调用，子线程将消息发送给主线程--*/
//主线程创建消息处理群
private  Handler handler=new Handler(){
	public void handleMessage(Message msg) {
		if (CHANGE_UI == msg.what) {
			imgShow.setImageBitmap((Bitmap)msg.obj);
		}else if (TOASTMSG == msg.what) {
			Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
		}
		
	};
};

在子线程中指定msg.what和msg.obj 的数据
/************--  匿名创建访问网络的子线程 --***********/
new Thread(){
	public void run() {
		
		URL url;
		try {
			url = new URL(path);
		
			//根据URl发送get请求
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			//conn.setReadTimeout(5000);//已经读到的数据
			conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/4.0)");
			
			int responseCode = conn.getResponseCode();
			if (responseCode==200) {
				InputStream is = conn.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(is);
			//	imgShow.setImageBitmap(bitmap);
				//TODO 告诉主线程要修改下UI，值为bitmap
				Message msg=new Message();
				msg.what=CHANGE_UI;
				msg.obj=bitmap;
				handler.sendMessage(msg);
			}else {
				String error="显示图片失败";
				Message msg=new Message();
				msg.what=TOASTMSG;
				msg.obj=error;
				handler.sendMessage(msg);
			}
		}catch (Exception e) {
			String error="获取图片失败";
			Message msg=new Message();
			msg.what=TOASTMSG;
			msg.obj=error;
			handler.sendMessage(msg);
			e.printStackTrace();
		}
	};
	
}.start();
	