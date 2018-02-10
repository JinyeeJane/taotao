package com.taotao.controller;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;

import com.taotao.common.utils.FtpUtil;

public class FTPTest {
	@Test
	public void testFtpClient() throws Exception{
		//创建FTPClient对象
		FTPClient ftpClient=new FTPClient();
		//创建FTP连接，ip地址和端口号
		ftpClient.connect("192.168.244.128", 21);
		//登陆FTP服务器，用户名和密码
		ftpClient.login("ftpuser", "ftpuser");
		//设置上传的路径
		ftpClient.changeWorkingDirectory("/home/ftpuser/www/images");
		//修改上传文件传输格式，二进制格式，用于防止图片传输过程中损坏
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		//读取一个本地文件
		FileInputStream inputStream=new FileInputStream(new File("C:\\Users\\Public\\Pictures\\TEST_AAADLOBG.JPG"));
		//上传文件，给出远程存储名和上传文件的InputStream
		ftpClient.storeFile("hello.jpg", inputStream);
		//关闭连接
		ftpClient.logout();
	}
	
	@Test
	public void testFtpUtils() throws Exception{
		FileInputStream inputStream=new FileInputStream(new File("C:\\Users\\Public\\Pictures\\TEST_AAADLOBG.JPG"));
		FtpUtil.uploadFile("192.168.244.128", 21, "ftpuser", "ftpuser", "/home/ftpuser/www/images", "2016/08/10", "hello.jpg", inputStream);
	}
}
