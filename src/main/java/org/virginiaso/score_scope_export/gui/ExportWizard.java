package org.virginiaso.score_scope_export.gui;

import javafx.stage.Stage;

public class ExportWizard extends Wizard {
	public ExportWizard(Stage owner) {
		super(owner,
			new WizardPageFactory("Export tournament results to Duosmium", id -> new LoginPage(id)),
			new WizardPageFactory("Which tournament?", id -> new TournamentPage(id)),
			new WizardPageFactory("How to Use the Exported File", id -> new FinalInstructionsPage(id)));
	}

	@Override
	public void finish() {
		getOwner().close();
	}

	@Override
	public void cancel() {
		getOwner().close();
	}
}
