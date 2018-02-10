package com.taotao.sso.controller;

import javax.naming.spi.DirStateFactory.Result;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.ExceptionUtil;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public Object checkData(@PathVariable String param,@PathVariable Integer type,String callback){
		//创建返回对象
		TaotaoResult result=null;
		//参数有效性校验
		if (StringUtils.isBlank(param)) {
			result= TaotaoResult.build(400, "参数内容不得为空");
		}
		if (type==null) {
			result= TaotaoResult.build(400, "参数类型不得为空");
		}
		if (type!=1&&type!=2&&type!=3) {
			result= TaotaoResult.build(400, "校验内容类型错误");
		}
		//不合法
		if (result!=null) {
			//支持jsonp
			if (callback!=null) {
				MappingJacksonValue mappingJacksonValue=new MappingJacksonValue(result);
				mappingJacksonValue.setJsonpFunction(callback);
				return mappingJacksonValue;
			}else {
				return result;
			}
		}
		//合法，调用服务
		try {
			result=userService.checkData(param, type);
		} catch (Exception e) {
			result= TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		//回调包装操作
		if (callback!=null) {
			MappingJacksonValue mappingJacksonValue=new MappingJacksonValue(result);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}else {
			return result;
		}
	}
	
	@RequestMapping(value="/register",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult createUser(TbUser user){
		try {
			TaotaoResult result=userService.createUser(user);
			return result;
		} catch (Exception e) {
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
	}
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult userLogin(String username,String password,HttpServletRequest request,HttpServletResponse response){
		try {
			TaotaoResult result=userService.userLogin(username, password,request,response);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}	
	}
	
	@RequestMapping("/token/{token}")
	@ResponseBody
	public Object getUserByToken(@PathVariable String token,String callback){
		TaotaoResult taotaoResult=null;
		try {
			taotaoResult=userService.getUserByToken(token);
		} catch (Exception e) {
			e.printStackTrace();
			taotaoResult=TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
		}
		if (StringUtils.isBlank(callback)) {
			return taotaoResult;
		}else {
			MappingJacksonValue mappingJacksonValue=new MappingJacksonValue(taotaoResult);
			mappingJacksonValue.setJsonpFunction(callback);
			return mappingJacksonValue;
		}
	}
}
