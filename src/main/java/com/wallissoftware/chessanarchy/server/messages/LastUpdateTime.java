package com.wallissoftware.chessanarchy.server.messages;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class LastUpdateTime {

	private final static String LAST_UPDATE_TIME_KEY = "lut";

	public static boolean isTimeToUpdate() {
		final Long latest = getLastUpdateTime();
		if (latest == null) {
			return true;
		} else if (System.currentTimeMillis() - latest > 1000) {
			return true;
		}
		return false;

	}

	public static Long getLastUpdateTime() {
		final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		final Long result = (Long) cache.get(LAST_UPDATE_TIME_KEY);
		if (result != null) {
			return result;
		}
		markUpdated();
		return getLastUpdateTime();
	}

	public static void markUpdated() {
		final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.put(LAST_UPDATE_TIME_KEY, System.currentTimeMillis());
	}

}
