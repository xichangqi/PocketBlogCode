<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="GBK" import="com.blog.dao.*,com.blog.bean.User"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<LINK href="global.css" type="text/css" rel="stylesheet"/>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>写日志</title>
</head>
	<script type="text/javascript">
		function check(action){
			if(document.form1.content.value == "")
			{
				alert("请把信息填写完整!");
				return;
			}
			if(action == "new_diary")
			{
				if(document.form1.title.value =="")
				{
					alert("请把信息填写完整!");
					return;
				}	
			}
				document.form1.action.value = action;
				document.form1.submit();
		}
	</script>
	
<body  class="bodyBack">
<%
	request.setCharacterEncoding(ConstantUtil.CHAR_ENCODING);
	String result = (String)request.getAttribute("writeResult");
	User user = (User)session.getAttribute("user");
	
%>

	<form action="WriteServlet" method="post" name="form1"> 
		<table align="center" border="0" width="600px" height="155px" style="padding: 0em; margin: 0em ;table-layout:fixed" "cellspacing="0" cellpadding="0" background="img/aaaaa.jpg">  
			<tr>
			<td>
			<input name="title" class="f2_input"/> <br/>
			&nbsp;&nbsp;<TEXTAREA class="status-update-textarea" id="sendinfo" name="content" rows="3" cols="60"></TEXTAREA>
			</td>
			</tr>
			</table>
			<table>
			<tr>
			<td width="420px"><a name="seeDiary" href="WriteServlet?action=seeDiary" target="content">查看日记</a>
				<a name="seeAlbum" href="PhotoServlet?action=seeAlbum" target="content">查看相册</a>
				
				<% if(result != null){%>
					
					<font id="result"><%=result%></font>
					
					<script type="text/javascript">
					window.parent.frames['login'].location.reload()
					setInterval('display()',5000);
					
					function display()
					{
						
						document.getElementById("result").innerHTML = " "
					}
					
					</script>
					<% }%>
				
				
				
			</td>
			<td><input class="btn" type="button" value="更新心情" onclick="check('new_state')" />
				<input class="btn" type="button" value="发布日记" onclick="check('new_diary')"/></td>
			</tr>
			</table>
		<input type="hidden" name="action" value="new_diary" />
	</form>

</body>
</html>