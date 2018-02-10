package com.taotao.portal.pojo;

import org.apache.commons.lang3.ObjectUtils.Null;

import com.taotao.pojo.TbItem;

public class ItemInfo extends TbItem {
	
	public String[] getImages() {
		String image=getImage();
		if (getImage()!=null) {
			String[] images=image.split(",");
			return images;
		}
		return null;
	}
	
}
