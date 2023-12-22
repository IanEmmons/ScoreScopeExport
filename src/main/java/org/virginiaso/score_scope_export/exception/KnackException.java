package org.virginiaso.score_scope_export.exception;

public class KnackException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public KnackException(String formatStr, Object... args) {
		super(formatStr.formatted(args));
	}

	public KnackException(Throwable cause, String formatStr, Object... args) {
		super(formatStr.formatted(args), cause);
	}

	public KnackException(String message, Throwable cause, boolean enableSuppression,
		boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
