package cn.itcast.bookstore.cart.domain;

import java.math.BigDecimal;

import cn.itcast.bookstore.book.domain.Book;


/**
 * 购物车条目
 * @author liuhonglei
 *
 */
public class CartItem {
	private Book book = new Book();
	private int count;
	//小计，
	public double getSubtotal(){
		BigDecimal d1 = new BigDecimal(book.getPrice()+"");
		BigDecimal d2 = new BigDecimal(count+"");
		BigDecimal d3 = d1.multiply(d2);
		return d3.doubleValue();
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	

}
