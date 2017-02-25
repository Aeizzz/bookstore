package cn.itcast.bookstore.category.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.book.dao.BookDao;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.web.servlet.admin.CategoryException;
import cn.itcast.jdbc.TxQueryRunner;

public class Categorydao {
	private QueryRunner qr = new TxQueryRunner();
	
	public List<Category> findALL() {
		try {
			String sql = "select * from category";
			return qr.query(sql, new BeanListHandler<Category>(Category.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public void add(Category category) {
		try{
			String sql="insert into category values(?,?)";
			qr.update(sql, category.getCid(),category.getCname());
			
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	/**
	 * 删除分类
	 * @param cid
	 */
	public void delete(String cid) {
		try{
			String sql="delete from category where cid=?";
			qr.update(sql,cid);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
			
			
		
	}
	/**
	 * 加载分类
	 * @param cid
	 * @return
	 */
	public Category load(String cid) {
		try{
			String sql="select * from category where cid=?";
			Category category = qr.query(sql, new BeanHandler<Category>(Category.class),cid);
			return category;
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}

	public void edit(Category category) {
		try{
			String sql="update category set cname=? where cid=?";
			qr.update(sql, category.getCname(),category.getCid());
			
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
}
