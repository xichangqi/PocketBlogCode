package com.blog.servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.blog.bean.User;
import com.blog.dao.ConstantUtil;
import com.blog.dao.FriendControl;

public class FriendServlet extends HttpServlet
{

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		this.doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		
		req.setCharacterEncoding(ConstantUtil.CHAR_ENCODING);  
		String action = (String)req.getParameter("action");	
		System.out.println("UserServlet===== action:"+action);
		
		if(action.equals("searchFriend"))  //请求为搜索好友
		{
			String keyword = req.getParameter("keyword");
			ArrayList<User> friendList = null;
			FriendControl controler = new FriendControl();
			friendList = controler.searchFriendByName(keyword);
			HttpSession session = req.getSession(); 
			if(friendList != null)
			{
				session.setAttribute("friendList",friendList);
				req.getRequestDispatcher("searchList.jsp").forward(req,resp);
				
			}
			
		}
		
		else if(action.equals("makeFriend"))  //加好友请求
		{
			String my_id = req.getParameter("my_id");
			String stranger_id = req.getParameter("stranger_id");
			FriendControl controler = new FriendControl();
			
			int result = controler.makeFriend(my_id, stranger_id);
			req.setAttribute("A",String.valueOf(result));
			System.out.println("添加朋友结果："+result);
			req.getRequestDispatcher("searchList.jsp").forward(req,resp);
		}
		
		else if(action.equals("friendList"))  //显示某用户好友列表
		{
			HttpSession session = req.getSession();
			User user = (User)session.getAttribute("user");
			String u_no = user.u_no;
			
			FriendControl controler = new FriendControl();
			controler.getFriendList(u_no);
			
		}
		
	}

}
