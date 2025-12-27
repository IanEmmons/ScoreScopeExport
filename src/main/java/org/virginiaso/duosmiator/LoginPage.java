package org.virginiaso.duosmiator;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class LoginPage extends WizardPage {
	public LoginPage(String id) {
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

		grid.add(getAppSelectionPane(), 0, ++rowIndex, 2, 1);

		var userNameLbl = new Label("_User Name:");
		userNameLbl.setMnemonicParsing(true);
		var userNameBox = new TextField();
		userNameBox.textProperty().setValue(WizardData.inst().userName.get());
		WizardData.inst().userName.bind(userNameBox.textProperty());
		userNameLbl.setLabelFor(userNameBox);
		grid.add(userNameLbl, 0, ++rowIndex);
		grid.add(userNameBox, 1, rowIndex);

		var pwLbl = new Label("_Password:");
		pwLbl.setMnemonicParsing(true);
		var pwBox = new PasswordField();
		WizardData.inst().password.bind(pwBox.textProperty());
		pwLbl.setLabelFor(pwBox);
		grid.add(pwLbl, 0, ++rowIndex);
		grid.add(pwBox, 1, rowIndex);

		Platform.runLater(() -> userNameBox.requestFocus());

		return grid;
	}

	private static ScrollPane getAppSelectionPane() {
		var appInstances = KnackAppInstance.getAppInstances();

		var appChoiceLabel = new Label("Choose the tournament scoring application:");
		var appInstPane = new TilePane();
		appInstPane.setOrientation(Orientation.VERTICAL);
		appInstPane.setTileAlignment(Pos.TOP_LEFT);
		appInstPane.setPadding(new Insets(5, 5, 5, 5));
		appInstPane.setVgap(5);
		appInstPane.setBorder(Border.stroke(null));
		appInstPane.setPrefRows(appInstances.size() + 1);
		appInstPane.setPrefColumns(1);
		appInstPane.getChildren().add(appChoiceLabel);

		var appInstGroup = new ToggleGroup();
		appInstGroup.selectedToggleProperty().addListener((_, _, newValue) -> {
			WizardData.inst().appInstance.setValue((KnackAppInstance) newValue.getUserData());
		});

		appInstances.forEach(appInstance ->
			addRadioButton(appInstance, appInstGroup, appInstPane));
		appInstGroup.selectToggle(appInstGroup.getToggles().getFirst());

		var scrollPane = new ScrollPane(appInstPane);
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

		return scrollPane;
	}

	private static void addRadioButton(KnackAppInstance appInstance, ToggleGroup group, TilePane pane) {
		var rb = new RadioButton(appInstance.name());
		rb.setUserData(appInstance);
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
		var appInstance = WizardData.inst().appInstance.getValue();
		var userName = WizardData.inst().userName.getValue();
		var password = WizardData.inst().password.getValue();
		if (appInstance == null) {
			Alerts.show("Missing input",
				"You must choose an application, either the VASO Portal or one of the ScoreScopes.");
		} else if (userName == null || userName.isBlank()) {
			Alerts.show("Missing input", "You must provide a user name (usually an email address).");
		} else if (password == null || password.isBlank()) {
			Alerts.show("Missing input", "You must provide a password.");
		} else {
			var task = new KnackTask(appInstance, userName, password);
			var progress = Alerts.newProgressAlert(this, task);
			ExportApplication.EXEC.execute(task);
			progress.showAndWait();
			if (task.hasSucceeded()) {
				super.nextPage();
			}
		}
	}
}
