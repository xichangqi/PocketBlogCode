<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="GBK" import="com.blog.dao.*,com.blog.bean.*,java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
</head>
<script type="text/javascript">
function showResult(result)
{
	
}

</script>
<body>
	<%
	User user = (User)session.getAttribute("user");		//获取session中数据
	String me = user.u_no;
	FriendControl controler = new FriendControl();
	String mFResult = (String)request.getAttribute("A");
	if(mFResult != null && !mFResult.equals("-1"))  //添加好友成功
		{%>
		
		<script type="text/javascript">
		
		//alert("添加好友成功!");
		parent.friend.location.reload();
		
		</script>
			
	<%}

ArrayList<User> friendList = (ArrayList<User>)session.getAttribute("friendList");
if(friendList == null){		  //没有搜索到相关内容
				
	%>
			<h3>对不起，找不到相关的好友</h3>
	<%
			}
		
		
		
			else{		//搜索结果不为空
				for(User u:friendList){		//遍历搜索结果
	%>
			<table>
				<tr>
					<td rowspan="3">
						
						<img alt="<%=u.u_name %>" src="head.jsp?hid=<%=u.h_id %>" width="52" height="52">
						
					</td>
					<td><%=u.u_name %></td>
					
					
				</tr>
				<tr>
					<td width="450px"><%=u.u_email %></td>
					<td>
					<%
					if(!controler.isMyFriend(user.u_no,u.u_no))
					{%>
					
					<a href="FriendServlet?action=makeFriend&my_id=<%=me%>&stranger_id=<%=u.u_no %>">添加好友</a><img src="img/add.png" width="35" height="35"/>
						
					<% 
					
					}
					
					else{%>
						
						[已是我的好友]
					<%}
					%>
					
					</td>
				</tr>
				<tr>
					<td><%=u.u_state %></td>
				</tr>
				</table><hr/>
				
	<%
				}
		}
	%>
</body>
</html>