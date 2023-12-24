package org.virginiaso.score_scope_export;

import org.apache.commons.lang3.tuple.Pair;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class TournamentPage extends WizardPage {
	public TournamentPage(String id) {
		super(id);
	}

	@Override
	protected Parent getContent() {
		var divisions = WizardData.inst().teamResults.stream()
			.map(TeamResults::division)
			.distinct()
			.sorted()
			.toList();

		var tourneys = WizardData.inst().tournaments.stream()
			.map(Tournament::name)
			.toList();

		var rowIndex = -1;

		var grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		var scenetitle = new Text(getId());
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, ++rowIndex, 2, 1);

		var tourneyChoiceLabel = new Label("Choose the tournament to export:");
		var tourneyChoicePane = new TilePane();
		tourneyChoicePane.setOrientation(Orientation.VERTICAL);
		tourneyChoicePane.setTileAlignment(Pos.TOP_LEFT);
		tourneyChoicePane.setPadding(new Insets(5, 5, 5, 5));
		tourneyChoicePane.setVgap(5);
		tourneyChoicePane.setBorder(Border.stroke(null));
		tourneyChoicePane.setPrefRows(3);
		tourneyChoicePane.setPrefColumns(1);
		tourneyChoicePane.getChildren().add(tourneyChoiceLabel);

		var toggleGroup = new ToggleGroup();
		toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			@SuppressWarnings("unchecked")
			var userData = (Pair<String, String>) newValue.getUserData();
			WizardData.inst().selectedTournament.setValue(userData.getLeft());
			WizardData.inst().selectedDivision.setValue(userData.getRight());
		});

		tourneys.forEach(
			tourney -> divisions.forEach(
				division -> addRadioButton(tourney, division, toggleGroup, tourneyChoicePane)));
		toggleGroup.selectToggle(toggleGroup.getToggles().getFirst());

		grid.add(tourneyChoicePane, 0, ++rowIndex, 2, 1);

		return grid;
	}

	private static void addRadioButton(String name, String division, ToggleGroup group, TilePane pane) {
		var itemLabel = "%1$s, Division %2$s".formatted(name, division);
		var rb = new RadioButton(itemLabel);
		rb.setUserData(Pair.of(name, division));
		rb.setToggleGroup(group);
		pane.getChildren().add(rb);
	}

	@Override
	public void manageButtons() {
		super.manageButtons();
		enableFinishButton(false);
	}

	@Override
	public void nextPage() {
		var selectedTournament = WizardData.inst().selectedTournament.get();
		var selectedDivision = WizardData.inst().selectedDivision.get();
		if (selectedTournament == null || selectedDivision == null) {
			Alerts.show("Missing input", "You must choose a tournament from the list.");
		} else {
			var fileChooser = new FileChooser();
			fileChooser.setTitle("Choose Duosmium export file");
			fileChooser.setInitialFileName("%1$s-%2$s.xlsx".formatted(
				WizardData.inst().selectedTournament.get(),
				WizardData.inst().selectedDivision.get()));
			fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Excel Workbook (.xlsx)", "*.xlsx"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));
			var outputFile = fileChooser.showSaveDialog(this.getScene().getWindow());

			var task = new ExportWriterTask(outputFile);
			var progress = Alerts.newProgressAlert(this, task);
			ExportApplication.EXEC.execute(task);
			progress.showAndWait();
			if (task.hasSucceeded()) {
				super.nextPage();
			}
		}
	}
}
