package org.virginiaso.score_scope_export;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class SchoolNameTest {
	@SuppressWarnings("static-method")
	@ParameterizedTest
	@CsvFileSource(resources = "/schoolNames.txt", numLinesToSkip = 1, delimiter = '|')
	public void testDuosmiumSchoolName(String input, String expected, String abbrevName) {
		var team = new TeamResults("", "", "", "", "", input, "", ",", false,
			BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		var actual = team.schoolNameForDuosmium();
		assertEquals(expected, actual);
	}

	@SuppressWarnings("static-method")
	@ParameterizedTest
	@CsvFileSource(resources = "/schoolNames.txt", numLinesToSkip = 1, delimiter = '|')
	public void testDuosmiumAbbrevName(String input, String expected, String abbrevName) {
		var team = new TeamResults("", "", "", "", "", input, "", ",", false,
			BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
		var actual = team.schoolAbbrevForDuosmium();
		var abbrevNameNonNull = (abbrevName == null) ? "" : abbrevName;
		assertEquals(abbrevNameNonNull, actual);
	}
}
