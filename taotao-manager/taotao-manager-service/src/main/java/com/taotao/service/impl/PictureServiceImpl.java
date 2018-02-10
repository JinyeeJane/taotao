package com.taotao.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.taotao.common.utils.FtpUtil;
import com.taotao.common.utils.IDUtils;
import com.taotao.service.PictureService;
@Service
public class PictureServiceImpl implements PictureService {
	
	@Value("${FTP_ADDRESS}")
	private String FTP_ADDRESS;
	@Value("${FTP_PORT}")
	private Integer FTP_PORT;
	@Value("${FTP_USER}")
	private String FTP_USER;
	@Value("${FTP_PASSWORD}")
	private String FTP_PASSWORD;
	@Value("${FTP_BASE_PATH}")
	private String FTP_BASE_PATH;
	@Value("${IMAGE_BASE_PATH}")
	private String IMAGE_BASE_PATH;
	
	@Override
	public Map uploadPicture(MultipartFile uploadFile){
		Map resultMap=new HashMap<>();
		try {
			//生成新文件名，包括取原名字的后缀和重命名，通过IDUtils类来控制不重名，按日期造出图片存放地址
			String oldName=uploadFile.getOriginalFilename();
			String newName=IDUtils.genImageName();
			newName=newName+oldName.substring(oldName.lastIndexOf("."));
			String imagePath=new DateTime().toString("yyyy/MM/dd");
			//使用FtpUtils工具类上传
			boolean result=FtpUtil.uploadFile(FTP_ADDRESS, FTP_PORT, FTP_USER, FTP_PASSWORD, FTP_BASE_PATH, imagePath , newName, uploadFile.getInputStream());
			//返回结果
			if (!result) {
				resultMap.put("error", 1);
				resultMap.put("message", "文件上传失败");
				return resultMap;
			}
			resultMap.put("error", 0);
			resultMap.put("url", IMAGE_BASE_PATH+imagePath+"/"+newName);
			return resultMap;
		} catch (IOException e) {
			resultMap.put("error", 1);
			resultMap.put("message", "上传发生异常");
			return resultMap;
		}
	}

}
