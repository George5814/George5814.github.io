/**************---Dao---***********************/
	
	//分页查询数据
	public List<Msg> getMsgPaging(int startRow,int endRow) throws SQLException{
		List<Msg> msgs=new ArrayList<Msg>();
		db.connection(this.conn);
		String sql="SELECT * FROM (  " +
				"SELECT A.*, ROWNUM RN  FROM (SELECT * FROM mymessage order by time desc) A   " +
				"WHERE ROWNUM <=?) WHERE RN >=? ";
		Object obj[]={endRow,startRow};
		ResultSet rSet=db.executeQuery(sql, obj);
		while (rSet.next()) {
			Msg msg=new Msg();
			msg.setId(rSet.getInt("id"));
			msg.setName(rSet.getString("name"));
			msg.setContext(rSet.getString("context"));
			msg.setTime(rSet.getString("time"));
			msg.setTitle(rSet.getString("title"));
			msgs.add(msg);
		}
		
		return msgs;
	}


/*****************---service方法---**************************/
	//分页查询
	public List<Msg> getMsgPaging(int pageNow){
		List<Msg> msgs=new ArrayList<Msg>();
	
		try {
			 int pageSize=page.pageSize;
			//判断获得的页码是否比最大页码小
			this.conn=db.connection();
			MsgDao md=new  MsgDao(this.conn);
			int startRow=pageSize*(pageNow-1)+1;
			int endRow=pageSize*pageNow;
			System.out.println("startRow:"+startRow);
			System.out.println("endRow:"+endRow);
			msgs=md.getMsgPaging(startRow, endRow);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (db!=null) {
				try {
					db.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return msgs;
	}

	//显示分页栏
	public Page showPageNum(int  pageNow){
		//分页栏显示的页码数
		int showPageNum=7;
		page.setPageSize(8);
		page.setShowPageNum(showPageNum);
		page.setPageCount(this.getPageCount());
		if (pageNow<=1) {
			page.setPageNow(1);
		}else if (pageNow>=page.getPageCount()) {
			page.setPageNow(page.getPageCount());
		}else {
			page.setPageNow(pageNow);
		}
		System.out.println("page.getPageCount()="+page.getPageCount());
		if (page.getPageCount()>page.getShowPageNum()&&page.getPageNow()<page.getPageCount()-page.getShowPageNum()/2) {
			if (page.getPageNow()<=page.getShowPageNum()/2) {
				page.setStart(1);
				page.setEnd(page.getShowPageNum());
			}else {
				page.setStart(page.getPageNow()-page.getShowPageNum()/2);
				page.setEnd(page.getPageNow()+page.getShowPageNum()/2);
			}
			//处理头部分
		}else if (page.getPageCount()<=page.getShowPageNum()) {
			page.setStart(1);
			page.setEnd(page.getPageCount());
			System.out.println("--------2-----------");
			//处理尾部分
		}else if (page.getPageCount()>=page.getPageNow()-page.getShowPageNum()/2) {
			page.setStart(page.getPageCount()-page.getShowPageNum());
			page.setEnd(page.getPageCount());
			System.out.println("--------3-----------");
		}
		return page;
	}

/************************----servlet方法----*********************/
		int pageNow=1;
		String sPageNum=request.getParameter("pageNow");
		
		//还未判断是否有非法字符
		if (sPageNum!=null) {
			pageNow=Integer.parseInt(sPageNum);
		}
		Page page=ms.showPageNum(pageNow);
		request.setAttribute("page", page);
		//request.setAttribute("pageNow", pageNow);
		//request.setAttribute("pageCount", pageCount);
		List<Msg> lists=new ArrayList<Msg>();
		lists.clear();
		lists=ms.getMsgPaging(page.getPageNow());
		request.setAttribute("lists", lists);
		request.getRequestDispatcher("../getMsg.jsp").forward(request, response);

/********************---tools---***************************/
public class Page
{
	public  int pageSize=10;//设置每页显示多少条记录
	private int start=1;	//显示页码的开始页码
	private int end=1;		//显示页码的结束页码
	private int pageCount;//记录的总页数
	private int showPageNum;//需要显示的页码数
	private int pageNow;	//当前页
}

/******************----页面显示---*************************/
   <div class="pagination"> 
	<%
		Page p=(Page)request.getAttribute("page");
		if(p.getPageNow()<=1){
			out.print("<li class='disablepage'>首页&nbsp;</li> ");
			out.print("<li class='disablepage'>上一页&nbsp;</li> ");
		}else{
			out.print("<a href='servlet/MsgServlet?method=find&pageNow=1'>首页</a>&nbsp; ");
			out.print("<a href='servlet/MsgServlet?method=find&pageNow="+(p.getPageNow()-1)+"'>上一页 </a>&nbsp;");
		}
		
		for(int i=p.getStart();i<=p.getEnd();i++){
			if(p.getPageNow()==i){
				out.print("<li class='currentpage'>"+i+"</li>&nbsp;");
			}else{
				out.print("<a href='servlet/MsgServlet?method=find&pageNow="+i+"'>"+i+"</a>&nbsp;");
			}
		}
		if(p.getPageNow()>=p.getPageCount()){
			out.print("<li class='disablepage'>下一页 </li>&nbsp; ");
			out.print("<li class='disablepage'>尾页</li>&nbsp;");
		}else{
			out.print("<a href='servlet/MsgServlet?method=find&pageNow="+(p.getPageNow()+1)+"'>下一页</a>&nbsp;");
			out.print("<a href='servlet/MsgServlet?method=find&pageNow="+p.getPageCount()+"'>尾页</a>&nbsp;");
		}
		
	%>
   </div><br/> 	
	
	