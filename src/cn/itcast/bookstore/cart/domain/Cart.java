package cn.itcast.bookstore.cart.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 购物车类
 * @author liuhonglei
 *
 */
public class Cart {
	private Map<String,CartItem> map = new LinkedHashMap<String, CartItem>();
	
	public double getTotal(){
		BigDecimal total = new BigDecimal("0.0");
		for (CartItem cartItem : map.values()) {
			BigDecimal d1 = new BigDecimal(cartItem.getSubtotal()+"");
			total = total.add(d1);
		}
		return total.doubleValue();
	}
	
	
	//添加
	public void add(CartItem cartItem){
		String bid=cartItem.getBook().getBid();
		if(map.containsKey(cartItem.getBook().getBid())){
			CartItem _cartItem =map.get(cartItem.getBook().getBid());
			_cartItem.setCount(_cartItem.getCount()+cartItem.getCount());
			map.put(cartItem.getBook().getBid(), _cartItem);
		}else{
			map.put(cartItem.getBook().getBid(), cartItem);
		}
	}
	//清空
	public void clear(){
		map.clear();
	}
	//删除
	public void delete(String bid){
		map.remove(bid);
	}
	//返回所有条目
	public Collection<CartItem> getCartItems(){
		return map.values();
	}

}
