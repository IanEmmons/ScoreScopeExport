package org.virginiaso.duosmiator;

import javafx.stage.Stage;

public class ExportWizard extends Wizard {
	public static final String LOGIN_PAGE_ID = "Export tournament results to Duosmium";
	public static final String TRACK_PAGE_ID = "Which tournament track?";
	public static final String INSTRUCTIONS_PAGE_ID = "How to Use the Exported File";

	public ExportWizard(Stage owner) {
		super(owner,
			new WizardPageFactory(LOGIN_PAGE_ID, id -> new LoginPage(id)),
			new WizardPageFactory(TRACK_PAGE_ID, id -> new TrackPage(id)),
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
