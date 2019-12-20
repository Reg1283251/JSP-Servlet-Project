package com.itcast.store.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itcast.store.domain.Category;
import com.itcast.store.service.CategoryService;
import com.itcast.store.service.serivceImp.CategoryServiceImp;
import com.itcast.store.utils.JedisUtils;
import com.itcast.store.web.base.BaseServlet;

import net.sf.json.JSONArray;
import redis.clients.jedis.Jedis;

/**
 * Servlet implementation class CategoryServlet
 */
public class CategoryServlet extends BaseServlet {

	public void findAllCats(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			CategoryService CategoryService = new CategoryServiceImp(); 
			List<Category> list = CategoryService.getAllCats(); // ��ȫ������ת����JSON��ʽ������ 
			 String json = JSONArray.fromObject(list).toString();
			 // ��ȫ��������Ϣ��Ӧ���ͻ��� // ���������������Ӧ��������json��ʽ���ַ���
			  response.setContentType("application/json;charset=utf-8");
			 response.getWriter().print(json); // ���������Ӧ��jspҳ����
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
