package cn.itcast.bookstore.order.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.bookstore.cart.domain.Cart;
import cn.itcast.bookstore.cart.domain.CartItem;
import cn.itcast.bookstore.order.domain.Order;
import cn.itcast.bookstore.order.domain.OrderItem;
import cn.itcast.bookstore.order.service.OrderException;
import cn.itcast.bookstore.order.service.OrderService;
import cn.itcast.bookstore.user.domain.User;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

public class OrderServlet extends BaseServlet {

	private OrderService orderService = new OrderService();

	/**
	 * �ױ��ص����� Ҫ�жϱ������ǲ���ͨ���ױ��ص��ķ�ʽ������
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public String back(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 1. ��ȡ11 + 1
		 */
		String p1_MerId = request.getParameter("p1_MerId");
		String r0_Cmd = request.getParameter("r0_Cmd");
		String r1_Code = request.getParameter("r1_Code");
		String r2_TrxId = request.getParameter("r2_TrxId");
		String r3_Amt = request.getParameter("r3_Amt");
		String r4_Cur = request.getParameter("r4_Cur");
		String r5_Pid = request.getParameter("r5_Pid");
		String r6_Order = request.getParameter("r6_Order");
		String r7_Uid = request.getParameter("r7_Uid");
		String r8_MP = request.getParameter("r8_MP");
		String r9_BType = request.getParameter("r9_BType");

		String hmac = request.getParameter("hmac");

		Properties props = new Properties();
		InputStream input = this.getClass().getClassLoader()
				.getResourceAsStream("merchantInfo.properties");
		props.load(input);
		String keyValue = props.getProperty("keyValue");

		boolean bool = PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd,
				r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid,
				r8_MP, r9_BType, keyValue);

		if (!bool) {// ���У��ʧ��
			request.setAttribute("msg", "������ʲô�ö�����");
			return "f:/jsps/msg.jsp";
		}

		/*
		 * 3. ��ȡ״̬������ȷ���Ƿ�Ҫ�޸Ķ���״̬���Լ���ӻ��ֵ�ҵ�����
		 */
		orderService.pay(r6_Order);// �п��ܶ����ݿ���в�����Ҳ���ܲ�������

		/*
		 * 4. �жϵ�ǰ�ص���ʽ ���Ϊ��Ե㣬��Ҫ������success��ͷ���ַ���
		 */
		if (r9_BType.equals("2")) {
			response.getWriter().print("success");
		}

		/*
		 * 5. ����ɹ���Ϣ��ת����msg.jsp
		 */
		request.setAttribute("msg", "֧���ɹ����ȴ����ҷ�������������~");
		return "f:/jsps/msg.jsp";

	}

	/**
	 * ֧��,ȥ����
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String pay(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/**
		 * ׼��13����
		 * 
		 */
		String oid = request.getParameter("oid");
		Order order = orderService.load(oid);

		double total = order.getTotal();
		Properties props = new Properties();
		InputStream input = this.getClass().getClassLoader()
				.getResourceAsStream("merchantInfo.properties");
		props.load(input);

		String p0_Cmd = "Buy";
		String p1_MerId = props.getProperty("p1_MerId");
		String p2_Order = request.getParameter("oid");
		// ��������0.01Ԫ
		String p3_Amt = String.valueOf(total);
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		String p8_Url = props.getProperty("p8_Url");
		String p9_SAF = "";
		String pa_MP = "";
		String pd_FrpId = request.getParameter("pd_FrpId");
		String pr_NeedResponse = "1";
		/**
		 * ����hmac
		 */
		String keyValue = props.getProperty("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);

		/*
		 * �����ױ�����ַ��13+1������
		 */
		StringBuilder url = new StringBuilder(props.getProperty("url"));
		url.append("?p0_Cmd=").append(p0_Cmd);
		url.append("&p1_MerId=").append(p1_MerId);
		url.append("&p2_Order=").append(p2_Order);
		url.append("&p3_Amt=").append(p3_Amt);
		url.append("&p4_Cur=").append(p4_Cur);
		url.append("&p5_Pid=").append(p5_Pid);
		url.append("&p6_Pcat=").append(p6_Pcat);
		url.append("&p7_Pdesc=").append(p7_Pdesc);
		url.append("&p8_Url=").append(p8_Url);
		url.append("&p9_SAF=").append(p9_SAF);
		url.append("&pa_MP=").append(pa_MP);
		url.append("&pd_FrpId=").append(pd_FrpId);
		url.append("&pr_NeedResponse=").append(pr_NeedResponse);
		url.append("&hmac=").append(hmac);

		/*
		 * �ض����ױ�
		 */
		response.sendRedirect(url.toString());

		return null;
	}

	/**
	 * ȷ���ջ�
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String confirm(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * ��ȡoid������ ����service����
		 * 
		 * 3����
		 * 
		 */
		String oid = request.getParameter("oid");
		try {
			orderService.confirm(oid);
			request.setAttribute("msg", "��ϲ���׳ɹ�");
		} catch (OrderException e) {
			request.setAttribute("msg", e.getMessage());
		}

		return "f:/jsps/msg.jsp";
	}

	/**
	 * ���ض���
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setAttribute("order",
				orderService.load(request.getParameter("oid")));

		return "f:/jsps/order/desc.jsp";
	}

	/**
	 * ��Ӷ���
	 */
	public String add(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// ��session�л�ȡcart
		Cart cart = (Cart) request.getSession().getAttribute("cart");
		// ��cartת����order
		Order order = new Order();
		order.setOid(CommonUtils.uuid());
		order.setOrdertime(new Date());
		order.setState(1);
		User user = (User) request.getSession().getAttribute("session_user");
		order.setOwner(user);
		order.setTotal(cart.getTotal());
		/**
		 * ����������Ŀ����
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for (CartItem cartItem : cart.getCartItems()) {
			OrderItem OI = new OrderItem();// ����������Ŀ
			OI.setIid(CommonUtils.uuid());
			OI.setCount(cartItem.getCount());
			OI.setBook(cartItem.getBook());
			OI.setSubtotal(cartItem.getSubtotal());
			OI.setOrder(order);// ������������
			orderItemList.add(OI);// �Ѷ�����Ŀ�ӵ�����������

		}
		// �����ж����ӵ�������Ŀ��
		order.setOrderItemList(orderItemList);

		// ��չ��ﳵ
		cart.clear();

		/**
		 * ����orderService��Ӷ���
		 */
		orderService.add(order);
		/**
		 * ����order��request���У�ת����/jsp/order/desc.jsp
		 */

		request.setAttribute("order", order);

		return "f:/jsps/order/desc.jsp";
	}

	/**
	 * �ҵĶ���
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String myOrder(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/**
		 * ��session��ȡ��ǰ�û� ʹ��uid����orderService#myOrder(uid)�õ����û���List<Order>
		 * �Ѷ����б�����request�У�ת����jsps/order/list.jsp
		 */
		User user = (User) request.getSession().getAttribute("session_user");
		String uid = user.getUid();
		List<Order> orderList = orderService.myOrder(uid);
		request.setAttribute("orderList", orderList);

		return "f:/jsps/order/list.jsp";
	}
}
