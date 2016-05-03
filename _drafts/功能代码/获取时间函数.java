//JAVA获得系统时间
public static String getTime(){
SimpleDateFormat sdf = new    SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time=sdf.format(new java.util.Date());
    return time;
}
