package cn.itcast.bookstore.book.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();

	/**
	 * 查询所有
	 * @return
	 */
		
	public List<Book> findALL() {
		try {
			String sql = "select * from book where del=false";
			return qr.query(sql,new BeanListHandler<Book>(Book.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 按分类查询
	 * @param cid
	 * @return
	 */
	public List<Book> findByCategory(String cid){
		try{
			String sql="select * from book where cid=? and del=false";
			return qr.query(sql, new BeanListHandler<Book>(Book.class),cid);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	/**
	 * 按照图书id查询
	 * @param bid
	 * @return
	 */
	public Book findByBid(String bid){
		try{
			String sql="select * from book where bid=?";
			return qr.query(sql, new BeanHandler<Book>(Book.class),bid);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	/**
	 * 查询指定分类下图书本数
	 * @param cid
	 * @return
	 */
	public int getCountByCid(String cid) {
		try{
			String sql="select count(*) from book where cid=?";
			Number number = (Number) qr.query(sql, new ScalarHandler(),cid);
			return number.intValue();
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	public void delete(String bid){
		try{
			String sql="update book set del=true where bid=?";
			qr.update(sql,bid);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	public void mod(Book book) {
		try{
			String sql="update book set bname=?,price=?,author=?,image=?,cid=? where bid=?";
			Object [] params={book.getBname(),book.getPrice(),
					book.getAuthor(),book.getImage(),
					book.getCid(),book.getBid()};
			qr.update(sql,params);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	public void add(Book book) {
		try{
			String sql="insert into book values(?,?,?,?,?,?,?)";
			Object[] params={book.getBid(),book.getBname(),
					book.getPrice(),book.getAuthor(),
					book.getImage(),book.getCid(),
					book.isDel()};
			qr.update(sql, params);
			
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	
	
	
	

}
