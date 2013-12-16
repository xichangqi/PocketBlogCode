package com.blog.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.blog.bean.User;
import com.blog.dao.ConstantUtil;
import com.blog.dao.UserControl;
import com.blog.dao.WriteControl;
public class UserServlet extends HttpServlet
{

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		request.setCharacterEncoding(ConstantUtil.CHAR_ENCODING);
		String action = (String)request.getParameter("action");	
		System.out.println("UserServlet===== action:"+action);
		
		
		if(action.equals("login")){		
			
			HttpSession session = request.getSession();
			UserControl controler = new UserControl();
			String u_no = (String)request.getParameter("u_no");			
			String u_pwd = (String)request.getParameter("u_pwd");		
			User user = controler.checkLogin(u_no, u_pwd);
			if(null != user)
			{
				session.setAttribute("user", user);
			}
			else{
				request.setAttribute("loginResult",ConstantUtil.errorMessages.get(0)); 
				ConstantUtil.errorMessages.clear();
			}
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
		
		
		else if(action.equals("register"))  
		{
			HttpSession session = request.getSession();
			UserControl controler = new UserControl();
			String u_name = (String)request.getParameter("u_name");
			String u_pwd = (String)request.getParameter("u_pwd");
			String u_email = (String)request.getParameter("u_email");
			String u_state = (String)request.getParameter("u_state");
			System.out.println("name:"+u_name);
			User user = controler.registerUser(u_name, u_pwd, u_email, u_state,1);
			
			if(null != user) 
			{
				request.setAttribute("newUser", user);
			}
			
			else{
				
			request.setAttribute("result",ConstantUtil.errorMessages.get(0));
			}
			request.getRequestDispatcher("register.jsp").forward(request,response);
		}
		
			
		else if(action.equals("logout")){		
				HttpSession session = request.getSession();
			    session = request.getSession();
				session.setAttribute("user", null);
				request.setAttribute("logout","logout");
				request.getRequestDispatcher("login.jsp").forward(request,response);
				
		}
		
			else if(action.equals("modify")){				
				request.getRequestDispatcher("personalInfo.jsp").forward(request,response);
			}
		
			else if(action.equals("changeInfo"))  
			{
				String uname = request.getParameter("uname");	
				String uemail = request.getParameter("uemail");	
				String ustate = request.getParameter("ustate");	
				HttpSession session = request.getSession();		
				User user = (User)session.getAttribute("user");	
				String uno = user.u_no;
				UserControl controler = new UserControl();
				int result = controler.changeUserInfo(uno, uname, uemail, ustate);
				if(result == 1){		
					user.u_name = uname;
					user.u_email = uemail;
					user.u_state = ustate;
					session.setAttribute("user", user);
				}
				request.setAttribute("changeInfoResult", result);
				request.getRequestDispatcher("personalInfo.jsp").forward(request, response);//��ת
			}
		
		
			else if(action.equals("changeHead")){		//actionΪ�޸�ͷ��
				String hid = request.getParameter("hid");	//��ȡ����
				String uno = request.getParameter("uno");	//��ȡ����
				UserControl controler = new UserControl();
				if(controler.changeUserHead(uno, hid) == 1){		//�޸ĳɹ�
					HttpSession session = request.getSession();
					User u = (User)session.getAttribute("user");
					u.h_id = Integer.valueOf(hid);
					session.setAttribute("user", u);
					request.setAttribute("changeHeadResult","success");
				}
				request.getRequestDispatcher("personalInfo.jsp").forward(request, response);
			}
		
		
			else if(action.equals("toFriendPage")){					//action为去好友的微博
				HttpSession session = request.getSession();		//获得Session
				User user = (User)session.getAttribute("user");	//获得登录用户
				String visitor = user.u_no;						//访问者的id
				String u_no = (String)request.getParameter("uno");
				WriteControl controler = new WriteControl();
				controler.addVisitor(u_no, visitor);//添加最近访客
				request.getRequestDispatcher("friendPage.jsp").forward(request, response);
			}
		
		
			else if(action.equals("deleteFriend")){					//删除好友
				String u_no = request.getParameter("u_no");
				String friend_no = request.getParameter("u_noToDelete");
				UserControl controler = new UserControl();
				controler.deleteFriend(u_no, friend_no);
				request.getRequestDispatcher("friend.jsp").forward(request, response);
			}
	
		

	}

}
