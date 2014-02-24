package com.wallissoftware.chessanarchy.server.messages;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gson.Gson;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.wallissoftware.chessanarchy.server.gamestate.GameState;
import com.wallissoftware.chessanarchy.server.gamestate.LatestGameStateId;
import com.wallissoftware.chessanarchy.server.governments.MoveRequest;
import com.wallissoftware.chessanarchy.server.session.SessionUtils;
import com.wallissoftware.chessanarchy.shared.game.Color;

@Singleton
public class UpdateMessagesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		LastUpdateTime.markUpdated();
		final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		@SuppressWarnings("unchecked")
		final Set<Map<String, String>> messageQueue = (Set<Map<String, String>>) cache.get(MessageServlet.MESSAGE_QUEUE_KEY);
		if (messageQueue != null) {
			final Long previousId = LatestMessageId.get();
			cache.clearAll();
			final Map<String, Object> messageMap = new HashMap<String, Object>();
			if (previousId != null) {
				messageMap.put("previous", previousId + "");
			}
			messageMap.put("created", System.currentTimeMillis() + "");

			messageMap.put("messages", messageQueue);

			updateGameState(req.getSession(), messageQueue);

			final MessageCache messageCache = new MessageCache(previousId, new Gson().toJson(messageMap));
			final Objectify ofy = ObjectifyService.ofy();
			ofy.save().entities(messageCache).now();

			LatestMessageId.set(messageCache.getId());

		}
	}

	private void updateGameState(final HttpSession session, final Set<Map<String, String>> messageQueue) {
		final Objectify ofy = ObjectifyService.ofy();
		final Long latestGameStateId = LatestGameStateId.get();
		if (latestGameStateId != null) {
			final GameState gameState = ofy.load().type(GameState.class).id(LatestGameStateId.get()).getValue();
			if (gameState != null) {
				final Map<String, String> moveMap = gameState.getLegalMoves();
				final Color currentPlayer = gameState.getCurrentPlayer();
				for (final Map<String, String> message : messageQueue) {
					if (gameState.swapColors() && message.containsKey("color")) {
						message.put("color", Color.valueOf(message.get("color")) == Color.WHITE ? Color.BLACK.name() : Color.WHITE.name());
					}
					final String msg = message.get("message");
					if (msg.toLowerCase().startsWith("/team")) {
						final String clr = msg.substring(5).trim().toUpperCase();
						try {
							Color color = Color.valueOf(clr);
							if (gameState.swapColors()) {
								color = color == Color.WHITE ? Color.BLACK : Color.WHITE;
							}
							SessionUtils.setColor(session, color);
						} catch (final Exception e) {

						}
					}

					if (currentPlayer == Color.valueOf(message.get("color")) || moveMap.containsKey(message.get("message"))) {
						gameState.addMoveRequest(new MoveRequest(Color.valueOf(message.get("color")), message.get("userId"), moveMap.get(message.get("message"))));
					}
				}

				final String serverMessage = gameState.processMoveRequests();
				if (serverMessage != null) {
					final Map<String, String> serverMessageMap = new HashMap<String, String>();
					serverMessageMap.put("userId", "Game Master");
					serverMessageMap.put("name", "Game Master");
					serverMessageMap.put("created", System.currentTimeMillis() + "");
					serverMessageMap.put("message", serverMessage);
					messageQueue.add(serverMessageMap);
				}
				ofy.save().entity(gameState);

				if (gameState.isComplete()) {
					createNewGameState(!gameState.swapColors());
				}
			}

		} else {
			createNewGameState(false);
		}

	}

	private void createNewGameState(final boolean swapColors) {
		final Objectify ofy = ObjectifyService.ofy();
		final GameState gameState = new GameState(swapColors);
		ofy.save().entity(gameState).now();
		LatestGameStateId.set(gameState.getId());

	}

}
