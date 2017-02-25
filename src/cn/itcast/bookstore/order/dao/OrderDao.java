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
	 * ��Ӷ���
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
	 * ���ɶ�����Ŀ
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
		 * ͨ��uid��ѯ��ǰ�û��Ķ���order
		 * ѭ������ÿ��order��Ϊ���������OrderItem
		 */
		try{
			/**
			 * �õ���ǰ�û������ж���
			 */
			String sql = "select * from orders where uid=?";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class),uid);
			/**
			 * ѭ������ÿ��������Ϊ��������ж�����Ŀ
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
	 * ����ָ���Ķ������ҵĶ�����Ŀ
	 * @param order
	 * @throws SQLException 
	 */
	private void loadOrderItems(Order order) throws SQLException {
		//����ѯ
		/**
		 * ��ѯ���ű�orderItem,book
		 */
		String sql ="select * from orderitem i,book b where i.bid=b.bid and oid=?";
		/*
		 * ��Ϊ
		 */
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(),order.getOid());
		
		
		List<OrderItem> orderitemList = toOrderItemList(mapList);
		order.setOrderItemList(orderitemList);
	}
	/**
	 * ��mapList��ÿ��mapת�����������󣬲�������ϵ
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
	 * ��һ��mapת����һ��orderItem����
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
			 * �õ���ǰ�û������ж���
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
	 * ��ѯ����״̬
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
	 * �޸Ķ���״̬
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
			 * �õ���ǰ�û������ж���
			 */
			String sql = "select * from orders";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class));
			/**
			 * ѭ������ÿ��������Ϊ��������ж�����Ŀ
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
			 * �õ���ǰ�û������ж���
			 */
			int State = Integer.parseInt(state);
			String sql = "select * from orders where state=?";
			List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class),State);
			/**
			 * ѭ������ÿ��������Ϊ��������ж�����Ŀ
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
