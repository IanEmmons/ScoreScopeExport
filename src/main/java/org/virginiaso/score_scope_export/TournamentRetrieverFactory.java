package org.virginiaso.score_scope_export;

import java.lang.reflect.Type;
import java.time.Instant;

import org.virginiaso.score_scope_export.PortalRetriever.ReportResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class TournamentRetrieverFactory {
	private static class TournamentSerializer implements JsonSerializer<Tournament>,
	JsonDeserializer<Tournament> {
		@Override
		public JsonElement serialize(Tournament src, Type typeOfSrc,
			JsonSerializationContext context) {
			JsonObject date = new JsonObject();
			date.add("iso_timestamp", new JsonPrimitive(src.date().toString()));

			JsonObject result = new JsonObject();
			result.add("id", new JsonPrimitive(src.id()));
			result.add("field_6", new JsonPrimitive(src.name()));
			result.add("field_7", date);
			result.add("field_2020", new JsonPrimitive(
				src.oneTrophyPerSchool() ? "School" : "Team"));
			result.add("field_2021", new JsonPrimitive(src.numBMedalsPerEvent()));
			result.add("field_2022", new JsonPrimitive(src.numCMedalsPerEvent()));
			result.add("field_2023", new JsonPrimitive(src.numBTrophies()));
			result.add("field_2024", new JsonPrimitive(src.numCTrophies()));
			return result;
		}

		@Override
		public Tournament deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) {
			var jObj = json.getAsJsonObject();

			var id = Util.normalizeSpace(jObj.get("id").getAsString());
			var name = Util.normalizeSpace(jObj.get("field_6").getAsString());
			var dateTime = Instant.parse(jObj.get("field_7").getAsJsonObject()
				.get("iso_timestamp").getAsString());
			var oneTrophyPer = Util.normalizeSpace(jObj.get("field_2020").getAsString());
			var numBMedalsPerEvent = jObj.get("field_2021").getAsInt();
			var numCMedalsPerEvent = jObj.get("field_2022").getAsInt();
			var numBTrophies = jObj.get("field_2023").getAsInt();
			var numCTrophies = jObj.get("field_2024").getAsInt();
			return new Tournament(
				id,
				name,
				dateTime,
				"School".equals(oneTrophyPer),
				numBMedalsPerEvent,
				numCMedalsPerEvent,
				numBTrophies,
				numCTrophies);
		}
	}

	private TournamentRetrieverFactory() {}	// prevent instantiation

	public static PortalRetriever<Tournament> create() {
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(Tournament.class, new TournamentSerializer())
			.create();
		return new PortalRetriever<>(gson, KnackView.TOURNAMENTS,
			new TypeToken<ReportResponse<Tournament>>(){}.getType());
	}
}
