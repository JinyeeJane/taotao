package com.taotao.portal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.HttpClientUtil;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbContent;
import com.taotao.portal.service.ContentService;
@Service
public class ContentServiceImpl implements ContentService {
	
	@Value("${REST_BASE_URL}")
	private String REST_BASE_URL;
	@Value("${REST_INDEX_AD_URL}")
	private String REST_INDEX_AD_URL;
	
	@Override
	public String getContentList(){
		// 调用rest工程提供的中的服务
		String result=HttpClientUtil.doGet(REST_BASE_URL+REST_INDEX_AD_URL);
		//把字符串转换成TaotaoResult
		try {
			TaotaoResult taotaoResult=TaotaoResult.formatToList(result,TbContent.class);
			//取内容列表
			List<TbContent> list=(List<TbContent>) taotaoResult.getData();
			//构造一个jsp要求的pojo列表
			List<Map> resultList=new ArrayList<>();
			for (TbContent tbContent:list) {
				Map map=new HashMap<>();
				map.put("src", tbContent.getPic());
				map.put("height", 240);
				map.put("width", 670);
				map.put("srcB", tbContent.getPic2());
				map.put("widthB", 550);
				map.put("heightB", 240);
				map.put("href", tbContent.getUrl());
				map.put("alt", tbContent.getSubTitle());
				resultList.add(map);
			}
			return JsonUtils.objectToJson(resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
//		//调用服务层的服务查询打广告位的数据
//				String result = HttpClientUtil.doGet(REST_BASE_URL + REST_INDEX_AD_URL);
//				//把json数据转换成对象
//				TaotaoResult taotaoResult = TaotaoResult.formatToList(result, TbContent.class);
//				List<ADItem>itemList = new ArrayList<>();
//				if (taotaoResult.getStatus() == 200 ) {
//					List<TbContent>contentList = (List<TbContent>) taotaoResult.getData();
//					for (TbContent tbContent : contentList) {
//						ADItem item = new ADItem();
//						item.setHeight(240);
//						item.setWidth(670);
//						item.setSrc(tbContent.getPic());
//						item.setHeightB(240);
//						item.setWidth(550);
//						item.setSrcB(tbContent.getPic2());
//						item.setAlt(tbContent.getTitleDesc());
//						item.setHref(tbContent.getUrl());
//						itemList.add(item);
//					}
//					
//				}
//				return JsonUtils.objectToJson(itemList);
//			
//
	}

}
