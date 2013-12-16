package com.blog.bean;

public class User {
	public String u_no;
	public String u_name;
	public String u_email;
	public String u_state;
	public int h_id;
	public User(String u_no,String u_name,String u_email,String u_state,int h_id){
		this.u_no = u_no;
		this.u_name = u_name;
		this.u_state = u_state;
		this.u_email = u_email;
		this.h_id = h_id;
	}
}