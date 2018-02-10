package com.taotao.portal.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.utils.CookieUtils;
import com.taotao.pojo.TbUser;
import com.taotao.portal.service.UserService;
import com.taotao.portal.service.impl.UserServiceImpl;

public class LoginInterceptor implements HandlerInterceptor{

	@Autowired
	private UserServiceImpl userServiceImpl;
	
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//Hander执行之前处理，从cookie中取token查取用户信息，调用sso服务
		String token=CookieUtils.getCookieValue(request, "TT_TOKEN");
		TbUser user=userServiceImpl.getUserByToken(token);
		//判断用户登陆情况
		if (user==null) {
			//未登录，跳转登录页面
			response.sendRedirect(userServiceImpl.SSO_BASE_URL+userServiceImpl.SSO_PAGE_LOGIN+"?redirect="+request.getRequestURL());
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

}
