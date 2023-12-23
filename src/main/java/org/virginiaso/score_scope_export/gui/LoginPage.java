package org.virginiaso.score_scope_export.gui;

import org.virginiaso.score_scope_export.KnackApp;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
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
		WizardData.inst.userName.bind(userNameBox.textProperty());
		userNameLbl.setLabelFor(userNameLbl);
		grid.add(userNameLbl, 0, ++rowIndex);
		grid.add(userNameBox, 1, rowIndex);

		var pwLbl = new Label("_Password:");
		pwLbl.setMnemonicParsing(true);
		var pwBox = new PasswordField();
		WizardData.inst.password.bind(pwBox.textProperty());
		pwLbl.setLabelFor(pwBox);
		grid.add(pwLbl, 0, ++rowIndex);
		grid.add(pwBox, 1, rowIndex);

		return grid;
	}

	private static TilePane getAppSelectionPane() {
		var appChoiceLabel = new Label("Choose the tournament scoring application:");

		var rb1 = new RadioButton("_ScoreScope");
		rb1.setMnemonicParsing(true);
		rb1.setUserData(KnackApp.SCORE_SCOPE);

		var rb2 = new RadioButton("_VASO Division B/C Portal");
		rb2.setUserData(KnackApp.VASO_PORTAL);
		rb2.setMnemonicParsing(true);

		var knackAppGroup = new ToggleGroup();
		rb1.setToggleGroup(knackAppGroup);
		rb2.setToggleGroup(knackAppGroup);
		knackAppGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			WizardData.inst.knackApp.setValue((KnackApp) newValue.getUserData());
		});

		var knackAppPane = new TilePane();
		knackAppPane.setOrientation(Orientation.VERTICAL);
		knackAppPane.setTileAlignment(Pos.TOP_LEFT);
		knackAppPane.setPadding(new Insets(5, 5, 5, 5));
		knackAppPane.setVgap(5);
		knackAppPane.setBorder(Border.stroke(null));
		knackAppPane.setPrefRows(3);
		knackAppPane.setPrefColumns(1);
		knackAppPane.getChildren().add(appChoiceLabel);
		knackAppPane.getChildren().add(rb1);
		knackAppPane.getChildren().add(rb2);

		return knackAppPane;
	}

	@Override
	public void manageButtons() {
		super.manageButtons();
		enableFinishButton(false);
	}

	@Override
	public void nextPage() {
		var knackApp = WizardData.inst.knackApp.getValue();
		var userName = WizardData.inst.userName.getValue();
		var password = WizardData.inst.password.getValue();
		if (knackApp == null) {
			Alerts.show("Missing input",
				"You must choose an application, either ScoreScope or the VASO Portal.");
		} else if (userName == null || userName.isBlank()) {
			Alerts.show("Missing input", "You must provide a user name (usually an email address).");
		} else if (password == null || password.isBlank()) {
			Alerts.show("Missing input", "You must provide a password.");
		} else {
			var task = new KnackTask(knackApp, userName, password);
			var progress = Alerts.newProgressAlert(this, task);
			ExportApplication.EXEC.execute(task);
			progress.showAndWait();
			if (task.hasSucceeded()) {
				super.nextPage();
			}
		}
	}
}
