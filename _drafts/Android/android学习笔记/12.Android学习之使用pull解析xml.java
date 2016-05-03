/*********--pull解析器---******************/
pull解析xml文件
public static List<WeatherInfo> getWeatherInfos(InputStream inStream){
	XmlPullParser xmlPullParser = Xml.newPullParser();
	List<WeatherInfo> wInfos=null;
	WeatherInfo info=null;
	try {
		xmlPullParser.setInput(inStream, "utf-8");
		int type=xmlPullParser.getEventType();
		while (type!=XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("infos".equals(xmlPullParser.getName())) {
					wInfos=new ArrayList<WeatherInfo>();
				}else if ("city".equals(xmlPullParser.getName())) {
					info=new WeatherInfo();
					info.setId(Integer.parseInt(xmlPullParser.getAttributeValue(0)));
				}else if ("wind".equals(xmlPullParser.getName())) {
					info.setWind(xmlPullParser.nextText());
				}else if ("weather".equals(xmlPullParser.getName())) {
					info.setWeather(xmlPullParser.nextText());
				}else if ("temp".equals(xmlPullParser.getName())) {
					info.setTemp(xmlPullParser.nextText());
				}else if ("pm".equals(xmlPullParser.getName())) {
					info.setPM(Integer.parseInt(xmlPullParser.nextText()));
				}
				
				break;
			case XmlPullParser.END_TAG :
				if ("city".equals(xmlPullParser.getName())) {
					wInfos.add(info);
					info=null;
				}
				break;

			default:
				break;
			}
			type=xmlPullParser.next();
		}
	} catch (Exception e) {
		wInfos=null;
		e.printStackTrace();
	} 
	return wInfos;
	
}

类加载器加载配置文件
/*** 	MainActivity.class.getClassLoader().getResourceAsStream("weatherinfos.xml") ***/

tvShow = (TextView) findViewById(R.id.tv_show);

List<WeatherInfo> weatherInfos = Weather
		.getWeatherInfos(MainActivity.class.getClassLoader()
				.getResourceAsStream("weatherinfos.xml"));
StringBuffer sBuffer=new StringBuffer();
for (WeatherInfo weatherInfo : weatherInfos) {
	sBuffer.append(weatherInfo.toString());
	sBuffer.append("\n");
}
tvShow.setText(sBuffer.toString());


/****************weatherinfos.xml 文件**********************/
<?xml version="1.0" encoding="utf-8"?>
<infos>
    <city id="1">
        <name>北京</name>
        <wind>风速3-5级</wind>
        <weather>今日多云转晴</weather>
        <temp>2-3度</temp>
        <pm>300</pm>
    </city>
    <city id="2">
        <name>上海</name>
        <wind>风速3-5级</wind>
        <weather>今日晴空万里</weather>
        <temp>12-15度</temp>
        <pm>20</pm>
    </city>
    <city id="3">
        <name>广州</name>
        <wind>风速6-9级</wind>
        <weather>今日有暴雨</weather>
        <temp>22-23度</temp>
        <pm>100</pm>
    </city>
    <city id="4">
        <name>济南</name>
        <wind>风速3-5级</wind>
        <weather>今日多云</weather>
        <temp>-2-3度</temp>
        <pm>200</pm>
    </city>
    <city id="5">
        <name>青岛</name>
        <wind>风速3-5级</wind>
        <weather>今日晴</weather>
        <temp>2-5度</temp>
        <pm>100</pm>
    </city>
</infos>
