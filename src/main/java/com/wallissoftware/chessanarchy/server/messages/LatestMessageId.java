package com.wallissoftware.chessanarchy.server.messages;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class LatestMessageId {

	private final static String LATEST_MESSAGE_ID_KEY = "lmi";

	public static Long get() {
		final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		final Long latest = (Long) cache.get(LATEST_MESSAGE_ID_KEY);
		if (latest != null) {
			return latest;
		}
		final Objectify ofy = ObjectifyService.ofy();
		final MessageCache messageCache = ofy.load().type(MessageCache.class).order("-creationTime").first().getValue();
		if (messageCache != null) {
			set(messageCache.getId());
			return messageCache.getId();
		}
		return null;

	}

	public static void set(final Long id) {
		final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.put(LATEST_MESSAGE_ID_KEY, id);
	}

}
