package org.virginiaso.score_scope_export;

public enum TournamentLevel {
	INVITATIONAL("Invitational"),
	REGIONAL("Regionals"),
	STATE("States"),
	NATIONAL("Nationals");

	private final String duosmiumLabel;

	private TournamentLevel(String duosmiumLabel) {
		this.duosmiumLabel = duosmiumLabel;
	}

	public String label() {
		return duosmiumLabel;
	}
}
