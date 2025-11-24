package org.virginiaso.score_scope_export;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

public record TeamResults(
		String trackId,
		String teamId,
		String teamNum,
		String schoolName,
		String teamName,
		String cityState,
		boolean isExhibitionTeam,
		BigDecimal scoreNoPenalty,	// Summed Team Rank Score
		BigDecimal penalty,
		BigDecimal finalScore)		// Team Rank Score
	implements Comparable<TeamResults> {
	private static final Pattern TEAM_NUM_PATTERN = Pattern.compile("^[AaBbCc]([0-9]+)$");
	private static final List<Pair<Pattern, String>> SCHOOL_TRANSLATIONS = List.of(
		Pair.of(Pattern.compile("^BASIS Washington, D\\.C\\.$"), "BASIS Independent Washington, D.C."),
		Pair.of(Pattern.compile("^Frost MS$"), "Robert Frost Middle School"),
		Pair.of(Pattern.compile("^Nysmith School$"), "Nysmith School for the Gifted"),
		Pair.of(Pattern.compile(" +ES$"), " Elementary School"),
		Pair.of(Pattern.compile(" +HS$"), " High School"),
		Pair.of(Pattern.compile(" +MS$"), " Middle School"),
		Pair.of(Pattern.compile(" +SS$"), " Secondary School")
		);
	private static final List<Pair<Pattern, String>> SCHOOL_ABBREV_TRANSLATIONS = List.of(
		Pair.of(Pattern.compile("^BASIS Independent McLean$"), "BASIS McLean"),
		Pair.of(Pattern.compile("^BASIS Washington, D\\.C\\.$"), "BASIS DC"),
		Pair.of(Pattern.compile("^Frost MS$"), "Frost Middle School"),
		Pair.of(Pattern.compile("^Nysmith School$"), "")	// empty ==> use original name
		// Add an entry for TJHSST
		);

	public String bareTeamNum() {
		var m = TEAM_NUM_PATTERN.matcher(teamNum());
		return m.matches()
			? Integer.toString(Integer.parseInt(m.group(1)))
			: teamNum();
	}

	private String[] splitCityState() {
		var components = cityState().split(Pattern.quote(","));
		if (components.length != 2) {
			throw new IllegalStateException(
				"City/state value '%1$s' should be in the form 'city, state'"
				.formatted(cityState()));
		}
		return components;
	}

	public String schoolNameForDuosmium() {
		return Util.applyTranslations(schoolName(), SCHOOL_TRANSLATIONS);
	}

	public String schoolAbbrevForDuosmium() {
		var abbrev = Util.applyTranslations(schoolName(), SCHOOL_ABBREV_TRANSLATIONS);
		if (abbrev.isEmpty()) {
			return schoolName();
		} else if (abbrev.equals(schoolName())) {
			return "";
		} else {
			return abbrev;
		}
	}

	public String city() {
		return splitCityState()[0].strip();
	}

	public String state() {
		return splitCityState()[1].strip();
	}

	public String adjustedTeamName(TournamentLevel tournamentLevel) {
		if (tournamentLevel == TournamentLevel.STATE) {
			return "";
		} else if ("Regional Team".equals(teamName())) {
			return "";
		} else {
			return teamName();
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(teamId());
	}

	@Override
	public boolean equals(Object rhs) {
		if (this == rhs) {
			return true;
		} else if (!(rhs instanceof TeamResults rhsAsTeamResults)) {
			return false;
		} else {
			return Objects.equals(this.teamId(), rhsAsTeamResults.teamId());
		}
	}

	@Override
	public int compareTo(TeamResults rhs) {
		return Objects.compare(this.teamId(), rhs.teamId(), String.CASE_INSENSITIVE_ORDER);
	}

	@Override
	public String toString() {
		return "TeamResults [%1$s, %2$s]".formatted(teamNum(), teamName());
	}
}
