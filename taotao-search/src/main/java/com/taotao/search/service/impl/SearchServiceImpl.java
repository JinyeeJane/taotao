package com.taotao.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.search.dao.SearchDao;
import org.springframework.stereotype.Service;

import com.taotao.search.pojo.SearchResult;
import com.taotao.search.service.SearchService;
@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private SearchDao searchDao;
	
	@Override
	public SearchResult search(String queryString, int page, int rows) throws Exception {
		//创建查询对象
		SolrQuery query=new SolrQuery();
		//查询条件
		query.setQuery(queryString);
		//设置分页
		query.setStart(rows*(page-1));
		query.setRows(rows);
		//设置默认搜索
		query.set("df", "item_keywords");
		//设置高亮
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em style=\"color:red\">");
		query.setHighlightSimplePost("</em>");
		//执行查询
		SearchResult searchResult=searchDao.search(query);
		//计算总页数
		long recordCount=searchResult.getRecordCount();
		long pageCount=recordCount/rows;
		if (recordCount%rows>0) {
			pageCount++;
		}
		searchResult.setPageCount(pageCount);
		searchResult.setCurPage(page);
		return searchResult;
	}

}
