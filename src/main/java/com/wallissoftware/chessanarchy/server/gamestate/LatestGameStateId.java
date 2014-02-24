package com.wallissoftware.chessanarchy.server.gamestate;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class LatestGameStateId {

	private final static String LATEST_GAME_STATE_ID_KEY = "lgs";

	public static Long get() {
		final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		final Long latest = (Long) cache.get(LATEST_GAME_STATE_ID_KEY);
		if (latest != null) {
			return latest;
		}
		final Objectify ofy = ObjectifyService.ofy();
		final GameState gameState = ofy.load().type(GameState.class).order("-creationTime").first().getValue();
		if (gameState != null) {
			set(gameState.getId());
			return gameState.getId();
		}
		return null;

	}

	public static void set(final Long id) {
		final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.put(LATEST_GAME_STATE_ID_KEY, id);
	}

}
