package cn.itcast.bookstore.user.service;


import cn.itcast.bookstore.user.dao.UserDao;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.domain.adminUser;

/*
 * User��ҵ���
 */


public class UserService {
	private UserDao userDao = new UserDao();
	
	
	/**
	 * ע��
	 * @param form
	 * @throws UserException
	 */
	public void regist(User form)throws UserException{
		/**
		 * У���û���
		 */
		User user = userDao.findByUsername(form.getUsername());
		if(user != null) throw new UserException("�û����ѱ�ע�ᣡ");
		
		/**
		 * У��Email
		 */
		user = userDao.findByUsername(form.getEmail());
		if(user != null) throw new UserException("Email�ѱ�ע�ᣡ");
		/**
		 * �����û������ݿ���
		 */
		userDao.add(form);
	}
	
	
	/**
	 * ����
	 * @param code
	 * @throws UserException
	 */
	public void active(String code)throws UserException{
		/**
		 * ʹ��code��ѯ���ݿ�
		 */
		User user = userDao.findByCode(code);
		if(user==null) throw new UserException("��������Ч");
		/**
		 * �����δ�����򼤻�������Ѽ���˵���Ƕ��μ���׳��쳣
		 */
		if(user.isStats()) throw new UserException("���Ѿ������˲����ٴμ���");
		/**
		 * �޸��û���״̬
		 */
		userDao.updateState(user.getUid(), true);
		
	}
	
	/**
	 * ��¼
	 * @param from
	 * @return
	 * @throws UserException
	 */
	
	public User login(User from) throws UserException{
		User user = userDao.findByUsername(from.getUsername());
		if(user==null) throw new UserException("�û�������");
		if(!user.getPassword().equals(from.getPassword())) throw new UserException("�������");
		if(user.isStats()) throw new UserException("��δ������Ͽ�ȥ����"+user.getEmail()+"����");
		
		return user;
	}


	public adminUser adminLogin(adminUser adminUser) {
		return userDao.adminLogin(adminUser);
		
	}
	
	
	
	
}
