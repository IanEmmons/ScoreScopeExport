package org.virginiaso.score_scope_export;

import java.lang.reflect.Type;
import java.util.Objects;

import org.virginiaso.score_scope_export.PortalRetriever.ReportResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class TeamRankByEventRetrieverFactory {
	private static class TeamRankByEventSerializer implements JsonDeserializer<TeamRankByEvent> {
		private KnackApp knackApp;

		public TeamRankByEventSerializer(KnackApp knackApp) {
			this.knackApp = Objects.requireNonNull(knackApp, "knackApp");
		}

		private String field(Field field) {
			return Config.inst().getKnackFieldId(knackApp, field);
		}

		@Override
		public TeamRankByEvent deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) {
			var jObj = json.getAsJsonObject();

			var rawScoreId = Util.normalizeSpace(jObj.get("id").getAsString());
			var tournamentId = Util.normalizeSpace(jObj
				.get(field(Field.EVENTS_BY_TOURNAMENT_TOURNAMENT)).getAsJsonArray().get(0)
				.getAsJsonObject().get("id").getAsString());
			var tournamentName = Util.normalizeSpace(jObj
				.get(field(Field.EVENTS_BY_TOURNAMENT_TOURNAMENT)).getAsJsonArray().get(0)
				.getAsJsonObject().get("identifier").getAsString());
			var divisionId = Util.normalizeSpace(jObj
				.get(field(Field.EVENTS_BY_TOURNAMENT_DIVISION)).getAsJsonArray().get(0)
				.getAsJsonObject().get("id").getAsString());
			var division = Util.normalizeSpace(jObj
				.get(field(Field.EVENTS_BY_TOURNAMENT_DIVISION)).getAsJsonArray().get(0)
				.getAsJsonObject().get("identifier").getAsString());
			var event = Util.normalizeSpace(jObj
				.get(field(Field.EVENTS_BY_TOURNAMENT_EVENT_NAME)).getAsString());
			var isTrialEvent = Util.getAsBoolean(jObj
				.get(field(Field.RAW_SCORES_IS_TRIAL_EVENT)));
			var teamNum = Util.normalizeSpace(jObj
				.get(field(Field.TEAMS_TEAM_NUM)).getAsString());
			var rank = Util.normalizeSpace(jObj
				.get(field(Field.RAW_SCORES_DUOSMIUM_RANK)).getAsString());
			return new TeamRankByEvent(rawScoreId, tournamentId, tournamentName, divisionId,
				division, event, isTrialEvent, teamNum, rank);
		}
	}

	private TeamRankByEventRetrieverFactory() {}	// prevent instantiation

	public static PortalRetriever<TeamRankByEvent> create(KnackApp knackApp) {
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(TeamRankByEvent.class, new TeamRankByEventSerializer(knackApp))
			.create();
		return new PortalRetriever<>(gson, knackApp, KnackView.RANKS,
			new TypeToken<ReportResponse<TeamRankByEvent>>(){}.getType());
	}
}
