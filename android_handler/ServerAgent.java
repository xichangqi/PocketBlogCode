package com.blog.android_handler;
import static com.blog.dao.ConstantUtil.DIARY_SUCCESS;
import static com.blog.dao.ConstantUtil.REGISTER_FAIL;
import static com.blog.dao.ConstantUtil.UPDATE_STATE_SUCCESS;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA_2_3.portable.OutputStream;

import com.blog.bean.Comments;
import com.blog.bean.Diary;
import com.blog.bean.PhotoInfo;
import com.blog.bean.User;
import com.blog.bean.Visitor;
import com.blog.dao.FriendControl;
import com.blog.dao.PhotoControl;
import com.blog.dao.UserControl;
import com.blog.dao.WriteControl;
import com.sun.corba.se.spi.ior.WriteContents;


public class ServerAgent extends Thread{
	public Socket socket;
	public DataInputStream din;
	public DataOutputStream dout;
	boolean flag = false;
	
	public ServerAgent(Socket socket){
		this.socket = socket;
		try {
			this.din = new DataInputStream(socket.getInputStream());
			this.dout = new DataOutputStream(socket.getOutputStream());
			flag =true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//�������߳�ִ�з���
	
	
	
	public void run(){
		while(flag){
			try {
				String msg = din.readUTF();			//���տͻ��˷�������Ϣ
				
				System.out.println("�յ�����Ϣ�ǣ�"+msg);	
				if(msg.startsWith("<#LOGIN#>")){				//��ϢΪ��¼
					String content = msg.substring(9);			//�����Ϣ����
					String [] sa = content.split("\\|");
					UserControl controler = new UserControl();
					ArrayList<String> result = controler.checkAndroidLogin(sa[0], sa[1]);
					if(result.size()>1){			//��¼�ɹ�
						StringBuilder sb = new StringBuilder();
						sb.append("<#LOGIN_SUCCESS#>");
						for(String s:result){
							sb.append(s);
							sb.append("|");
						}
						String loginInfo = sb.substring(0,sb.length()-1);
						dout.writeUTF(loginInfo);			//�����û��Ļ�����Ϣ			
					}
					else{				//��¼ʧ��
						String loginInfo = "<#LOGIN_FAIL#>"+result.get(0);
						dout.writeUTF(loginInfo);
						}
					}
				else if(msg.startsWith("<#USER_LOGOUT#>")){			//��ϢΪ�û��ǳ�
					this.din.close();
					this.dout.close();
					this.flag = false;
					this.socket.close();
					this.socket = null;
				}
				else if(msg.startsWith("<#REGISTER#>")){			//��ϢΪ�û�ע��
					msg = msg.substring(12);	//����ַ���ֵ
					String [] sa = msg.split("\\|");	//�и��ַ���
					UserControl controler = new UserControl();
					User user = controler.registerUser(sa[0], sa[1], sa[2], sa[3],1);
					if(user == null){		//ע��ʧ��
						dout.writeUTF("<#REG_FAIL#>");
					}
					else{
						dout.writeUTF("<#REG_SUCCESS#>"+user.u_no);  //�����û��˺�
					}
				}
				
				else if(msg.startsWith("<#NEW_DIARY#>")){					//��ϢΪ�������ռ�
					msg = msg.substring(13);				//�����Ϣ����
					String [] sa = msg.split("\\|");		//����ַ�������
					WriteControl controler = new WriteControl();
					String result = controler.writeNewDiary(sa[0], sa[1], sa[2]);
					String reply = "";
					if(result.equals(DIARY_SUCCESS)){			//��־�����ɹ�
						reply = "<#DIARY_SUCCESS#>";
					}
					else {
						reply = "<#DIARY_FAIL#>";
					}
					dout.writeUTF(reply);					//�����ظ���Ϣ
				}
				
			
				else if(msg.startsWith("<#NEW_STATUS#>")){				//��ϢΪ��������
					msg = msg.substring(14);			//��ȡ����
					String [] sa = msg.split("\\|");	//�и��ַ���
					WriteControl controler = new WriteControl();
					String result = controler.updateState(sa[1], sa[0]);	//
					if(result.equals(UPDATE_STATE_SUCCESS)){		//�����������ɹ�
						dout.writeUTF("<#STATUS_SUCCESS#>");
					}
					else{
						dout.writeUTF("<#STATUS_FAIL#>");			//�����������ʧ����Ϣ
					}
				}
				
				
				else if(msg.startsWith("<#FRIEND_LIST#>")){			//��ϢΪ��ú����б�
					msg = msg.substring(15);
					FriendControl controler = new FriendControl();
					ArrayList<User> list = controler.getFriendList(msg);	//��ú��ѵ��б�
					dout.writeInt(list.size());		//��֪�ͻ��˺����б��ĳ���
					for(int i=0;i<list.size();i++){
						User u = list.get(i);			//��øô���User����
						StringBuilder sb = new StringBuilder();	//����StringBuilder
						sb.append(u.u_no);
						sb.append("|");
						sb.append(u.u_name);
						sb.append("|");
						sb.append(u.u_email);
						sb.append("|");
						sb.append(u.u_state);
						sb.append("|");
						sb.append(u.h_id);
						dout.writeUTF(sb.toString());			//����������Ϣ
						PhotoControl pControler = new PhotoControl();
						Blob blob = pControler.getHeadBlob(String.valueOf(u.h_id));	//���ָ���û���ͷ��Blob
						byte [] buf = blob.getBytes(1l, (int)blob.length());	//����ֽ�����
						dout.writeInt(buf.length);
						dout.write(buf);
						dout.flush();				
					}
				}
				
				else if(msg.startsWith("<#VISITOR_LIST#>")){					//��ϢΪ��ʾ����ÿ�����
					msg = msg.substring(16);			//��ȡ��Ϣ����
					UserControl controler = new UserControl();
					ArrayList<Visitor> visitorList = controler.getVisitors(msg);		//��ȡ�ÿ��б�
					int size = visitorList.size();
					dout.writeInt(size);			//��֪�ͻ������ݵĳ���
					for(int i=0;i<size;i++){		//�����ÿ��б�
						StringBuilder sb = new StringBuilder();
						Visitor v = visitorList.get(i);
						sb.append(v.v_no);
						sb.append("|");
						sb.append(v.v_name);
						sb.append("|");
						sb.append(v.v_date);
						sb.append("|");
						sb.append(v.h_id);
						dout.writeUTF(sb.toString());
						PhotoControl pControler = new PhotoControl();
						Blob blob = pControler.getHeadBlob(v.h_id);	//���ͷ��Blob����
						byte [] buf = blob.getBytes(1l, (int)blob.length());	//��ȡ�ֽ�����
						dout.writeInt(buf.length);
						dout.write(buf);		//���ֽ����鷢��
						dout.flush();
					}
				}
				else if(msg.startsWith("<#GET_DIARY#>")){			//��ϢΪ����ռ��б�
					msg = msg.substring(13);					//��ȡ��Ϣ����
					String [] sa = msg.split("\\|");			//�ָ��ַ���
					WriteControl controler = new WriteControl();
					ArrayList<Diary> diaryList = controler.getUserDiary(sa[0], Integer.valueOf(sa[1]), 5);
					int size = diaryList.size();
					dout.writeInt(size);						//��ͻ��˷����ռǳ���
					for(int i=0;i<size;i++){
						StringBuilder sb = new StringBuilder();
						Diary d= diaryList.get(i);
						sb.append(d.rid);			//��־id
						sb.append("|");
						sb.append(d.title);		//��־����
						sb.append("|");
						sb.append(d.content);		//��־����
						sb.append("|");
						sb.append(d.time);			//��־����ʱ��
						dout.writeUTF(sb.toString());		//�����ռ��б�
					}
				}
				
				
				else if(msg.startsWith("<#GET_ALBUM_LIST#>")){			//��ϢΪ��ȡ����б�
					msg = msg.substring(18);			//��ȡ����
					PhotoControl controler = new PhotoControl();
					ArrayList<String []> albumList = controler.getAlbumList(msg);
					if(albumList.size() == 0){//����û������
						dout.writeUTF("<#NO_ALBUM#>");
					}
					else{			//�û�����б���Ϊ��
						StringBuilder sb = new StringBuilder();
						for(String [] sa:albumList){
							sb.append(sa[0]);
							sb.append("|");
							sb.append(sa[1]);
							sb.append("|");
							sb.append(sa[2]);
							sb.append("$");
						}
						dout.writeUTF(sb.substring(0, sb.length()-1));						
					}
				}
				
			
				else if(msg.startsWith("<#GET_ALBUM#>")){				//��ϢΪ���ָ�����
					msg = msg.substring(13);		//��ȡ����
					PhotoControl controler = new PhotoControl();
					int albumSize = controler.getAlbumSize(msg);		//��ȡ��᳤��
					dout.writeInt(albumSize);					//����᳤�ȷ����ͻ���
					List<PhotoInfo> photoList = controler.getPhotoInfoByAlbum(msg, 1, albumSize);//��ȡͼƬ��Ϣ
					for(int i=0;i<albumSize;i++){				//ѭ����ȡͼƬ����
						PhotoInfo pi = photoList.get(i);		//���ͼƬ��Ϣ
						StringBuilder sb = new StringBuilder();
						sb.append(pi.p_id);
						sb.append("|");
						sb.append(pi.p_name);
						sb.append("|");
						sb.append(pi.p_des);
						sb.append("|");
						sb.append(pi.x_id);
						dout.writeUTF(sb.toString());
						Blob blob = controler.getPhotoBlob(pi.p_id);		//���ͼƬBlob
						byte [] buf = blob.getBytes(1l, (int)blob.length());		//����ֽ�����
						dout.writeInt(buf.length);				//��֪�ͻ������鳤��
						dout.write(buf);
						dout.flush();
					}						
				}
				
				
				else if(msg.startsWith("<#NEW_ALBUM#>")){				//��ϢΪ���������
					msg = msg.substring(13);				//��ȡ����
					String [] sa = msg.split("\\|");		//�ָ��ַ���
					PhotoControl controler = new PhotoControl();
					int result = controler.createAlbum(sa[0], sa[1]);		//���������
					if(result == 1){		//�����ɹ�
						dout.writeUTF("<#NEW_ALBUM_SUCCESS#>");		//���سɹ���Ϣ
					}
					else{
						dout.writeUTF("<#NEW_ALBUM_FAIL#>");			//���ش���ʧ����Ϣ
					}
				}
				
				
				else if(msg.startsWith("<#NEW_PHOTO#>")){			//��ϢΪ�ϴ���Ƭ
					msg = msg.substring(13);			//��ȡ����
					String [] sa = msg.split("\\|");		//�ָ��ַ���
					int size = din.readInt();			//��ȡͼƬ��С
					byte [] buf = new byte[size];		//�����ֽ�����
					for(int i=0;i<size;i++){
						buf[i] = din.readByte();
					}
					PhotoControl controler = new PhotoControl();
					int result = controler.insertPhotoFromAndroid(buf, sa[0], sa[1], sa[2]);	//����ͼƬ
					if(result == 1){
						dout.writeUTF("<#NEW_PHOTO_SUCCESS#>");		//�����ϴ��ɹ���Ϣ
					}
					else{
						dout.writeUTF("<#NEW_PHOTO_FAIL#>");		//�����ϴ�ʧ�ܵ���Ϣ
					}
				}
				
			
				
				
				else if(msg.startsWith("<#GET_COMMENT#>")){			//��ϢΪ��ȡָ����־�������б�
					msg = msg.substring(15);						//��ȡ����
					WriteControl controler = new WriteControl();
					ArrayList<Comments> cmtList = controler.getComments(msg);	//��������б�
					int size = cmtList.size();			//���۵ĸ���
					dout.writeInt(size);				//�������۵ĸ���
					String reply = din.readUTF();		//�ȴ��ͻ��˷���
					if(reply.equals("<#READY_TO_READ_COMMENT#>")){	//����ͻ����Ѿ�׼����
						for(Comments c:cmtList){
							StringBuilder sb = new StringBuilder();
							sb.append(c.cmtNo);
							sb.append("|");
							sb.append(c.cmtName);
							sb.append("|");
							sb.append(c.content);
							sb.append("|");
							sb.append(c.date);
							dout.writeUTF(sb.toString());		//������Ϣ���ͻ���
						}
					}
				}
				
				
				
				else if(msg.startsWith("<#NEW_COMMENT#>")){		//��ϢΪ��������
					msg = msg.substring(15);
					String [] sa = msg.split("\\|");
					WriteControl controler = new WriteControl();
					int result = controler.addComment(sa[0], sa[1], sa[2]);
					if(result == 1){
						dout.writeUTF("<#NEW_COMMENT_SUCESS#>");
					}
					else{
						dout.writeUTF("<#NEW_COMMENT_FAIL#>");
					}
				}
				
			
				else if(msg.startsWith("<#SEARCH_CONTACT#>")){					//��ϢΪ������ϵ��
					msg = msg.substring(18);			//��ȡ����
					FriendControl controler = new FriendControl();
					ArrayList<User> result = controler.searchFriendByName(msg);
					int size = result.size();
					dout.writeInt(size);				//��֪�ͻ������������
					if(size >0){			//������������Ϊ��
							for(int i=0;i<size;i++){
								StringBuffer sb = new StringBuffer();
								User u = result.get(i);
								sb.append(u.u_no);
								sb.append("|");
								sb.append(u.u_name);
								sb.append("|");
								sb.append(u.u_email);
								sb.append("|");
								sb.append(u.u_state);
								sb.append("|");
								sb.append(u.h_id);
								dout.writeUTF(sb.toString());			//����΢���û���Ϣ
								PhotoControl pControler = new PhotoControl();
								Blob blob = pControler.getHeadBlob(String.valueOf(u.h_id));		//���ͷ��Blob
								byte [] buf = blob.getBytes(1l, (int)blob.length());	//�ֽ�����
								dout.writeInt(buf.length);			//�����ֽ����鳤��
								dout.write(buf);
							}
					}
				}
				
				
				
				else if(msg.startsWith("<#CHANGE_ALBUM_ACCESS#>")){		//��ϢΪ�޸����Ȩ��
					msg = msg.substring(23);
					String [] sa = msg.split("\\|");		//�ָ��ַ���
					PhotoControl controler = new PhotoControl();
					int result = controler.changeAlbumAccess(sa[0], sa[1]);	//�޸�Ȩ��
					if(result == 1){		//�޸ĳɹ�
						dout.writeUTF("<#ALBUM_ACCESS_SUCCESS#>");
					}
					else{
						dout.writeUTF("<#ALBUM_ACCESS_FAIL#>");
					}
				}
				
				
				
				
			
				/*else if(msg.startsWith("<#DELETE_DIARY#>")){		//��ϢΪɾ����־
					msg = msg.substring(16);		//��ȡ����
					WriteControl controler = new WriteControl();
					int result = controler.deleteDiary(msg);
					if(result == 1){		//ɾ���ɹ�
						dout.writeUTF("<#DELETE_DIARY_SUCCESS#>");
					}
					else{
						dout.writeUTF("<#DELETE_DIARY_FAIL#>");
					}
				}*/
				
				
				
				
				/*else if(msg.startsWith("<#MODIFY_DIARY#>")){		//��ϢΪ�޸���־
					msg = msg.substring(16);		//��ȡ����
					String [] sa = msg.split("\\|");	//�ָ��ַ���
					WriteControl controler = new WriteControl();
					int result = controler.modifyDiary(sa[0], sa[1], sa[2]);
					if(result == 1){		//�޸ĳɹ�
						dout.writeUTF("<#MODIFY_DIARY_SUCCESS#>");
					}
					else{
						dout.writeUTF("<#MODIFY_DIARY_FAIL#>");
					}
				}*/
				
				
				
				
				else if(msg.startsWith("<#DELETE_PHOTO#>")){			//��ϢΪɾ����Ƭ
					msg = msg.substring(16);
					PhotoControl controler = new PhotoControl();
					int result = controler.deletePhoto(msg);
					if(result == 1){
						dout.writeUTF("<#DELETE_PHOTO_SUCCESS#>");
					}
					else{
						dout.writeUTF("<#DELETE_PHOTO_FAIL#>");
					}
				}
				
				
				
				else if(msg.startsWith("<#GET_ALBUM_LIST_BY_ACCESS#>")){	//��ϢΪ����Ȩ�޻������б�
					msg = msg.substring(28);	//��ȡ����
					String [] sa = msg.split("\\|");
					PhotoControl controler = new PhotoControl();
					ArrayList<String []> albumList = controler.getAlbumListByAccess(sa[0], sa[1]);
					if(albumList.size() == 0){
						dout.writeUTF("<#NO_ALBUM#>");
					}
					else{
						StringBuilder sb = new StringBuilder();
						for(String [] albumInfo:albumList){
							sb.append(albumInfo[0]);
							sb.append("|");
							sb.append(albumInfo[1]);
							sb.append("$");
						}
						dout.writeUTF(sb.toString());						
					}
				}
			}
			catch(SocketException se){
				try {
					dout.close();
					din.close();
					socket.close();
					socket = null;
					flag = false;
				} catch (IOException e) {
					e.printStackTrace();
				}				
			}
			catch(EOFException eof){
				try {
					dout.close();
					din.close();
					socket.close();
					socket = null;
					flag = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				
			
			//�������͵���������д���
				
			catch (Exception e) {
				flag = false;  //�ͻ�������Ͽ�����ֹ�߳�
				e.printStackTrace();
			}
		}
	}
}