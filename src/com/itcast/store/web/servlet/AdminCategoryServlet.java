package com.itcast.store.web.servlet;

import java.util.List;

import com.itcast.store.domain.Category;
import com.itcast.store.service.CategoryService;
import com.itcast.store.service.serivceImp.CategoryServiceImp;
import com.itcast.store.utils.UUIDUtils;
import com.itcast.store.web.base.BaseServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AdminCategoryServlet
 */
public class AdminCategoryServlet extends BaseServlet {
	//�鿴������Ʒ��servlet
	public String findAllCats(HttpServletRequest request, HttpServletResponse response) throws Exception{
		CategoryService categoryService = new CategoryServiceImp();
		List<Category> list = categoryService.getAllCats();
		request.setAttribute("allCats", list);
		return "/admin/category/list.jsp";
	}
	
	//���ӷ���
	public String addCategoryUI(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		return "/admin/category/add.jsp";
	}
	public String addCategory(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String cname=req.getParameter("cname");
		String id=UUIDUtils.getId(); //���id
		Category c=new Category();
		c.setCid(id);
		c.setCname(cname);
		CategoryService CategoryService=new CategoryServiceImp();
		CategoryService.addCategory(c);
		resp.sendRedirect("/store/AdminCategoryServlet?method=findAllCats");//�ض���
		return null;
	}
	
	//�޸���Ʒ��������
	public String editCategoryUI(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String cid = request.getParameter("cid");
		CategoryService categoryService = new CategoryServiceImp();
		Category category = categoryService.findCategoryByCid(cid);
		request.setAttribute("category", category);
		return "/admin/category/edit.jsp";
	}
	public String editCategory(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String cid=request.getParameter("cid");
		String cname = request.getParameter("cname");
		CategoryService categoryService = new CategoryServiceImp();
		Category category = new Category();
		category.setCid(cid);
		category.setCname(cname);
		categoryService.editCategory(category);
		response.sendRedirect("/store/AdminCategoryServlet?method=findAllCats");
		return null;
	}
	public void deleteCategory(HttpServletRequest request, HttpServletResponse response)throws Exception{
		String cid = request.getParameter("cid");
		CategoryService categoryService = new CategoryServiceImp();
		try {
			categoryService.deleteCategory(cid);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			response.sendRedirect("/store/AdminCategoryServlet?method=findAllCats");
		}
		
	}
}
