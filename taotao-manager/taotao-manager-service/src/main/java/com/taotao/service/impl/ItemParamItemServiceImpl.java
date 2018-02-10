package com.taotao.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EUDateGridResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItemParamItemExample;
import com.taotao.pojo.TbItemParamItemExample.Criteria;
import com.taotao.service.ItemParamItemService;
@Service
public class ItemParamItemServiceImpl implements ItemParamItemService {
	
	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;
	
	@Override
	public String getItemParamByItemId(Long itemId) {
		//根据商品id查询规格参数
		TbItemParamItemExample example=new TbItemParamItemExample();
		Criteria criteria=example.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		//执行查询
		List<TbItemParamItem> list=itemParamItemMapper.selectByExampleWithBLOBs(example);
		//无规格参数情况
		if (list==null||list.size()==0) {
			return "";
		}
		//有规格参数则取出用于显示
		TbItemParamItem itemParamItem=list.get(0);
		String paramData=itemParamItem.getParamData();
		//把规格参数json数据转化为java对象
		List<Map> jsonList=JsonUtils.jsonToList(paramData, Map.class);
		//生成html
		StringBuffer sb=new StringBuffer();
		sb.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\">\n");
		sb.append("<tbody>\n");
		for (Map m1:jsonList) {
			sb.append("<tr>\n");
			sb.append("<th colspan=\"2\">"+m1.get("group")+"</th>\n");
			sb.append("</tr>\n");
			List<Map> jsonList2=(List<Map>)m1.get("params");
			for (Map m2:jsonList2){
				sb.append("</tr>\n");
				sb.append("<td>"+m2.get("k")+"</td>\n");
				sb.append("<td>"+m2.get("v")+"</td>\n");
				sb.append("</tr>\n");
			}
		}
		sb.append("</tbody>\n");
		sb.append("</table>");
		return sb.toString();
	}

	
}
