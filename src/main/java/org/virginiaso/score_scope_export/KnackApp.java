package org.virginiaso.score_scope_export;

import java.util.Objects;

public enum KnackApp {
	SCORE_SCOPE("ScoreScope"),
	VASO_PORTAL("the VASO Portal");

	private final String title;

	private KnackApp(String title) {
		this.title = Objects.requireNonNull(title, "title");
	}

	public String title() {
		return title;
	}
}
