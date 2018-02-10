package com.taotao.rest.jedis;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrJTest {
	@Test
	public void addDocument() throws Exception{
		//创建连接
		SolrServer solrServer=new HttpSolrServer("http://192.168.244.128:8080/solr");
		//创建文档对象
		SolrInputDocument document=new SolrInputDocument();
		document.addField("id","test001");
		document.addField("item_title","测试商品");
		document.addField("item_price",54321);
		//把文档对象写入索引库
		solrServer.add(document);
		//提交
		solrServer.commit();
	}
	
	@Test
	public void delDocument() throws Exception{
		//创建连接
		SolrServer solrServer=new HttpSolrServer("http://192.168.244.128:8080/solr");
		//按id删除
		solrServer.deleteById("test001");
		//删除全部
		//solrServer.deleteByQuery("*.*");
		//提交
		solrServer.commit();
	}
	
	@Test
	public void queryDocument() throws Exception{
		//创建连接
		SolrServer solrServer=new HttpSolrServer("http://192.168.244.128:8080/solr");
		//创建查询对象
		SolrQuery solrQuery=new SolrQuery();		
		//设置查询条件，执行查询
		solrQuery.setQuery("*:*");
		QueryResponse response=solrServer.query(solrQuery);
		//查询结果
		SolrDocumentList solrDocumentList=response.getResults();
		System.out.println(solrDocumentList.getNumFound());
		for (SolrDocument solrDocument:solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
			System.out.println(solrDocument.get("item_price"));
			System.out.println(solrDocument.get("item_image"));
		}
	}
	
}
