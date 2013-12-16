package com.blog.servlet;

import static com.blog.dao.ConstantUtil.CHAR_ENCODING;
import static com.blog.dao.ConstantUtil.UPDATE_STATE_SUCCESS;
import static com.blog.dao.ConstantUtil.USER_NOT_LOGIN;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.blog.bean.User;
import com.blog.dao.WriteControl;

public class WriteServlet extends HttpServlet
{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{

		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		req.setCharacterEncoding(CHAR_ENCODING);
		String action = req.getParameter("action");
		System.out.println("action=========" + action);

		if (action.equals("new_state"))
		{ // action为更新心情
			HttpSession session = req.getSession();
			User user = (User) session.getAttribute("user");
			WriteControl controler = new WriteControl();
			if (user == null)
			{ // 用户没有登录
				req.setAttribute("result", USER_NOT_LOGIN);
				req.getRequestDispatcher("write.jsp").forward(req, resp);
				return;
			}
			String u_no = user.u_no; // 获得用户的id
			String content = (String) req.getParameter("content");
			String result = controler.updateState(u_no, content);
			if(result.equals(UPDATE_STATE_SUCCESS))
			{
					user.u_state = content;
			}
			session.setAttribute("user", user); // 更新Session的内容
			req.setAttribute("writeResult", result); // 将结果设置到request的属性中
			req.getRequestDispatcher("write.jsp").forward(req, resp);// 返回
		}

		else if (action.equals("new_diary")) // 写入新日志请求
		{

			HttpSession session = req.getSession();
			User user = (User) session.getAttribute("user");
			WriteControl controler = new WriteControl();
			
			String u_no = user.u_no; // 获得用户的id
			String title = req.getParameter("title"); //标题
			String content = req.getParameter("content");//内容
			String result = controler.writeNewDiary(title, content,u_no);
			
			req.setAttribute("writeResult", result); // 将结果设置到request的属性中
			req.getRequestDispatcher("write.jsp").forward(req, resp);// 返回

		}
		
		
		else if(action.equals("seeDiary")){		//action为查看日记
			HttpSession session = req.getSession();		//获取Session
			User user = (User)session.getAttribute("user");	//获得User对象
			if(user != null){
				
				req.setAttribute("u_no", user.u_no);
			}
			req.getRequestDispatcher("diary.jsp").forward(req, resp);
		}
		
		
		else if(action.equals("makeComment")){		//action为发表评论
			String c_content = req.getParameter("comment");
			String r_id = req.getParameter("r_id");
			String visitor = req.getParameter("visitor");
			WriteControl controler = new WriteControl();
			int result = controler.addComment(c_content, r_id, visitor);
			if(result == 1){		//添加评论成功
				req.setAttribute("commentResult","success");
			}
			
			else
			{
				req.setAttribute("commentResult", "faild");
			}
			req.getRequestDispatcher("diary.jsp").forward(req, resp);
		}
		
		
		
		else if(action.equals("toModifyDiary")){			//action为去修改日志的页面
			req.getRequestDispatcher("modifyDiary.jsp").forward(req, resp);
		}
		
		
		
		else if(action.equals("deleteDiary")){			//action为删除指定日志
			String rid=req.getParameter("r_id");
			WriteControl controler = new WriteControl();
			int result = controler.deleteDiary(rid);
			req.getRequestDispatcher("diary.jsp").forward(req, resp);
			
		}
		
		else if(action.equals("modifyDiary")){				//action为修改日志
			String rid = req.getParameter("r_id");
			String rtitle = req.getParameter("r_title");
			String rcontent = req.getParameter("r_content");
			WriteControl controler = new WriteControl();
			int result = controler.modifyDiary(rid, rtitle, rcontent);
			req.setAttribute("result", result);
			req.getRequestDispatcher("modifyDiary.jsp").forward(req, resp);
		}
		
		
		
		
		
		
		
		
		
		
	}
}
