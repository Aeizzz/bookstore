package cn.itcast.bookstore.category.service;

import java.util.List;

import cn.itcast.bookstore.book.dao.BookDao;
import cn.itcast.bookstore.category.dao.Categorydao;
import cn.itcast.bookstore.category.domain.Category;
import cn.itcast.bookstore.category.web.servlet.admin.CategoryException;



public class CategoryService {
	private Categorydao categorydao = new Categorydao();
	private BookDao bookDao = new BookDao();
	public List<Category> findALL() {
		return categorydao.findALL();
		
	}

	public void add(Category category) {
		categorydao.add(category);
		
	}

	public void delete(String cid) throws CategoryException {
		
		int count=bookDao.getCountByCid(cid);
		if(count>0) throw new CategoryException("�÷����»���ͼ�鲻��ɾ��");
		categorydao.delete(cid);
		
	}

	public Category load(String cid) {
		return categorydao.load(cid);
	}
	/**
	 * �޸ķ���
	 * @param category
	 */
	public void edit(Category category) {
		categorydao.edit(category);
		
	}
	
	
	

}
