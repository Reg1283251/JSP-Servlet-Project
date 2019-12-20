package com.itcast.store.web.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.itcast.store.domain.Category;
import com.itcast.store.domain.PageModel;
import com.itcast.store.domain.Product;
import com.itcast.store.service.CategoryService;
import com.itcast.store.service.ProductService;
import com.itcast.store.service.serivceImp.CategoryServiceImp;
import com.itcast.store.service.serivceImp.ProductServiceImp;
import com.itcast.store.utils.MyBeanUtils;
import com.itcast.store.utils.UUIDUtils;
import com.itcast.store.utils.UploadUtils;
import com.itcast.store.web.base.BaseServlet;

/**
 * Servlet implementation class AdminProductServlet
 */
public class AdminProductServlet extends BaseServlet {
	//����ҳ�鿴����������Ʒ
	public String findAllUpProductsWithPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int curNum = Integer.parseInt(request.getParameter("currentPageNum"));
		ProductService productService = new ProductServiceImp();
		PageModel pm =productService.findAllUpProductsWithPage(curNum);
		request.setAttribute("page", pm);
		return "/admin/product/list.jsp";
	}
	
	//��ת��������Ʒҳ��
	public String addProductUI(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CategoryService service = new CategoryServiceImp();
		List<Category> list = service.getAllCats();
		int pflag = Integer.parseInt(request.getParameter("pflag"));
		
		request.setAttribute("allCats", list);
		request.setAttribute("pflag", pflag);
		return "/admin/product/add.jsp";
	}
	//������Ʒ�����ݿ�
	public String addProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//�洢��������
		Map<String,String> map=new HashMap<String,String>();
		//Я�����е�������servcie,dao
		Product product=new Product();
		int pflag = 1;
		try {
			//����req.getInputStream();��ȡ����������ȫ������,���в�ֺͷ�װ
			DiskFileItemFactory fac=new DiskFileItemFactory();
			ServletFileUpload upload=new ServletFileUpload(fac);
			List<FileItem> list = upload.parseRequest(request);
			//��������
		
			for (FileItem item : list) {
				if(item.isFormField()){
					//�����ǰ��FileItem��������ͨ��
					//����ͨ����name���Ե�ֵ��Ϊ��,����ȡ����������Ϊֵ,����MAP��
					// {username<==>tom,password<==>1234}
					if(item.getFieldName().equals("pflag")) {
						pflag = Integer.parseInt(item.getString("utf-8"));
						continue;
					}
					map.put(item.getFieldName(), item.getString("utf-8"));
				}else{
					//�����ǰ��FileItem�������ϴ���
					
					//��ȡ��ԭʼ���ļ�����
					String oldFileName=item.getName();
					System.out.println(oldFileName);
					char m = oldFileName.charAt(0);
					if(m=='c' || m=='d')
						m='1';
					//��ȡ��Ҫ�����ļ�������,�ı��ļ���
					String newFileName=UploadUtils.getUUIDName(oldFileName);
					//ͨ��FileItem��ȡ������������,ͨ�����������Ի�ȡ��ͼƬ����������
					InputStream is=item.getInputStream();
					//��ȡ����ǰ��Ŀ��products/3�µ���ʵ·��
					//D:\tomcat\tomcat71_sz07\webapps\store\products\1
					String realPath=getServletContext().getRealPath("/products/" + m);
					//String dir=UploadUtils.getDir(oldFileName); // /f/e/d/c/4/9/8/4
					//String path=realPath+"\\" + newFileName; //D:\tomcat\tomcat71_sz07\webapps\store_v5\products\3/f/e/d/c/4/9/8/4
					//�ڴ�������һ��Ŀ¼
					
					File newDir=new File(realPath);
					if(!newDir.exists()){
						newDir.mkdirs();
					}
					
					//�ڷ���˴���һ�����ļ�(��׺������ϴ�������˵��ļ�����׺һ��)
					File finalFile=new File(newDir,newFileName);
					if(!finalFile.exists()){
						finalFile.createNewFile();
					}
					//�����Ϳ��ļ���Ӧ�������
					OutputStream os=new FileOutputStream(finalFile);
					//���������е�����ˢ���������
					IOUtils.copy(is, os);
					//�ͷ���Դ
					IOUtils.closeQuietly(is);
					IOUtils.closeQuietly(os);
					//��map�д���һ����ֵ�Ե����� userhead<===> /image/11.bmp
					// {username<==>tom,password<==>1234,userhead<===>image/11.bmp}
					map.put("pimage", "products/"+m+"/"+newFileName);
				}
			}

			
			//����BeanUtils��MAP�е�������䵽Product������
			BeanUtils.populate(product, map);
			product.setPid(UUIDUtils.getId());
			product.setPdate(new Date());
			product.setPflag(pflag);
			
			//����servcie_dao��user��Я�������ݴ������ݲֿ�,�ض��򵽲�ѯȫ����Ʒ��Ϣ·��
			ProductService ProductService=new ProductServiceImp();
			ProductService.saveProduct(product);
			if(pflag==1) {
				response.sendRedirect("/store/AdminProductServlet?method=findAllUpProductsWithPage&currentPageNum=1");
			}else if(pflag==0) {
				response.sendRedirect("/store/AdminProductServlet?method=findPushDownProduct&currentPageNum=1");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//��ת���޸���Ʒ����
	public String editProductUI(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pid = request.getParameter("pid");
		String currentPageNum = request.getParameter("currentPageNum");
		ProductService service = new ProductServiceImp();
		Product product = service.getProductById(pid);
		CategoryService service1 = new CategoryServiceImp();
		List<Category> list = service1.getAllCats();
		request.setAttribute("allCats", list);
		request.setAttribute("product", product);
		request.setAttribute("currentPageNum", currentPageNum);
		return "/admin/product/edit.jsp";
	}
	//�޸���Ʒ
	public String editProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String[]> map = request.getParameterMap();
		String currentPageNum = request.getParameter("currentPageNum");
		/*
		 * for(String list: map.keySet()){ for(String x: map.get(list)) {
		 * System.out.print(x); } System.out.println(); }
		 */
		Product product = new Product();
		//��������
		MyBeanUtils.populate(product, map); //��mapת��bean����
		ProductService service = new ProductServiceImp();
		service.editProduct(product);
		response.sendRedirect("/store/AdminProductServlet?method=findPushDownProduct&currentPageNum="+currentPageNum);
		return null;
	}
	
	//����Ʒ�¼ܲ���
	public String deleteProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pid = request.getParameter("pid");
		int curNum = Integer.parseInt(request.getParameter("currentPageNum"));
		ProductService service = new ProductServiceImp();
		service.DownProduct("1", pid); //1Ϊ�¼�
		response.sendRedirect("/store/AdminProductServlet?method=findAllUpProductsWithPage&currentPageNum="+curNum);
		return null;
	}
	
	//���¼���Ʒչʾ
	public String findPushDownProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int curNum = Integer.parseInt(request.getParameter("currentPageNum"));
		ProductService productService = new ProductServiceImp();
		PageModel pm =productService.findPushDownProduct(curNum);
		request.setAttribute("page", pm);
		return "/admin/product/pushDown_list.jsp";
	}
	//����Ʒ�ϼ�
	public String upProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String pid = request.getParameter("pid");
		int curNum = Integer.parseInt(request.getParameter("currentPageNum"));
		ProductService service = new ProductServiceImp();
		service.DownProduct("0", pid); //0Ϊ�ϼ�
		response.sendRedirect("/store/AdminProductServlet?method=findPushDownProduct&currentPageNum="+curNum);
		return null;
	}
	//findProductsByCategory
	//ͨ������cid�������з������Ʒ
	public String findProductsByCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String cid = request.getParameter("cid");
		int currentPageNum = Integer.parseInt(request.getParameter("currentPageNum"));
		ProductService service = new ProductServiceImp();
		PageModel list = service.findProductsByCategory(cid, currentPageNum);
		request.setAttribute("page", list);
		return "/admin/product/list.jsp";
	}
}
