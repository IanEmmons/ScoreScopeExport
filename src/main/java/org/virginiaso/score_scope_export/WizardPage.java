package org.virginiaso.score_scope_export;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Represents on page in a wizard interface.
 */
public abstract class WizardPage extends VBox {
	private final Button cancelButton;
	private final Button prevButton;
	private final Button nextButton;
	private final Button finishButton;

	public WizardPage(String id) {
		cancelButton = new Button("Cancel");
		prevButton = new Button("_Previous");
		nextButton = new Button("_Next");
		finishButton = new Button("_Finish");

		setId(id);
		setSpacing(5);
		setPadding(new Insets(10, 10, 10, 10));

		var spring = new Region();
		VBox.setVgrow(spring, Priority.ALWAYS);
		getChildren().addAll(getContent(), spring, getButtons());

		cancelButton.setMnemonicParsing(true);
		prevButton.setMnemonicParsing(true);
		nextButton.setMnemonicParsing(true);
		finishButton.setMnemonicParsing(true);

		cancelButton.setOnAction(event -> getWizard().cancel());
		prevButton.setOnAction(event -> priorPage());
		nextButton.setOnAction(event -> nextPage());
		finishButton.setOnAction(event -> getWizard().finish());
	}

	private HBox getButtons() {
		Region spring = new Region();
		HBox.setHgrow(spring, Priority.ALWAYS);
		HBox buttonBar = new HBox(5);
		cancelButton.setCancelButton(true);
		finishButton.setDefaultButton(true);
		buttonBar.getChildren().addAll(spring, cancelButton, prevButton, nextButton, finishButton);
		return buttonBar;
	}

	protected abstract Parent getContent();

	public boolean hasNextPage() {
		return getWizard().hasNextPage();
	}

	public boolean hasPriorPage() {
		return getWizard().hasPriorPage();
	}

	public void nextPage() {
		getWizard().nextPage();
	}

	public void priorPage() {
		getWizard().priorPage();
	}

	public void navTo(String id) {
		getWizard().navTo(id);
	}

	public Wizard getWizard() {
		return (Wizard) getParent();
	}

	public void manageButtons() {
		if (!hasPriorPage()) {
			enablePrevButton(false);
		}

		if (!hasNextPage()) {
			enableNextButton(false);
		}
	}

	protected void enableCancelButton(boolean enableButton) {
		cancelButton.setDisable(!enableButton);
	}

	protected void enablePrevButton(boolean enableButton) {
		prevButton.setDisable(!enableButton);
	}

	protected void enableNextButton(boolean enableButton) {
		nextButton.setDisable(!enableButton);
	}

	protected void enableFinishButton(boolean enableButton) {
		finishButton.setDisable(!enableButton);
	}
}
