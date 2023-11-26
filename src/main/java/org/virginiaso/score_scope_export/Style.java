package org.virginiaso.score_scope_export;

import java.util.EnumMap;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;

public enum Style {
	PLAIN,
	VERTICAL_PLAIN,
	TITLE,
	SUB_TITLE,
	EVEN_ROW,
	ODD_ROW,
	EVEN_PLACING,
	ODD_PLACING;

	public static EnumMap<Style, CellStyle> createCellStyles(Workbook workbook) {
		EnumMap<Style, CellStyle> result = new EnumMap<>(Style.class);

		result.put(Style.PLAIN, newStyle(workbook, 0xff, 0xff, 0xff));
		result.put(Style.VERTICAL_PLAIN, newStyle(workbook, 0xff, 0xff, 0xff,
			style -> style.setRotation((short) 90)));
		result.put(Style.TITLE, newStyle(workbook, 0x94, 0xb3, 0xf1));
		result.put(Style.SUB_TITLE, newStyle(workbook, 0xe0, 0xea, 0xfd,
			style -> style.setWrapText(true)));
		result.put(Style.EVEN_ROW, newStyle(workbook, 0xff, 0xef, 0xc1));
		result.put(Style.ODD_ROW, newStyle(workbook, 0xfe, 0xf7, 0xdc));
		result.put(Style.EVEN_PLACING, newStyle(workbook, 0xff, 0xef, 0xc1,
			style -> style.setAlignment(HorizontalAlignment.CENTER)));
		result.put(Style.ODD_PLACING, newStyle(workbook, 0xff, 0xef, 0xc1,
			style -> style.setAlignment(HorizontalAlignment.CENTER)));

		return result;
	}

	private static CellStyle newStyle(Workbook workbook, int r, int g, int b,
			Consumer<CellStyle> modifier) {
		var style = newStyle(workbook, r, g, b);
		modifier.accept(style);
		return style;
	}

	private static CellStyle newStyle(Workbook workbook, int r, int g, int b) {
		var font = workbook.createFont();
		font.setFontName("Times New Roman");
		font.setFontHeightInPoints((short) 12);

		var style = workbook.createCellStyle();
		style.setFillForegroundColor(newColor(r, g, b));
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(font);
		style.setBorderBottom(BorderStyle.HAIR);
		style.setBorderTop(BorderStyle.HAIR);
		style.setBorderLeft(BorderStyle.HAIR);
		style.setBorderRight(BorderStyle.HAIR);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		return style;
	}

	private static Color newColor(int r, int g, int b) {
		return new XSSFColor(new byte[] { (byte) r, (byte) g, (byte) b });
	}
}
