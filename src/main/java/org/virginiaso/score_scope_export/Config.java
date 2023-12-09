package org.virginiaso.score_scope_export;

import java.io.File;
import java.util.Properties;

public class Config {
	private static class ConfigHolder {
		private static final Config INSTANCE = new Config();
	}

	private static final String CONFIGURATION_RESOURCE = "configuration.properties";

	private final Properties props;
	private final String tournamentName;
	private final String tournamentDivision;
	private final KnackApp knackAppEnumerator;
	private final File downloadDir;
	private final File outputFile;
	private final String knackUserName;
	private final String knackPassword;

	/**
	 * Get the singleton instance of Config. This follows the "lazy initialization
	 * holder class" idiom for lazy initialization of a static field. See Item 83 of
	 * Effective Java, Third Edition, by Joshua Bloch for details.
	 *
	 * @return the instance
	 */
	public static Config inst() {
		return ConfigHolder.INSTANCE;
	}

	private Config() {
		props = Util.loadPropertiesFromResource(CONFIGURATION_RESOURCE);
		tournamentName = props.getProperty("tournament_name");
		tournamentDivision = props.getProperty("tournament_division");
		knackAppEnumerator = KnackApp.valueOf(props.getProperty("application_name"));
		downloadDir = Util.parseFileArgument(props, "download_dir");
		outputFile = Util.parseFileArgument(props, "output_file");
		knackUserName = props.getProperty("knack_user_name");
		knackPassword = props.getProperty("knack_password");
	}

	public String getTournamentName() {
		return tournamentName;
	}

	public String getTournamentDivision() {
		return tournamentDivision;
	}

	public KnackApp getKnackAppEnumerator() {
		return knackAppEnumerator;
	}

	public File getDownloadDir() {
		return downloadDir;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public String getKnackUserName() {
		return knackUserName;
	}

	public String getKnackPassword() {
		return knackPassword;
	}

	public String getKnackAppId() {
		return getKnackAppId(knackAppEnumerator);
	}

	public String getKnackUrlPath(KnackView knackView) {
		return getKnackUrlPath(knackAppEnumerator, knackView);
	}

	public String getKnackAppId(KnackApp knackApp) {
		return props.getProperty("application_id.%1$s".formatted(knackApp));
	}

	public String getKnackUrlPath(KnackApp knackApp, KnackView knackView) {
		var propKey = "scene_view.%1$s.%2$s".formatted(knackView, knackApp);
		var viewNums = props.getProperty(propKey, "").split(",");
		if (viewNums.length != 2) {
			throw new IllegalArgumentException(
				"Configuration item %1$s must contain '<scene#>,<view#>'".formatted(propKey));
		}
		return "scene_%1$s/views/view_%2$s".formatted(viewNums[0], viewNums[1]);
	}
}
