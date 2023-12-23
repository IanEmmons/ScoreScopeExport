package org.virginiaso.score_scope_export.gui;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.virginiaso.score_scope_export.ExportWriter;

import javafx.concurrent.Task;

public class ExportWriterTask extends Task<Void> {
	private final File outputFile;
	private boolean hasSucceeded;

	public ExportWriterTask(File outputFile) {
		this.outputFile = Objects.requireNonNull(outputFile, "outputFile");
		hasSucceeded = true;
		updateTitle("Duosmium File Export");
	}

	@Override
	protected Void call() {
		try {
			updateMessage("Exporting %1$s, Division %2$s to Duosmium...".formatted(
				WizardData.inst().selectedTournament.get(),
				WizardData.inst().selectedDivision.get()));

			var exportWriter = new ExportWriter(outputFile,
				WizardData.inst().selectedTournament.get(),
				WizardData.inst().selectedDivision.get());
			exportWriter.writeExport(WizardData.inst().tournaments,
				WizardData.inst().teamResults, WizardData.inst().teamRanksByEvent);

			updateMessage("Duosmium export complete.");
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
