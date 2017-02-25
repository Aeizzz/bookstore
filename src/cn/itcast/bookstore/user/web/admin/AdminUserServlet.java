package cn.itcast.bookstore.user.web.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.user.domain.User;
import cn.itcast.bookstore.user.domain.adminUser;
import cn.itcast.bookstore.user.service.UserService;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class AdminUserServlet extends BaseServlet {
	private UserService userService = new UserService();
	/**
	 * µÇÂ¼
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		adminUser adminUser = CommonUtils.toBean(request.getParameterMap(), adminUser.class);
		adminUser _user = userService.adminLogin(adminUser);
		if(_user!=null){
			if(!_user.getPassword().equals(adminUser.getPassword().trim()))
			{
				request.setAttribute("msg", "ÃÜÂë´íÎó");
				return "f:/adminjsps/login.jsp";
			}
			return "f:/adminjsps/admin/main.jsp";

		}else{
			request.setAttribute("msg", "ÕËºÅ´íÎó");
			return "f:/adminjsps/login.jsp";
		}
		
		
	}

}
