package org.virginiaso.score_scope_export.gui;

import javafx.stage.Stage;

public class ExportWizard extends Wizard {
	public ExportWizard(Stage owner) {
		super(owner,
			new WizardPageFactory("Export tournament results to Duosmium", id -> new LoginPage(id)),
			new WizardPageFactory("Which tournament?", id -> new TournamentPage(id)));
	}

	@Override
	public void finish() {
		System.out.println("Finished");
		getOwner().close();
	}

	@Override
	public void cancel() {
		System.out.println("Cancelled");
		getOwner().close();
	}
}
