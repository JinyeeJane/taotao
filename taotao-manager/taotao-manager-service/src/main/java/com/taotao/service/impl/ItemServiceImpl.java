package com.taotao.service.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EUDateGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.IDUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.mapper.TbItemParamMapper;
import com.taotao.pojo.TbItemExample.Criteria;
import com.taotao.pojo.TbItemParam;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;
@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemDescMapper itemDescMapper;
	
	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;
	
	@Override
	public TbItem getItemById(long itemId) {
		//TbItem item=itemMapper.selectByPrimaryKey(itemId);
		TbItemExample example=new TbItemExample();
		Criteria criteria=example.createCriteria();
		criteria.andIdEqualTo(itemId);
		List<TbItem> list=itemMapper.selectByExample(example);
		if (list!=null&&list.size()>0) {
			TbItem item=list.get(0);
			return item;
		}
		return null;
	}
	@Override
	public EUDateGridResult getItemList(int page, int rows) {
		//查询商品列表
		TbItemExample example=new TbItemExample();
		//分页处理
		PageHelper.startPage(page,rows);
		List<TbItem> list=itemMapper.selectByExample(example);
		//创建一个返回值对象
		EUDateGridResult result=new EUDateGridResult();
		result.setRows(list);
		//取记录总条数
		PageInfo<TbItem> pageInfo=new PageInfo<>(list);
		result.setTotal(pageInfo.getTotal());
		return result;
	}
	@Override
	public TaotaoResult createItem(TbItem item,String desc,String itemParam) throws Exception {
		// item信息补全，生成商品id，设置状态，设置创建时间和更新时间
		Long itemId=IDUtils.genItemId();
		item.setId(itemId);
		item.setStatus((byte)1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		//把商品信息插入数据库
		itemMapper.insert(item);
		//调用商品描述上传方法，保证在同一个事务中完成商品信息和商品描述的写入
		TaotaoResult result=insertItemDesc(itemId, desc); 
		//判断是否成功，如果失败了完成回滚
		if (result.getStatus()!=200) {
			throw new Exception();
		}
		//调用方法添加商品规格参数模板信息
		result=insertItemParamItem(itemId, itemParam);
		//判断是否成功，如果失败了完成回滚
		if (result.getStatus()!=200) {
			throw new Exception();
		}
		return TaotaoResult.ok();
	}
	
	//上传商品描述的方法，考虑商品描述是大文本数据，单独存放一个表提高效率
	private TaotaoResult insertItemDesc(Long itemId,String desc){
		TbItemDesc itemDesc=new TbItemDesc();
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(new Date());
		itemDescMapper.insert(itemDesc);
		return TaotaoResult.ok();
	}
	
	//商品规格参数模板插入，大文本数据单独存表
	private TaotaoResult insertItemParamItem(Long itemId,String param) {
		//创建一个pojo
		TbItemParamItem itemParamItem=new TbItemParamItem();
		itemParamItem.setItemId(itemId);
		itemParamItem.setParamData(param);
		itemParamItem.setCreated(new Date());
		itemParamItem.setUpdated(new Date());
		//插入数据库
		itemParamItemMapper.insert(itemParamItem);
		return TaotaoResult.ok();
	}
}
