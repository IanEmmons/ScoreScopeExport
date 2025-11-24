package org.virginiaso.score_scope_export;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;

import javafx.concurrent.Task;

public class ExportWriterTask extends Task<Void> {
	private final File outputFile;
	private boolean hasSucceeded;

	public ExportWriterTask(File outputFile) {
		this.outputFile = Objects.requireNonNull(outputFile, "outputFile");
		hasSucceeded = false;
		updateTitle("Duosmium File Export");
	}

	@Override
	protected Void call() {
		try {
			var selectedTrackId = WizardData.inst().selectedTrackId.get();
			var selectedTrack = WizardData.inst().getTrackById(selectedTrackId)
				.orElseThrow(() -> new IllegalStateException(
					"No tournaments with ID " + selectedTrackId));

			updateMessage("Exporting %1$s (Division %2$s) to Duosmium...".formatted(
				selectedTrack.name(),
				selectedTrack.division()));

			var exportWriter = new ExportWriter(outputFile, selectedTrack);
			exportWriter.writeExport(WizardData.inst().teamResults,
				WizardData.inst().ranksByEvent);

			updateMessage("Duosmium export complete.");
			hasSucceeded = true;
		} catch (IOException | RuntimeException ex) {
			hasSucceeded = false;
			Pair<String, String> msg = Alerts.getNestedExceptionMessage(ex);
			updateMessage("%1$s: %2$s".formatted(msg.getLeft(), msg.getRight()));
		}
		updateProgress(1, 1);
		return null;
	}

	public boolean hasSucceeded() {
		return hasSucceeded;
	}
}
