package org.virginiaso.score_scope_export.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.virginiaso.score_scope_export.exception.KnackException;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;

public class Alerts {
	private static final boolean SHOW_FULL_EXCEPTION_OUTPUT = true;

	private Alerts() {}	// prevents instantiation

	public static void showNestedException(Throwable ex) {
		Map<Class<? extends Throwable>, String> nesting = getExceptionNesting(ex);
		if (nesting.keySet().contains(KnackException.class)) {
			show("Unable to Download Information", nesting.get(KnackException.class));
		} else if (nesting.keySet().contains(ConnectException.class)) {
			show("Unable to Connect", "Please check your network connection");
		} else {
			if (SHOW_FULL_EXCEPTION_OUTPUT) {
				StringWriter swtr = new StringWriter();
				ex.printStackTrace(new PrintWriter(swtr));
				show("Unrecognized Exception", swtr.toString());
			} else {
				var message = nesting.entrySet().stream()
					.map(entry -> "%1$s: %2$s".formatted(entry.getKey().getName(), entry.getValue()))
					.collect(Collectors.joining(System.lineSeparator()));
				show("Unrecognized Exception", message);
			}
		}
	}

	private static Map<Class<? extends Throwable>, String> getExceptionNesting(Throwable ex) {
		Map<Class<? extends Throwable>, String> nesting = new LinkedHashMap<>();
		for (Throwable nextEx = ex; nextEx != null; nextEx = ex.getCause()) {
			nesting.put(ex.getClass(), ex.getMessage());
		}
		return nesting;
	}

	public static void show(String headerText, String message) {
		var alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(headerText);
		alert.getDialogPane().setContentText(message);
		alert.showAndWait();
	}

	/**
	 * Creates the Alert and necessary controls to observe the task. After calling,
	 * start the task running and then show the alert. Uses the following properties
	 * of the task:
	 * <ul>
	 * <li>titleProperty</li>
	 * <li>messageProperty</li>
	 * <li>progressProperty</li>
	 * <li>runningProperty</li>
	 * </ul>
	 *
	 * @param owner Alert owner
	 * @param task  Task to observe
	 * @return A new alert
	 */
	public static Alert newProgressAlert(Task<?> task) {
		var alert = new Alert(Alert.AlertType.NONE);
		alert.titleProperty().bind(task.titleProperty());
		alert.contentTextProperty().bind(task.messageProperty());

		var pIndicator = new ProgressIndicator();
		pIndicator.progressProperty().bind(task.progressProperty());
		alert.setGraphic(pIndicator);

		alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
		alert.getDialogPane().lookupButton(ButtonType.OK)
			.disableProperty().bind(task.runningProperty());

		alert.getDialogPane().cursorProperty().bind(
			Bindings.when(task.runningProperty())
				.then(Cursor.WAIT)
				.otherwise(Cursor.DEFAULT));

		return alert;
	}
}
