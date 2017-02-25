package cn.itcast.bookstore.order.service;

import java.sql.SQLException;
import java.util.List;

import javax.naming.ldap.Rdn;

import cn.itcast.bookstore.order.dao.OrderDao;
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.jdbc.JdbcUtils;

public class OrderService {
	private OrderDao orderDao = new OrderDao();

	/**
	 * 添加订单
	 * 
	 * @param order
	 */
	public void add(Order order) {
		try {
			// 开启事务
			JdbcUtils.beginTransaction();
			orderDao.addOrder(order);
			orderDao.addOrderItemList(order.getOrderItemList());

			// 提交事务
			JdbcUtils.commitTransaction();

		} catch (SQLException e) {
			// 回滚事务
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}

	}

	/**
	 * 我的订单
	 * 
	 * @param uid
	 * @return
	 */
	public List<Order> myOrder(String uid) {
		// TODO Auto-generated method stub
		return orderDao.findByUid(uid);
	}

	public Order load(String oid) {
		return orderDao.load(oid);

	}

	public void confirm(String oid) throws OrderException {
		/**
		 * 通过oid检验订单状态，如果不是3抛异常
		 */
		int state = orderDao.getStateByOid(oid);
		if (state != 3)
			throw new OrderException("订单确认失败");
		/**
		 * 修改订单状态为4，表示交易成功
		 */
		orderDao.updateState(oid, 4);

	}

	public void pay(String oid) {
		int state = orderDao.getStateByOid(oid);
		if (state == 1) {
			orderDao.updateState(oid, 2);
		}

	}

	public List<Order> findAll() {
		return  orderDao.findAll();
	}

	public List<Order> findByState(String state) {
		return orderDao.findByState(state);
	}

	public void fahuo(String oid) throws OrderException {
		int state = orderDao.getStateByOid(oid);
		if(state!=2) throw new OrderException("订单未付款");
		orderDao.updateState(oid, 3);
	}
	
	

}
