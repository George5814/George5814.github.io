1.通过GET方式传参，通过new String(request.getParameter("").getBytes("ISO-8859-1"),"UTF-8"); 的方式转换为需要的编码，如UTF-8等。
response.setCharacterEncoding("UTF-8")
 
2.通过POST方式传参，通过声明request.setCharacterEncoding("UTF-8");的方式声明编码类型来解决。