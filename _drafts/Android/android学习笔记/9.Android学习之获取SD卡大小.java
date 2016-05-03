/**********---  手机外存(SD卡)大小 ---*************/

tvShow=(TextView)findViewById(R.id.tv_show);
File SDCard=Environment.getExternalStorageDirectory();
StatFs fs=new StatFs(SDCard.getPath());

long blockSize=fs.getBlockSizeLong();

long totleAcount=fs.getBlockCountLong();


long availAcount = fs.getAvailableBlocksLong();

long totleSize =totleAcount*blockSize;
long availSize =availAcount*blockSize;

String totleStr=Formatter.formatFileSize(this,totleSize);
String availStr=Formatter.formatFileSize(this,availSize);
File dataDirectory = Environment.getDataDirectory();
StatFs dataFs=new StatFs(dataDirectory.getPath());

long useBlockSize =dataFs.getBlockSizeLong();
long useTotleAcount = dataFs.getBlockCountLong();
long useAvailAcount = dataFs.getAvailableBlocksLong();
long useTotleSize =useTotleAcount*useBlockSize;
long useAvailSize =useAvailAcount*useBlockSize;

String useTotleStr=Formatter.formatFileSize(this,useTotleSize);
String useAvailStr=Formatter.formatFileSize(this,useAvailSize);


tvShow.setText("总空间："+totleStr+"\n可用空间："+availStr+"\nROM总空间："+useTotleStr+"\nROM可用空间："+useAvailStr);
