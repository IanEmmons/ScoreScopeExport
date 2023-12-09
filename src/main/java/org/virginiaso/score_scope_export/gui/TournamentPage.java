package org.virginiaso.score_scope_export.gui;

import org.apache.commons.lang3.tuple.Pair;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.TilePane;

public class TournamentPage extends WizardPage {
	public TournamentPage() {
		super("Choose the tournament to export:");
	}

	@Override
	protected Parent getContent() {
		var tourneyChoiceLabel = new Label(getId());

		var pane = new TilePane();
		pane.setOrientation(Orientation.VERTICAL);
		pane.setTileAlignment(Pos.TOP_LEFT);
		pane.setPadding(new Insets(5, 5, 5, 5));
		pane.setVgap(5);
		pane.setBorder(Border.stroke(null));
		pane.setPrefRows(3);
		pane.setPrefColumns(1);
		pane.getChildren().add(tourneyChoiceLabel);

		var toggleGroup = new ToggleGroup();

		WizardData.inst.tournaments.forEach(tourney -> {
			if (tourney.numBTrophies() > 0) {
				addRadioButton(tourney.name(), "B", toggleGroup, pane);
			}
			if (tourney.numCTrophies() > 0) {
				addRadioButton(tourney.name(), "C", toggleGroup, pane);
			}
		});

		toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			@SuppressWarnings("unchecked")
			var userData = (Pair<String, String>) newValue.getUserData();
			WizardData.inst.selectedTournament.setValue(userData.getLeft());
			WizardData.inst.selectedDivision.setValue(userData.getRight());
		});

		return pane;
	}

	private static void addRadioButton(String name, String division, ToggleGroup toggleGroup, TilePane pane) {
		var itemLabel = "%1$s, Division %2$s".formatted(name, division);
		var rb = new RadioButton(itemLabel);
		rb.setUserData(Pair.of(name, division));
		rb.setToggleGroup(toggleGroup);
		pane.getChildren().add(rb);
	}
}
