package org.virginiaso.duosmiator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.DuplicateHeaderMode;

public record KnackAppInstance(
	String name,
	KnackAppType type,
	String id)
{
	private static final String FILE_NAME = "KnackApplicationInstances.csv";

	public static List<KnackAppInstance> getAppInstances() {
		List<KnackAppInstance> result = new ArrayList<>();
		var csvFormat = CSVFormat.DEFAULT.builder()
			.setCommentMarker('#')
			.setIgnoreSurroundingSpaces(true)
			.setIgnoreEmptyLines(true)
			.setAllowMissingColumnNames(false)
			.setDuplicateHeaderMode(DuplicateHeaderMode.DISALLOW)
			.setHeader()	// uses first row as header mapping
			.get();
		try (
			var strm = openInstanceList();
			var rdr = new InputStreamReader(strm, Util.CHARSET);
			var records = csvFormat.parse(rdr);
		) {
			for (var record : records) {
				var name = record.get("ApplicationName");
				var type = KnackAppType.valueOf(record.get("ApplicationType"));
				var id = record.get("ApplicationID");
				result.add(new KnackAppInstance(name, type, id));
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
		return result;
	}

	private static InputStream openInstanceList() throws FileNotFoundException {
		var cwd = new File(System.getProperty("user.dir"));
		var instanceListFile = new File(cwd, FILE_NAME);
		return instanceListFile.isFile()
			? new FileInputStream(instanceListFile)
			: Util.getResourceAsInputStream(FILE_NAME);
	}
}
