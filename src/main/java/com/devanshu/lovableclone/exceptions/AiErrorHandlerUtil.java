package com.devanshu.lovableclone.exceptions;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import com.google.genai.errors.ApiException;

public final class AiErrorHandlerUtil {

	private AiErrorHandlerUtil() {
	}

	private static final String GENERIC_ERROR = "AI generation failed due to an unexpected error. Please try again later.";

	public static String getUserFriendlyMessage(final Throwable error) {

		if (error == null) {
			return GENERIC_ERROR;
		}

		for (Throwable cause : getCauseChain(error)) {

			// 1. Google GenAI API errors
			if (cause instanceof ApiException apiException) {
				return handleGoogleApiException(apiException);
			}

			// 2. Timeout errors
			if (cause instanceof SocketTimeoutException || cause instanceof HttpTimeoutException
					|| cause instanceof TimeoutException) {

				return "The AI request timed out. Please try again.";
			}

			// 3. Network connectivity errors
			if (cause instanceof UnknownHostException || cause instanceof ConnectException) {

				return "Unable to connect to the AI service. Please check your connection and try again.";
			}

			if (cause instanceof IOException) {
				return "A network error occurred while communicating with the AI service. Please try again.";
			}
		}

		return GENERIC_ERROR;
	}

	private static String handleGoogleApiException(final ApiException exception) {

		int code = exception.code();

		String status = normalize(exception.status());
		String message = normalize(exception.message());

		String combined = status + " " + message;

		// HTTP 400: Invalid request or token/context limit
		if (code == 400) {

			if (containsAny(combined, "context length", "maximum context", "input token", "token limit",
					"too many tokens", "request too large")) {

				return "Your request is too large for the AI model to process. "
						+ "Please reduce the amount of content and try again.";
			}

			return "The AI service could not process your request. " + "Please review your prompt and try again.";
		}

		// HTTP 401: Invalid or missing API credentials
		if (code == 401 || status.contains("UNAUTHENTICATED")) {
			return "The AI service authentication failed. Please contact support.";
		}

		// HTTP 403: Permission or API access issue
		if (code == 403 || status.contains("PERMISSION_DENIED")) {
			return "The AI service denied access to this request. Please contact support.";
		}

		// HTTP 404: Model or endpoint not found
		if (code == 404 || status.contains("NOT_FOUND")) {
			return "The requested AI model is currently unavailable. Please try again later.";
		}

		// HTTP 408: Request timeout
		if (code == 408 || status.contains("DEADLINE_EXCEEDED")) {
			return "The AI request timed out. Please try again.";
		}

		// HTTP 409: Conflict
		if (code == 409) {
			return "The AI service could not process this request due to a temporary conflict. Please try again.";
		}

		// HTTP 429: Quota exhaustion or rate limiting
		if (code == 429 || status.contains("RESOURCE_EXHAUSTED")) {

			if (containsAny(combined, "quota", "billing", "daily limit", "monthly limit", "free tier limit",
					"limit exceeded")) {

				return "The AI service usage quota has been exhausted. Please try again later.";
			}

			return "The AI service is receiving too many requests. " + "Please wait a moment and try again.";
		}

		// HTTP 503: Model overloaded or temporarily unavailable
		if (code == 503 || status.contains("UNAVAILABLE")) {

			if (containsAny(combined, "high demand", "overloaded", "capacity", "server busy")) {

				return "Our AI model is currently experiencing high traffic. " + "Please try again in a few moments.";
			}

			return "The AI service is temporarily unavailable. Please try again in a few moments.";
		}

		// Other server-side errors
		if (code >= 500 && code < 600) {
			return "The AI service is experiencing a temporary server issue. " + "Please try again later.";
		}

		// Other client-side errors
		if (code >= 400 && code < 500) {
			return "The AI service could not process your request. Please try again.";
		}

		return GENERIC_ERROR;
	}

	private static List<Throwable> getCauseChain(final Throwable error) {

		List<Throwable> causes = new ArrayList<>();

		Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());

		Throwable current = error;

		while (current != null && visited.add(current)) {
			causes.add(current);
			current = current.getCause();
		}

		return causes;
	}

	private static String normalize(final String value) {
		return value == null ? "" : value.toLowerCase(Locale.ROOT);
	}

	private static boolean containsAny(final String value, final String... keywords) {

		for (String keyword : keywords) {
			if (value.contains(keyword)) {
				return true;
			}
		}

		return false;
	}
}