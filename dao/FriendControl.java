package com.blog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.blog.bean.User;
import static com.blog.dao.ConnectionUtil.getConnection;
import static com.blog.dao.ConnectionUtil.closeConnect;
import static com.blog.dao.ConnectionUtil.getMax;
import static com.blog.dao.ConstantUtil.FEIEND;


public class FriendControl
{
	private Connection con = null;
	private PreparedStatement ps = null;
	private ResultSet rs = null;
	
	public  ArrayList<User> searchFriendByName(String u_name){
		ArrayList<User> result = new ArrayList<User>();
		System.out.println("关键字："+u_name);
		try{
			con = getConnection();
			ps = con.prepareStatement("select u_no,u_name,u_email,u_state,h_id from user where u_name like '%"+u_name+"%'");
			//String str = "\'%"+u_name+"%\'";  //'%小明%'
			//ps.setString(1,u_name);
			rs = ps.executeQuery();
			int i = 1;
			while(rs.next()){			//遍历结果集
				System.out.println(i);
				i++;
				String uno = rs.getInt(1)+"";
				String uname = new String(rs.getString(2));
				String uemail = new String(rs.getString(3));
				String ustate = new String(rs.getString(4));
				int hid = Integer.valueOf(new String(rs.getString(5)));
				User u = new User(uno, uname, uemail, ustate, hid);		//创建User对象
				result.add(u);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			closeConnect(con, ps);
		}
		return result;
	}
	
	public boolean isMyFriend(String my_no,String stranger_no)
	{
		
		
		
		try
		{
			con = getConnection();
			ps = con.prepareStatement("select f_id from friend where u_noz=? and u_noy=?");
			ps.setInt(1,Integer.valueOf(my_no) );
			ps.setInt(2,Integer.valueOf(stranger_no));
			
			rs = ps.executeQuery();
			
			if(rs.next())  //非空，说明有匹配朋友关系的用户
			{
				return true;
			}
		} 
		
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			closeConnect(con, ps);
		}
		
		return false;
	}
	
	public int makeFriend(String my_id,String stranger_id)
	{
		int result = -1;
		int friend_id = getMax(FEIEND);  //得到朋友关系表中最大的id（已经完成自增）
		try
		{
			con = getConnection();
			ps = con.prepareStatement("insert into friend(f_id,u_noz,u_noy) values(?,?,?)");
			ps.setInt(1, friend_id);
			ps.setInt(2, Integer.valueOf(my_id));
			ps.setInt(3,Integer.valueOf(stranger_id));
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
	
	
	//获得好友列表
	public  ArrayList<User> getFriendList(String u_no)
	{
		ArrayList<User> result = new ArrayList<User>();
		con = getConnection();
		try
		{
			ps = con.prepareStatement("select user.u_no,user.u_name,user.u_email,user.u_state,user.h_id " +
						"from user ,friend " +
						"where user.u_no=friend.u_noy and friend.u_noz=?");
			ps.setInt(1, Integer.valueOf(u_no));
			rs = ps.executeQuery();
			
			while(rs.next())
			{
				String no = rs.getInt(1)+"";
				String name = rs.getString(2);
				String email = rs.getString(3);
				String state = rs.getString(4);
				int  hid = rs.getInt(5);
				User user = new User(no, name, email, state, hid);
				result.add(user);
			}
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
	

}
