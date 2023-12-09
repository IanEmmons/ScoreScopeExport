package org.virginiaso.score_scope_export.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ExportApplication extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		var wizard = new ExportWizard(primaryStage);
		primaryStage.setScene(new Scene(wizard, 640, 480));
		primaryStage.show();
	}
}
