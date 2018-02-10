package com.taotao.rest.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItemParamItemExample;
import com.taotao.pojo.TbItemParamItemExample.Criteria;
import com.taotao.rest.dao.JedisClient;
import com.taotao.rest.service.ItemService;
@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${REDIS_ITEM_KEY}")
	private String REDIS_ITEM_KEY;
	@Value("${REDIS_ITEM_EXPIRE}")
	private int REDIS_ITEM_EXPIRE;
	
	@Override
	public TaotaoResult getItemBaseInfo(long itemId) {
		//先从缓存中取商品信息，取不到则查询数据库，在把商品信息写入缓存并设置有效期
		try {
			String json=jedisClient.get(REDIS_ITEM_KEY+":"+itemId+":base");
			if (!StringUtils.isBlank(json)) {
				//把json转为pojo
				TbItem item=JsonUtils.jsonToPojo(json, TbItem.class);
				return TaotaoResult.ok(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//根据id从数据库中查询商品信息
		TbItem item=itemMapper.selectByPrimaryKey(itemId);
		//写入缓存
		try {
			jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":base", JsonUtils.objectToJson(item));
			//设置有效期
			jedisClient.expire(REDIS_ITEM_KEY+":"+itemId+":base", REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//返回pojo对象
		return TaotaoResult.ok(item);
	}

	@Override
	public TaotaoResult getItemDesc(long itemId) {
		//先从缓存中取商品描述信息，取不到则查询数据库，在把商品信息写入缓存并设置有效期
		try {
			String json=jedisClient.get(REDIS_ITEM_KEY+":"+itemId+":desc");
			if (!StringUtils.isBlank(json)) {
				//把json转为pojo
				TbItemDesc itemDesc=JsonUtils.jsonToPojo(json, TbItemDesc.class);
				return TaotaoResult.ok(itemDesc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		TbItemDesc itemDesc=itemDescMapper.selectByPrimaryKey(itemId);
		//写入缓存
		try {
			jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":desc", JsonUtils.objectToJson(itemDesc));
			//设置有效期
			jedisClient.expire(REDIS_ITEM_KEY+":"+itemId+":desc", REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return TaotaoResult.ok(itemDesc);
	}

	@Override
	public TaotaoResult getItemParam(long itemId) {
		//先从缓存中取商规格参数，取不到则查询数据库，在把商品信息写入缓存并设置有效期
		try {
			String json=jedisClient.get(REDIS_ITEM_KEY+":"+itemId+":param");
			if (!StringUtils.isBlank(json)) {
				//把json转为pojo
				TbItemParamItem paramItem=JsonUtils.jsonToPojo(json, TbItemParamItem.class);
				return TaotaoResult.ok(paramItem);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//查询数据库
		TbItemParamItemExample  example=new TbItemParamItemExample();
		Criteria criteria=example.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		List<TbItemParamItem> list=itemParamItemMapper.selectByExampleWithBLOBs(example);
		if (list!=null&&list.size()>0) {
			TbItemParamItem paramItem=list.get(0);
			//写入缓存
			try {
				jedisClient.set(REDIS_ITEM_KEY+":"+itemId+":param", JsonUtils.objectToJson(paramItem));
				//设置有效期
				jedisClient.expire(REDIS_ITEM_KEY+":"+itemId+":param", REDIS_ITEM_EXPIRE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return TaotaoResult.ok();
		}
		return TaotaoResult.build(400, "无此商品规格");
	}
}
