package com.wallissoftware.chessanarchy.server.messages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.google.inject.Singleton;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

@Singleton
public class UpdateMessagesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		LastUpdateTime.markUpdated();
		final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		@SuppressWarnings("unchecked")
		final Set<Map<String, Object>> messageQueue = (Set<Map<String, Object>>) cache.get(MessageServlet.MESSAGE_QUEUE_KEY);
		if (messageQueue != null) {
			final Long previousId = LatestMessageId.get();
			cache.clearAll();
			final Map<String, Object> messageMap = new HashMap<String, Object>();
			if (previousId != null) {
				messageMap.put("previous", previousId + "");
			}
			messageMap.put("created", System.currentTimeMillis() + "");

			messageMap.put("messages", messageQueue);

			final MessageCache messageCache = new MessageCache(previousId, new Gson().toJson(messageMap));
			final Objectify ofy = ObjectifyService.ofy();
			final Map<Key<MessageCache>, MessageCache> savedResult = ofy.save().entities(messageCache).now();
			final Iterator<MessageCache> it = savedResult.values().iterator();

			LatestMessageId.set(it.next().getId());

		}
	}

}
