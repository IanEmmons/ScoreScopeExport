package org.virginiaso.score_scope_export.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class FinalInstructionsPage extends WizardPage {
	public FinalInstructionsPage(String id) {
		super(id);
	}

	@Override
	protected Parent getContent() {
		var rowIndex = -1;

		var grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		var scenetitle = new Text(getId());
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, ++rowIndex, 1, 1);

		var instructions = new TextArea();
		instructions.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
		instructions.setWrapText(true);
		instructions.setText("""
			How to submit your %1$s export to Duosmium:

			* Visit Duosmium and make a copy of their input template.

			* Open the export file in Excel.

			* Paste (values only) the orange-yellow sections of the export file
			  into the corresponding pages of the template.

			* Preview the input template, correct issues, and submit.

			""".formatted(WizardData.inst().knackApp.getValue().title()));
		grid.add(instructions, 0, ++rowIndex, 1, 1);

		return grid;
	}
}
