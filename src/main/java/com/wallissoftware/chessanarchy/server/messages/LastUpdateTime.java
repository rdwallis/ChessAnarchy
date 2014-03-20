package com.wallissoftware.chessanarchy.server.messages;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.wallissoftware.chessanarchy.shared.CAConstants;

public class LastUpdateTime {

    private final static String LAST_UPDATE_TIME_KEY = "lut";

    public static boolean isTimeToUpdate() {
        final Long latest = getLastUpdateTime(false);
        if (latest == null) {
            return true;
        } else if (System.currentTimeMillis() - latest > CAConstants.SYNC_DELAY) {
            return true;
        }
        return false;

    }

    public static Long getLastUpdateTime() {
        return getLastUpdateTime(true);
    }

    private static Long getLastUpdateTime(final boolean preventNull) {
        final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
        final Long result = (Long) cache.get(LAST_UPDATE_TIME_KEY);
        if (result != null) {
            return result;
        } else if (preventNull) {
            markUpdated();
            return getLastUpdateTime();
        } else {
            return null;
        }
    }

    public static void markUpdated() {
        final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
        cache.put(LAST_UPDATE_TIME_KEY, System.currentTimeMillis());
    }

}
