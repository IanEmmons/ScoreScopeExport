package org.virginiaso.score_scope_export;

import java.util.Properties;

public class Config {
	private static class ConfigHolder {
		private static final Config INSTANCE = new Config();
	}

	private static final String CONFIGURATION_RESOURCE = "configuration.properties";

	private final Properties props;

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
