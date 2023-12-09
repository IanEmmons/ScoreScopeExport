package org.virginiaso.score_scope_export.gui;

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
	private final Button prevButton;
	private final Button nextButton;
	private final Button cancelButton;
	private final Button finishButton;

	public WizardPage(String title) {
		prevButton = new Button("_Previous");
		nextButton = new Button("N_ext");
		cancelButton = new Button("Cancel");
		finishButton = new Button("_Finish");

		setId(title);
		setSpacing(5);
		setPadding(new Insets(10, 10, 10, 10));

		var spring = new Region();
		VBox.setVgrow(spring, Priority.ALWAYS);
		getChildren().addAll(getContent(), spring, getButtons());

		prevButton.setOnAction(event -> priorPage());
		nextButton.setOnAction(event -> nextPage());
		cancelButton.setOnAction(event -> getWizard().cancel());
		finishButton.setOnAction(event -> getWizard().finish());
	}

	private HBox getButtons() {
		Region spring = new Region();
		HBox.setHgrow(spring, Priority.ALWAYS);
		HBox buttonBar = new HBox(5);
		cancelButton.setCancelButton(true);
		finishButton.setDefaultButton(true);
		buttonBar.getChildren().addAll(spring, prevButton, nextButton, cancelButton, finishButton);
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
			prevButton.setDisable(true);
		}

		if (!hasNextPage()) {
			nextButton.setDisable(true);
		}
	}

	protected void enableNextButton(boolean enableButton) {
		nextButton.setDisable(!enableButton);
	}

	protected void enableFinishButton(boolean enableButton) {
		finishButton.setDisable(!enableButton);
	}
}
