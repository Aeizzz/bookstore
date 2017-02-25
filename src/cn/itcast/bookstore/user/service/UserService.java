package cn.itcast.bookstore.user.service;


import cn.itcast.bookstore.user.dao.UserDao;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.domain.adminUser;

/*
 * User的业务层
 */


public class UserService {
	private UserDao userDao = new UserDao();
	
	
	/**
	 * 注册
	 * @param form
	 * @throws UserException
	 */
	public void regist(User form)throws UserException{
		/**
		 * 校验用户名
		 */
		User user = userDao.findByUsername(form.getUsername());
		if(user != null) throw new UserException("用户名已被注册！");
		
		/**
		 * 校验Email
		 */
		user = userDao.findByUsername(form.getEmail());
		if(user != null) throw new UserException("Email已被注册！");
		/**
		 * 插入用户到数据库中
		 */
		userDao.add(form);
	}
	
	
	/**
	 * 激活
	 * @param code
	 * @throws UserException
	 */
	public void active(String code)throws UserException{
		/**
		 * 使用code查询数据库
		 */
		User user = userDao.findByCode(code);
		if(user==null) throw new UserException("激活码无效");
		/**
		 * 如果是未激活则激活，否则是已激活说明是二次激活，抛出异常
		 */
		if(user.isStats()) throw new UserException("你已经激活了不用再次激活");
		/**
		 * 修改用户的状态
		 */
		userDao.updateState(user.getUid(), true);
		
	}
	
	/**
	 * 登录
	 * @param from
	 * @return
	 * @throws UserException
	 */
	
	public User login(User from) throws UserException{
		User user = userDao.findByUsername(from.getUsername());
		if(user==null) throw new UserException("用户不存在");
		if(!user.getPassword().equals(from.getPassword())) throw new UserException("密码错误");
		if(user.isStats()) throw new UserException("尚未激活请赶快去邮箱"+user.getEmail()+"激活");
		
		return user;
	}


	public adminUser adminLogin(adminUser adminUser) {
		return userDao.adminLogin(adminUser);
		
	}
	
	
	
	
}
