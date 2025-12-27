package org.virginiaso.duosmiator;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.EnumMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.DuplicateHeaderMode;

public class Config {
	private static class ConfigHolder {
		private static final Config INSTANCE = new Config();
	}

	private static final String CONFIGURATION_RESOURCE = "configuration.csv";

	private final EnumMap<KnackAppType, EnumMap<ConfigItem, String>> configValues;

	/// Get the singleton instance of Config. This follows the "lazy initialization
	/// holder class" idiom for lazy initialization of a static field. See Item 83 of
	/// Effective Java, Third Edition, by Joshua Bloch for details.
	///
	/// @return the instance
	public static Config inst() {
		return ConfigHolder.INSTANCE;
	}

	private Config() {
		configValues = new EnumMap<>(KnackAppType.class);
		Arrays.stream(KnackAppType.values())
			.forEach(app -> configValues.put(app, new EnumMap<>(ConfigItem.class)));
		try {
			var csvFormat = CSVFormat.DEFAULT.builder()
				.setCommentMarker('#')
				.setIgnoreSurroundingSpaces(true)
				.setIgnoreEmptyLines(true)
				.setAllowMissingColumnNames(false)
				.setDuplicateHeaderMode(DuplicateHeaderMode.DISALLOW)
				.setHeader()	// uses first row as header mapping
				.get();
			try (
				var strm = Util.getResourceAsInputStream(CONFIGURATION_RESOURCE);
				var rdr = new InputStreamReader(strm, Util.CHARSET);
				var records = csvFormat.parse(rdr);
			) {
				for (var record : records) {
					var paramName = record.get("PARAMETER");
					var portalValue = record.get(KnackAppType.VASO_PORTAL.toString());
					var ssValue = record.get(KnackAppType.SCORE_SCOPE.toString());
					var configItem = ConfigItem.valueOf(paramName);
					configValues.get(KnackAppType.VASO_PORTAL).put(configItem, portalValue);
					configValues.get(KnackAppType.SCORE_SCOPE).put(configItem, ssValue);
				}
			} catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		} catch (RuntimeException ex) {
			ex.printStackTrace();
		}
	}

	public String get(KnackAppType appType, ConfigItem configItem) {
		return configValues.get(appType).get(configItem);
	}

	public String get(KnackAppInstance appInstance, ConfigItem configItem) {
		return get(appInstance.type(), configItem);
	}
}
