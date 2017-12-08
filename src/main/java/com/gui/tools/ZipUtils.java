package com.gui.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * 压缩工具类
 * @author dunhanson
 * @since 2017-11-30
 */
public class ZipUtils {
	@Test
	public void test(){
		try {
			ZipUtils.compress("D:\\Program Files\\apache-tomcat-7.0.75\\", "D:\\tomcat.zip");
			System.out.println("SUCCESS");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 进行压缩
	 * @param source 源路径
	 * @param export 输出路径
	 * @throws IOException
	 */
	public static void compress(String source, String export) throws IOException{
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(export));
		writeToZip(new File(source), "", zipOut);
		zipOut.close();
	}
	
	/**
	 * 添加压缩文件
	 * @param file File对象（文件&目录）
	 * @param parentPath 父目录路径
	 * @param zipOut ZipOutputStream对象
	 * @throws IOException
	 */
	public static void writeToZip(File file, String parentPath, ZipOutputStream zipOut) throws IOException{
		if(file.isDirectory()){//目录
			parentPath += (file.getName() + File.separator);
			File[] files = file.listFiles();
			for(int i = 0; i < files.length; i++){
				//进入递归
				writeToZip(files[i], parentPath, zipOut);
			}
		}else{//文件
			zipOut.putNextEntry(new ZipEntry(parentPath + file.getName()));
			InputStream input = new FileInputStream(file);
			zipOut.write(IOUtils.toByteArray(input));
			input.close();
		}
	}
}
