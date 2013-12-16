package com.blog.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionUtil
{
	public static Connection getConnection(){
		Connection con = null;
		//使用JDBC直接访问数据库
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost/kdwb_db","root","root");			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return con;
	}
	
	//关闭连接
	public static void closeConnect(Connection con,PreparedStatement ps)
	{
		try
		{
			if(con != null)
			{
				con.close();
			}
			con = null;
			
			if(ps != null)
			{
				ps.close();
			}
			ps = null;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}	
	
	
	
	//方法:获取指定表中的当前id最大编号，该方法为同步方法
	public static synchronized int getMax(String table){
		int max = -1;
		Connection con = null;			//声明数据库连接对象
		Statement st = null;
		ResultSet rs = null;
		try{
			con = getConnection();		//获取数据库连接
			st = con.createStatement();	//创建一个Statement对象
			String sql = "update max_id set "+table+"="+table+"+1";
			st.executeUpdate(sql);					//更新最大编号
			rs = st.executeQuery("select "+table+" from max_id");				//查询最大编号
			if(rs.next()){
				max = rs.getInt(1);
				return max;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(rs != null){
					rs.close();
					rs = null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				if(st != null){
					st.close();
					st = null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			try{
				if(con != null){
					con.close();
					con = null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return max;
	}

}
