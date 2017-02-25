package cn.itcast.bookstore.order.web.servlet.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.order.service.OrderException;
import cn.itcast.bookstore.order.service.OrderService;
import cn.itcast.servlet.BaseServlet;

public class AdminOrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();
	/**
	 * 查询所有订单
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("orderList",orderService.findAll());
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/**
	 * 按照是否发货状态查询
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByState(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String state = request.getParameter("state");
		request.setAttribute("orderList",orderService.findByState(state));
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	
	/**
	 * 发货
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String fahuo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String oid=request.getParameter("oid");
		try {
			orderService.fahuo(oid);
		} catch (OrderException e) {
			request.setAttribute("msg","订单未付款");
			return "f:/adminjsps/admin/msg.jsp";
		}
		return "f:/adminjsps/admin/order/list.jsp";
	}


}
