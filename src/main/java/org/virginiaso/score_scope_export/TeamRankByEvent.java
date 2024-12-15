package org.virginiaso.score_scope_export;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.tuple.Pair;

public record TeamRankByEvent(
		String rawScoreId,
		String tournamentId,
		String tournamentName,	// Events by Tournament > Tournament
		String divisionId,
		String division,			// Events by Tournament > Division
		String event,				// Events by Tournament > Event Name
		boolean isTrialEvent,	// Trial Event?
		String teamNum,			// Teams > Team #
		String rank)				// Duosmium rank
	implements Comparable<TeamRankByEvent> {

	private static final List<Pair<Pattern, String>> EVENT_TRANSLATIONS = List.of(
		Pair.of(Pattern.compile(" +[BC]$"), ""),
		Pair.of(Pattern.compile("^Anatomy \\& Physiology$"), "Anatomy and Physiology"),
		Pair.of(Pattern.compile("^Potions \\& Poisons$"), "Potions and Poisons")
		);

	public String eventForDuosmium() {
		return Util.applyTranslations(event(), EVENT_TRANSLATIONS);
	}

	@Override
	public int hashCode() {
		return Objects.hash(rawScoreId());
	}

	@Override
	public boolean equals(Object rhs) {
		if (this == rhs) {
			return true;
		} else if (!(rhs instanceof TeamRankByEvent teamRankByEvent)) {
			return false;
		} else {
			return Objects.equals(this.rawScoreId(), teamRankByEvent.rawScoreId());
		}
	}

	@Override
	public int compareTo(TeamRankByEvent rhs) {
		return new CompareToBuilder()
			.append(this.tournamentName(), rhs.tournamentName())
			.append(this.division(), rhs.division())
			.append(this.event(), rhs.event())
			.append(this.teamNum(), rhs.teamNum())
			.append(this.rawScoreId(), rhs.rawScoreId())
			.toComparison();
	}

	@Override
	public String toString() {
		return "Student [tournament=%s, div=%s, event=%s, team=%s, rank=%s]".formatted(
			tournamentName(), division(), event(), teamNum(), rank());
	}
}
