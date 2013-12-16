
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*,com.blog.bean.*"%>
<%@page import="com.blog.dao.UserControl"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<LINK href="global.css" type="text/css" rel="stylesheet"/>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
</head>
<body class="bodyBack">
<%
	User user = (User)session.getAttribute("user");
	if(user != null){
		UserControl controler = new UserControl();
		ArrayList<Visitor> visitorList = controler.getVisitors(user.u_no);
		if(visitorList.size() == 0){
%>
		<center>暂无访客记录！</center>
<%
		}
		else{
			for(Visitor v:visitorList){
%>
		<table>
			<tr>
				<td rowspan="2" >
					<img alt="<%=v.v_name %>" src="head.jsp?hid=<%=v.h_id %>" width="48" height="48" >
				</td>
				<td>
					<a href="UserServlet?action=toFriendPage&uno=<%=v.v_no %>" target="content"><%=v.v_name %></a>
				</td>
			</tr>
			<tr>
				<td><%=v.v_date %></td>
			</tr>
		</table>
<%
			}
		}
	}
	else{
%>
	<center>用户未登录！</center>
<%
	}
%>
</body>
</html>