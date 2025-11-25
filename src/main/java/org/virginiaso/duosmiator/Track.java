package org.virginiaso.duosmiator;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record Track(
	String trackId,
	String name,
	Instant date,
	TournamentLevel tournamentLevel,
	String division,
	boolean oneTrophyPerSchool,
	int numSchoolsProgressing,
	int numMedalsPerEvent,
	int numTrophies) {

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
}
