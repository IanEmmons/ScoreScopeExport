package org.virginiaso.score_scope_export;

import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;

import javafx.concurrent.Task;

public class KnackTask extends Task<Void> {
	private final KnackApp knackApp;
	private final String userName;
	private final String password;
	private boolean hasSucceeded;

	public KnackTask(KnackApp knackApp, String userName, String password) {
		this.knackApp = Objects.requireNonNull(knackApp, "knackApp");
		this.userName = Objects.requireNonNull(userName, "userName");
		this.password = Objects.requireNonNull(password, "password");
		hasSucceeded = false;

		updateTitle("Fetching data from %1$s".formatted(this.knackApp.title()));
	}

	@Override
	protected Void call() {
		try {
			updateMessage("Logging in...");
			PortalUserToken.inst().initialize(knackApp, userName, password);

			updateMessage("Fetching the list of tournaments...");
			WizardData.inst().replaceTournaments(
				TournamentRetrieverFactory.create(knackApp).retrieveReport());

			updateMessage("Fetching the list of teams...");
			WizardData.inst().replaceTeamResults(
				TeamResultsRetrieverFactory.create(knackApp).retrieveReport());

			updateMessage("Fetching team ranks in each event...");
			WizardData.inst().replaceRanks(
				TeamRankByEventRetrieverFactory.create(knackApp).retrieveReport());

			updateMessage("Data retrieval complete.");
			hasSucceeded = true;
		} catch (RuntimeException ex) {
			hasSucceeded = false;
			Pair<String, String> msg = Alerts.getNestedExceptionMessage(ex);
			updateMessage("%1$s: %2$s".formatted(msg.getLeft(), msg.getRight()));
		}
		updateProgress(1, 1);
		return null;
	}

	public boolean hasSucceeded() {
		return hasSucceeded;
	}
}
