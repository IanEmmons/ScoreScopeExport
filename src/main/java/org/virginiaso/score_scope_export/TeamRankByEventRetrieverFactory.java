package org.virginiaso.score_scope_export;

import java.lang.reflect.Type;

import org.virginiaso.score_scope_export.PortalRetriever.ReportResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class TeamRankByEventRetrieverFactory {
	private static class TeamRankByEventSerializer implements JsonSerializer<TeamRankByEvent>,
			JsonDeserializer<TeamRankByEvent> {
		@Override
		public JsonElement serialize(TeamRankByEvent src, Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject tournament = new JsonObject();
			tournament.add("id", new JsonPrimitive(src.tournamentId()));
			tournament.add("identifier", new JsonPrimitive(src.tournamentName()));

			JsonArray tournamentArray = new JsonArray();
			tournamentArray.add(tournament);

			JsonObject division = new JsonObject();
			division.add("id", new JsonPrimitive(src.divisionId()));
			division.add("identifier", new JsonPrimitive(src.division()));

			JsonArray divisionArray = new JsonArray();
			divisionArray.add(division);

			JsonObject result = new JsonObject();
			result.add("id", new JsonPrimitive(src.rawScoreId()));
			result.add("field_1717.field_1699", tournamentArray);
			result.add("field_1699", tournamentArray);
			result.add("field_1717.field_1702", divisionArray);
			result.add("field_1702", divisionArray);
			result.add("field_1717.field_1753", new JsonPrimitive(src.event()));
			result.add("field_1753", new JsonPrimitive(src.event()));
			result.add("field_1718", new JsonPrimitive(src.isTrialEvent()));
			result.add("field_1721.field_1478", new JsonPrimitive(src.teamNum()));
			result.add("field_1478", new JsonPrimitive(src.teamNum()));
			result.add("field_1981", new JsonPrimitive(src.rank()));
			return result;
		}

		@Override
		public TeamRankByEvent deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) {
			var jObj = json.getAsJsonObject();

			var rawScoreId = Util.normalizeSpace(jObj.get("id").getAsString());
			var tournamentId = Util.normalizeSpace(jObj.get("field_1699")
				.getAsJsonArray().get(0).getAsJsonObject()
				.get("id").getAsString());
			var tournamentName = Util.normalizeSpace(jObj.get("field_1699")
				.getAsJsonArray().get(0).getAsJsonObject()
				.get("identifier").getAsString());
			var divisionId = Util.normalizeSpace(jObj.get("field_1702")
				.getAsJsonArray().get(0).getAsJsonObject()
				.get("id").getAsString());
			var division = Util.normalizeSpace(jObj.get("field_1702")
				.getAsJsonArray().get(0).getAsJsonObject()
				.get("identifier").getAsString());
			var event = Util.normalizeSpace(jObj.get("field_1753").getAsString());
			var isTrialEvent = Util.getAsBoolean(jObj.get("field_1718"));
			var teamNum = Util.normalizeSpace(jObj.get("field_1478").getAsString());
			var rank = Util.normalizeSpace(jObj.get("field_1981").getAsString());
			return new TeamRankByEvent(rawScoreId, tournamentId, tournamentName, divisionId,
				division, event, isTrialEvent, teamNum, rank);
		}
	}

	private TeamRankByEventRetrieverFactory() {}	// prevent instantiation

	public static PortalRetriever<TeamRankByEvent> create() {
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(TeamRankByEvent.class, new TeamRankByEventSerializer())
			.create();
		return new PortalRetriever<>(gson, KnackView.RANKS,
			new TypeToken<ReportResponse<TeamRankByEvent>>(){}.getType());
	}
}
