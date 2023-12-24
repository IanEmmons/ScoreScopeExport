package org.virginiaso.score_scope_export;

import javafx.stage.Stage;

public class ExportWizard extends Wizard {
	public static final String LOGIN_PAGE_ID = "Export tournament results to Duosmium";
	public static final String TOURNAMENT_PAGE_ID = "Which tournament?";
	public static final String INSTRUCTIONS_PAGE_ID = "How to Use the Exported File";

	public ExportWizard(Stage owner) {
		super(owner,
			new WizardPageFactory(LOGIN_PAGE_ID, id -> new LoginPage(id)),
			new WizardPageFactory(TOURNAMENT_PAGE_ID, id -> new TournamentPage(id)),
			new WizardPageFactory(INSTRUCTIONS_PAGE_ID, id -> new FinalInstructionsPage(id)));
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
