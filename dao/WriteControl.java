package com.blog.dao;

import static com.blog.dao.ConnectionUtil.closeConnect;
import static com.blog.dao.ConnectionUtil.getConnection;
import static com.blog.dao.ConnectionUtil.getMax;
import static com.blog.dao.ConstantUtil.DIARY;
import static com.blog.dao.ConstantUtil.DIARY_FAIL;
import static com.blog.dao.ConstantUtil.DIARY_SUCCESS;
import static com.blog.dao.ConstantUtil.UPDATE_STATE_FAIL;
import static com.blog.dao.ConstantUtil.UPDATE_STATE_SUCCESS;
import static com.blog.dao.ConstantUtil.COMMENT;
import static com.blog.dao.ConstantUtil.VISIT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.blog.bean.Diary;

import com.blog.bean.Comments;

public class WriteControl
{
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;

	// 更新心情
	public String updateState(String u_no, String state)
	{
		String result = null;
		con = getConnection();
		try
		{
			ps = con.prepareStatement("update user set u_state=? where u_no=?");
			ps.setString(1, state);
			ps.setInt(2, Integer.valueOf(u_no));
			int count = ps.executeUpdate();
			if (count == 1) // 修改成功
			{
				result = UPDATE_STATE_SUCCESS;
			}

			else
			{

				result = UPDATE_STATE_FAIL;
			}

		}

		catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnect(con, ps);
		}

		return result;
	}

	// 写入新日志
	public String writeNewDiary(String title, String content, String author)
	{
		String result = null;
		int diary_id = getMax(DIARY);

		try
		{
			con = getConnection();
			ps = con
					.prepareStatement("insert into diary(r_id,r_title,r_content,u_no) values(?,?,?,?) ");
			ps.setInt(1, diary_id);
			ps.setString(2, title);
			ps.setString(3, content);
			ps.setInt(4, Integer.valueOf(author)); // 用户ID

			int count = ps.executeUpdate();

			if (count == 1)
			{
				result = DIARY_SUCCESS;
			} else
			{
				result = DIARY_FAIL;
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnect(con, ps);
		}
		return result;
	}

	// 得到某用户日志总数
	public int getDiarySize(String u_no)
	{
		int result = 0;
		try
		{

			con = getConnection();
			ps = con
					.prepareStatement("select count(r_id) as count from diary where u_no=?");
			ps.setInt(1, Integer.valueOf(u_no)); // 设置参数
			rs = ps.executeQuery();
			if (rs.next())
			{ // 查看结果集 只有一条数据，就是日志数量
				result = rs.getInt(1);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnect(con, ps);
		}
		return result;
	}

	// 查询用户的日记列表的方法
	public ArrayList<Diary> getUserDiary(String u_no, int currentPage, int span)
	{
		ArrayList<Diary> result = new ArrayList<Diary>();
		// 声明结果集对象
		int start = (currentPage - 1) * span; // 计算起始位置
		String sql = "select diary.r_id,diary.r_title,diary.r_content,date_format(diary.r_date,'%Y-%c-%e %k:%i:%s'),diary.u_no,user.u_name from diary,user where diary.u_no=? and diary.u_no=user.u_no  order by diary.r_date desc limit ?,?"; // 构建语句对象
		// 日记标题、日记内容、日记时间、日记所属者、日记所属者昵称
		try
		{
			con = getConnection(); // 获得连接
			ps = con.prepareStatement(sql);

			ps.setInt(1, Integer.valueOf(u_no));
			ps.setInt(2, start);
			ps.setInt(3, span);

			rs = ps.executeQuery(); // 执行查询

			while (rs.next())
			{ // 读取结果集生成日记对象
				String rid = rs.getInt(1) + "";
				String title = rs.getString(2);
				String content = rs.getString(3);
				String date = rs.getString(4);
				String uno = rs.getInt(5) + "";
				String uname = rs.getString(6);
				Diary d = new Diary(rid, title, content, uname, uno, date);
				result.add(d);
			}

			for (Diary d : result)
			{ // 为每个日记生成评论列表
				ArrayList<Comments> cmtList = getComments(d.rid);
				d.setCommentList(cmtList);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnect(con, ps);
		}
		return result;
	}

	// 获取用户日志评论列表的方法
	public ArrayList<Comments> getComments(String r_id)
	{
		ArrayList<Comments> result = new ArrayList<Comments>();

		String sql = "select date_format(comment.c_date,'%Y-%c-%e %k:%i:%s'),comment.c_content,user.u_name,comment.u_no"
				+ " from comment,user where comment.r_id=? and user.u_no=comment.u_no order by comment.c_date desc";
		try
		{
			con = getConnection(); // 获得连接
			ps = con.prepareStatement(sql); // 获得预编译语句
			ps.setInt(1, Integer.valueOf(r_id)); // 设置参数
			rs = ps.executeQuery(); // 执行查询
			while (rs.next())
			{
				String date = rs.getString(1);
				String content = rs.getString(2);
				String uname = rs.getString(3);
				String uno = rs.getString(4) + "";
				Comments c = new Comments(date, content, uname, uno);
				result.add(c);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	public  int addComment(String c_comment,String r_id,String u_no){
		int result = -1;
		Connection con = null;
		PreparedStatement ps = null;
		try{
			con = getConnection();	//获得数据库连接
			ps = con.prepareStatement("insert into comment(c_id,c_content,u_no,r_id) values(?,?,?,?)");
			ps.setInt(1, getMax(COMMENT));		//设置自动编号的值
			ps.setString(2, c_comment);			//设置评论内容字段
			ps.setInt(3, Integer.valueOf(u_no));	//设置用户编号
			ps.setInt(4, Integer.valueOf(r_id));	//设置日记编号
			result = ps.executeUpdate();			//执行插入操作
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//添加访问记录
	public  int addVisitor(String host,String visitor){
		int result = -1;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			closeConnect(con, ps);
		
			}
		return result;
	}
	
	
	//方法：删除指定日记
	public  int deleteDiary(String rid){
		int result = -1;
		
		try{
			deleteAllCommentByDiary(rid);			//先删除评论
			con = getConnection();
			ps = con.prepareStatement("delete from diary where r_id=?");
			ps.setInt(1, Integer.valueOf(rid));
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	//方法：删除指定日志的所有评论
	public  int deleteAllCommentByDiary(String rid){
		int result = 0;
		try{
			con = getConnection();
			ps = con.prepareStatement("delete from comment where r_id=?");
			ps.setInt(1, Integer.valueOf(rid));
			result = ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			closeConnect(con, ps);
		}
		return result;
	}
	
	
	
	//方法：修改指定日志
	public  int modifyDiary(String rid,String rtitle,String rcontent){
		int result = 0;
		try{
			con = getConnection();
			ps = con.prepareStatement("update diary set r_title=?,r_content=?,r_date=now() where r_id=?");
			ps.setString(1,rtitle);
			ps.setString(2,rcontent);
			ps.setInt(3, Integer.valueOf(rid));
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
