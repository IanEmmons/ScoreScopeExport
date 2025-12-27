package org.virginiaso.duosmiator;

import java.lang.reflect.Type;
import java.util.Objects;

import org.virginiaso.duosmiator.PortalRetriever.ReportResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class RankByEventRetrieverFactory {
	private static class TeamRankByEventSerializer implements JsonDeserializer<RankByEvent> {
		private KnackAppType appType;

		public TeamRankByEventSerializer(KnackAppType appType) {
			this.appType = Objects.requireNonNull(appType, "appType");
		}

		private String field(ConfigItem field) {
			return Config.inst().get(appType, field);
		}

		@Override
		public RankByEvent deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) {
			var jObj = json.getAsJsonObject();

			var rawScoreId = Util.normalizeSpace(jObj.get("id").getAsString());
			var trackId = Util.normalizeSpace(jObj
				.get(field(ConfigItem.TEAMS_TOURNAMENT_TRACK)).getAsJsonArray().get(0)
				.getAsJsonObject().get("id").getAsString());
			var event = Util.normalizeSpace(jObj
				.get(field(ConfigItem.EVENTS_BY_TRACK_EVENT_NAME)).getAsString());
			var isTrialEvent = Util.getAsBoolean(jObj
				.get(field(ConfigItem.RAW_SCORES_IS_TRIAL_EVENT)));
			var teamNum = Util.normalizeSpace(jObj
				.get(field(ConfigItem.TEAMS_TEAM_NUM)).getAsString());
			var rank = Util.normalizeSpace(jObj
				.get(field(ConfigItem.RAW_SCORES_DUOSMIUM_RANK)).getAsString());
			return new RankByEvent(rawScoreId, trackId, event, isTrialEvent, teamNum, rank);
		}
	}

	private RankByEventRetrieverFactory() {}	// prevent instantiation

	public static PortalRetriever<RankByEvent> create(KnackAppInstance appInstance) {
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(RankByEvent.class, new TeamRankByEventSerializer(appInstance.type()))
			.create();
		return new PortalRetriever<>(gson, appInstance, ConfigItem.RANKS_VIEW,
			new TypeToken<ReportResponse<RankByEvent>>(){}.getType());
	}
}
