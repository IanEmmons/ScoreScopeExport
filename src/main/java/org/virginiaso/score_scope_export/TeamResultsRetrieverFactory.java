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

		private String field(ConfigItem field) {
			return Config.inst().get(knackApp, field);
		}

		@Override
		public TeamResults deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			var jObj = json.getAsJsonObject();

			var trackId = getTrackId(jObj);
			var teamId = Util.normalizeSpace(jObj.get("id").getAsString());
			var teamNum = Util.normalizeSpace(jObj
				.get(field(ConfigItem.TEAMS_TEAM_NUM)).getAsString());
			var schoolName = Util.normalizeSpace(jObj
				.get(field(ConfigItem.SCHOOL_AWARD_CEREMONY_NAME)).getAsString());
			var teamName = Util.normalizeSpace(jObj
				.get(field(ConfigItem.TEAMS_TEAM_NAME)).getAsString());
			var cityState = Util.normalizeSpace(jObj
				.get(field(ConfigItem.SCHOOL_CITY_STATE)).getAsString());
			var isExhibitionTeam = Util.getAsBoolean(jObj
				.get(field(ConfigItem.TEAMS_IS_EXHIBITION_TEAM)));
			var scoreNoPenalty = Util.getAsBigDecimal(jObj
				.get(field(ConfigItem.TEAMS_SUMMED_TEAM_POINT_SCORE)));
			var penalty = Util.getAsBigDecimal(jObj.get(field(ConfigItem.TEAMS_PENALTY)));
			var finalScore = Util.getAsBigDecimal(jObj
				.get(field(ConfigItem.TEAMS_TEAM_POINT_SCORE)));
			return new TeamResults(trackId, teamId,
				teamNum, schoolName, teamName, cityState, isExhibitionTeam, scoreNoPenalty,
				penalty, finalScore);
		}

		/**
		 * The VASO Portal branch of this function follows the expected pattern in which
		 * the Knack grid returns a JSON structure for a foreign key that contains the row
		 * ID of the connected row in the referenced table (Tournament Track in this case).
		 *
		 * Unfortunately, in ScoreScope (where the referenced table is Division), Knack
		 * doesn't follow the pattern. It just gives us the division label (no row ID), and
		 * so we look up the division label in the list of tracks and get the ID from there.
		 */
		private String getTrackId(JsonObject jObj) {
			if (KnackApp.VASO_PORTAL == knackApp) {
				return Util.normalizeSpace(jObj
					.get(field(ConfigItem.TEAMS_TOURNAMENT_TRACK)).getAsJsonArray().get(0)
					.getAsJsonObject().get("id").getAsString());
			} else {
				var divLabel = Util.normalizeSpace(jObj
					.get(field(ConfigItem.TOURNAMENT_TRACK_DIVISION)).getAsString());

				return WizardData.inst().tracks.stream()
					.filter(track -> Objects.equals(track.division(), divLabel))
					.findFirst()
					.orElseThrow(() -> new IllegalStateException(
						"None of the tracks has division '%1$s'".formatted(divLabel)))
					.trackId();
			}
		}
	}

	private TeamResultsRetrieverFactory() {}	// prevent instantiation

	public static PortalRetriever<TeamResults> create(KnackApp knackApp) {
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(TeamResults.class, new TeamResultsSerializer(knackApp))
			.create();
		return new PortalRetriever<>(gson, knackApp, ConfigItem.TEAM_RESULTS_VIEW,
			new TypeToken<ReportResponse<TeamResults>>(){}.getType());
	}
}
