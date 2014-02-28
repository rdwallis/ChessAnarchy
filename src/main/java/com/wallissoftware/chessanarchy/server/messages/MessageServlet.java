package com.wallissoftware.chessanarchy.server.messages;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.wallissoftware.chessanarchy.server.gamestate.GameState;
import com.wallissoftware.chessanarchy.server.gamestate.LatestGameStateId;
import com.wallissoftware.chessanarchy.server.session.SessionUtils;
import com.wallissoftware.chessanarchy.shared.game.Color;

@Singleton
public class MessageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String MESSAGE_QUEUE_KEY = "mq";

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		final String idStr = req.getParameter("id");

		final Long id;
		final boolean idSupplied = idStr != null;
		boolean toGameStart = false;
		if (idSupplied) {
			id = Long.valueOf(idStr);
			toGameStart = req.getParameter("tgs") != null;
		} else {
			id = LatestMessageId.get();
		}
		if (id != null) {

			final Objectify ofy = ObjectifyService.ofy();
			MessageCache messageCache = ofy.load().type(MessageCache.class).id(id).getValue();
			if (messageCache != null) {
				resp.setContentType("application/json");
				final String maxAge = idSupplied ? "31556926" : "1";
				resp.setHeader("cache-control", "public, max-age=" + maxAge);

				if (toGameStart) {
					resp.getWriter().write("[");
					resp.getWriter().write(messageCache.getJson());
					while (messageCache.getPreviousId() != null) {
						messageCache = ofy.load().type(MessageCache.class).id(messageCache.getPreviousId()).getValue();
						resp.getWriter().write(",");
						resp.getWriter().write(messageCache.getJson());
						if (messageCache.isGameStart()) {
							break;
						}
					}
					resp.getWriter().write("]");
				} else {
					resp.getWriter().write(messageCache.getJson());
				}

				return;
			}
		}
		resp.sendError(404);

	}

	/*@Override
	protected long getLastModified(final HttpServletRequest req) {
		final Long lastUpdateTime = LastUpdateTime.getLastUpdateTime();
		if (lastUpdateTime == null) {
			return System.currentTimeMillis();
		}
		return lastUpdateTime;
	}*/

	@Override
	protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		final StringWriter writer = new StringWriter();
		IOUtils.copy(req.getInputStream(), writer, "UTF-8");
		final String message = StringEscapeUtils.escapeHtml(writer.toString());
		boolean sessionModified = false;
		if (message != null && !message.isEmpty()) {
			final Map<String, String> map = new HashMap<String, String>();
			map.put("userId", SessionUtils.getUserId(req.getSession()));
			map.put("name", SessionUtils.getName(req.getSession()));
			if (message.toLowerCase().startsWith("/nick")) {
				String name = message.substring(5);
				name = name.replace(" ", "");
				if (name.length() > 20) {
					name = name.substring(0, 20);
				}
				SessionUtils.setName(req.getSession(), name);
				sessionModified = true;
			}
			if (message.toLowerCase().startsWith("/team")) {
				final String clr = message.substring(5).trim().toUpperCase();
				try {
					Color color = Color.valueOf(clr);
					final Long id = LatestGameStateId.get();

					if (id != null) {
						final Objectify ofy = ObjectifyService.ofy();
						final GameState gameState = ofy.load().type(GameState.class).id(id).getValue();
						if (gameState != null) {
							if (gameState.swapColors()) {
								color = color == Color.WHITE ? Color.BLACK : Color.WHITE;
							}
							SessionUtils.setColor(req.getSession(), color);
							sessionModified = true;
						}
					}

				} catch (final Exception e) {

				}
			}
			map.put("id", UUID.randomUUID().toString());
			map.put("created", System.currentTimeMillis() + "");
			map.put("message", message);
			final Color color = SessionUtils.getColor(req.getSession());
			if (color != null) {
				map.put("color", color.name());
			}

			final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
			@SuppressWarnings("unchecked")
			Set<Map<String, String>> messageQueue = (Set<Map<String, String>>) cache.get(MESSAGE_QUEUE_KEY);
			if (messageQueue == null) {
				messageQueue = new HashSet<Map<String, String>>();
			}
			messageQueue.add(map);
			cache.put(MESSAGE_QUEUE_KEY, messageQueue);
			if (messageQueue.size() % 100 == 0 || LastUpdateTime.isTimeToUpdate()) {
				final Queue queue = QueueFactory.getDefaultQueue();
				queue.add(withUrl("/admin/processmessages").method(Method.GET));
			}

		}
		if (sessionModified) {
			resp.setContentType("application/json");
			resp.getWriter().write(SessionUtils.getUserJson(req.getSession()));
		}

	}

}
