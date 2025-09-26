package org.virginiaso.score_scope_export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;

public class ExportWriter {
	private static final String SUB_TITLE_TEXT
		= "Copy yellow cells and paste (values only) into Duosmium template";
	private static final int DEFAULT_ZOOM = 140;

	private final File outputFile;
	private final String tournamentName;
	private final String division;
	private Workbook workbook;
	private EnumMap<Style, CellStyle> styles;

	public ExportWriter(File outputFile, String tournamentName, String division) {
		this.outputFile = Objects.requireNonNull(outputFile, "outputFile");
		this.tournamentName = Objects.requireNonNull(tournamentName, "tournamentName");
		this.division = Objects.requireNonNull(division, "division");
	}

	public void writeExport(List<Tournament> allTournaments, List<TeamResults> allTeamResults,
			List<TeamRankByEvent> allTeamRanksByEvent) throws IOException {
		Tournament tournament = allTournaments.stream()
			.filter(t -> tournamentName.equals(t.name()))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException(
				"No tournaments with name " + tournamentName));
		List<TeamResults> teamResults = allTeamResults.stream()
			.filter(tr -> tournamentName.equals(tr.tournamentName()))
			.filter(tr -> division.equals(tr.division()))
			.toList();
		List<TeamRankByEvent> ranks = allTeamRanksByEvent.stream()
			.filter(r -> tournamentName.equals(r.tournamentName()))
			.filter(r -> division.equals(r.division()))
			.toList();

		try (Workbook wkbk = new XSSFWorkbook(XSSFWorkbookType.XLSX)) {
			workbook = wkbk;
			styles = Style.createCellStyles(workbook);

			createOverviewSheet(tournament);
			createEventSheet(ranks);
			createTeamSheet(teamResults, tournament.tournamentLevel());
			createPlacingsSheet(teamResults, ranks);
			writeOutputFile();

			styles = null;
			workbook = null;
		}
	}

	private void createOverviewSheet(Tournament tournament) {
		var sheet = workbook.createSheet("1. General Instructions");
		sheet.setZoom(DEFAULT_ZOOM);
		var rowNum = 1;

		addOverviewRow(sheet, ++rowNum, "1A. Name", tournamentNameForDuosmium(tournamentName));
		addOverviewRow(sheet, ++rowNum, "1B. Short Name", "");
		addOverviewRow(sheet, ++rowNum, "1C. Location", "");
		addOverviewRow(sheet, ++rowNum, "1D. State", "");
		addOverviewRow(sheet, ++rowNum, "1E. Level", tournament.tournamentLevel().label());
		addOverviewRow(sheet, ++rowNum, "1F. Division", division);
		addOverviewRow(sheet, ++rowNum, "1G. Year", Integer.toString(tournament.competitionYear()));
		addOverviewRow(sheet, ++rowNum, "1H. Date", tournament.formattedDate());
		addOverviewRow(sheet, ++rowNum, "1I. Start Date", "");
		addOverviewRow(sheet, ++rowNum, "1J. End Date", "");
		addOverviewRow(sheet, ++rowNum, "1K. Awards Date", tournament.formattedDate());
		addOverviewRow(sheet, ++rowNum, "1L. Medals", Integer.toString(tournament.numMedalsPerEvent(division)));
		addOverviewRow(sheet, ++rowNum, "1M. Trophies", Integer.toString(tournament.numTrophies(division)));
		addOverviewRow(sheet, ++rowNum, "1N. Bids", numBids(tournament, division));
		addOverviewRow(sheet, ++rowNum, "1O. N-Offset", "0");
		addOverviewRow(sheet, ++rowNum, "1P. Drops", "0");
		addOverviewRow(sheet, ++rowNum, "1Q. Source", "");

		autoSizeColumns(sheet);
		addTitleRow(sheet, 0, Style.TITLE, "Tournament Information");
		addTitleRow(sheet, 1, Style.SUB_TITLE, SUB_TITLE_TEXT);
	}

	private static String tournamentNameForDuosmium(String name) {
		return Pattern.compile(" *20[0-9][0-9] *").matcher(name).replaceAll(" ").strip();
	}

	private static String numBids(Tournament tournament, String division) {
		var numBids = tournament.numBids(division);
		return (numBids < 0) ? "" : Integer.toString(numBids);
	}

	private void addOverviewRow(Sheet sheet, int rowNum, String label, String value) {
		var row = sheet.createRow(rowNum);
		var labelCell = row.createCell(0, CellType.STRING);
		labelCell.setCellStyle(styles.get(Style.PLAIN));
		labelCell.setCellValue(label);
		var valueCell = row.createCell(1, CellType.STRING);
		var valueStyle = (rowNum % 2 == 0)
			? Style.EVEN_ROW
			: Style.ODD_ROW;
		valueCell.setCellStyle(styles.get(valueStyle));
		valueCell.setCellValue(value);
	}

	private void createEventSheet(List<TeamRankByEvent> ranks) {
		Map<String, Boolean> eventMap = ranks.stream().collect(Collectors.toMap(
			TeamRankByEvent::eventForDuosmium,	// key mapper
			TeamRankByEvent::isTrialEvent,		// value mapper
			(v1, _) -> v1,								// merge function - shouldn't be invoked
			LinkedHashMap::new));					// map factory

		var sheet = workbook.createSheet("2. Events");
		sheet.setZoom(DEFAULT_ZOOM);
		var rowNum = 1;

		addTableRow(sheet, Style.PLAIN, ++rowNum, "2A. Event Name", "2B. Trial/Trialed");

		for (var entry : eventMap.entrySet()) {
			var event = entry.getKey();
			var isTrial = entry.getValue().booleanValue();
			addAlternatingTableRow(sheet, ++rowNum, event, isTrial ? "Trial" : "");
		}

		autoSizeColumns(sheet);
		addTitleRow(sheet, 0, Style.TITLE, "Events");
		addTitleRow(sheet, 1, Style.SUB_TITLE, SUB_TITLE_TEXT);
	}

	private void createTeamSheet(List<TeamResults> teamResults, TournamentLevel tournamentLevel) {
		var sheet = workbook.createSheet("4. Teams");
		sheet.setZoom(DEFAULT_ZOOM);
		var rowNum = 1;

		addTableRow(sheet, Style.PLAIN, ++rowNum,
			"4A. Team #",
			"4B. School",
			"4C. School Abbrev.",
			"4D. Suffix",
			"4E. City",
			"4F. State",
			"4G. Track",
			"4H. Exhibition?",
			"4I. Penalties");

		for (var teamResult : teamResults) {
			List<String> entries = new ArrayList<>();
			entries.add(teamResult.bareTeamNum());
			entries.add(teamResult.schoolNameForDuosmium());
			entries.add(teamResult.schoolAbbrevForDuosmium());
			entries.add(teamResult.adjustedTeamName(tournamentLevel));
			entries.add(teamResult.city());
			entries.add(teamResult.state());
			entries.add("");
			entries.add(teamResult.isExhibitionTeam() ? "Yes" : "");
			entries.add(teamResult.penalty().toString());
			addAlternatingTableRow(sheet, ++rowNum, entries.toArray(new String[0]));
		}

		autoSizeColumns(sheet);
		addTitleRow(sheet, 0, Style.TITLE, "Teams");
		addTitleRow(sheet, 1, Style.SUB_TITLE, SUB_TITLE_TEXT);
	}

	private void createPlacingsSheet(List<TeamResults> teamResults, List<TeamRankByEvent> ranks) {
		var sheet = workbook.createSheet("5. Placings");
		sheet.setZoom(DEFAULT_ZOOM);

		addTeamsToPlacings(sheet, teamResults);
		addEventsToPlacings(sheet, ranks);
		addPlacings(sheet, teamResults, ranks);

		autoSizeColumns(sheet);
		addTitleRow(sheet, 0, Style.TITLE, "Placings");
		addTitleRow(sheet, 1, Style.SUB_TITLE, SUB_TITLE_TEXT);
	}

	private void addTeamsToPlacings(Sheet placingsSheet, List<TeamResults> teamResults) {
		var rowNum = 1;

		addTableRow(placingsSheet, Style.PLAIN, ++rowNum,
			"Team #",
			"School",
			"Suffix");

		for (var teamResult : teamResults) {
			List<String> entries = new ArrayList<>();
			entries.add(teamResult.bareTeamNum());
			entries.add(teamResult.schoolName());
			entries.add(teamResult.teamName());
			addTableRow(placingsSheet, Style.PLAIN, ++rowNum, entries.toArray(new String[0]));
		}
	}

	private void addEventsToPlacings(Sheet placingsSheet, List<TeamRankByEvent> ranks) {
		var events = ranks.stream()
			.map(TeamRankByEvent::event)
			.collect(Collectors.toCollection(() -> new LinkedHashSet<>()));

		var firstRow = placingsSheet.getRow(2);
		var colNum = 2;
		for (var event : events) {
			var cell = firstRow.createCell(++colNum, CellType.STRING);
			cell.setCellStyle(styles.get(Style.VERTICAL_PLAIN));
			cell.setCellValue(event);
		}
	}

	private static int PLACINGS_ROW_OFFSET = 3;
	private static int PLACINGS_COL_OFFSET = 3;
	private void addPlacings(Sheet placingsSheet, List<TeamResults> teamResults,
		List<TeamRankByEvent> ranks) {
		var teamNums = teamResults.stream()
			.map(TeamResults::teamNum)
			.toList();
		var events = ranks.stream()
			.map(TeamRankByEvent::event)
			.collect(Collectors.toCollection(() -> new LinkedHashSet<>()))
			.stream()
			.toList();

		for (var rByE : ranks) {
			var teamIndex = teamNums.indexOf(rByE.teamNum());
			var eventIndex = events.indexOf(rByE.event());
			var rowNum = teamIndex + PLACINGS_ROW_OFFSET;
			var colNum = eventIndex + PLACINGS_COL_OFFSET;
			var row = placingsSheet.getRow(rowNum);
			var rowStyle = (rowNum % 2 == 0)
				? Style.EVEN_PLACING
				: Style.ODD_PLACING;
			var cell = row.createCell(colNum, CellType.STRING);
			cell.setCellStyle(styles.get(rowStyle));
			cell.setCellValue(rByE.rank());
		}
		for (int teamIndex = 0; teamIndex < teamNums.size(); ++teamIndex) {
			for (int eventIndex = 0; eventIndex < events.size(); ++eventIndex) {
				var rowNum = teamIndex + PLACINGS_ROW_OFFSET;
				var colNum = eventIndex + PLACINGS_COL_OFFSET;
				var row = placingsSheet.getRow(rowNum);
				var rowStyle = (rowNum % 2 == 0)
					? Style.EVEN_PLACING
					: Style.ODD_PLACING;
				var cell = row.getCell(colNum);
				if (cell == null) {
					cell = row.createCell(colNum, CellType.STRING);
					cell.setCellStyle(styles.get(rowStyle));
					cell.setCellValue("");
				}
			}
		}
	}

	private void addAlternatingTableRow(Sheet sheet, int rowNum, String... cellContents) {
		var rowStyle = (rowNum % 2 == 0)
			? Style.EVEN_ROW
			: Style.ODD_ROW;
		addTableRow(sheet, rowStyle, rowNum, cellContents);
	}

	private void addTableRow(Sheet sheet, Style rowStyle, int rowNum, String... cellContents) {
		var row = sheet.createRow(rowNum);
		var cellStyle = styles.get(rowStyle);
		var columnNum = -1;
		for (var cellText : cellContents) {
			var cell = row.createCell(++columnNum, CellType.STRING);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(cellText);
		}
	}

	private static void autoSizeColumns(Sheet sheet) {
		var row = sheet.getRow(sheet.getFirstRowNum());
		for (int col = row.getFirstCellNum(); col <= row.getLastCellNum(); ++col) {
			sheet.autoSizeColumn(col);
			sheet.setColumnWidth(col, (3 * sheet.getColumnWidth(col)) / 2);
		}
	}

	private void addTitleRow(Sheet sheet, int rowNum, Style titleStyle, String text) {
		var lastRowNum = sheet.getLastRowNum();
		var firstCellNum = sheet.getRow(lastRowNum).getFirstCellNum();
		var lastCellNum = sheet.getRow(lastRowNum).getLastCellNum();

		var titleRow = sheet.createRow(rowNum);
		var titleCell = titleRow.createCell(0, CellType.STRING);
		if ((lastCellNum - firstCellNum) > 1) {
			var range = new CellRangeAddress(rowNum, rowNum, firstCellNum, lastCellNum - 1);
			sheet.addMergedRegion(range);
		}
		titleCell.setCellStyle(styles.get(titleStyle));
		titleCell.setCellValue(text);
		if (titleStyle == Style.SUB_TITLE) {
			var height = titleRow.getHeightInPoints();
			titleRow.setHeightInPoints(2 * height);
		}
	}

	private void writeOutputFile() throws IOException {
		if (outputFile.isFile()) {
			outputFile.delete();
		} else {
			outputFile.getAbsoluteFile().getParentFile().mkdirs();
		}
		try (OutputStream os = new FileOutputStream(outputFile)) {
			workbook.write(os);
		}
	}
}
