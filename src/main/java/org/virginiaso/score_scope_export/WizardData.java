package org.virginiaso.score_scope_export;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Holder for the data entered by the user.
 */
public class WizardData {
	private static class WizardDataHolder {
		private static final WizardData INSTANCE = new WizardData();
	}

	/**
	 * Get the singleton instance of WizardData. This follows the "lazy
	 * initialization holder class" idiom for lazy initialization of a static field.
	 * See Item 83 of Effective Java, Third Edition, by Joshua Bloch for details.
	 *
	 * @return the instance
	 */
	public static WizardData inst() {
		return WizardDataHolder.INSTANCE;
	}

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
		tournaments.sort(Comparator.comparing(Tournament::name));
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
