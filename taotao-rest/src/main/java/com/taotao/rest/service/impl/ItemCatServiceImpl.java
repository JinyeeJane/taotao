package com.taotao.rest.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.mapper.TbItemCatMapper;
import com.taotao.pojo.TbItemCat;
import com.taotao.pojo.TbItemCatExample;
import com.taotao.pojo.TbItemCatExample.Criteria;
import com.taotao.rest.pojo.CatNode;
import com.taotao.rest.pojo.CatResult;
import com.taotao.rest.service.ItemCatService;
@Service
public class ItemCatServiceImpl implements ItemCatService {
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Override
	public CatResult getItemCatList() {
		CatResult catResult=new CatResult();
		//查询分类列表
		catResult.setData(getCatList(0));
		return catResult;
	}
	//递归查询分类列表
	private List<?> getCatList(long parentId) {
		//创建查询条件
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria=example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list=itemCatMapper.selectByExample(example);
		//遍历list创建节点，由于排版限制，第一层只加载14个
		int count=0;     //计数器
		List resultList=new ArrayList ();
		for (TbItemCat tbItemCat:list) {
			//是否有子节点
			if (tbItemCat.getIsParent()) {
				CatNode catNode=new CatNode();
				if (parentId==0) {
					catNode.setName("<a href='/products/"+tbItemCat.getId()+".html'>"+tbItemCat.getName()+"</a>");
				}else {
					catNode.setName(tbItemCat.getName());
				}
				catNode.setUrl("/products/"+tbItemCat.getId()+".html");
				//此处递归
				catNode.setItem(getCatList(tbItemCat.getId()));
				resultList.add(catNode);
				count++;
				if (parentId==0&&count>=14) {
					break;
				}
			}
			//无子节点
			else {
				resultList.add("/products/"+tbItemCat.getId()+".html|"+tbItemCat.getName());
			}
		}
		return resultList;
	}
}
