/*************** ---xml文件--- ********/
public  void backupSms(View view) throws IOException{
StringBuilder sBuilder=new StringBuilder();
sBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
sBuilder.append("<smss>");
for (SmsInfo smsInfo : smsInfos) {
	sBuilder.append("<sms>");
	
	sBuilder.append("<address>");
	sBuilder.append(smsInfo.getAddress());
	sBuilder.append("</address>");
	
	sBuilder.append("<type>");
	sBuilder.append(smsInfo.getType());
	sBuilder.append("</type>");
	
	sBuilder.append("<body>");
	sBuilder.append(smsInfo.getBody());
	sBuilder.append("</body>");
	
	sBuilder.append("<date>");
	sBuilder.append(smsInfo.getDate());
	sBuilder.append("</date>");
	
	sBuilder.append("</sms>");
}
sBuilder.append("</smss>");
File file = new File(Environment.getExternalStorageDirectory(), "backupSms.xml");

FileOutputStream out=new FileOutputStream(file);
out.write((sBuilder.toString()).getBytes());
out.flush();
out.close();
Log.i(TAG,sBuilder.toString());
Toast.makeText(this, "短信保存成功",Toast.LENGTH_SHORT).show();
}

android中的xml序列化器

public  void backupSms2(View view){
	try {
	XmlSerializer xmlSerializer = Xml.newSerializer();
	File file=new File(Environment.getExternalStorageDirectory(),"backup2.xml");
		FileOutputStream out=new FileOutputStream(file);
		xmlSerializer.setOutput(out, "utf-8");
		xmlSerializer.startDocument("utf-8", true);
		xmlSerializer.startTag(null, "smss");
		for (SmsInfo smsInfo : smsInfos) {
			xmlSerializer.startTag(null, "sms");
			xmlSerializer.attribute(null, "id",smsInfo.getId() );
				xmlSerializer.startTag(null, "body");
				xmlSerializer.text(smsInfo.getBody());
				xmlSerializer.endTag(null, "body");
				
				xmlSerializer.startTag(null, "address");
				xmlSerializer.text(smsInfo.getAddress());
				xmlSerializer.endTag(null, "address");
				
				xmlSerializer.startTag(null, "date");
				xmlSerializer.text(smsInfo.getDate()+"");
				xmlSerializer.endTag(null, "date");
				
				xmlSerializer.startTag(null, "type");
				xmlSerializer.text(smsInfo.getType()+"");
				xmlSerializer.endTag(null, "type");
			xmlSerializer.endTag(null, "sms");
		}
		xmlSerializer.endTag(null, "smss");
		xmlSerializer.endDocument();
		out.close();
		Toast.makeText(this, "短信备份成功",Toast.LENGTH_SHORT).show();
	}catch (IOException e) {
		Toast.makeText(this, "短信备份失败",Toast.LENGTH_SHORT).show();
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}