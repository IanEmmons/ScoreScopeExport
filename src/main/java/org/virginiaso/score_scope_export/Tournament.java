package org.virginiaso.score_scope_export;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record Tournament(
	String id,
	String name,
	Instant date,
	boolean oneTrophyPerSchool,
	int numBMedalsPerEvent,
	int numCMedalsPerEvent,
	int numBTrophies,
	int numCTrophies) {

	public String formattedDate() {
		return DateTimeFormatter
			.ofPattern("uuuu-MM-dd")
			.withLocale(Locale.US)
			.withZone(ZoneId.of("UTC"))
			.format(date());
	}

	public int competitionYear() {
		var localDate = LocalDate.ofInstant(date(), ZoneId.of("UTC"));
		var year = localDate.getYear();
		var month = localDate.getMonthValue();
		if (month > 6) {
			++year;
		}
		return year;
	}

	public int numMedalsPerEvent() {
		return "B".equals(Config.inst().getTournamentDivision())
			? numBMedalsPerEvent()
			: numCMedalsPerEvent();
	}

	public int numTrophies() {
		return "B".equals(Config.inst().getTournamentDivision())
			? numBTrophies()
			: numCTrophies();
	}
}
