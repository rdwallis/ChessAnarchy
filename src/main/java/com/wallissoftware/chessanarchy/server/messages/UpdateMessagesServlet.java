package com.wallissoftware.chessanarchy.server.messages;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
import com.googlecode.objectify.VoidWork;
import com.wallissoftware.chessanarchy.server.gamestate.GameState;
import com.wallissoftware.chessanarchy.server.gamestate.LatestGameStateId;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.MoveRequest;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;

@Singleton
public class UpdateMessagesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		LastUpdateTime.markUpdated();
		final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		@SuppressWarnings("unchecked")
		Set<Map<String, String>> msgQueue = (Set<Map<String, String>>) cache.get(GetMessageServlet.MESSAGE_QUEUE_KEY);
		if (msgQueue == null) {
			msgQueue = new HashSet<Map<String, String>>();
		}
		final Set<Map<String, String>> messageQueue = msgQueue;
		final Long previousId = LatestMessageId.get();
		cache.clearAll();

		final Objectify ofy = ObjectifyService.factory().begin();
		final Long latestGameStateId = LatestGameStateId.get();
		final Set<Map<String, String>> gameStateMessages = new HashSet<Map<String, String>>();
		final Long previousGameStateId = LatestGameStateId.getPrevious();

		if (latestGameStateId != null) {
			gameStateMessages.addAll(ofy.load().type(GameState.class).id(latestGameStateId).getValue().getLastMessages(5));
		}

		if (gameStateMessages.size() < 5 && previousGameStateId != null) {
			gameStateMessages.addAll(ofy.load().type(GameState.class).id(previousGameStateId).getValue().getLastMessages(5));

		}

		ofy.transact(new VoidWork() {

			@Override
			public void vrun() {
				updateGameState(ofy, latestGameStateId, messageQueue);
				final Map<String, Object> messageMap = new HashMap<String, Object>();
				if (previousId != null) {
					messageMap.put("previous", previousId + "");
				}
				messageMap.put("created", System.currentTimeMillis() + "");

				messageQueue.addAll(gameStateMessages);
				messageMap.put("messages", messageQueue);
				final MessageCache messageCache = new MessageCache(previousId, new Gson().toJson(messageMap));
				ofy.save().entities(messageCache);

				LatestMessageId.set(messageCache.getId());

			}

		});

	}

	private boolean updateGameState(final Objectify ofy, final Long latestGameStateId, final Set<Map<String, String>> messageQueue) {

		if (latestGameStateId != null) {
			final GameState gameState = ofy.load().type(GameState.class).id(LatestGameStateId.get()).getValue();
			if (gameState != null) {

				final Map<String, String> moveMap = gameState.getLegalMoveMap();
				final Color currentPlayer = gameState.getCurrentPlayer();
				for (final Map<String, String> message : messageQueue) {
					if (message.containsKey("color")) {
						if (gameState.swapColors()) {
							message.put("color", Color.valueOf(message.get("color")).getOpposite().name());
						}
						if (gameState.isGovernmentElected()) {
							if (currentPlayer == Color.valueOf(message.get("color")) && moveMap.containsKey(message.get("message"))) {
								gameState.addMoveRequest(new MoveRequest(Color.valueOf(message.get("color")), message.get("userId"), moveMap.get(message.get("message"))));
							}
						} else if (gameState.isElectionStarted()) {
							if (SystemOfGovernment.isSystemOfGovernment(message.get("message"))) {
								gameState.addMoveRequest(new MoveRequest(Color.valueOf(message.get("color")), message.get("userId"), message.get("message")));
							}
						}
					}
				}

				gameState.processMoveRequests();
				messageQueue.addAll(gameState.getLastMessages(2));
				ofy.save().entity(gameState);

				if (gameState.isFinished()) {
					createNewGameState(ofy, !gameState.swapColors());
					return true;
				}

			}

		} else {
			createNewGameState(ofy, false);
			return true;
		}
		return false;

	}

	private GameState createNewGameState(final Objectify ofy, final boolean swapColors) {
		final GameState gameState = new GameState(swapColors);
		ofy.save().entity(gameState).now();
		LatestGameStateId.set(gameState.getId());
		return gameState;

	}

}
