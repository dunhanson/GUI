package com.gui.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Excel操作工具类
 * @author dunhanson
 * @since 2017-11-29
 */
public class ExcelUtils {
	
	/**
	 * 获取Workbook
	 * @return
	 */
	public static Workbook getWorkbook() {
		Workbook workbook = new HSSFWorkbook();
		workbook.createSheet();
		return workbook;
	}
	
	/**
	 * 往Workbook中写入记录
	 * @param outputStream 输出流
	 * @param rowValues 写入记录
	 * @param rowNum 读取的开始行号
	 * @param cellNum 读取的开始列号
	 */
	public static void write(OutputStream out, List<String[]> rowValues, int rowNum, int cellNum){
		Workbook workbook = null;
		try {
			workbook = getWorkbook();
			//通过索引获取Sheet对象
			Sheet sheet = workbook.getSheetAt(0);
			//往Sheet表中写入数据
			write(sheet, rowValues, rowNum, cellNum);
			//写入到输出流当中
			workbook.write(out);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(out);
			close(workbook);
		}
	}
	
	/**
	 * 往Sheet中写入记录
	 * @param sheet Sheet对象
	 * @param rowValues 行值
	 * @param rowNum 写入的开始行号
	 * @param cellNum 写入的开始列号
	 */
	public static void write(Sheet sheet, List<String[]> rowValues, int rowNum, int cellNum){
		//遍历行记录
		for(int i = 0; i < rowValues.size(); i++){
			//创建行
			Row row = sheet.createRow(rowNum - 1 + i);
			//获取列值数组
			String[] cellValues = rowValues.get(i);
			//遍历列记录
			for(int j = cellNum - 1; j < cellValues.length; j++){
				//创建列
				Cell cell = row.createCell(j);
				//列赋值
				cell.setCellValue(cellValues[j]);
			}
		}
	}
		
	/**
	 * 读取一列信息
	 * @param rowNum 读取开始行号
	 * @param cellNum 读取列号
	 * @return
	 */
	public static List<String> readCell(InputStream in, int rowNum, int cellNum){
		Workbook workbook = null;
		List<String> list = new ArrayList<>();
		try {
			workbook = WorkbookFactory.create(in);
			Sheet sheet = workbook.getSheetAt(0);
			//行数
			int rowCount = sheet.getPhysicalNumberOfRows();
			for(int i = rowNum -1 ; i < rowCount; i++){//遍历行
				Row row = sheet.getRow(i);
				if(row != null) {
					Cell cell = row.getCell(cellNum - 1);
					if(cell != null) {
						String value = cell.getStringCellValue();
						if(StringUtils.isNotBlank(value)) {
							list.add(value);
						}		
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close(workbook);
		}
		return list;
	}
	
	/**
	 * 读取Sheet记录存入List集合
	 * @param sheet Sheet对象
	 * @param startRowNum 读取的起始行号
	 * @param startCellNum 读取的起始列号
	 * @return
	 */
	public static List<String[]> readSheetData(Sheet sheet, int startRowNum, int startCellNum){
		//行值集合
		List<String[]> rowsData = new ArrayList<>();
		//总行数
		int rowCount = sheet.getPhysicalNumberOfRows();
		for(int i = startRowNum - 1; i < rowCount; i++){//读取行
			//列值集合
			List<String> cellsData = new ArrayList<>();
			Row row = sheet.getRow(i);
			//总列数
			int cellCount = row.getPhysicalNumberOfCells();
			for(int j = startCellNum - 1; j < cellCount; j++){//读取列
				Cell cell = row.getCell(j);
				if(cell == null){
					cellsData.add(null);	
				}else{
					cellsData.add(cell.getStringCellValue());	
				}
			}
			rowsData.add(cellsData.toArray(new String[cellsData.size()]));
		}
		return rowsData;
	}
	
	/**
	 * 获取指定索引Sheet的数据
	 * @param inputStream 输入流
	 * @param sheetNum Sheet索引
	 * @return
	 */
	public static List<String[]> getSheetData(InputStream in, int sheetNum){
		List<String[]> rowsData = new ArrayList<>();
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(in);
			Sheet sheet = workbook.getSheetAt(sheetNum - 1);
			rowsData = readSheetData(sheet, 1, 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			close(workbook);
		}		
		return rowsData;
	}
	
	/**
	 * 关闭资源
	 * @param workbook
	 * @throws IOException 
	 */
	private static void close(Workbook workbook) {
		try {
			if(workbook != null){
				workbook.close();
			}			
		} catch (Exception e) {

		}
	}
	
}
