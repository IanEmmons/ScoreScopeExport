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
	private final ObservableList<WizardPage> pages;
	private final Stack<Integer> history;
	private int curPageIdx;

	public Wizard(Stage owner, WizardPage... nodes) {
		this.owner = owner;
		pages = FXCollections.observableArrayList(nodes);
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
		return (curPageIdx < pages.size() - 1);
	}

	public boolean hasPriorPage() {
		return !history.isEmpty();
	}

	public void navTo(int nextPageIdx, boolean pushHistory) {
		if (nextPageIdx < 0 || nextPageIdx >= pages.size()) {
			return;
		}
		if (curPageIdx != UNDEFINED && pushHistory) {
			history.push(curPageIdx);
		}

		var nextPage = pages.get(nextPageIdx);
		curPageIdx = nextPageIdx;
		getChildren().clear();
		getChildren().add(nextPage);
		nextPage.manageButtons();
	}

	public void navTo(int nextPageIdx) {
		navTo(nextPageIdx, true);
	}

	public void navTo(String id) {
		if (id == null) {
			return;
		}

		pages.stream()
			.filter(page -> id.equals(page.getId()))
			.findFirst()
			.ifPresent(page -> navTo(pages.indexOf(page)));
	}

	public void finish() {
	}

	public void cancel() {
	}
}
