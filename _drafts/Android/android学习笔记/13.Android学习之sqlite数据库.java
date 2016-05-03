/*******--- sqlite数据库 ---******/
sqlite数据库 ==>移动平台的嵌入式数据库

sqlite数据库因为是嵌入式数据库，处理的类型比较少，在底层存储时
都是以string类型存储的，但写sql语句时对长度的限制是提醒程序员的，
而对sqlite数据库存储是没有限制的 

要继承SQLiteOpenHelper
public class PersonSqliteOPenHelper extends SQLiteOpenHelper {
	
	private static String DBName="person.db";
	
	/**
	 * 数据库的构造方法 定义数据库的名称及数据库查询的结果集 数据库版本
	 * @param context
	 */
	public PersonSqliteOPenHelper (Context context) {
		super(context, DBName, null, 1);
	}

	/**
	 * 数据库第一次被创建时调用的方法
	 * @param db 被创建的数据库
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql="create table person (id integer  primary key autoincrement ,name varchar(20) ,phone varchar(11))";
		db.execSQL(sql);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}

/*************sql 操作数据库 *****************/
public class PersonDao {
	
	private PersonSqliteOPenHelper dHelper;
	
	private SQLiteDatabase db;
	
	public PersonDao(Context context){
		dHelper=new PersonSqliteOPenHelper(context);
	}
	
	/**
	 * 添加name和phone
	 * @param name
	 * @param phone
	 */
	public void add(String name,String phone){
		db=dHelper.getWritableDatabase();
		String sql="insert into person(name,phone) values(?,?)";
		db.execSQL(sql, new Object[]{name,phone});
		db.close();
	}
	
	/**
	 * 根据name查找记录是否存在
	 * @param name
	 * @return
	 */
	public boolean find(String name){
		db=dHelper.getReadableDatabase();
		String sql="select * from person where name=?";
		Cursor rawQuery = db.rawQuery(sql, new String[]{name});
		boolean result = rawQuery.moveToNext();
		rawQuery.close();
		db.close();
		return result;
		
	}
	
	/**
	 *  更新name人的手机号
	 * @param name
	 * @param newPhone
	 */
	public  void update(String name,String newPhone){
		db=dHelper.getWritableDatabase();
		String sql="update person set phone = ? where name = ?";
		String[] bindArgs={newPhone,name};
 		db.execSQL(sql, bindArgs);
 		db.close();
		
	}
	
	/**
	 * 根据name删除一条记录
	 * @param name
	 */
	public void delete(String name){
		db=dHelper.getWritableDatabase();
		String sql="delete from person where name = ?";
		String[] bindArgs={name};
		db.execSQL(sql, bindArgs);
		db.close();
	}
	
	/** 
	 * 查询所有记录
	 * @return
	 */
	public List<Person> findAll(){
		db=dHelper.getReadableDatabase();
		String sql="select * from person";
		Cursor rawQuery = db.rawQuery(sql, null);
		Person person=null;
		List<Person> persons=new ArrayList<Person>();
		while (rawQuery.moveToNext()) {
			String name =rawQuery.getString(rawQuery.getColumnIndex("name"));
			String phone =  rawQuery.getString(rawQuery.getColumnIndex("phone"));
			
			person=new Person();
			person.setName(name);
			person.setPhone(phone);
			persons.add(person);
		}
		rawQuery.close();
		db.close();
		return persons;
	}
}


