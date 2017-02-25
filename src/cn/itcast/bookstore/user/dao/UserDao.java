package cn.itcast.bookstore.user.dao;


import java.sql.SQLException;

import javax.management.RuntimeErrorException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.domain.adminUser;
import cn.itcast.jdbc.TxQueryRunner;

/*
 * User持久层
 */
public class UserDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 按用户名查询
	 * @param username
	 * @return
	 */
	public User findByUsername(String username){
		try{
			String sql="select * from tb_user where username= ?";
			return qr.query(sql, new BeanHandler<User>(User.class),username);
			
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按emai查询
	 * @param email
	 * @return
	 */
	public User findByEmail(String email){
		try{
			String sql="select * from tb_user where email= ?";
			return qr.query(sql, new BeanHandler<User>(User.class),email);
			
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 添加用户
	 */
	public void add(User user){
		try{
			String sql="insert into tb_user values (?,?,?,?,?,?)";
			Object [] params ={user.getUid(),user.getUsername(),
			                   user.getPassword(),user.getEmail(),
			                   user.getCode(),user.isStats()};
			qr.update(sql,params);
			
 			
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 按激活码查询
	 */
	public User findByCode(String code){
		try{
			String sql="select * from tb_user where code= ?";
			return qr.query(sql, new BeanHandler<User>(User.class),code);
			
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 跟新状态
	 * 修改指定用户的指定状态
	 */
	public void updateState(String uid,boolean state){
		try{
			String sql="update tb_user set state=? where uid=?";
			qr.update(sql, state,uid);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}

	public adminUser adminLogin(adminUser adminUser) {
		try{
			String sql="select * from ad_user where username= ?";
			return qr.query(sql,new BeanHandler<adminUser>(adminUser.class),adminUser.getUsername());
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	
}
