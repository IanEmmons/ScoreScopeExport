package org.virginiaso.score_scope_export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class PortalRetriever<Item> {
	public static class ReportResponse<Item> {
		public ReportResponse() {
			total_pages = -1;
			current_page = -1;
			total_records = -1;
			records = null;
		}

		public ReportResponse(List<Item> items) {
			total_pages = 1;
			current_page = 1;
			total_records = items.size();
			records = items;
		}

		public int total_pages;
		public int current_page;
		public int total_records;
		public List<Item> records;
	}

	private static final String REPORT_URL = "https://api.knack.com/v1/pages/"
		+ "%1$s/views/%2$s/records?format=raw&page=%3$d&rows_per_page=%4$d";
	private static final int PAGE_SIZE = 100;

	// From the config and factory:
	private final Type reportResponseType;
	private final Gson gson;
	private final KnackApp knackApp;
	private final ConfigItem knackView;

	// Computed here:
	private int totalPages;
	private int lastPageRead;	// 1-based
	private List<Item> reportItems;

	public PortalRetriever(Gson gson, KnackApp knackApp, ConfigItem knackView, Type reportResponseType) {
		this.reportResponseType = reportResponseType;
		this.gson = gson;
		this.knackApp = knackApp;
		this.knackView = knackView;

		totalPages = -1;
		lastPageRead = -1;
		reportItems = new ArrayList<>();
	}

	public List<Item> retrieveReport() {
		for (int currentPage = 1;; ++currentPage) {
			var httpRequest = getHttpRequest(currentPage);
			try (var httpClient = HttpClient.newHttpClient()) {
				httpClient
					.sendAsync(httpRequest, BodyHandlers.ofInputStream())
					.thenApply(HttpResponse::body)
					.thenAccept(is -> reportItems.addAll(readJsonReport(is, this)))
					.join();
				if (lastPageRead >= totalPages) {
					break;
				}
			}
		}
		return reportItems;
	}

	private HttpRequest getHttpRequest(int currentPage) {
		var sceneId = Config.inst().get(knackApp, knackView.getAssociatedScene());
		var viewId = Config.inst().get(knackApp, knackView);
		var url = REPORT_URL.formatted(sceneId, viewId, currentPage, PAGE_SIZE);
		return HttpRequest.newBuilder(URI.create(url))
			.GET()
			.header("Accept", Util.JSON_MEDIA_TYPE)
			.header("X-Knack-Application-Id", Config.inst().get(knackApp, ConfigItem.APPLICATION_ID))
			.header("X-Knack-REST-API-KEY", "knack")
			.header("Authorization", PortalUserToken.inst().getUserToken())
			.build();
	}

	private List<Item> readJsonReport(InputStream is, PortalRetriever<Item> retriever) {
		try (Reader rdr = new InputStreamReader(is, Util.CHARSET)) {
			ReportResponse<Item> response = gson.fromJson(rdr, reportResponseType);
			if (retriever != null) {
				retriever.totalPages = response.total_pages;
				retriever.lastPageRead = response.current_page;
			}
			return response.records;
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	public void saveRawReport(String reportName) {
		var fileName = "raw-%1$s-%2$s-report-body.json".formatted(knackApp, reportName);
		var httpRequest = getHttpRequest(1);
		try (var httpClient = HttpClient.newHttpClient()) {
			httpClient
				.sendAsync(httpRequest, BodyHandlers.ofInputStream())
				.thenApply(HttpResponse::body)
				.thenAccept(is -> writeToFile(is, fileName))
				.join();
		}
	}

	private static void writeToFile(InputStream is, String fileName) {
		try (var os = new FileOutputStream(fileName)) {
			is.transferTo(os);
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
