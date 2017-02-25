package cn.itcast.bookstore.order.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.bookstore.order.domain.OrderItem;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 添加订单
	 * @param order
	 */
	public void addOrder(Order order){
		try{
			String sql="insert into orders values(?,?,?,?,?,?)";
			Timestamp timestamp = new Timestamp(order.getOrdertime().getTime());
			Object [] params={order.getOid(),timestamp,
					order.getTotal(),order.getState(),
					order.getOwner().getUid(),order.getAddress()};
			qr.update(sql, params);
			
			
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
		
		
		
	}
	/**
	 * 生成订单条目
	 * @param orderItemList
	 */
	public void addOrderItemList(List<OrderItem> orderItemList){
		try{
			String sql="insert into orderitem values(?,?,?,?,?)";
			Object [][] params = new Object[orderItemList.size()][];
			for(int i=0;i<orderItemList.size();i++){
				OrderItem item = orderItemList.get(i);
				params[i] = new Object[]{item.getIid(),item.getCount(),
						item.getSubtotal(),item.getOrder().getOid(),
						item.getBook().getBid()};
			}
			qr.batch(sql, params);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}	
	}
	
	public List<Order> findByUid(String uid) {
		/**
		 * 通过uid查询当前用户的多有order
		 * 循环遍历每个order，为其加载所有OrderItem
		 */
		try{
			/**
			 * 得到当前用户的所有订单
			 */
			String sql = "select * from orders where uid=?";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class),uid);
			/**
			 * 循环遍历每个订单，为其加载所有订单条目
			 */
			for(Order order : orderList){
				loadOrderItems(order);
			}
			return orderList;
		}catch(SQLException e){
			throw new RuntimeException(e);
		}

	}
	/**
	 * 加载指定的订单左右的订单条目
	 * @param order
	 * @throws SQLException 
	 */
	private void loadOrderItems(Order order) throws SQLException {
		//多表查询
		/**
		 * 查询两张表，orderItem,book
		 */
		String sql ="select * from orderitem i,book b where i.bid=b.bid and oid=?";
		/*
		 * 因为
		 */
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(),order.getOid());
		
		
		List<OrderItem> orderitemList = toOrderItemList(mapList);
		order.setOrderItemList(orderitemList);
	}
	/**
	 * 把mapList中每个map转换成两个对象，并建立关系
	 * @param mapList
	 * @return
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String,Object> map:mapList){
			OrderItem item=toOrderItem(map);
			orderItemList.add(item);
		}

		return orderItemList;
	}
	/**
	 * 把一个map转换成一个orderItem对象
	 * @param map
	 * @return
	 */
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book =CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}
	public Order load(String oid) {
		// TODO Auto-generated method stub
		try{
			/**
			 * 得到当前用户的所有订单
			 */
			String sql = "select * from orders where oid=?";
			Order order = qr.query(sql, new BeanHandler<Order>(Order.class),oid);
			
			loadOrderItems(order);
			
			return order;
		}catch(SQLException e){
			throw new RuntimeException(e);
		}

	}
	
	/**
	 * 查询订单状态
	 * @param oid
	 * @return
	 */
	public int getStateByOid(String oid){
		try{
			String sql="select state from orders where oid=?";
			Number number = (Number) qr.query(sql, new ScalarHandler(),oid);
			return number.intValue();
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	/**
	 * 修改订单状态
	 * @param oid
	 * @param state
	 * @return
	 */
	public void updateState(String oid,int state){
		try{
			String sql="update orders set state=? where oid=?";
			qr.update(sql,state,oid);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	public List<Order> findAll() {
		try{
			/**
			 * 得到当前用户的所有订单
			 */
			String sql = "select * from orders";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class));
			/**
			 * 循环遍历每个订单，为其加载所有订单条目
			 */
			for(Order order : orderList){
				loadOrderItems(order);
			}
			return orderList;
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}
	public List<Order> findByState(String state) {
		try{
			/**
			 * 得到当前用户的所有订单
			 */
			int State = Integer.parseInt(state);
			String sql = "select * from orders where state=?";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class),State);
			/**
			 * 循环遍历每个订单，为其加载所有订单条目
			 */
			for(Order order : orderList){
				loadOrderItems(order);
			}
			return orderList;
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	
}
