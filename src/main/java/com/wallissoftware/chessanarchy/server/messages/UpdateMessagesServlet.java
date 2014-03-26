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
import com.google.gson.GsonBuilder;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.wallissoftware.chessanarchy.server.gamestate.GameState;
import com.wallissoftware.chessanarchy.server.gamestate.LatestGameStateId;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.MoveRequest;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;
import com.wallissoftware.chessanarchy.shared.message.Message;

@Singleton
public class UpdateMessagesServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected synchronized void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        LastUpdateTime.markUpdated();
        final MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

        final Set<String> keys = new HashSet<String>();
        for (int i = 0; i < SendMessageServlet.SHARD_COUNT; i++) {
            keys.add(SendMessageServlet.MESSAGE_QUEUE_KEY + i);

        }
        final Map<String, Object> messageCache = cache.getAll(keys);
        cache.deleteAll(keys);
        final Set<Message> messageQueue = new HashSet<Message>();
        for (final Object shard : messageCache.values()) {
            messageQueue.addAll((Set<Message>) shard);
        }

        final Long previousId = LatestMessageId.get();

        final Objectify ofy = ObjectifyService.factory().begin();
        final Long latestGameStateId = LatestGameStateId.get();
        final Set<Message> gameStateMessages = new HashSet<Message>();
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
                final MessageCache messageCache = new MessageCache(previousId, new GsonBuilder().disableHtmlEscaping().create().toJson(messageMap));
                ofy.save().entities(messageCache);

                LatestMessageId.set(messageCache.getId());

            }

        });

    }

    private boolean updateGameState(final Objectify ofy, final Long latestGameStateId, final Set<Message> messageQueue) {

        if (latestGameStateId != null) {
            final GameState gameState = ofy.load().type(GameState.class).id(LatestGameStateId.get()).getValue();
            if (gameState != null) {

                final Map<String, String> moveMap = gameState.getLegalMoveMap();
                final Color currentPlayer = gameState.getCurrentPlayer();
                for (final Message message : messageQueue) {
                    if (message.getColor() != null) {
                        if (gameState.swapColors()) {
                            message.swapColors();
                        }
                        if (gameState.isGovernmentElected()) {
                            if (currentPlayer == message.getColor() && moveMap.containsKey(message.getText())) {
                                gameState.addMoveRequest(new MoveRequest(message.getColor(), message.getUserId(), moveMap.get(message.getText())));
                            }
                        } else if (gameState.isElectionStarted()) {
                            if (SystemOfGovernment.isSystemOfGovernment(message.getText())) {
                                gameState.addMoveRequest(new MoveRequest(message.getColor(), message.getUserId(), message.getText()));
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
