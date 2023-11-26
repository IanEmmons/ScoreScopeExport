package org.virginiaso.score_scope_export;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

public record TeamResults(
		String tournamentId,
		String tournamentName,
		String division,
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

	public String bareTeamNum() {
		var m = TEAM_NUM_PATTERN.matcher(teamNum());
		return m.matches()
			? Integer.toString(Integer.parseInt(m.group(1)))
			: teamNum();
	}

	private String[] splitCityState() {
		var components = cityState().split(",");
		if (components.length != 2) {
			throw new IllegalStateException(
				"City/state value '%1$s' should be in the form 'city, state'"
				.formatted(cityState()));
		}
		return components;
	}

	public String city() {
		return splitCityState()[0].strip();
	}

	public String state() {
		return splitCityState()[1].strip();
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
