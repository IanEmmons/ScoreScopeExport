package org.virginiaso.score_scope_export.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ExportApplication extends Application {
	public static final ExecutorService exec = Executors.newCachedThreadPool();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Export Tournament Results to Duosmium");
		var wizard = new ExportWizard(primaryStage);
		primaryStage.setScene(new Scene(wizard, 640, 480));
		primaryStage.show();
	}
}
