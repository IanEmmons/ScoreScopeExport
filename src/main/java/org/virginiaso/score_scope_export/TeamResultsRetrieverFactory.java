package org.virginiaso.score_scope_export;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import org.virginiaso.score_scope_export.PortalRetriever.ReportResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class TeamResultsRetrieverFactory {
	private static class TeamResultsSerializer implements JsonSerializer<TeamResults>,
			JsonDeserializer<TeamResults> {
		@Override
		public JsonElement serialize(TeamResults src, Type typeOfSrc, JsonSerializationContext context) {
			var tournament = new JsonObject();
			tournament.add("id", new JsonPrimitive(src.tournamentId()));
			tournament.add("identifier", new JsonPrimitive(src.tournamentName()));

			var tournamentArray = new JsonArray();
			tournamentArray.add(tournament);

			var result = new JsonObject();
			result.add("id", new JsonPrimitive(src.teamId()));
			result.add("field_1857", tournamentArray);
			result.add("field_1143.field_12", new JsonPrimitive(src.division()));
			result.add("field_12", new JsonPrimitive(src.division()));
			result.add("field_1478", new JsonPrimitive(src.teamNum()));
			result.add("field_1202", new JsonPrimitive(src.teamName()));
			result.add("field_1979", new JsonPrimitive(src.isExhibitionTeam()));
			result.add("field_1754", new JsonPrimitive(src.scoreNoPenalty()));
			result.add("field_1947", new JsonPrimitive(src.penalty()));
			result.add("field_1948", new JsonPrimitive(src.finalScore()));
			return result;
		}

		@Override
		public TeamResults deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			var jObj = json.getAsJsonObject();

			var tournamentId = Util.normalizeSpace(jObj.get("field_1857")
				.getAsJsonArray().get(0).getAsJsonObject()
				.get("id").getAsString());
			var tournamentName = Util.normalizeSpace(jObj.get("field_1857")
				.getAsJsonArray().get(0).getAsJsonObject()
				.get("identifier").getAsString());
			var division = Util.normalizeSpace(jObj.get("field_12").getAsString());
			String teamId = Util.normalizeSpace(jObj.get("id").getAsString());
			String teamNum = Util.normalizeSpace(jObj.get("field_1478").getAsString());
			String teamName = Util.normalizeSpace(jObj.get("field_1202").getAsString());
			boolean isExhibitionTeam = jObj.get("field_1979").getAsBoolean();
			BigDecimal scoreNoPenalty = Util.getAsBigDecimal(jObj.get("field_1754"));
			BigDecimal penalty = Util.getAsBigDecimal(jObj.get("field_1947"));
			BigDecimal finalScore = Util.getAsBigDecimal(jObj.get("field_1948"));
			return new TeamResults(tournamentId, tournamentName, division, teamId, teamNum,
				teamName, isExhibitionTeam, scoreNoPenalty, penalty, finalScore);
		}
	}

	private TeamResultsRetrieverFactory() {}	// prevent instantiation

	public static PortalRetriever<TeamResults> create() {
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(TeamResults.class, new TeamResultsSerializer())
			.create();
		return new PortalRetriever<>(gson, "team_results",
			new TypeToken<ReportResponse<TeamResults>>(){}.getType());
	}
}
