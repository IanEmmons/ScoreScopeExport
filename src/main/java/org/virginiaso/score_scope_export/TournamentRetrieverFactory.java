package org.virginiaso.score_scope_export;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Objects;

import org.virginiaso.score_scope_export.PortalRetriever.ReportResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class TournamentRetrieverFactory {
	private static class TournamentSerializer implements JsonDeserializer<Tournament> {
		private KnackApp knackApp;

		public TournamentSerializer(KnackApp knackApp) {
			this.knackApp = Objects.requireNonNull(knackApp, "knackApp");
		}

		private String field(Field field) {
			return Config.inst().getKnackFieldId(knackApp, field);
		}

		@Override
		public Tournament deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) {
			var jObj = json.getAsJsonObject();

			var id = Util.normalizeSpace(jObj.get("id").getAsString());
			var name = Util.normalizeSpace(jObj.get(field(Field.TOURNAMENTS_TOURNAMENT_NAME))
				.getAsString());
			var dateTime = Instant.parse(jObj.get(field(Field.TOURNAMENTS_TOURNAMENT_DATE))
				.getAsJsonObject().get("iso_timestamp").getAsString());
			var numBBids = numBids(jObj, Field.TOURNAMENTS_NUM_B_BIDS);
			var numCBids = numBids(jObj, Field.TOURNAMENTS_NUM_C_BIDS);
			var numBMedalsPerEvent = jObj.get(field(Field.TOURNAMENTS_NUM_B_MEDALS)).getAsInt();
			var numCMedalsPerEvent = jObj.get(field(Field.TOURNAMENTS_NUM_C_MEDALS)).getAsInt();
			var numBTrophies = jObj.get(field(Field.TOURNAMENTS_NUM_B_TROPHIES)).getAsInt();
			var numCTrophies = jObj.get(field(Field.TOURNAMENTS_NUM_C_TROPHIES)).getAsInt();
			return new Tournament(id, name, dateTime, isOneTrophyPerSchool(jObj),
				numBBids, numCBids, numBMedalsPerEvent, numCMedalsPerEvent,
				numBTrophies, numCTrophies);
		}

		private boolean isOneTrophyPerSchool(JsonObject jObj) {
			if (KnackApp.SCORE_SCOPE == knackApp) {
				var oneTrophyPer = jObj.get(field(Field.TOURNAMENTS_ONE_TROPHY_PER)).getAsString();
				return "School".equals(Util.normalizeSpace(oneTrophyPer));
			} else {
				return !Util.getAsBoolean(jObj.get(field(Field.TOURNAMENTS_IS_STATE_TOURNAMENT)));
			}
		}

		private int numBids(JsonObject jObj, Field field) {
			if (KnackApp.SCORE_SCOPE == knackApp) {
				return -1;
			} else {
				var jPrim = jObj.get(field(field)).getAsJsonPrimitive();
				return jPrim.isNumber() ? jPrim.getAsInt() : -1;
			}
		}
	}

	private TournamentRetrieverFactory() {}	// prevent instantiation

	public static PortalRetriever<Tournament> create(KnackApp knackApp) {
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeAdapter(Tournament.class, new TournamentSerializer(knackApp))
			.create();
		return new PortalRetriever<>(gson, knackApp, KnackView.TOURNAMENTS,
			new TypeToken<ReportResponse<Tournament>>(){}.getType());
	}
}
