package org.virginiaso.score_scope_export;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonElement;

public class Util {
	public static final Charset CHARSET = StandardCharsets.UTF_8;
	public static final String JSON_MEDIA_TYPE = "application/json";
	private static final Pattern WHITESPACE = Pattern.compile("\\s\\s+");

	private Util() {}	// prevent instantiation

	public static String normalizeSpace(String str) {
		return (str == null)
			? null
			: WHITESPACE.matcher(str.strip()).replaceAll(" ");
	}

	public static String applyTranslations(String input, List<Pair<Pattern, String>> translations) {
		var translated = input;
		for (var translation : translations) {
			var pattern = translation.getLeft();
			var replacement = translation.getRight();
			translated = pattern.matcher(translated).replaceAll(replacement);
		}
		return translated;
	}

	public static BigDecimal getAsBigDecimal(JsonElement element) {
		if (element == null) {
			return BigDecimal.ZERO;
		} else if (!element.isJsonPrimitive()) {
			throw new IllegalArgumentException(
				"Expecting number or blank, but found non-primitive value");
		} else {
			var primitive = element.getAsJsonPrimitive();
			return primitive.isNumber()
				? primitive.getAsBigDecimal()
				: BigDecimal.ZERO;
		}
	}

	public static boolean getAsBoolean(JsonElement element) {
		if (element == null) {
			return false;
		} else if (!element.isJsonPrimitive()) {
			throw new IllegalArgumentException(
				"Expecting number or boolean, but found non-primitive value");
		} else {
			var primitive = element.getAsJsonPrimitive();
			if (primitive.isNumber()) {
				return (primitive.getAsInt() != 0);
			} else if (primitive.isBoolean()) {
				return primitive.getAsBoolean();
			} else {
				throw new IllegalArgumentException(
					"Expecting number or boolean, but found '%1$s'"
						.formatted(primitive.getAsString()));
			}
		}
	}

	public static InputStream getResourceAsInputStream(String resourceName) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream result = cl.getResourceAsStream(resourceName);
		if (result == null) {
			throw new MissingResourceException(null, null, resourceName);
		}
		return result;
	}

	public static <T> Stream<T> asStream(Iterable<T> it) {
		return StreamSupport.stream(it.spliterator(), false);
	}

	public static <T> Stream<T> asStream(Iterator<T> it) {
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(it, Spliterator.IMMUTABLE), false);
	}
}
