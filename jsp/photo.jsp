<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,java.sql.*,java.io.*"%>
<%@page import="com.blog.dao.PhotoControl"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<LINK href="global.css" type="text/css" rel="stylesheet"/>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
</head>
<body>
<%
	String pid = (String)request.getParameter("pid");			//读取图片ID
	PhotoControl controler = new PhotoControl();
	Blob blob = controler.getPhotoBlob(pid);
	System.out.println("blob:"+blob==null);
	if(blob == null){											//获取Blob图片失败
		response.sendRedirect("img/no_image.jpg");				//
	}
	else{													//成功获取数据
		long size = blob.length();							//获取长度
		byte [] bytes = blob.getBytes(1,(int)size);			//获取字节数组
		response.setContentType("image/jpeg");
		OutputStream outs = response.getOutputStream();		//获得输出流
		outs.write(bytes);									
		outs.flush();
		out.clear(); 										//必须加上的，否则异常
		out = pageContext.pushBody();						//返回PageContent对象
	}
%>
</body>
</html>