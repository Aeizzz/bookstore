package cn.itcast.bookstore.user.web.servlet;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.cart.domain.Cart;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.service.UserException;
import cn.itcast.bookstore.user.service.UserService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import cn.itcast.servlet.BaseServlet;

/**
 * User表述层
 * 
 * @author liuhonglei
 *
 */

public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();

	/**
	 * 激活功能
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String active(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 获取参数激活码
		 * 调用service方法完成激活
		 * 	>抛异常信息到request域中转发到msg.jsp
		 * 报错成功信息到request域，转发到msg.jsp
		 */
		String code = request.getParameter("code");
		try {
			userService.active(code);
			request.setAttribute("msg", "恭喜你注册成功！请马上登录");
		} catch (UserException e) {
			request.setAttribute("msg",e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}

	/**
	 * 注册功能
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String regist(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * 1.封装表单到form中 2.补全uid，code 3.输入校验 >保存错误信息，form但request域，转发到regist.jsp
		 * 4.调用service方法完成注册 >保存错误信息，form到request域，转发到regist.jsp 5.发邮件
		 * 6.保存成功信息转发到msg.jsp
		 */
		// 封装表单数据
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		// 补全
		form.setUid(CommonUtils.uuid());
		form.setCode(CommonUtils.uuid() + CommonUtils.uuid());
		/**
		 * 输入校验 1。创建map
		 */
		Map<String, String> errors = new HashMap<String, String>();

		// 用户名
		String username = form.getUsername();
		if (username == null || username.trim().isEmpty()) {
			errors.put("username", "用户名不能为空");
		} else if (username.length() < 3 || username.length() > 10) {
			errors.put("username", "用户名要在3~10之间");
		}

		// 密码
		String password = form.getPassword();
		if (password == null || password.trim().isEmpty()) {
			errors.put("password", "密码不能为空");
		} else if (password.length() < 3 || password.length() > 10) {
			errors.put("password", "密码要在3~10之间");
		}
		// email
		String email = form.getEmail();
		if (email == null || email.trim().isEmpty()) {
			errors.put("email", "email不能为空");
		} else if (!email.matches("\\w+@\\w+\\.\\w+")) {
			errors.put("email", "email格式错误");
		}
		/**
		 * 判断是否存在错误
		 */
		if (errors.size() > 0) {
			// 1.保存错误信息
			// 2.保存表单
			// 3.转发regist.jsp
			request.setAttribute("errors", errors);
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}
		/**
		 * 调用service的regist方法
		 */
		try {
			userService.regist(form);

		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}

		/*
		 * 发邮件 准备配置文件 获取配置文件内容
		 */
		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader()
				.getResourceAsStream("email_template.properties"));
		String host = properties.getProperty("host");//获取服务器主机
		String uname = properties.getProperty("uname");//获取用户名
		String pwd = properties.getProperty("pwd");//获取密码
		String from = properties.getProperty("from");//获取发件人
		String to = form.getEmail();//获取收件人
		String subject = properties.getProperty("subject");//获取主题
		String content = properties.getProperty("content");//获取邮件内容
		content = MessageFormat.format(content, form.getCode());//替换{0}
		
		Session session = MailUtils.createSession(host, uname, pwd);//得到session
		Mail mail = new Mail(from, to, subject, content);//创建邮件对象
		try{
			MailUtils.send(session, mail);//发邮件
		}catch(MessagingException e){
			
		}
		// 执行成功，保存成功信息转发但msg
		request.setAttribute("msg", "恭喜，注册成功！请马上到邮箱激活");
		return "f:/jsps/msg.jsp";

	}
	
	
	/**
	 * 登录
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 封装表单到from中
		 * 输入校验
		 * 调用service完成登录
		 * 	>保存错误信息，from到request转发到login.jsp
		 * 保存用户信息到session中重定向到index.jsp
		 */
		User from = CommonUtils.toBean(request.getParameterMap(), User.class);
		try {
			User user = userService.login(from);
			request.getSession().setAttribute("session_user", user);
			//给一个购物车
			request.getSession().setAttribute("cart", new Cart());
			
			return "r:/index.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", from);
			return "f:/jsps/user/login.jsp";
		}
	}
	
	/**
	 * 退出
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	
	public String quit(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().invalidate();
		return "r:/index.jsp";
	}
	
	
	
}
