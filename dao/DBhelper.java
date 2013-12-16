package com.blog.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBhelper
{
	private Connection connection ;
	
	public DBhelper()
	{
		this.connection = this.getConnection();
	}
	
	private Connection getConnection()
	{
		Connection con = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		try
		{
			 con=DriverManager.getConnection("jdbc:mysql://localhost/kdwb_db","root","root");
			return con;
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return con;
	}
	
	
	public void closeConnect(Connection con,PreparedStatement ps)
	{
		try
		{
			con.close();
			ps.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
		
		
		
	

}
