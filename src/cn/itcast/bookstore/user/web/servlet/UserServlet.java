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
 * User������
 * 
 * @author liuhonglei
 *
 */

public class UserServlet extends BaseServlet {
	private UserService userService = new UserService();

	/**
	 * �����
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
		 * ��ȡ����������
		 * ����service������ɼ���
		 * 	>���쳣��Ϣ��request����ת����msg.jsp
		 * ����ɹ���Ϣ��request��ת����msg.jsp
		 */
		String code = request.getParameter("code");
		try {
			userService.active(code);
			request.setAttribute("msg", "��ϲ��ע��ɹ��������ϵ�¼");
		} catch (UserException e) {
			request.setAttribute("msg",e.getMessage());
		}
		return "f:/jsps/msg.jsp";
	}

	/**
	 * ע�Ṧ��
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
		 * 1.��װ����form�� 2.��ȫuid��code 3.����У�� >���������Ϣ��form��request��ת����regist.jsp
		 * 4.����service�������ע�� >���������Ϣ��form��request��ת����regist.jsp 5.���ʼ�
		 * 6.����ɹ���Ϣת����msg.jsp
		 */
		// ��װ������
		User form = CommonUtils.toBean(request.getParameterMap(), User.class);
		// ��ȫ
		form.setUid(CommonUtils.uuid());
		form.setCode(CommonUtils.uuid() + CommonUtils.uuid());
		/**
		 * ����У�� 1������map
		 */
		Map<String, String> errors = new HashMap<String, String>();

		// �û���
		String username = form.getUsername();
		if (username == null || username.trim().isEmpty()) {
			errors.put("username", "�û�������Ϊ��");
		} else if (username.length() < 3 || username.length() > 10) {
			errors.put("username", "�û���Ҫ��3~10֮��");
		}

		// ����
		String password = form.getPassword();
		if (password == null || password.trim().isEmpty()) {
			errors.put("password", "���벻��Ϊ��");
		} else if (password.length() < 3 || password.length() > 10) {
			errors.put("password", "����Ҫ��3~10֮��");
		}
		// email
		String email = form.getEmail();
		if (email == null || email.trim().isEmpty()) {
			errors.put("email", "email����Ϊ��");
		} else if (!email.matches("\\w+@\\w+\\.\\w+")) {
			errors.put("email", "email��ʽ����");
		}
		/**
		 * �ж��Ƿ���ڴ���
		 */
		if (errors.size() > 0) {
			// 1.���������Ϣ
			// 2.�����
			// 3.ת��regist.jsp
			request.setAttribute("errors", errors);
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}
		/**
		 * ����service��regist����
		 */
		try {
			userService.regist(form);

		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", form);
			return "f:/jsps/user/regist.jsp";
		}

		/*
		 * ���ʼ� ׼�������ļ� ��ȡ�����ļ�����
		 */
		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader()
				.getResourceAsStream("email_template.properties"));
		String host = properties.getProperty("host");//��ȡ����������
		String uname = properties.getProperty("uname");//��ȡ�û���
		String pwd = properties.getProperty("pwd");//��ȡ����
		String from = properties.getProperty("from");//��ȡ������
		String to = form.getEmail();//��ȡ�ռ���
		String subject = properties.getProperty("subject");//��ȡ����
		String content = properties.getProperty("content");//��ȡ�ʼ�����
		content = MessageFormat.format(content, form.getCode());//�滻{0}
		
		Session session = MailUtils.createSession(host, uname, pwd);//�õ�session
		Mail mail = new Mail(from, to, subject, content);//�����ʼ�����
		try{
			MailUtils.send(session, mail);//���ʼ�
		}catch(MessagingException e){
			
		}
		// ִ�гɹ�������ɹ���Ϣת����msg
		request.setAttribute("msg", "��ϲ��ע��ɹ��������ϵ����伤��");
		return "f:/jsps/msg.jsp";

	}
	
	
	/**
	 * ��¼
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * ��װ����from��
		 * ����У��
		 * ����service��ɵ�¼
		 * 	>���������Ϣ��from��requestת����login.jsp
		 * �����û���Ϣ��session���ض���index.jsp
		 */
		User from = CommonUtils.toBean(request.getParameterMap(), User.class);
		try {
			User user = userService.login(from);
			request.getSession().setAttribute("session_user", user);
			//��һ�����ﳵ
			request.getSession().setAttribute("cart", new Cart());
			
			return "r:/index.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());
			request.setAttribute("form", from);
			return "f:/jsps/user/login.jsp";
		}
	}
	
	/**
	 * �˳�
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
