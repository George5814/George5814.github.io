/**********---  手机外存(SD卡) ---*************/
写到SD卡上时需要权限
android.permission.WRITE_EXTERNAL_STORAGE #写到外部存储上的权限
只需要将写的位置改为/sdcard/即可

判断是否存在SD卡，即判断SD卡是否已经挂载
if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) 
