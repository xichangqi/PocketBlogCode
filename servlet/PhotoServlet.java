package com.blog.servlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.blog.bean.User;
import com.blog.dao.PhotoControl;
import static com.blog.dao.ConstantUtil. CREATE_ALBUM_SUCESS;
import static com.blog.dao.ConstantUtil. CREATE_ALBUM_FAIL;
import static com.blog.dao.ConstantUtil.CHAR_ENCODING;



public class PhotoServlet extends HttpServlet
{

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		request.setCharacterEncoding(CHAR_ENCODING);
		String action = request.getParameter("action");
		System.out.println("action==="+action);
		 if(action.equals("seeAlbum")){			//action为查看相册
			 
			HttpSession session = request.getSession();
			User user = (User)session.getAttribute("user");
			if(user!=null){
				request.setAttribute("u_no", user.u_no);	//设置返回属性
			}
			request.getRequestDispatcher("album.jsp").forward(request, response);
		}
		 
		 else if(action.equals("createAlbum")){				//action为创建相册
				String albumName = request.getParameter("albumName");
				String u_no = request.getParameter("u_no");		//获取相册所属者id
				PhotoControl controler = new PhotoControl();
				if(controler.createAlbum(albumName, u_no) == 1){		//创建成功
					request.setAttribute("result", CREATE_ALBUM_SUCESS);
				}
				else{
					request.setAttribute("result", CREATE_ALBUM_FAIL);
				}
				request.getRequestDispatcher("uploadImage.jsp").forward(request, response);
			}
		
		 
		 else if(action.equals("change_album_access")){			//action为修改相册权限
				String xid = request.getParameter("xid");
				String access = request.getParameter("album_access");
				System.out.println("xid  -- "+xid+" access:"+access);
				PhotoControl controler = new PhotoControl();
				controler.changeAlbumAccess(xid, access);
				request.getRequestDispatcher("album.jsp").forward(request, response);
			}
		 
		 else if(action.equals("deletePhoto")){				//action为删除指定图片
				String pid = request.getParameter("p_id");
				PhotoControl controler = new PhotoControl();
				int result = controler.deletePhoto(pid);
				request.getRequestDispatcher("album.jsp").forward(request, response);
			}
		 
		 else if(action.equals("addPhotoComment")){				//action为addPhotoComment
				String content = request.getParameter("content");
				String uno = request.getParameter("u_no");
				String pid = request.getParameter("p_id");
				PhotoControl controler = new PhotoControl();
				int result = controler.addPhotoComment(content, pid, uno);
				if(result == 1){
					request.getRequestDispatcher("album.jsp").forward(request, response);
				}
		 	}
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		 
		}
	

}
		
		
		


