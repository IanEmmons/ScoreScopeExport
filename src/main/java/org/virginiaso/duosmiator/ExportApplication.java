package org.virginiaso.duosmiator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ExportApplication extends Application {
	private static final ThreadFactory THREAD_FACTORY = Executors.defaultThreadFactory();
	public static final ExecutorService EXEC = Executors.newCachedThreadPool(
		runnable -> {
			var thread = THREAD_FACTORY.newThread(runnable);
			thread.setDaemon(true);
			return thread;
		});

	public static void main(String[] args) {
		var foundUserNameIntroducer = false;
		for (var arg : args) {
			if (foundUserNameIntroducer) {
				WizardData.inst().userName.setValue(arg);
				foundUserNameIntroducer = false;
			} else if ("--username".equalsIgnoreCase(arg)) {
				foundUserNameIntroducer = true;
			}
		}
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
