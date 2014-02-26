package com.wallissoftware.chessanarchy.server.messages;

import java.io.IOException;
import java.util.HashMap;
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
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.wallissoftware.chessanarchy.server.gamestate.GameState;
import com.wallissoftware.chessanarchy.server.gamestate.LatestGameStateId;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.MoveRequest;

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

			final MessageCache messageCache = new MessageCache(updateGameState(messageQueue), previousId, new Gson().toJson(messageMap));
			final Objectify ofy = ObjectifyService.ofy();
			ofy.save().entities(messageCache).now();

			LatestMessageId.set(messageCache.getId());

		}
	}

	private boolean updateGameState(final Set<Map<String, String>> messageQueue) {
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

					if (message.get("color") != null && currentPlayer == Color.valueOf(message.get("color")) || moveMap.containsKey(message.get("message"))) {
						gameState.addMoveRequest(new MoveRequest(Color.valueOf(message.get("color")), message.get("userId"), moveMap.get(message.get("message"))));
					}
				}

				final String serverMessage = gameState.processMoveRequests();
				if (serverMessage != null) {

					messageQueue.add(createServerMessageMap(serverMessage));
				}
				ofy.save().entity(gameState);

				if (gameState.isComplete()) {

					messageQueue.add(createServerMessageMap("STARTING NEW GAME: " + createNewGameState(!gameState.swapColors())));
					return true;
				}
			}

		} else {

			messageQueue.add(createServerMessageMap("STARTING NEW GAME: " + createNewGameState(false)));
			return true;
		}
		return false;

	}

	private long createNewGameState(final boolean swapColors) {
		final Objectify ofy = ObjectifyService.ofy();
		final GameState gameState = new GameState(swapColors);
		ofy.save().entity(gameState).now();
		LatestGameStateId.set(gameState.getId());
		return gameState.getId();

	}

	private Map<String, String> createServerMessageMap(final String message) {
		final Map<String, String> serverMessageMap = new HashMap<String, String>();
		serverMessageMap.put("userId", "Game Master");
		serverMessageMap.put("name", "Game Master");
		serverMessageMap.put("created", System.currentTimeMillis() + "");
		serverMessageMap.put("message", message);
		return serverMessageMap;
	}

}
