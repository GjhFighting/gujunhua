package com.gkx.cti.caas.util;

import java.io.*;

import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import org.springframework.core.io.DefaultResourceLoader;


public class FileUtil {

	// 试一下
	public static void main(String[] args) throws IOException {
		String path = ResourceUtils.getURL("classpath:").getPath();
		System.out.println(path);
		DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
		Resource resource = defaultResourceLoader.getResource("classpath:/plugin/");
		File file = resource.getFile();
		System.out.println(file.getAbsolutePath());
	}

	public static String testfile(String filelocation) throws IOException {
		// 读取c盘的file1文件
		DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
		Resource resource = defaultResourceLoader.getResource("classpath:/plugin/" + filelocation);
		File file = resource.getFile();
		BufferedInputStream bis = null;
		FileInputStream fis = null;
		String string = "";
		try {
			// 第一步 通过文件路径来创建文件实例
			fis = new FileInputStream(file);
			/*
			 * 把FileInputStream实例 传递到 BufferedInputStream 目的是能快速读取文件
			 */
			bis = new BufferedInputStream(fis);
			/* available检查是不是读到了文件末尾 */
			while (bis.available() > 0) {
//              System.out.print((char)bis.read());
				string += (char) bis.read();
			}
			System.out.println("*******");
            System.out.println(string);
		} catch (FileNotFoundException fnfe) {
			System.out.println("文件不存在" + fnfe);
		} catch (IOException ioe) {
			System.out.println("I/O 错误: " + ioe);
		} finally {
			try {
				if (bis != null && fis != null) {
					fis.close();
					bis.close();
				}
			} catch (IOException ioe) {
				System.out.println("关闭InputStream句柄错误: " + ioe);
			}
		}
		return string;
	}
}


