package org.virginiaso.duosmiator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;

public class Alerts {
	private static final boolean SHOW_FULL_EXCEPTION_OUTPUT = true;

	private Alerts() {}	// prevents instantiation

	public static void show(String headerText, String message) {
		var alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(headerText);
		alert.getDialogPane().setContentText(message);
		alert.showAndWait();
	}

	/// Creates the Alert and necessary controls to observe the task. After calling,
	/// start the task running and then show the alert. Uses the following properties
	/// of the task:
	///
	/// - titleProperty
	/// - messageProperty
	/// - progressProperty
	/// - runningProperty
	///
	/// @param owner Alert owner
	/// @param task  Task to observe
	/// @return A new alert
	public static Alert newProgressAlert(Node owner, Task<?> task) {
		var alert = new Alert(Alert.AlertType.NONE);
		alert.initOwner(owner.getScene().getWindow());
		alert.titleProperty().bind(task.titleProperty());
		alert.contentTextProperty().bind(task.messageProperty());

		var progressIndicator = new ProgressIndicator();
		progressIndicator.progressProperty().bind(task.progressProperty());

		var separator = new Separator();
		separator.setOrientation(Orientation.VERTICAL);

		var hbox = new HBox();
		hbox.setSpacing(5);
		hbox.getChildren().addAll(progressIndicator, separator);

		alert.setGraphic(hbox);

		alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
		alert.getDialogPane().lookupButton(ButtonType.OK)
			.disableProperty().bind(task.runningProperty());

		alert.getDialogPane().cursorProperty().bind(
			Bindings.when(task.runningProperty())
				.then(Cursor.WAIT)
				.otherwise(Cursor.DEFAULT));

		return alert;
	}

	public static void showNestedException(Throwable ex) {
		Pair<String, String> msg = getNestedExceptionMessage(ex);
		show(msg.getLeft(), msg.getRight());
	}

	public static Pair<String, String> getNestedExceptionMessage(Throwable ex) {
		Map<Class<? extends Throwable>, String> nesting = getExceptionNesting(ex);
		if (nesting.keySet().contains(KnackException.class)) {
			return Pair.of("Unable to Download Information", nesting.get(KnackException.class));
		} else if (nesting.keySet().contains(ConnectException.class)) {
			return Pair.of("Unable to Connect", "Please check your network connection");
		} else {
			if (SHOW_FULL_EXCEPTION_OUTPUT) {
				StringWriter swtr = new StringWriter();
				ex.printStackTrace(new PrintWriter(swtr));
				return Pair.of("Unrecognized Error", swtr.toString());
			} else {
				var message = nesting.entrySet().stream()
					.map(entry -> "%1$s: %2$s".formatted(entry.getKey().getName(), entry.getValue()))
					.collect(Collectors.joining(System.lineSeparator()));
				return Pair.of("Unrecognized Error", message);
			}
		}
	}

	private static Map<Class<? extends Throwable>, String> getExceptionNesting(Throwable ex) {
		Map<Class<? extends Throwable>, String> nesting = new LinkedHashMap<>();
		for (Throwable nextEx = ex; nextEx != null; nextEx = nextEx.getCause()) {
			nesting.put(nextEx.getClass(), nextEx.getMessage());
		}
		return nesting;
	}
}
