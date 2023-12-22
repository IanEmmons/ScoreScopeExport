package org.virginiaso.score_scope_export.gui;

import java.util.Stack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Basic wizard infrastructure class. Represents a wizard interface as a
 * collection of wizard pages.
 */
public class Wizard extends StackPane {
	private static final int UNDEFINED = -1;

	private final Stage owner;
	private final ObservableList<WizardPageFactory> pageFactories;
	private final Stack<Integer> history;
	private int curPageIdx;

	@SafeVarargs
	public Wizard(Stage owner, WizardPageFactory... pageFactories) {
		this.owner = owner;
		this.pageFactories = FXCollections.observableArrayList(pageFactories);
		history = new Stack<>();
		curPageIdx = UNDEFINED;

		navTo(0);
	}

	public Stage getOwner() {
		return owner;
	}

	public void nextPage() {
		if (hasNextPage()) {
			navTo(curPageIdx + 1);
		}
	}

	public void priorPage() {
		if (hasPriorPage()) {
			navTo(history.pop(), false);
		}
	}

	public boolean hasNextPage() {
		return (curPageIdx < pageFactories.size() - 1);
	}

	public boolean hasPriorPage() {
		return !history.isEmpty();
	}

	public void navTo(int nextPageIdx, boolean pushHistory) {
		if (nextPageIdx < 0 || nextPageIdx >= pageFactories.size()) {
			return;
		}
		if (curPageIdx != UNDEFINED && pushHistory) {
			history.push(curPageIdx);
		}

		var nextPage = pageFactories.get(nextPageIdx).page();
		curPageIdx = nextPageIdx;
		getChildren().clear();
		getChildren().add(nextPage);
		nextPage.manageButtons();
	}

	public void navTo(int nextPageIdx) {
		navTo(nextPageIdx, true);
	}

	public void navTo(String id) {
		if (id == null || id.isBlank()) {
			return;
		}

		for (int i = 0; i < pageFactories.size(); ++i) {
			if (id.equals(pageFactories.get(i).pageId())) {
				navTo(i);
				break;
			}
		}
	}

	public void finish() {
	}

	public void cancel() {
	}
}
