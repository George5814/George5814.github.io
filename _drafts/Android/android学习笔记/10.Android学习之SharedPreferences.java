/*************---SharedPreferences ---***********/
SharedPreferencesµÄ´æÈ¡
@Override
public boolean saveUserInfo(Context context, String username, String pwd) {
	SharedPreferences sharedPreferences = context.getSharedPreferences(configFile, Context.MODE_PRIVATE);
	Editor editor=sharedPreferences.edit();
	editor.putString("username", username);
	editor.putString("pwd", pwd);
	editor.commit();
	Log.i(TAG, "saveUserInfo£ºusername="+username+"  pwd="+pwd);
	return true;
}

@Override
public Map<String, String> getUserInfo2(Context context) {
	SharedPreferences sharedPreferences = context.getSharedPreferences(configFile,Context.MODE_PRIVATE);
	Map<String, String> map=new HashMap<String, String>();
	String username=sharedPreferences.getString("username", "");
	String password=sharedPreferences.getString("pwd", "");
	map.put("username", username);
	map.put("password", password);
	Log.i(TAG, "getUserInfo2: username="+username+"  pwd="+password);
	
	return map;
}