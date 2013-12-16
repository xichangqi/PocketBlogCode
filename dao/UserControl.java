package com.blog.dao;

import static com.blog.dao.ConnectionUtil.closeConnect;
import static com.blog.dao.ConnectionUtil.getConnection;
import static com.blog.dao.ConnectionUtil.getMax;
import static com.blog.dao.ConstantUtil.CONNECTION_OUT;
import static com.blog.dao.ConstantUtil.LOGIN_FAIL;
import static com.blog.dao.ConstantUtil.REGISTER_FAIL;
import static com.blog.dao.ConstantUtil.USER;
import static com.blog.dao.ConstantUtil.VISIT;
import static com.blog.dao.ConstantUtil.DELETE_FAIL;
import static com.blog.dao.ConstantUtil.DELETE_SUCCESS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.blog.bean.User;
import com.blog.bean.Visitor;


public class UserControl
{
	private Connection con = null;		//声明获取数据库连接
	private PreparedStatement ps = null;					//声明Statement对象
	private ResultSet rs = null;
	
	public User checkLogin(String u_no,String u_pwd){
							//声明ResultSet对象
		try{
			con = getConnection();		//获取数据库连接
			if(con == null){			//判断数据库连接对象是否
				ConstantUtil.errorMessages.add(CONNECTION_OUT); //添加出错信息
				return null;
			}
			ps = con.prepareStatement("select u_no,u_name,u_email,u_state,h_id from user where u_no=? and u_pwd=?");
			
			ps.setInt(1,Integer.valueOf(u_no));				//设置预编译语句的参数
			ps.setString(2, u_pwd);				//设置预编译语句的参数
			rs = ps.executeQuery();
			ArrayList<String> result = new ArrayList<String>();
			if(rs.next()){				//判断结果集是否为空
				for(int i=1;i<=5;i++)
				{
					result.add(rs.getString(i));	//将结果集中数据存放到ArrayList中
					System.out.println(rs.getString(i));
				}
				
				String no = result.get(0);			//获得用户的号码
				String name = result.get(1);		//获得用户的昵称
				String email = result.get(2);		//获取用户电子邮件
				String state = result.get(3);	//获取用户状态
				int hid = new Integer(result.get(4));			//获取用户头像
				User user = new User(no, name, email, state, hid);  //构造对象
				
				return user;
			}
			else{						//如果数据库查无此人
				ConstantUtil.errorMessages.add(LOGIN_FAIL);	//返回登录出错信息
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			closeConnect(con, ps);  //关闭连接
			
		}
		
		return null;
	}
	
	
	
	//用户注册
	public User registerUser(String u_name,String u_pwd,String u_email,String u_state,int h_id){
		Connection con = null;		//声明数据库连接对象
		PreparedStatement ps = null;		//声明语句对象
		try{
			con = getConnection();
			if(con == null){			//判断是否成功获取连接
				ConstantUtil.errorMessages.add(CONNECTION_OUT); //添加出错信息
				return null;		//返回方法的执行
			}
			ps = con.prepareStatement("insert into user(u_no,u_name,u_pwd,u_email,u_state,h_id)" +
					"values(?,?,?,?,?,?)");		//构建SQL语句
			String u_no = String.valueOf(getMax(USER));	//获得分配给用户的帐号
			int no = Integer.valueOf(u_no);
			int hid = Integer.valueOf(h_id);
			ps.setInt(1, no);			//设置PreparedStatement的参数
			ps.setString(2, u_name);
			ps.setString(3, u_pwd);
			ps.setString(4, u_email);
			ps.setString(5, u_state);
			ps.setInt(6,hid);
			int count = ps.executeUpdate();			//执行插入
			if(count == 1){		//如果插入成功
				User user = new User(u_no, u_name, u_email, u_state, h_id);
				return user;
			}
			else{						//如果没有插入数据
				ConstantUtil.errorMessages.add(REGISTER_FAIL);		//获得出错信息
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);  //关闭连接
		}
		return null;
	}
	
	
	
	
	//修改用户信息（昵称，心情，邮箱）
	public int changeUserInfo(String uno,String uname,String uemail,String ustate){
		int result = -1;
		try{
			con = getConnection();	//获得连接
			ps = con.prepareStatement("update user set u_name=?,u_email=?,u_state=? where u_no=?");	//创建语句
			ps.setString(1, uname);
			ps.setString(2, uemail);
			ps.setString(3, ustate);
			ps.setInt(4, Integer.valueOf(uno));	//
			result = ps.executeUpdate();		//执行更新
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	//修改头像的方法
	public int changeUserHead(String u_id,String h_id)
	{
		int result = -1;
		con = getConnection();
		try
		{
			ps = con.prepareStatement("update user set h_id=? where u_no=?");
			ps.setInt(1, Integer.valueOf(h_id));
			ps.setInt(2, Integer.valueOf(u_id));
			result = ps.executeUpdate();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally{
			
			closeConnect(con, ps);
			
		}
		return result;
	}
	
	
	
	//增加访客
	public  int addVisitor(String host,String visitor){
		int result = -1;
		int hostId = Integer.valueOf(host);			//主人的id
		int visitorId = Integer.valueOf(visitor);	//访问者的id
		try{
			con = getConnection();		//获得数据库连接
			//首先查看Visitor是否来过
			ps = con.prepareStatement("select v_no from visit where u_no=? and v_no=?");
			ps.setInt(1, hostId);	//设置主人id
			ps.setInt(2, visitorId);
			rs = ps.executeQuery();
			if(rs.next()){			//提取结果集数据
				ps = con.prepareStatement("update visit set v_date=now() where u_no=? and v_no=?");
				ps.setInt(1, hostId);		//设置主人id
				ps.setInt(2, visitorId);	//设置访客id
				result = ps.executeUpdate();	//执行更新
			}
			else{					//最新的那个访客和当前访客不相同
				ps = con.prepareStatement("insert into visit(v_id,u_no,v_no) values(?,?,?)");
				ps.setInt(1, getMax(VISIT));				//设置主键值
				ps.setInt(2, Integer.valueOf(host));		//设置主人id
				ps.setInt(3, Integer.valueOf(visitor));		//设置访客id
				result = ps.executeUpdate();				//执行查询				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	
	//方法：通过微博号找到相应用户
	public  User getUser(String uno){
		User user = null;
		try{
			con = getConnection();
			ps = con.prepareStatement("select u_name,u_email,u_state,h_id from user where u_no=?");
			ps.setString(1, uno);
			rs = ps.executeQuery();
			while(rs.next()){		//遍历结果集
				String uname = rs.getString(1);
				String uemail = rs.getString(2);
				String ustate = rs.getString(3);
				int hid = Integer.valueOf(rs.getString(4));
				user = new User(uno, uname, uemail, ustate, hid);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return user;
	}
	
	
	//得到某个用户的访客列表
	public  ArrayList<Visitor> getVisitors(String uno){
		ArrayList<Visitor> result = new ArrayList<Visitor>();
		try{
			con = getConnection();		//获得数据库连接
			ps = con.prepareStatement("select user.u_no,user.u_name,user.h_id,date_format(visit.v_date,'%Y-%c-%e %k:%i:%s') from user,visit" +
					" where user.u_no=visit.v_no and visit.u_no=? order by visit.v_date desc");	//昵称、头像、时间
			ps.setInt(1, Integer.valueOf(uno));
			rs = ps.executeQuery();			//执行查询
			while(rs.next()){				//遍历结果集
				String v_no = rs.getInt(1)+"";
				String v_name = rs.getString(2);
				String h_id = rs.getInt(3)+"";
				String v_date = rs.getString(4);
				Visitor v = new Visitor(v_no, v_name, h_id, v_date);
				result.add(v);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//方法：删除指定好友
	public  String deleteFriend(String u_no,String u_noToDelete){
		String result = null;
		try{
			con = getConnection();	//获取数据库连接
			ps = con.prepareStatement("delete from friend where u_noz=? and u_noy=?");	//创建语句
			ps.setInt(1, Integer.valueOf(u_no));
			ps.setInt(2, Integer.valueOf(u_noToDelete));
			int count = ps.executeUpdate();		//执行语句
			if(count == 1){	//删除成功
				result = DELETE_SUCCESS;
			}
			else{
				result = DELETE_FAIL;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	//方法：检查用户名和密码是否正确
	public ArrayList<String> checkAndroidLogin(String u_no,String u_pwd){
		ArrayList<String> result = new ArrayList<String>();
		try{
			con = getConnection();		//获取数据库连接
			if(con == null){			//判断数据库连接对象是否
				result.add(CONNECTION_OUT);		//
				return result;
			}
			ps = con.prepareStatement("select u_no,u_name,u_email,u_state,h_id from user where u_no=? and u_pwd=?");
			ps.setString(1, u_no);				//设置预编译语句的参数
			ps.setString(2, u_pwd);				//设置预编译语句的参数
			rs = ps.executeQuery();
			if(rs.next()){				//判断结果集是否为空
				for(int i=1;i<=5;i++){
					result.add(rs.getString(i));	//将结果集中数据存放到ArrayList中
				}
			}
			else{						//如果数据库查无此人
				result.add(LOGIN_FAIL);	//返回登录出错信息
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}

	

}
