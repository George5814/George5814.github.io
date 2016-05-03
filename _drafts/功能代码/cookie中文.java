􀂃 存中文Cookie
String value = java.net.URLEncoder.encode("中文","utf-8"); 
Cookie cookie4 = new Cookie("chinese_code",value); 
cookie4.setMaxAge(60*60*24*6); 
response.addCookie(cookie4); 
􀂃 取中文Cookie
String value=java.net.URLDecoder.decode(cookie.getValue(), "utf-8");
