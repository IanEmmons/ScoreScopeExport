package org.virginiaso.duosmiator;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class TrackPage extends WizardPage {
	public TrackPage(String id) {
		super(id);
	}

	@Override
	protected Parent getContent() {
		var rowIndex = -1;

		var grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		var scenetitle = new Text(getId());
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, ++rowIndex, 2, 1);

		var trackChoiceLabel = new Label("Choose the tournament track to export:");
		var trackChoicePane = new TilePane();
		trackChoicePane.setOrientation(Orientation.VERTICAL);
		trackChoicePane.setTileAlignment(Pos.TOP_LEFT);
		trackChoicePane.setPadding(new Insets(5, 5, 5, 5));
		trackChoicePane.setVgap(5);
		trackChoicePane.setBorder(Border.stroke(null));
		trackChoicePane.setPrefRows(WizardData.inst().tracks.size() + 1);
		trackChoicePane.setPrefColumns(1);
		trackChoicePane.getChildren().add(trackChoiceLabel);

		var toggleGroup = new ToggleGroup();
		toggleGroup.selectedToggleProperty().addListener((_, _, newValue) -> {
			var userData = (String) newValue.getUserData();
			WizardData.inst().selectedTrackId.setValue(userData);
		});

		WizardData.inst().tracks.forEach(track ->
			addRadioButton(track, toggleGroup, trackChoicePane));
		toggleGroup.selectToggle(toggleGroup.getToggles().getFirst());

		var scrollPane = new ScrollPane(trackChoicePane);
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

		grid.add(scrollPane, 0, ++rowIndex, 2, 1);

		return grid;
	}

	private static void addRadioButton(Track track, ToggleGroup group, TilePane pane) {
		var itemLabel = "%1$s (Division %2$s)".formatted(track.name(), track.division());
		var rb = new RadioButton(itemLabel);
		rb.setUserData(track.trackId());
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
		var selectedTrackId = WizardData.inst().selectedTrackId.get();
		var selectedTrack = WizardData.inst().getTrackById(selectedTrackId);
		if (selectedTrack.isEmpty()) {
			Alerts.show("Missing input", "You must choose a tournament track from the list.");
		} else {
			var fileChooser = new FileChooser();
			fileChooser.setTitle("Choose Duosmium export file");
			fileChooser.setInitialFileName("%1$s-%2$s.xlsx".formatted(
				selectedTrack.get().name(),
				selectedTrack.get().division()));
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
