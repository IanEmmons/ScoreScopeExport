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
	int numBBids,
	int numCBids,
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

	public int numBids(String division) {
		return "B".equals(division)
			? numBBids()
			: numCBids();
	}

	public int numMedalsPerEvent(String division) {
		return "B".equals(division)
			? numBMedalsPerEvent()
			: numCMedalsPerEvent();
	}

	public int numTrophies(String division) {
		return "B".equals(division)
			? numBTrophies()
			: numCTrophies();
	}
}
