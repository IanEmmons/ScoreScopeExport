package org.virginiaso.score_scope_export;

import java.lang.reflect.Type;
import java.util.Objects;

import org.virginiaso.score_scope_export.PortalRetriever.ReportResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class TeamResultsRetrieverFactory {
	private static class TeamResultsSerializer implements JsonDeserializer<TeamResults> {
		private KnackApp knackApp;

		public TeamResultsSerializer(KnackApp knackApp) {
			this.knackApp = Objects.requireNonNull(knackApp, "knackApp");
		}

		private String field(Field field) {
			return Config.inst().getKnackFieldId(knackApp, field);
		}

		@Override
		public TeamResults deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			var jObj = json.getAsJsonObject();

			var tournamentId = Util.normalizeSpace(jObj
				.get(field(Field.TOURNAMENTS_TOURNAMENT)).getAsJsonArray().get(0)
				.getAsJsonObject().get("id").getAsString());
			var tournamentName = Util.normalizeSpace(jObj
				.get(field(Field.TOURNAMENTS_TOURNAMENT)).getAsJsonArray().get(0)
				.getAsJsonObject().get("identifier").getAsString());
			var teamId = Util.normalizeSpace(jObj.get("id").getAsString());
			var teamNum = Util.normalizeSpace(jObj
				.get(field(Field.TEAMS_TEAM_NUM)).getAsString());
			var schoolName = Util.normalizeSpace(jObj
				.get(field(Field.SCHOOL_SCHOOL_NAME)).getAsString());
			var teamName = Util.normalizeSpace(jObj
				.get(field(Field.TEAMS_TEAM_NAME)).getAsString());
			var cityState = Util.normalizeSpace(jObj
				.get(field(Field.SCHOOL_CITY_STATE)).getAsString());
			var isExhibitionTeam = Util.getAsBoolean(jObj
				.get(field(Field.TEAMS_EXHIBITION_TEAM)));
			var scoreNoPenalty = Util.getAsBigDecimal(jObj
				.get(field(Field.TEAMS_SUMMED_TEAM_POINT_SCORE)));
			var penalty = Util.getAsBigDecimal(jObj.get(field(Field.TEAMS_PENALTY)));
			var finalScore = Util.getAsBigDecimal(jObj
				.get(field(Field.TEAMS_TEAM_POINT_SCORE)));
			return new TeamResults(tournamentId, tournamentName, getDivision(jObj), teamId,
				teamNum, schoolName, teamName, cityState, isExhibitionTeam, scoreNoPenalty,
				penalty, finalScore);
		}

		private String getDivision(JsonObject jObj) {
			if (KnackApp.SCORE_SCOPE == knackApp) {
				return Util.normalizeSpace(jObj
					.get(field(Field.DIVISION_DIVISION)).getAsString());
			} else {
				return Util.normalizeSpace(jObj
					.get(field(Field.DIVISION_DIVISION)).getAsJsonArray().get(0)
					.getAsJsonObject().get("identifier").getAsString());
			}
		}
	}

	private TeamResultsRetrieverFactory() {}	// prevent instantiation

	public static PortalRetriever<TeamResults> create(KnackApp knackApp) {
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(TeamResults.class, new TeamResultsSerializer(knackApp))
			.create();
		return new PortalRetriever<>(gson, knackApp, KnackView.TEAM_RESULTS,
			new TypeToken<ReportResponse<TeamResults>>(){}.getType());
	}
}
