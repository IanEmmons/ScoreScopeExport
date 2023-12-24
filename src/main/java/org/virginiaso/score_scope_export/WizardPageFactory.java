package org.virginiaso.score_scope_export;

import java.util.Objects;
import java.util.function.Function;

public class WizardPageFactory {
	private final String pageId;
	private final Function<String, WizardPage> pageCreator;
	private WizardPage page;

	public WizardPageFactory(String pageId, Function<String, WizardPage> pageCreator) {
		this.pageId = Objects.requireNonNull(pageId, "pageId");
		this.pageCreator = Objects.requireNonNull(pageCreator, "pageCreator");
		page = null;
	}

	public String pageId() {
		return pageId;
	}

	public synchronized WizardPage page() {
		if (page == null) {
			page = pageCreator.apply(pageId);
		}
		return page;
	}
}
