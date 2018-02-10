package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.dao.JedisClient;
import com.taotao.sso.service.UserService;
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_USER_SESSION_KEY}")
	private String REDIS_USER_SESSION_KEY;
	@Value("${SSO_SESSION_EXPIRE}")
	private Integer SSO_SESSION_EXPIRE;
	
	@Override
	public TaotaoResult checkData(String content, Integer type) {
		//创建查询条件
		TbUserExample example=new TbUserExample();
		Criteria criteria=example.createCriteria();
		//校验数据方式选择:1,2,3分别代表判断username，phone，email是否已经被使用
		if(type==1){
			criteria.andUsernameEqualTo(content);
		}else if (type==2) {
			criteria.andPhoneEqualTo(content);
		}else {
			criteria.andEmailEqualTo(content);
		}
		//执行查询
		List<TbUser> list=userMapper.selectByExample(example);
		if (list==null||list.size()==0) {
			//可用
			return TaotaoResult.ok(true);
		}
		//被占用
		return TaotaoResult.ok(false);
	}

	@Override
	public TaotaoResult createUser(TbUser user) {
		//补全信息
		user.setUpdated(new Date());
		user.setCreated(new Date());
		//密码md5加密
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		//插入新用户
		userMapper.insert(user);
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult userLogin(String username, String password,HttpServletRequest request,HttpServletResponse response) {
		TbUserExample example=new TbUserExample();
		Criteria criteria=example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list=userMapper.selectByExample(example);;
		//无此用户名
		if (list==null||list.size()==0) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		//比对密码
		TbUser user=list.get(0);
		if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
		//密码正确，生成token
		String token=UUID.randomUUID().toString();
		//把用户信息写入redis，去掉密码信息保证安全
		user.setPassword(null);
		jedisClient.set(REDIS_USER_SESSION_KEY+":"+token, JsonUtils.objectToJson(user));
		//设置session过期时间
		jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token, SSO_SESSION_EXPIRE);
		//写cookie逻辑，cookie有效期设置关闭浏览器失效
		CookieUtils.setCookie(request, response, "TT_TOKEN", token);
		//返回
		return TaotaoResult.ok(token);
	}

	@Override
	public TaotaoResult getUserByToken(String token) {
		//根据token从redis中查询信息
		String json=jedisClient.get(REDIS_USER_SESSION_KEY+":"+token);
		//session为空
		if (StringUtils.isBlank(json)) {
			return TaotaoResult.build(400, "请重新登陆");
		}
		//不为空
		jedisClient.expire(REDIS_USER_SESSION_KEY+":"+token, SSO_SESSION_EXPIRE);
		return TaotaoResult.ok(JsonUtils.jsonToPojo(json, TbUser.class));
	}

}
