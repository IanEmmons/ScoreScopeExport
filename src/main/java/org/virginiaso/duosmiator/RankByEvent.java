package org.virginiaso.duosmiator;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

public record RankByEvent(
		String rawScoreId,
		String trackId,
		String event,				// Events by Tournament > Event Name
		boolean isTrialEvent,	// Trial Event?
		String teamNum,			// Teams > Team #
		String rank) {				// Duosmium rank

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
		} else if (!(rhs instanceof RankByEvent teamRankByEvent)) {
			return false;
		} else {
			return Objects.equals(this.rawScoreId(), teamRankByEvent.rawScoreId());
		}
	}
}
