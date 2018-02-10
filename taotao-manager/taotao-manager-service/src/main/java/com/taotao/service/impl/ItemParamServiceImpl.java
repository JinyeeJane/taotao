package com.taotao.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EUDateGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbItemCatMapper;
import com.taotao.mapper.TbItemParamMapper;
import com.taotao.pojo.ItemParamResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemParam;
import com.taotao.pojo.TbItemParamExample;
import com.taotao.pojo.TbItemParamExample.Criteria;
import com.taotao.service.ItemParamService;
@Service
public class ItemParamServiceImpl implements ItemParamService {
	
	@Autowired
	private TbItemParamMapper itempParamMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Override
	public TaotaoResult getItemParamByCid(long cid) {
		TbItemParamExample example=new TbItemParamExample();
		Criteria criteria=example.createCriteria();
		criteria.andItemCatIdEqualTo(cid);
		List<TbItemParam> list=itempParamMapper.selectByExampleWithBLOBs(example);
		if (list!=null&&list.size()>0) {
			return TaotaoResult.ok(list.get(0));
		}
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult insertItemParam(TbItemParam itemParam) {
		//补全pojo
		itemParam.setCreated(new Date());
		itemParam.setUpdated(new Date());
		itempParamMapper.insert(itemParam);
		return TaotaoResult.ok();
	}

	@Override
	public EUDateGridResult getItemParamList(int page, int rows) {
		//分页处理
		PageHelper.startPage(page,rows);
		List<TbItemParam> list=itempParamMapper.selectByExampleWithBLOBs(new TbItemParamExample());
		List<ItemParamResult> resultList=new ArrayList<>();
		for(TbItemParam itemParam:list){
			long cid=itemParam.getItemCatId();
			TbItemCat itemCat=itemCatMapper.selectByPrimaryKey(cid);
			String itemCatName=itemCat.getName();
			ItemParamResult paramResult=new ItemParamResult();
			paramResult.setId(itemParam.getId());
			paramResult.setItemCatId(cid);
			paramResult.setItemCatName(itemCatName);
			paramResult.setParamData(itemParam.getParamData());
			paramResult.setCreated(itemParam.getCreated());
			paramResult.setUpdated(itemParam.getUpdated());
			resultList.add(paramResult);
		}
		//创建一个返回值对象
		EUDateGridResult result=new EUDateGridResult();
		result.setRows(resultList);
		//取记录总条数
		PageInfo<ItemParamResult> pageInfo=new PageInfo<>(resultList);
		result.setTotal(pageInfo.getTotal());
		return result;
	}

}
