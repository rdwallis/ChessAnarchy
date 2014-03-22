package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.http.client.URL;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.dispatch.JsessionUrlEncoder;
import com.wallissoftware.chessanarchy.client.dispatch.SuccessCallback;
import com.wallissoftware.chessanarchy.client.game.chat.events.MessageInLimboEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.MessageInLimboEvent.MessageInLimboHandler;
import com.wallissoftware.chessanarchy.client.game.chat.events.ReceivedMessageCacheEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.RemoveMessageEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.RemoveMessageEvent.RemoveMessageHandler;
import com.wallissoftware.chessanarchy.client.game.chat.messageinput.MessageInputPresenter;
import com.wallissoftware.chessanarchy.client.game.chat.model.JsonMessage;
import com.wallissoftware.chessanarchy.client.game.chat.model.JsonMessageCache;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.ResyncGameStateRequestEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.ResyncGameStateRequestEvent.ResyncGameStateRequestHandler;
import com.wallissoftware.chessanarchy.client.game.gamestate.model.GameState;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.shared.CAConstants;
import com.wallissoftware.chessanarchy.shared.message.Message;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public class MessageLogPresenter extends PresenterWidget<MessageLogPresenter.MyView> implements MessageLogUiHandlers, MessageInLimboHandler, RemoveMessageHandler, ResyncGameStateRequestHandler {
    public interface MyView extends View, HasUiHandlers<MessageLogUiHandlers> {

        boolean isScrollBarShowing();

        void addMessage(MessageWrapper message, boolean immediately);

        void removeLimboMessage(String messageId);
    }

    private List<MessageWrapper> messages = new ArrayList<MessageWrapper>();

    private List<MessageWrapper> gameMasterMessages = new ArrayList<MessageWrapper>();

    private Map<String, MessageWrapper> awaitingValidationMessages = new HashMap<String, MessageWrapper>();

    private Set<String> loadedMessageIds = new HashSet<String>();

    private Set<String> loadedMessageCacheIds = new HashSet<String>();

    private Set<String> missingMessageCacheIds = new HashSet<String>();

    private final MessageInputPresenter messageInputPresenter;

    private final GameStateProvider gameStateProvider;

    @Inject
    MessageLogPresenter(final EventBus eventBus, final MyView view, final MessageInputPresenter messageInputPresenter, final GameStateProvider gameStateProvider) {
        super(eventBus, view);
        getView().setUiHandlers(this);
        this.messageInputPresenter = messageInputPresenter;
        gameStateProvider.setMessageLogPresenter(this);
        this.gameStateProvider = gameStateProvider;
    }

    @Override
    protected void onBind() {
        super.onBind();
        addRegisteredHandler(MessageInLimboEvent.getType(), this);
        addRegisteredHandler(RemoveMessageEvent.getType(), this);
        addRegisteredHandler(ResyncGameStateRequestEvent.getType(), this);
        fetchGameStateMessages();
        fetchLatestMessage();
        Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

            @Override
            public boolean execute() {
                fetchLatestMessage();
                invalidateMessages();
                return true;
            }

        }, (int) CAConstants.SYNC_DELAY);
    }

    private final SuccessCallback<JsArray<JsonMessage>> gameStateCallback = new SuccessCallback<JsArray<JsonMessage>>() {

        @Override
        public void onSuccess(final JsArray<JsonMessage> msgAry) {
            for (int i = 0; i < msgAry.length(); i++) {
                addMessage(new MessageWrapper(msgAry.get(i)), false);
            }
            fireEvent(new GameStateUpdatedEvent());

        }
    };

    private long lastGameStateFetchTime = 0;

    private void fetchGameStateMessages() {
        if (System.currentTimeMillis() - lastGameStateFetchTime > 10000) {
            final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
            jsonp.requestObject(URL.encode(CAConstants.HOST + "/gamemessages"), gameStateCallback);
            lastGameStateFetchTime = System.currentTimeMillis();
        }

    }

    private boolean addMessage(final MessageWrapper message, final boolean inLimbo) {
        if (!inLimbo) {
            messageInputPresenter.markMessageArrived(message);
        }
        validateMessage(message);
        if (loadedMessageIds.add(message.getId())) {
            final GameState gameState = gameStateProvider.getGameState();
            final Set<MessageWrapper> fakeMessages = message.getFakeMessages(gameState.getWhiteGovernment(), gameState.getBlackGovernment());
            if (fakeMessages != null) {
                for (final MessageWrapper fakeMessage : fakeMessages) {
                    addMessage(fakeMessage, inLimbo);
                    validateMessage(fakeMessage);
                }
            }
            if (message.getMove() == null || fakeMessages == null) {
                getView().addMessage(message, inLimbo);
            }

            messages.add(message);
            if (message.isFromGameMaster()) {

                gameMasterMessages.add(message);
                return true;

            }
        }
        return false;

    }

    private void validateMessage(final MessageWrapper message) {
        if (!message.isFromGameMaster()) {
            return;
        }
        final Iterator<Entry<String, MessageWrapper>> it = awaitingValidationMessages.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<String, MessageWrapper> entry = it.next();
            if (entry.getKey().equals(message.getId())) {
                entry.getValue().setValid(true);
                it.remove();
                return;
            }
        }
        if (SyncedTime.get() - message.getCreated() < 8000) {
            awaitingValidationMessages.put(message.getId(), message);
        }

    }

    private void invalidateMessages() {
        for (final MessageWrapper message : awaitingValidationMessages.values()) {
            if (SyncedTime.get() - message.getCreated() > 10000) {
                message.setValid(false);
            }
        }
    }

    public String getGameId(int gamesAgo) {
        Collections.sort(gameMasterMessages);
        for (final MessageWrapper gameMasterMessage : gameMasterMessages) {
            final String gameId = gameMasterMessage.getNewGameId();
            if (gameId != null && SyncedTime.get() - 10000 > gameMasterMessage.getCreated()) {

                if (gamesAgo == 0) {
                    return gameId;
                }
                gamesAgo -= 1;
            }
        }
        return null;
    }

    private List<MessageWrapper> getMessagesForGame(final String gameId) {
        if (gameId != null) {
            Collections.sort(gameMasterMessages);
            for (final MessageWrapper gameMasterMessage : gameMasterMessages) {
                if (gameId.equals(gameMasterMessage.getNewGameId())) {
                    Collections.sort(messages);
                    final List<MessageWrapper> result = new ArrayList<MessageWrapper>();
                    result.add(gameMasterMessage);
                    for (int index = Collections.binarySearch(messages, gameMasterMessage) - 1; index >= 0; index--) {
                        if (messages.get(index).getNewGameId() == null) {
                            result.add(messages.get(index));
                        } else {
                            return result;
                        }

                    }
                    return result;
                }
            }
        }
        return new ArrayList<MessageWrapper>();
    }

    public List<MessageWrapper> getMessagesForGames(final String... gameIds) {
        final List<MessageWrapper> result = new ArrayList<MessageWrapper>();
        for (final String gameId : gameIds) {
            result.addAll(getMessagesForGame(gameId));
        }
        return result;
    }

    private void fetchLatestMessage() {
        fetchMessage(null);
    }

    private void fetchMessage(final String id) {
        final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
        if (JsessionUrlEncoder.cookiesEnabled()) {
            if (id == null) {
                jsonp.setPredeterminedId("fm");
            } else {
                jsonp.setPredeterminedId(id);
            }
        }
        jsonp.requestObject(URL.encode(CAConstants.HOST + "/message" + (id == null ? "" : "?id=" + id)), fetchMessageCallback);

    }

    final SuccessCallback<JsonMessageCache> fetchMessageCallback = new SuccessCallback<JsonMessageCache>() {

        @Override
        public void onSuccess(final JsonMessageCache messageCache) {

            if (loadedMessageCacheIds.add(messageCache.getId())) {
                if (loadedMessageCacheIds.size() > 4 && !loadedMessageCacheIds.contains(messageCache.getPreviousId()) && missingMessageCacheIds.add(messageCache.getPreviousId()) && Math.abs(SyncedTime.get() - messageCache.getCreated()) < 5000) {
                    fetchMessage(messageCache.getPreviousId());
                }
                boolean gameStateUpdated = false;
                for (final Message message : messageCache.getMessages()) {
                    gameStateUpdated = addMessage(new MessageWrapper(message), false) || gameStateUpdated;

                }
                if (Math.abs(SyncedTime.get() - messageCache.getCreated()) < 10000) {
                    fireEvent(new ReceivedMessageCacheEvent(messageCache));
                }
                if (gameStateUpdated) {
                    fireEvent(new GameStateUpdatedEvent());
                }
            }

        }
    };

    @Override
    public void prependEarlierMessages() {
        /*final MessageLink tail = MessageLink.getTail();
        if (tail != null && tail.getPreviousId() != null) {
        	fetchMessage(tail.getPreviousId(), 10);
        }*/

    }

    public List<MessageWrapper> getCurrentGameMessages() {
        return getMessagesForGame(getGameId(0));
    }

    @Override
    public void onMessageInLimbo(final MessageInLimboEvent event) {
        addMessage(event.getMessage(), true);

    }

    public List<MessageWrapper> getCurrentGameMasterMessages() {
        final List<MessageWrapper> result = new ArrayList<MessageWrapper>();
        for (final MessageWrapper message : getCurrentGameMessages()) {
            if (message.isFromGameMaster()) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public void onRemoveMessage(final RemoveMessageEvent event) {
        getView().removeLimboMessage(event.getMessageId());

    }

    @Override
    public void onResyncGameStateRequest(final ResyncGameStateRequestEvent event) {
        fetchGameStateMessages();

    }

}
