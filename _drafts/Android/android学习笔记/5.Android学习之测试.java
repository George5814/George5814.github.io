/***********---测试---***************/
根据测试是否知道源代码：黑盒测试(不知道源代码，只关心过程和结果)、
白盒测试：根据源代码写测试用例	
--根据测试的粒度：
	方法测试：function test
	单元测试：unit test
	集成测试：intergration test
--根据测试的次数：
	冒烟测试：smoke test（Android 猴子	）
		cmd#monkey 5000 测试5000次点击
	压力测试：press test
/***********---测试配置---***************/
1.测试类要继承AndroidTestCase
	public class TestCalcServer extends AndroidTestCase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	public void testAdd(){
		CalcServer calcServer=new CalcServer();
		int result = calcServer.add(3, 4);
		assertEquals(7, result);
		
	
	}

}
2.在mainfest添加配置
//targetPackage 要测试的包名
<instrumentation 
        android:name="android.test.InstrumentationTestRunner"
        android:targetPackage="com.skd.junittest" 
        ></instrumentation>
<application >
	 <uses-library android:name="android.test.runner"/>
</applicaion>


/***********------***************/

/***********------***************/

/***********------***************/

/***********------***************/

