package com.taotao.service;
import java.util.*;

import com.taotao.common.pojo.EUTreeNode;
import com.taotao.pojo.TbItemCat;

public interface ItemCatService {
	public List<EUTreeNode> getCatList(Long parentId);
}
