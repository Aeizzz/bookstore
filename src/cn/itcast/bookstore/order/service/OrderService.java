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
	 * ��Ӷ���
	 * 
	 * @param order
	 */
	public void add(Order order) {
		try {
			// ��������
			JdbcUtils.beginTransaction();
			orderDao.addOrder(order);
			orderDao.addOrderItemList(order.getOrderItemList());

			// �ύ����
			JdbcUtils.commitTransaction();

		} catch (SQLException e) {
			// �ع�����
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}

	}

	/**
	 * �ҵĶ���
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
		 * ͨ��oid���鶩��״̬���������3���쳣
		 */
		int state = orderDao.getStateByOid(oid);
		if (state != 3)
			throw new OrderException("����ȷ��ʧ��");
		/**
		 * �޸Ķ���״̬Ϊ4����ʾ���׳ɹ�
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
		if(state!=2) throw new OrderException("����δ����");
		orderDao.updateState(oid, 3);
	}
	
	

}
