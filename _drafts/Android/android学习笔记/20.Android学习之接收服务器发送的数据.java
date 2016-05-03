/************-- 接收服务器发送的数据，并转为字符串 --*************/
public class StreamTools {
	public static String readInputStream(InputStream inputStream) throws IOException {
		StringBuffer sBuffer=new StringBuffer(); 
		int length=0;
		ByteArrayOutputStream bOutputStream=new ByteArrayOutputStream();
		byte[] buffer=new byte[1024*5];
		while ((length=inputStream.read(buffer))!=-1) {
			bOutputStream.write(buffer, 0, length);	
		}
		inputStream.close();
		byte[] result=bOutputStream.toByteArray();
		String res=new String(result);
		if (res.contains("utf-8")) {
			return res;
		}else if (res.contains("GB2312")||res.contains("gb2312")) {
			return new String(result, "GB2312");
		}
		return res;
	}  
}

