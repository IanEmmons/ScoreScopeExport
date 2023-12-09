package org.virginiaso.score_scope_export.gui;

import org.virginiaso.score_scope_export.KnackApp;
import org.virginiaso.score_scope_export.Tournament;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
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
	public final ListProperty<Tournament> tournaments = new SimpleListProperty<>();
	public final StringProperty selectedTournament = new SimpleStringProperty();
	public final StringProperty selectedDivision = new SimpleStringProperty();
}
