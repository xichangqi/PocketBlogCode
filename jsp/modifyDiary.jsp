<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.sql.*,com.blog.dao.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<LINK href="global.css" type="text/css" rel="stylesheet"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<head>
		<title></title>
	</head>
	
	<script type="text/javascript">
	function goBack()
	{
		history.go(-1);
	}
	
	</script>
	
	
	<body>
<%
		Integer result = (Integer)request.getAttribute("result");
		String rid = request.getParameter("r_id");
		String a= request.getParameter("r_title");
		System.out.print(a);
		String rtitle = new String(a.getBytes("ISO-8859-1"),"UTF-8");
		String b = request.getParameter("r_content");
		String rcontent = new String(b.getBytes("ISO-8859-1"),"UTF-8");
%>
	<form action="WriteServlet" method="post" >
		<table border="0" align="center">
			<tr bgcolor="#8FBC8F">
				<td colspan="2">编辑日志</td>
			</tr>
			<tr>
				<td colspan="2">标题：
				<input type="text" class="f2_input" name="r_title" value="<%=rtitle %>" ></input></td>
			</tr>
			<tr>
				<td colspan="2">
					<textarea class="f2_input2" rows="15" cols="80" name="r_content"><%=rcontent %></textarea>
				</td>
			</tr>
			<tr>
				<td align="right">
					<input class="btn" type="submit" class="btn" value="重新发表" ></input>
				</td>
				<td align="left">
					<input class="btn" type="button" class="btn" value="返回" onclick="goBack()" ></input>
				</td>
			</tr>
		</table>
		<input type="hidden" name="r_id" value="<%=rid %>" />
		<input type="hidden" name="action" value="modifyDiary" />
	</form>
<%
	if(result != null)
	{
		if(result ==1){
%>
		<script type="text/javascript">
		alert("日志修改成功!");
		history.go(-1);
		</script>
		
<%
		}
		else {
%>
		<center>日志修改失败，请查看日志列表！</center>
<%
		}
	}
%>
	</body>
</html>