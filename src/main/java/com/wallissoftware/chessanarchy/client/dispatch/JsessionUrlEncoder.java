package com.wallissoftware.chessanarchy.client.dispatch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.shared.CAConstants;

public class JsessionUrlEncoder {

	private static boolean cookiesEnabled = false;
	private static boolean alreadyChecked = false;

	public static String encode(String url) {

		final String[] split = url.split("\\?");
		url = URL.encode(split[0]) + (cookiesEnabled() ? "" : ";jsessionid=" + User.get().getSessionId());
		if (split.length > 1) {
			url += "?";
			final String[] keyValues = split[1].split("=");
			for (int i = 0; i < keyValues.length; i++) {
				if (i % 2 != 0) {
					url += URL.encodeQueryString(keyValues[i]);
				} else {
					url += keyValues[i] + "=";
				}
			}
		}

		return url;
	}

	public static boolean cookiesEnabled() {
		return GWT.getHostPageBaseURL().equals(CAConstants.HOST) || checkCookiesEnabled();
	}

	private static boolean checkCookiesEnabled() {
		if (!alreadyChecked) {
			alreadyChecked = true;
			final JsonpRequestBuilder jsonP = new JsonpRequestBuilder();
			jsonP.requestBoolean(CAConstants.HOST + "/cookiesEnabled", new SuccessCallback<Boolean>() {

				@Override
				public void onSuccess(final Boolean result) {
					cookiesEnabled = result;

				}
			});
		}
		return cookiesEnabled;
	}

}
