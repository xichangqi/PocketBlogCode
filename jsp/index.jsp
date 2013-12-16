<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="GBK" %>
<%@ page import="com.blog.bean.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html> 
	<LINK href="global.css" type="text/css" rel="stylesheet"/>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>¿Ú´üÎ¢²©</title>
	</head>
	<body background="img/back.jpg">
	
	<br/><br/><br/>
		<table class="bodyBack" width="80%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td align="left" valign="top" bgcolor="#9c9c9c" width="500px" style=" padding: 0em; margin: 0em;" class="bodyBack">
					<iframe name="write" src="write.jsp"  width="100%" frameborder="0" scrolling="no" scrolling="no" height="200px"> </iframe>
				</td>
				<td align="left" valign="bottom" width="250px">
					<iframe id="login" name="login" width="100%" src="login.jsp" frameborder="0" scrolling="no"></iframe>
				</td>
			</tr>
			<tr>
				<td height="100%">
					<iframe id="content" name="content" width="100%" height="800px" scrolling="no" frameborder="0" src="welcome.jsp"></iframe>
				</td>
				<td height="100%"> 
					<iframe id="friend" name="friend" width="100%" scrolling="auto" height="800px" src="contacts.jsp" frameborder="0"></iframe>
				</td>
			</tr>
		</table>
	</body>
</html>