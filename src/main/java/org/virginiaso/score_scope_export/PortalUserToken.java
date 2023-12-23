package org.virginiaso.score_scope_export;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.stream.Collectors;

import org.virginiaso.score_scope_export.exception.KnackException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PortalUserToken {
	private static class PortalUserTokenHolder {
		private static final PortalUserToken INSTANCE = new PortalUserToken();
	}

	private static final String TOKEN_URL = "https://api.knack.com/v1/applications/%1$s/session";
	private static final String TOKEN_BODY = "{\"email\":\"%1$s\",\"password\":\"%2$s\"}";

	private String userToken;

	/**
	 * Get the singleton instance of PortalUserToken. This follows the "lazy
	 * initialization holder class" idiom for lazy initialization of a static field.
	 * See Item 83 of Effective Java, Third Edition, by Joshua Bloch for details.
	 *
	 * @return the instance
	 */
	public static PortalUserToken inst() {
		return PortalUserTokenHolder.INSTANCE;
	}

	private PortalUserToken() {
		userToken = null;
	}

	public void initialize(KnackApp knackApp, String userName, String password) {
		userToken = null;
		var url = TOKEN_URL.formatted(Config.inst().getKnackAppId(knackApp));
		var requestBody = TOKEN_BODY.formatted(userName, password);
		var httpRequest = HttpRequest.newBuilder(URI.create(url))
			.POST(BodyPublishers.ofString(requestBody))
			.header("Content-Type", Util.JSON_MEDIA_TYPE)
			.build();
		try (var httpClient = HttpClient.newHttpClient()) {
			httpClient
				.sendAsync(httpRequest, BodyHandlers.ofString())
				.thenApply(HttpResponse::body)
				.thenAccept(this::interpretResponse)
				.join();
		}
		//System.out.format("Found user token '%1$s'%n", userToken);
	}

	private void interpretResponse(String jsonResponseBody) {
		JsonObject response = JsonParser.parseString(jsonResponseBody).getAsJsonObject();
		if (response.get("session") != null) {
			userToken = response
				.get("session").getAsJsonObject()
				.get("user").getAsJsonObject()
				.get("token").getAsString();
		} else {
			System.out.format("Error -- Response Body: '%1$s'%n", jsonResponseBody);
			JsonArray errors = response.get("errors").getAsJsonArray();
			var errorMessages = Util.asStream(errors)
				.map(PortalUserToken::errorAsString)
				.collect(Collectors.joining(System.lineSeparator()));
			throw new KnackException(errorMessages);
		}
	}

	private static String errorAsString(JsonElement error) {
		if (error.isJsonPrimitive()) {
			return error.getAsString();
		} else if (error.isJsonObject()) {
			return error.getAsJsonObject().get("message").getAsString();
		} else {
			throw new IllegalStateException("Unknown JSON parser error format");
		}
	}

	public String getUserToken() {
		return userToken;
	}
}
