/******--- 文件访问的权限 ---*********/
私有、可读、可写、可读可写
switch (flag) {
	case R.id.rb_private:
		Log.i(TAG, "flag="+flag);
		mode=Context.MODE_PRIVATE;
		break;
	case R.id.rb_readable:
		Log.i(TAG, "flag="+flag);
		mode=Context.MODE_WORLD_READABLE;
		break;
	case R.id.rb_writeable:
		Log.i(TAG, "flag="+flag);
		mode=Context.MODE_WORLD_WRITEABLE;
		break;
	case R.id.rb_public:
		Log.i(TAG, "flag="+flag);
		mode=Context.MODE_WORLD_READABLE+Context.MODE_WORLD_WRITEABLE;
		break;

	default:
		break;
}	