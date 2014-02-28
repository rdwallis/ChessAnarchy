package com.wallissoftware.chessanarchy.client.time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class SyncedTime {

	private static long diff = 0;

	private static List<Long> diffs = new ArrayList<Long>();

	private static boolean accurateEnough = false;

	private final static Logger logger = Logger.getLogger(SyncedTime.class.getName());

	static {
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

			private int count = 5;

			@Override
			public boolean execute() {

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
				return --count >= 0 && !accurateEnough;

			}
		}, 1019);

	}

	public static long get() {
		return System.currentTimeMillis() - diff;
	}

	private static void setDiff(final String text) {

		try {
			final long d = System.currentTimeMillis() - Long.valueOf(text);
			logger.info("Time diff with server = " + d);
			diffs.add(d);
			recalcDiff();
		} catch (final Exception e) {

		}

	}

	private static void recalcDiff() {
		final long lastDiff = diff;
		if (diffs.size() < 4) {
			long total = 0;
			for (final long d : diffs) {
				total += d;
			}
			diff = total / diffs.size();
		} else {
			Collections.sort(diffs);
			long total = 0;
			for (int i = 0; i < diffs.size() - 2; i++) {
				total += diffs.get(i);
			}
			diff = total / (diffs.size() - 2);

		}
		logger.info("Calculated time diff with server = " + diff);
		accurateEnough = Math.abs(diff - lastDiff) < 200;

	}
}
