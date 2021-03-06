package com.wallissoftware.chessanarchy.client.time;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.wallissoftware.chessanarchy.client.dispatch.SuccessCallback;
import com.wallissoftware.chessanarchy.shared.CAConstants;

public class SyncedTime {

    private static double diff = 0;

    private static List<Double> diffs = new ArrayList<Double>();

    private static boolean accurateEnough = false;

    private final static Logger logger = Logger.getLogger(SyncedTime.class.getName());

    static {
        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

            private int count = 5;

            @Override
            public boolean execute() {
                final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
                jsonp.requestDouble(CAConstants.HOST + "/time", new SuccessCallback<Double>() {

                    @Override
                    public void onSuccess(final Double result) {
                        setDiff(result);

                    }

                });
                return --count >= 0 && !accurateEnough;

            }
        }, 4019);

    }

    public static double getDiff() {

        return diff;
    }

    public static double get() {
        return Duration.currentTimeMillis() - diff;
    }

    private static void setDiff(final Double serverTime) {

        try {
            final double d = Duration.currentTimeMillis() - serverTime;
            logger.info("Time diff with server = " + d);
            diffs.add(d);
            recalcDiff();
        } catch (final Exception e) {

        }

    }

    private static void recalcDiff() {
        final double lastDiff = diff;
        if (diffs.size() < 4) {
            double total = 0;
            for (final double d : diffs) {
                total += d;
            }
            diff = total / diffs.size();
        } else {
            Collections.sort(diffs);
            double total = 0;
            for (int i = 0; i < diffs.size() - 2; i++) {
                total += diffs.get(i);
            }
            diff = total / (diffs.size() - 2);

        }
        logger.info("Calculated time diff with server = " + diff);
        accurateEnough = Math.abs(diff - lastDiff) < 200;

    }
}
