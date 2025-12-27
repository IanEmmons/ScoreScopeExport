package org.virginiaso.duosmiator;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Objects;

import org.virginiaso.duosmiator.PortalRetriever.ReportResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class TrackRetrieverFactory {
	private static class TrackSerializer implements JsonDeserializer<Track> {
		private KnackAppType appType;

		public TrackSerializer(KnackAppType appType) {
			this.appType = Objects.requireNonNull(appType, "appType");
		}

		private String field(ConfigItem field) {
			return Config.inst().get(appType, field);
		}

		@Override
		public Track deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) {
			var jObj = json.getAsJsonObject();

			var trackId = Util.normalizeSpace(jObj.get("id").getAsString());
			var name = Util.normalizeSpace(jObj.get(field(ConfigItem.TOURNAMENT_TRACK_FULL_TOURNAMENT_NAME))
				.getAsString());
			var dateTime = Instant.parse(jObj.get(field(ConfigItem.TOURNAMENTS_TOURNAMENT_DATE))
				.getAsJsonObject().get("iso_timestamp").getAsString());
			var numSchoolsProgressing = numBids(jObj, ConfigItem.TOURNAMENT_TRACK_NUM_SCHOOLS_PROGRESSING);
			var numMedalsPerEvent = jObj.get(field(ConfigItem.TOURNAMENT_TRACK_NUM_MEDALS)).getAsInt();
			var numTrophies = jObj.get(field(ConfigItem.TOURNAMENT_TRACK_NUM_TROPHIES)).getAsInt();
			return new Track(trackId, name, dateTime, tournamentLevel(jObj),
				getDivision(jObj), isOneTrophyPerSchool(jObj), numSchoolsProgressing,
				numMedalsPerEvent, numTrophies);
		}

		private TournamentLevel tournamentLevel(JsonObject jObj) {
			if (KnackAppType.VASO_PORTAL == appType) {
				return Util.getAsBoolean(jObj.get(field(ConfigItem.TOURNAMENTS_IS_STATE_TOURNAMENT)))
					? TournamentLevel.STATE
					: TournamentLevel.REGIONAL;
			} else {
				return TournamentLevel.INVITATIONAL;
			}
		}

		private String getDivision(JsonObject jObj) {
			var divElement = jObj.get(field(ConfigItem.TOURNAMENT_TRACK_DIVISION));
			if (KnackAppType.VASO_PORTAL == appType) {
				divElement = divElement.getAsJsonArray().get(0)
					.getAsJsonObject().get("identifier");
			}
			return Util.normalizeSpace(divElement.getAsString());
		}

		private boolean isOneTrophyPerSchool(JsonObject jObj) {
			if (KnackAppType.VASO_PORTAL == appType) {
				return !Util.getAsBoolean(jObj.get(field(ConfigItem.TOURNAMENTS_IS_STATE_TOURNAMENT)));
			} else {
				var oneTrophyPer = jObj.get(field(ConfigItem.TOURNAMENTS_ONE_TROPHY_PER)).getAsString();
				return "School".equals(Util.normalizeSpace(oneTrophyPer));
			}
		}

		private int numBids(JsonObject jObj, ConfigItem field) {
			if (KnackAppType.VASO_PORTAL == appType) {
				var jPrim = jObj.get(field(field)).getAsJsonPrimitive();
				return jPrim.isNumber() ? jPrim.getAsInt() : -1;
			} else {
				return -1;
			}
		}
	}

	private TrackRetrieverFactory() {}	// prevent instantiation

	public static PortalRetriever<Track> create(KnackAppInstance appInstance) {
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(Track.class, new TrackSerializer(appInstance.type()))
			.create();
		return new PortalRetriever<>(gson, appInstance, ConfigItem.TOURNAMENT_TRACKS_VIEW,
			new TypeToken<ReportResponse<Track>>(){}.getType());
	}
}
