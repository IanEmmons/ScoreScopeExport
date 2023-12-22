package org.virginiaso.score_scope_export.gui;

import java.util.Objects;

import org.virginiaso.score_scope_export.KnackApp;
import org.virginiaso.score_scope_export.PortalUserToken;
import org.virginiaso.score_scope_export.TeamRankByEventRetrieverFactory;
import org.virginiaso.score_scope_export.TeamResultsRetrieverFactory;
import org.virginiaso.score_scope_export.TournamentRetrieverFactory;

import javafx.concurrent.Task;

public class KnackTask extends Task<Void> {
	private final KnackApp knackApp;
	private final String userName;
	private final String password;

	public KnackTask(KnackApp knackApp, String userName, String password) {
		this.knackApp = Objects.requireNonNull(knackApp, "knackApp");
		this.userName = Objects.requireNonNull(userName, "userName");
		this.password = Objects.requireNonNull(password, "password");

		updateTitle("Fetching data from %1$s".formatted(
			this.knackApp == KnackApp.SCORE_SCOPE
				? "ScoreScope"
				: "the VASO Portal"));
	}

	@Override
	protected Void call() throws Exception {
		updateMessage("Logging in...");
		PortalUserToken.inst().initialize(knackApp, userName, password);

		updateMessage("Fetching the list of tournaments...");
		WizardData.inst.replaceTournaments(
			TournamentRetrieverFactory.create().retrieveReport());

		updateMessage("Fetching the list of teams...");
		WizardData.inst.replaceTeamResults(
			TeamResultsRetrieverFactory.create().retrieveReport());

		updateMessage("Fetching team ranks in each event...");
		WizardData.inst.replaceRanks(
			TeamRankByEventRetrieverFactory.create().retrieveReport());

		updateMessage("Data retrieval complete.");
		updateProgress(1, 1);
		return null;
	}
}
