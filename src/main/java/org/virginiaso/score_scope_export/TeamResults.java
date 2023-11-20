package org.virginiaso.score_scope_export;

import java.math.BigDecimal;
import java.util.Objects;

public record TeamResults(
		String tournamentId,
		String tournamentName,
		String division,
		String teamId,
		String teamNum,
		String teamName,				// Award Ceremony Team Name
		boolean isExhibitionTeam,
		BigDecimal scoreNoPenalty,	// Summed Team Rank Score
		BigDecimal penalty,
		BigDecimal finalScore)		// Team Rank Score
	implements Comparable<TeamResults> {

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
