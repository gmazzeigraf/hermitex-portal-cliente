package br.com.graflogic.hermitex.cliente.service.util;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * 
 * @author gmazz
 *
 */
public class ExcelUtil {

	// Excel
	public static XSSFSheet ajustaColumns(XSSFSheet sheet, int startRow) {
		for (int i = 0; i < sheet.getRow(startRow).getLastCellNum(); i++) {
			sheet.autoSizeColumn(i);
		}

		return sheet;
	}

	public static XSSFCellStyle ajustaHeaderStyle(XSSFCellStyle headerStyle) {
		headerStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		headerStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		headerStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		headerStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);

		return headerStyle;
	}

	public static XSSFCellStyle ajustaLineStyle(XSSFCellStyle lineStyle) {
		lineStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		lineStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		lineStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		lineStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);

		return lineStyle;
	}
}