package com.blog.dao;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static com.blog.dao.ConstantUtil.HEAD;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.blog.bean.HeadImg;
import com.blog.bean.P_Comments;
import com.blog.bean.PhotoInfo;
import static com.blog.dao.ConnectionUtil.closeConnect;
import static com.blog.dao.ConnectionUtil.getConnection;
import static com.blog.dao.ConnectionUtil.getMax;
import static com.blog.dao.ConstantUtil.ALBUM;
import static com.blog.dao.ConstantUtil.PHOTO;
import static com.blog.dao.ConstantUtil.P_COMMENT;
public class PhotoControl {
	private Blob result = null;
	private Connection con = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	public  Blob getHeadBlob(String hid){
		
		try{
			con = getConnection();	//获得连接
			ps = con.prepareStatement("select h_data from head where h_id=?");
			ps.setInt(1, Integer.valueOf(hid));		//设置参数
			rs = ps.executeQuery();		//执行查询
			if(rs.next()){		//查找结果集
				result = rs.getBlob(1);		//赋值
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);  //关闭连接
		}
		return result;
	}
	
	
	
	
	
	public ArrayList<HeadImg> gethandList(int pageNo,int count,int u_no)
	{
		 
		 con = getConnection();	//获得连接
		int start = (pageNo-1)*count;	//计算开始位置
		ArrayList<HeadImg> list = new ArrayList<HeadImg>(); 
		try
		{
			ps = con.prepareStatement("select h_id from head where u_no=? limit ?,?");
			ps.setInt(1, u_no);
			ps.setInt(2,start);
			ps.setInt(3, count);
			rs = ps.executeQuery();
			while(rs.next())
			{
				HeadImg head = new HeadImg(rs.getInt(1));
				list.add(head);
			}
			return list;
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return null;
	}
	
	
	
	
	public  int getHeadSize(String u_no){
		int result = 0;
		try{
			con = getConnection();		//获得连接
			ps = con.prepareStatement("select count(h_id) as count from head where u_no=?");
			int uno = Integer.valueOf(u_no);
			ps.setInt(1,uno);
			rs = ps.executeQuery();	//执行查询
			if(rs.next()){		//查到数据
				result = rs.getInt(1);	//读取数据
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con,ps);
		}
		
		return result;
	}
	
	
	
	public  int insertHeadFile(File file,String hdes,String uno){
		int result = -1;
		Connection con = null;
		PreparedStatement ps = null;
		FileInputStream fis = null;
		try{
			con = getConnection();		//获得数据库连接
			ps = con.prepareStatement("insert into head(h_id,h_des,h_data,u_no) values(?,?,?,?)");//设置参数
			int max = getMax(HEAD);
			ps.setInt(1, max);
			ps.setString(2, hdes);
			fis = new FileInputStream(file);
			ps.setBinaryStream(3, fis,(int)file.length());
			ps.setInt(4, Integer.valueOf(uno));
			result = ps.executeUpdate();		//执行插入
			System.out.println("插入图片成功");
		}
		catch(Exception e){
			
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	//方法：获取所有的相册
	public ArrayList<String []> getAlbumList(String u_no){
		ArrayList<String []> result = new ArrayList<String []>();
		Connection con = null;		//声明数据库连接对象
		PreparedStatement ps = null;	//声明预编译语句
		ResultSet rs = null;			//声明ResultSet对象
		try{
			con = getConnection();		//获得数据库连接
			ps = con.prepareStatement("select x_id,x_name,x_access from album where u_no=?");
			ps.setInt(1, Integer.valueOf(u_no));	//设置参数
			rs = ps.executeQuery();		//执行查询
			while(rs.next()){			//遍历结果集
				String [] sa = new String[3];
				sa[0] = rs.getInt(1)+"";
				sa[1] = rs.getString(2);
				sa[2] = rs.getInt(3)+"";
				result.add(sa);				//加入到列表中
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//方法：根据权限获取相册信息
	public  ArrayList<String []> getAlbumListByAccess(String uno,String visitor){
		ArrayList<String []> result = new ArrayList<String []>();
		try{
			con = getConnection();
			FriendControl controler = new FriendControl();
			if(controler.isMyFriend(uno, visitor)){//检查访问者和被访问者是否为好友
				//不是好友，获得的是好友可见以及公开的相册
				ps = con.prepareStatement("select x_id,x_name from album where u_no=? and x_access<2");
			}
			
			else{
				//不是好友，仅仅获得公开的相册
				ps = con.prepareStatement("select x_id,x_name from album where u_no=? and x_access=0");
			}
			
			ps.setInt(1, Integer.valueOf(uno));
			rs = ps.executeQuery();		//执行查询
			while(rs.next()){	//如果结果集中有数据
				String xid = rs.getInt(1)+"";  //相册id
				String xname = rs.getString(2);//相册名称
				String [] sa = new String[]{xid,xname};
				result.add(sa);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	
	//方法：从数据库读取对应相册的图片的信息
	public  ArrayList<PhotoInfo> getPhotoInfoByAlbum(String xid,int pageNo,int span){
		ArrayList<PhotoInfo> result = new ArrayList<PhotoInfo>();
		int start = span*(pageNo-1);		//计算起始位置
		try{
			con = getConnection();
			ps = con.prepareStatement("select p_id,p_name,p_des,x_id from photo" +
					" where x_id=? order by p_id limit "+start+","+span);		//创建语句
			ps.setInt(1, Integer.valueOf(xid));		//设置参数
			rs = ps.executeQuery();
			while(rs.next()){		//遍历结果集
				String p_id = rs.getInt(1)+"";
				String p_name = rs.getString(2);	//相片名称
				String p_des = rs.getString(3);//相片描述
				String x_id = rs.getInt(4)+"";
				PhotoInfo p = new PhotoInfo(p_id, p_name, p_des, x_id);		//获得Photo对象
				result.add(p);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	//方法：从数据库中取出制定相册的长度
	public int getAlbumSize(String xid){
		int result = -1;
		try{
			con = getConnection();		//获得连接
			ps = con.prepareStatement("select count(*) as count from photo where x_id=?");
			ps.setInt(1, Integer.valueOf(xid));		//设置参数
			rs = ps.executeQuery();		//执行查询
			if(rs.next()){
				result = rs.getInt(1);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//方法：查询指定相册的权限
	public  int getAlbumAccess(String xid){
		int result = 0;
		
		try{
			con = getConnection();
			ps = con.prepareStatement("select x_access from album where x_id=?");
			ps.setInt(1, Integer.valueOf(xid));
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getInt(1);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//方法：获得指定图片的评论
	public ArrayList<P_Comments> getPhotoComment(String p_id){
		ArrayList<P_Comments> result = new ArrayList<P_Comments>();
		try{
			con = getConnection();
			ps = con.prepareStatement("select p_comment.c_content,p_comment.u_no,user.u_name,date_format(p_comment.c_date,'%Y-%c-%e %k:%i:%s')" +
					" from p_comment,user where p_comment.u_no=user.u_no and p_id=? order by p_comment.c_date");
			ps.setInt(1, Integer.valueOf(p_id));
			rs = ps.executeQuery();
			while(rs.next()){
				String content = rs.getString(1);
				String u_no = rs.getString(2);
				String u_name = rs.getString(3);
				String date = rs.getString(4);
				P_Comments pc = new P_Comments(content, u_no, u_name, date);
				result.add(pc);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	//创建一个相册
	public  int createAlbum(String name,String u_no){
		int result = -1;
		try{
			con = getConnection();
			ps = con.prepareStatement("insert into album(x_id,x_name,u_no) values(?,?,?)");
			ps.setInt(1, getMax(ALBUM));
			ps.setString(2, name);
			ps.setInt(3, Integer.valueOf(u_no));		//设置预编译语句的参数
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	public  int insertPhoto(File file,String name,String desc,String x_no){
		int result = -1;
		Connection con = null;
		PreparedStatement ps = null;
		FileInputStream fis = null;
		try{
			con = getConnection();		//获得数据库连接
			ps = con.prepareStatement("insert into photo(p_id,p_name,p_des,p_data,x_id) values(?,?,?,?,?)");//设置参数
			int max = getMax(PHOTO);
			ps.setInt(1, max);
			ps.setString(2,name);
			ps.setString(3,desc);
			fis = new FileInputStream(file);
			ps.setBinaryStream(4, fis,(int)file.length());
			ps.setInt(5, Integer.valueOf(x_no));
			result = ps.executeUpdate();		//执行插入
			System.out.println("插入图片成功");
		}
		catch(Exception e){
			
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	//得到某个照片
	public Blob getPhotoBlob(String id){
		Blob result = null;
		Connection con = null;	//数据库对象
		PreparedStatement ps = null;	//预编译语句
		ResultSet rs = null;		//结果集
		try{
			con = getConnection();		//获得连接
			ps = con.prepareStatement("select p_data from photo where p_id=?");	//创建语句
			ps.setInt(1, Integer.valueOf(id));	//设置参数
			rs = ps.executeQuery();		//设置参数
			if(rs.next()){	//
				result = rs.getBlob(1);		//获得Blob对象
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//方法：修改相册的权限
	public int changeAlbumAccess(String xid,String newAccess){
		int result = 0;
		try{
			con = getConnection();		//获得数据库连接
			ps = con.prepareStatement("update album set x_access=? where x_id=?");
			ps.setInt(1, Integer.valueOf(newAccess));
			ps.setInt(2, Integer.valueOf(xid));		
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//方法：删除指定图片
	public  int deletePhoto(String pid){
		int result = -1;
		try{
			deleteAllCommentByPhoto(pid);
			con = getConnection();		//获得连接
			ps = con.prepareStatement("delete from photo where p_id=?");
			ps.setInt(1, Integer.valueOf(pid));		//设置删除的照片的id
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	//方法：删除指定图片的所有评论
	public  int deleteAllCommentByPhoto(String pid){
		int result = 0;
		try{
			con = getConnection();
			ps = con.prepareStatement("delete from p_comment where p_id=?");
			ps.setInt(1, Integer.valueOf(pid));
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		finally{
			closeConnect(con, ps);
		}
		        
		return result;
	}
	
	
	
	//方法：添加新的图片评论
	public int addPhotoComment(String content,String p_id,String u_no){
		int result = 0;
		
		try{
			con = getConnection();
			ps = con.prepareStatement("insert into p_comment(c_id,c_content,u_no,p_id) values(?,?,?,?)");
			ps.setInt(1, getMax(P_COMMENT));
			ps.setString(2, content);
			ps.setInt(3, Integer.valueOf(u_no));
			ps.setInt(4, Integer.valueOf(p_id));
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	//方法：获取某相册相片数
	public int getAlbumSize(int x_id){
		int result = 0;
		
		try{
			con = getConnection();
			ps = con.prepareStatement("select count(p_id) as conut from photo where x_id=?");
			ps.setInt(1,x_id);
			rs = ps.executeQuery();
			
			if(rs.next())
			{
				result = rs.getInt(1);  //取出相册中照片数量
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	
	//方法：安卓端向数据库插入照片
	public  int insertPhotoFromAndroid(byte [] buf,String pname,String pdes,String x_id){
		int result =-1;
		try{
			con = getConnection();
			ps = con.prepareStatement("insert into photo(p_id,p_name,p_des,p_data,x_id) values(?,?,?,?,?)");
			ps.setInt(1, getMax(PHOTO));
			ps.setString(2, pname);
			ps.setString(3, pdes);
			InputStream in = new ByteArrayInputStream(buf);
			ps.setBinaryStream(4, in,(int)(in.available()));		
			ps.setInt(5, Integer.valueOf(x_id));
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
}




