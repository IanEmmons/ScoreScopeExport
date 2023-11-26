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
	private final String portalAppName;
	private final File portalReportDir;
	private final File outputFile;
	private final String portalUser;
	private final String portalPassword;
	private final String portalApplicationId;

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
		tournamentName = props.getProperty("tournament.name");
		tournamentDivision = props.getProperty("tournament.division");
		portalAppName = props.getProperty("portal.application.name");
		portalReportDir = Util.parseFileArgument(props, "portal.report.dir");
		outputFile = Util.parseFileArgument(props, "output.file");
		portalUser = props.getProperty("portal.user");
		portalPassword = props.getProperty("portal.password");
		var appIdPropKey = "portal.%1$s.application.id".formatted(portalAppName);
		portalApplicationId = props.getProperty(appIdPropKey);
	}

	public String getTournamentName() {
		return tournamentName;
	}

	public String getTournamentDivision() {
		return tournamentDivision;
	}

	public String getPortalAppName() {
		return portalAppName;
	}

	public File getPortalReportDir() {
		return portalReportDir;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public String getPortalUser() {
		return portalUser;
	}

	public String getPortalPassword() {
		return portalPassword;
	}

	public String getPortalApplicationId() {
		return portalApplicationId;
	}

	public String getPortalUrlPath(String reportName) {
		var propKey = "portal.%1$s.%2$s.scene_view".formatted(portalAppName, reportName);
		var scene_view = props.getProperty(propKey, "").split(",");
		if (scene_view.length != 2) {
			throw new IllegalArgumentException("Configuration item %1$s must contain '<scene#>,<view#>'".formatted(propKey));
		}
		return "scene_%1$s/views/view_%2$s".formatted(scene_view[0], scene_view[1]);
	}
}
