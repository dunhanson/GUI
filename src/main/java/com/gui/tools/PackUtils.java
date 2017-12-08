package com.gui.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * 增量打包工具
 * @author dunhanson
 * @since 20017-11-29
 */
public class PackUtils {
	private static final String GENERAL_SRC = "/src/";
	private static final String GENERAL_WEBROOT = "/WebRoot/";
	private static final String GENERAL_WEBCONTENT = "/WebContent/";
	private static final String MAVEN_JAVA = "/src/main/java/";
	private static final String MAVEN_RESOURCES = "/src/main/resources/";
	private static final String MAVEN_WEBAPP = "/src/main/webapp/";
	private static final String WEB_INFO_CLASSES = "/WEB-INF/classes/";
	public static String projectPath; 		//项目路径
	public static String exportPath; 		//输出路径
	public static String excelPath;			//Excel文件路径
	public static String projectName;		//项目名称
	public static Integer rowNum = 2; 	//Excel读取开始行数
	public static Integer cellNum = 2; 	//Excel读取开始列数
	
	@Test
	public void test() throws IOException {
		projectPath = "D:\\Program Files\\tomcat\\apache-tomcat-7.0.79\\webapps\\bxkc-operation";
		exportPath = "E:\\部署打包\\pack";
		excelPath = "E:\\部署打包\\部署清单-General.xlsx";
		pack();
		System.out.println("SUCCESS.");
	}
	
	//@Test
	public void test2(){
		String str = "/src/main/webapp/page/novice_welfare/jsp/order-detail.jsp";
		System.out.println(str.startsWith(MAVEN_WEBAPP));
	}
	

	/**
	 * 增量打包
	 * @param projectPath 项目路径
	 * @param exportPath 输出路径
	 * @param excelPath Excel文件路径
	 * @param startRowNum Excel读取开始行数
	 * @param startCellNum Excel读取开始列数
	 * @throws IOException
	 */
	public static void pack() throws IOException {
		//配置检查
		if(StringUtils.isBlank(projectPath)) {
			throw new RuntimeException("项目路径为空！");
		}else if(!new File(projectPath).exists()) {
			throw new RuntimeException("项目路径不存在！");
		}else if(StringUtils.isBlank(excelPath)) {
			throw new RuntimeException("EXCEL文件路径为空！");
		}else if(!new File(projectPath).exists()) {
			throw new RuntimeException("EXCEL文件路径不存在！");
		}
		String projectName = projectPath.substring(projectPath.lastIndexOf(File.separator) + 1).trim();
		exportPath += (File.separator + projectName);
		//清空输出目录
		FileUtils.deleteQuietly(new File(exportPath));
		//需要增量打包的代码路径List集合
		List<String> pathList = ExcelUtils.readCell(new FileInputStream(excelPath), rowNum, cellNum);
		//处理过的代码路径Set集合
		Set<String> pathSet = new HashSet<>();
		//遍历代码路径，进行字符串替换，改为class路径
		for(int i = 0; i < pathList.size(); i++) {
			String path = pathList.get(i);
			if(StringUtils.isNotBlank(path)) {//路径不为空
				path = getPath(path);
				if(StringUtils.isNotBlank(path)){
					pathSet.add(path);	
				}
			}
		}
		//复制打包文件
		copyFile(pathSet);
		ZipUtils.compress(PackUtils.exportPath, PackUtils.exportPath + ".zip");
	}
	
	/**
	 * 复制需要增量打包的文件到输出目录
	 * @param pathSet
	 * @throws IOException
	 */
	public static void copyFile(Set<String> pathSet) throws IOException {
		//遍历处理过的代码路径，进行增量打包（复制class文件到输出路径）
		for (String path : pathSet) {
			String realPath = projectPath + path;
			File realFile = new File(realPath);
			if(realFile.isFile() && !realFile.exists()) { //文件不存在抛出异常终止运行
				throw new RuntimeException("文件不存在：" + realPath);
			}
			if(realFile.isFile()) { //只对文件进行操作 
				//同级目录的所有文件
				File[] siblingFiles = realFile.getParentFile().listFiles();
				for (File siblingFile : siblingFiles) { //遍历同级目录获取内部类
					//同级文件文件名
					String siblingFileName = siblingFile.getName();
					//如果是内部类
					if(siblingFileName.contains("$") && siblingFileName.endsWith(".class")) {
						int beginIndex = siblingFileName.indexOf("$");
						int endIndex = siblingFileName.lastIndexOf(".class");
						String target = siblingFileName.substring(beginIndex, endIndex);
						//是否为符合的内部类
						if(siblingFileName.replace(target, "").equals(realFile.getName())) {
							//内部类路径
							String newPath = path.replace(realFile.getName(), siblingFileName);
							//复制文件
							FileUtils.copyFile(siblingFile, new File(exportPath + newPath));
						}
					}
				}
				//复制文件
				FileUtils.copyFile(realFile, new File(exportPath + path));
			}
		}	
	}
	
	/**
	 * 源文件目录转换为class路径
	 * @param path
	 * @return
	 */
	public static String getPath(String path) {
		if(path.indexOf("/") == -1){
			//throw new RuntimeException("路径错误：" + path);
			return "";
		}
		path = path.substring(path.indexOf("/") + 1);
		if(path.indexOf("/") == -1){
			//throw new RuntimeException("路径错误：" + path);
			return "";
		}		
		path = path.substring(path.indexOf("/"));
		if(path.startsWith(MAVEN_JAVA)) {
			path = path.replace(MAVEN_JAVA, WEB_INFO_CLASSES);
		}else if(path.startsWith(MAVEN_RESOURCES)) {
			path = path.replace(MAVEN_RESOURCES, WEB_INFO_CLASSES);
		}else if(path.startsWith(MAVEN_WEBAPP)) {
			path = path.replace(MAVEN_WEBAPP, "/");
		}else if(path.startsWith(GENERAL_SRC)) {
			path = path.replace(GENERAL_SRC, WEB_INFO_CLASSES);
		}else if(path.startsWith(GENERAL_WEBROOT)) {
			path = path.replace(GENERAL_WEBROOT, "/");
		}else if(path.startsWith(GENERAL_WEBCONTENT)) {
			path = path.replace(GENERAL_WEBCONTENT, "/");
		}else {
			path = path.substring(path.indexOf("/") + 1);
			path = path.substring(path.indexOf("/") + 1);
			path = WEB_INFO_CLASSES + path;
		}
		path = path.replace(".java", ".class");
		return path.replace("/", File.separator);
	}
	
}
