package com.wallissoftware.chessanarchy.server.wipe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.wallissoftware.chessanarchy.server.gamestate.GameState;
import com.wallissoftware.chessanarchy.server.messages.MessageCache;

@Singleton
public class WipeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		final QueryResultIterable<Key<MessageCache>> it = ObjectifyService.ofy().load().type(MessageCache.class).keys().iterable();
		ObjectifyService.ofy().delete().keys(it);

		final QueryResultIterable<Key<GameState>> itg = ObjectifyService.ofy().load().type(GameState.class).keys().iterable();
		ObjectifyService.ofy().delete().keys(itg);

		final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		cache.clearAll();

	}

}
