package org.virginiaso.score_scope_export.gui;

import org.virginiaso.score_scope_export.KnackApp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GuiApp extends Application {
	private KnackApp knackApp;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Export to Duosmium");

		var rowIndex = -1;

		var grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		var scenetitle = new Text("Export tournament results to Duosmium");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, ++rowIndex, 2, 1);

		grid.add(getAppSelectionPane(), 0, ++rowIndex, 2, 1);

		Label userName = new Label("User Name:");
		grid.add(userName, 0, ++rowIndex);

		var userTextField = new TextField();
		grid.add(userTextField, 1, rowIndex);

		var pw = new Label("Password:");
		grid.add(pw, 0, ++rowIndex);

		var pwBox = new PasswordField();
		grid.add(pwBox, 1, rowIndex);

		var btn = new Button();
		btn.setText("Sign In");
		btn.setOnAction(event -> System.out.println("Hello World!"));
		var btnHBox = new HBox(10);
		btnHBox.setAlignment(Pos.BOTTOM_RIGHT);
		btnHBox.getChildren().add(btn);
		grid.add(btnHBox, 0, ++rowIndex, 2, 1);

		primaryStage.setScene(new Scene(grid, 640, 480));
		primaryStage.show();
	}

	private TilePane getAppSelectionPane() {
		var appChoiceLabel = new Label("Choose the tournament scoring application:");

		var rb1 = new RadioButton("ScoreScope");
		rb1.setUserData(KnackApp.SCORE_SCOPE);
		//rb1.setSelected(true);

		var rb2 = new RadioButton("VASO Division B/C Portal");
		rb2.setUserData(KnackApp.VASO_PORTAL);

		var knackAppGroup = new ToggleGroup();
		rb1.setToggleGroup(knackAppGroup);
		rb2.setToggleGroup(knackAppGroup);
		knackAppGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			knackApp = (KnackApp) newValue.getUserData();
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
}
