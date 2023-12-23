package org.virginiaso.score_scope_export;

import java.io.IOException;

import org.virginiaso.score_scope_export.exception.CmdLineException;

public class App {
	public static void main(String[] args) {
		try {
			App app = new App(args);
			app.run();
		} catch (CmdLineException ex) {
			if (ex.getMessage() != null && !ex.getMessage().isBlank()) {
				System.out.format("%n%1$s%n%n", ex.getMessage());
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	/** @throws CmdLineException If the syntax of the command line is incorrect. */
	private App(String[] args) throws CmdLineException {
	}

	@SuppressWarnings("static-method")
	private void run() throws IOException {
		PortalUserToken.inst().initialize(Config.inst().getKnackAppEnumerator(),
			Config.inst().getKnackUserName(), Config.inst().getKnackPassword());
		var exportWriter = new ExportWriter(Config.inst().getOutputFile(),
			Config.inst().getTournamentName(), Config.inst().getTournamentDivision());
		exportWriter.writeExport(TournamentRetrieverFactory.create().readLatestReportFile(),
			TeamResultsRetrieverFactory.create().readLatestReportFile(),
			TeamRankByEventRetrieverFactory.create().readLatestReportFile());
	}
}
