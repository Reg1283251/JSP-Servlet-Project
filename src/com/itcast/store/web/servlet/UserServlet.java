package com.itcast.store.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.time.DateUtils;

import com.itcast.store.domain.User;
import com.itcast.store.service.userService;
import com.itcast.store.service.serivceImp.userServiceImp;
import com.itcast.store.utils.MailUtils;
import com.itcast.store.utils.MailUtils2;
import com.itcast.store.utils.UUIDUtils;
import com.itcast.store.utils.YanZhengCode;
import com.itcast.store.web.base.BaseServlet;

import net.sf.json.JSONObject;

/**
 * �û���user��:ϵͳ��ͻ��˽���������servlet����
 */
public class UserServlet extends BaseServlet {
	//�û�ע�����
	public String registUI(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		return "/jsp/register.jsp";
	}
	//�û�ע���˺�
	public String userRegist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//���ձ�����
		Map<String, String[]> map = request.getParameterMap();
		User user = new User();
		
		try {
			//����ʱ��
			DateConverter dt = new DateConverter();
			dt.setPattern("yyyy-MM-dd");
			
			ConvertUtils.register(dt, java.util.Date.class);
			
			BeanUtils.populate(user, map); //��mapת��bean����
			//����ΪMyBeanUtils.populate(user, map)
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//�������uid	
		user.setUid(UUIDUtils.getCode());
		user.setState(0);
		//������֤��
		user.setCode(YanZhengCode.achieveCode());
		//����ҵ���ע�Ṧ��
		userService  UserService = new userServiceImp();
		try {
			//�����ʼ�
			MailUtils2.sendMail(user.getEmail(), user.getCode());
			//ע��ɹ������û����䷢����Ϣ��ת������ʾҳ��
			UserService.userRegist(user);
			request.setAttribute("msg", "�û�ע��ɹ�����ǰ�����䣬�鿴QQ�����ȡ������");
			return "/jsp/active.jsp";
		} catch (Exception e) {
			//ע��ʧ�ܣ�ת������ʾҳ��
			request.setAttribute("msg", "�û�ע��ʧ�ܣ�������ע��");
			return "/jsp/info.jsp";
			// TODO: handle exception
		}
	}
	//�����û���servlet����
	public String active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String code = request.getParameter("code");
			userService service = new userServiceImp();
			boolean flag = service.userActive(code);
			if(flag) {
				response.getWriter().print(1);
			}else {
				response.getWriter().print(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	//�û���¼����
	public String loginUI(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return "/jsp/login.jsp";
	}
	//�û���¼servlet����
	public String userLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {
		String username=request.getParameter("username");
		String password=request.getParameter("password");
		try {
			userService userService = new userServiceImp();
			User user = userService.userLogin(username, password);
			request.getSession().setAttribute("loginUser", user);
			request.getSession().setMaxInactiveInterval(10*60);//����session10����ʧЧ
			response.sendRedirect("/store/index.jsp"); //�ض���
			return null;
		} catch (Exception e) {
			String msg = e.getMessage(); //��ȡ�쳣�������Ϣ
			request.setAttribute("msg", msg);
			return "/jsp/login.jsp";
		}
	}
	//�˳���¼
	public String logOut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		//���session
		request.getSession().invalidate();
		response.sendRedirect("/store/index.jsp");
		return null;
	}
	//ajax��������Ч���û����Ƿ����
	
	public void checkUserName(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		try {
			String username = request.getParameter("username");
			userService service = new userServiceImp();
			boolean flag = service.checkUserName(username);
			if(!flag) {
				response.getWriter().print(1);//������
			}else {
				response.getWriter().print(2);//����
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//��session��ȡ��loginUser
	public void checkPersonal(HttpServletRequest request, HttpServletResponse response) throws Exception{
		User user = (User)request.getSession().getAttribute("loginUser");
		if(user.getBirthday()!=null)
			user.setBirthday(new java.util.Date(user.getBirthday().getTime()));//���ݿ�������java.sql.date,����Ҫת����java.util.data
		String json = JSONObject.fromObject(user).toString();
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().print(json);
	}
	//�޸��û���Ϣ
	public String changeUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
		User user = new User();
		try {
			user.setUid(request.getParameter("uid"));
			user.setUsername(request.getParameter("username"));
			user.setPassword(request.getParameter("password"));
			user.setName(request.getParameter("name"));
			user.setEmail(request.getParameter("email"));
			user.setTelephone(request.getParameter("telephone"));
			user.setSex(request.getParameter("sex"));
			
			user.setState(Integer.parseInt(request.getParameter("state")));
			user.setCode(request.getParameter("code"));
			if(request.getParameter("birthday")!="") {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date date = formatter.parse(request.getParameter("birthday"));
				user.setBirthday(date);
			}else {
				User u = (User) request.getSession().getAttribute("loginUser");
				user.setBirthday(u.getBirthday());
			}
			//request.getSession().removeAttribute("loginUser");//remove����ĳһ�����ԣ���invalidate�������е�����
			request.getSession().setAttribute("loginUser", user);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		userService userService = new userServiceImp();
		userService.changeUser(user);
		//response.sendRedirect("/store/jsp/personal.jsp"); //�ض���
		return "/jsp/personal.jsp";
	}
}
