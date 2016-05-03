/****************-----dao-------***********************/
//获取表中记录的行数
	public int getRowCount() throws SQLException{
		int  rowCount=0;
		db.conn(this.conn);
		String sql="select count(id) from mymessage";
		ResultSet rSet=db.executeQuery(sql);
		if(rSet.next()){
			rowCount=rSet.getInt(1);
		}
		return rowCount;
	}
	
	//分页查询数据
	public List<MyMessage> getMsgPaging(int startRow,int endRow) throws SQLException{
		List<MyMessage> msgs=new ArrayList<MyMessage>();
		db.conn(this.conn);
		String sql="SELECT * FROM (  " +
				"SELECT A.*, ROWNUM RN  FROM (SELECT * FROM mymessage) A   " +
				"WHERE ROWNUM <=?) WHERE RN >=?";
		Object obj[]={endRow,startRow};
		ResultSet rSet=db.executeQuery(sql, obj);
		while (rSet.next()) {
			MyMessage msg=new MyMessage();
			msg.setId(rSet.getInt("id"));
			msg.setName(rSet.getString("name"));
			msg.setContext(rSet.getString("context"));
			msg.setTime(rSet.getString("time"));
			msg.setTitle(rSet.getString("title"));
			msgs.add(msg);
		}
		
		return msgs;
	}

/**************************--service--********************************/
//分页查询
	public List<MyMessage> getMsgPaging(int pageNow){
		List<MyMessage> msgs=new ArrayList<MyMessage>();
		this.conn=db.conn();
		MyMessageDao md=new  MyMessageDao(this.conn);
		try {
			int startRow=pageSize*(pageNow-1)+1;
			int endRow=pageSize*pageNow;
			System.out.println("startRow:"+startRow);
			System.out.println("endRow:"+endRow);
			msgs=md.getMsgPaging(startRow, endRow);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if (db!=null) {
				db.close();
			}
		}
		return msgs;
	}
	
	//显示页数
	public int getPageCount(){
		int pageCount=0;
		int rowCount=0;
		this.conn=db.conn();
		MyMessageDao md=new  MyMessageDao(this.conn);
		try {
			rowCount=md.getRowCount();
			if(rowCount%pageSize==0){
				pageCount=rowCount/pageSize;
			}else{
				pageCount=rowCount/pageSize+1;
			}
			System.out.println("pageCount:"+pageCount);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if (db!=null) {
				db.close();
			}
		}
		
		return pageCount;
	}

/*********************---servlet--**************************************/
	MyMessageService ms=new MyMessageService();
		int pageCount=ms.getPageCount();
		int pageNow=1;
		String sPageNum=request.getParameter("pageNow");
		if (sPageNum!=null) {
			pageNow=Integer.parseInt(sPageNum);
		}
			
		List<MyMessage> list=ms.getMsgPaging(pageNow);


		if(pageNow>1){
			out.print("<a href='GetMessageServlet?pageNow="+1+"'>首页&nbsp</a>");
			out.print("<a href='GetMessageServlet?pageNow="+(pageNow-1)+"'>上一页&nbsp</a>");
		}
			
		for (int i = 1; i <= pageCount; i++) {
			
			if(pageCount>10&&pageNow>=3&&i>=pageNow-2&&i<=pageNow+2)
				out.print("<a href='GetMessageServlet?pageNow="+i+"'>"+i+"&nbsp</a>");
			else if(pageCount>10&&pageNow<=3&&i<=pageNow+3) {
				out.print("<a href='GetMessageServlet?pageNow="+i+"'>"+i+"&nbsp</a>");
			}else if(pageCount<=10){
				out.print("<a href='GetMessageServlet?pageNow="+i+"'>"+i+"&nbsp</a>");
			}
		}
		if(pageNow<pageCount){
			out.print("<a href='GetMessageServlet?pageNow="+(pageNow+1)+"'>下一页&nbsp</a>");
			out.print("<a href='GetMessageServlet?pageNow="+pageCount+"'>尾页&nbsp</a>");
		}
		