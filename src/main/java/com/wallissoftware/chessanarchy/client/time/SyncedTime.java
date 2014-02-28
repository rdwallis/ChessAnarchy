package com.wallissoftware.chessanarchy.client.time;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class SyncedTime {

	private static long diff = 0;

	static {
		final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "/time");
		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(final Request request, final Response response) {
					if (200 == response.getStatusCode()) {
						setDiff(response.getText());
					} else {
					}

				}

				@Override
				public void onError(final Request request, final Throwable exception) {
					// TODO Auto-generated method stub

				}
			});
		} catch (final RequestException e) {

		}

	}

	public static long get() {
		return System.currentTimeMillis() - diff;
	}

	private static void setDiff(final String text) {
		try {
			diff = System.currentTimeMillis() - Long.valueOf(text);
		} catch (final Exception e) {

		}

	}
}
