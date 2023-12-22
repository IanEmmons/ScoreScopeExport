package org.virginiaso.score_scope_export.gui;

import java.util.ArrayList;
import java.util.List;

import org.virginiaso.score_scope_export.KnackApp;
import org.virginiaso.score_scope_export.TeamRankByEvent;
import org.virginiaso.score_scope_export.TeamResults;
import org.virginiaso.score_scope_export.Tournament;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Holder for the data entered by the user.
 */
public class WizardData {
	public static final WizardData inst = new WizardData();

	public final Property<KnackApp> knackApp = new SimpleObjectProperty<>();
	public final StringProperty userName = new SimpleStringProperty();
	public final StringProperty password = new SimpleStringProperty();
	public final List<Tournament> tournaments = new ArrayList<>();
	public final List<TeamResults> teamResults = new ArrayList<>();
	public final List<TeamRankByEvent> teamRanksByEvent = new ArrayList<>();
	public final StringProperty selectedTournament = new SimpleStringProperty();
	public final StringProperty selectedDivision = new SimpleStringProperty();

	public void replaceTournaments(List<Tournament> newValues) {
		tournaments.clear();
		tournaments.addAll(newValues);
	}

	public void replaceTeamResults(List<TeamResults> newValues) {
		teamResults.clear();
		teamResults.addAll(newValues);
	}

	public void replaceRanks(List<TeamRankByEvent> newValues) {
		teamRanksByEvent.clear();
		teamRanksByEvent.addAll(newValues);
	}
}
