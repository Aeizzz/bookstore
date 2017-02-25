package cn.itcast.bookstore.book.web.servlet.admin;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.bookstore.book.domain.Book;
import cn.itcast.bookstore.book.service.BookService;
import cn.itcast.bookstore.category.service.CategoryService;
import cn.itcast.commons.CommonUtils;

public class AdminAddBookServlet extends HttpServlet {
	private BookService bookService = new BookService();
	private CategoryService categoryService = new CategoryService();
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		/**
		 * 
		 */
		DiskFileItemFactory factory = new DiskFileItemFactory(20*1024,new File("E:/f/temp"));
		
		ServletFileUpload sfu = new ServletFileUpload(factory);
		sfu.setFileSizeMax(20*1024);
		try {
			List<FileItem> fileItemList = sfu.parseRequest(request);
			Map<String,String> map = new HashMap<String, String>();
			for (FileItem fileItem : fileItemList) {
				if(fileItem.isFormField()){
					map.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
				}
			}
			Book book =CommonUtils.toBean(map, Book.class);
			book.setBid(CommonUtils.uuid());
			book.setDel(false);
			
			/**
			 * �����ϴ����ļ�
			 * 	�����·��
			 * 	������ļ�����
			 * 
			 */
			//�õ������Ŀ¼
			String savepath = this.getServletContext().getRealPath("/book_img");
			//�õ��ļ�����
			String filename = CommonUtils.uuid()+"_"+fileItemList.get(1).getName();
			
			/*
			 * У���ļ�����չ��
			 */
			if(!filename.toLowerCase().endsWith("jpg")) {
				request.setAttribute("msg", "���ϴ���ͼƬ����JPG��չ����");
				request.setAttribute("categoryList", categoryService.findALL());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
				return;
			}
			
			
			//ʹ��Ŀ¼���ļ����ƴ���Ŀ���ļ�
			File destFile = new File(savepath, filename);
			fileItemList.get(1).write(destFile);
			
			book.setImage("book_img/"+filename);
			
			/*
			 * У��ͼƬ�ĳߴ�
			 */
			Image image = new ImageIcon(destFile.getAbsolutePath()).getImage();
			if(image.getWidth(null) > 200 || image.getHeight(null) > 200) {
				destFile.delete();//ɾ������ļ���
				request.setAttribute("msg", "���ϴ���ͼƬ�ߴ糬����200 * 200��");
				request.setAttribute("categoryList", categoryService.findALL());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
				return;
			}
			
			
			bookService.add(book);
			
			request.getRequestDispatcher("/admin/AdminBookServlet?method=findAll").forward(request, response);
			
		}catch (Exception e) {
			if(e instanceof FileUploadBase.FileSizeLimitExceededException) {
				request.setAttribute("msg", "���ϴ����ļ�������15KB");
				request.setAttribute("categoryList", categoryService.findALL());
				request.getRequestDispatcher("/adminjsps/admin/book/add.jsp")
						.forward(request, response);
			}
			
		}
		
			
		
	}

}
