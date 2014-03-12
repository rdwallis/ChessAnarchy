package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.shared.CAConstants;
import com.wallissoftware.chessanarchy.shared.message.Message;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public class MessageLogPresenter extends PresenterWidget<MessageLogPresenter.MyView> implements MessageLogUiHandlers, MessageInLimboHandler, RemoveMessageHandler {
	public interface MyView extends View, HasUiHandlers<MessageLogUiHandlers> {

		boolean isScrollBarShowing();

		void addMessage(MessageWrapper message, boolean immediately);

		void removeLimboMessage(String messageId);
	}

	private List<MessageWrapper> messages = new ArrayList<MessageWrapper>();

	private List<MessageWrapper> gameMasterMessages = new ArrayList<MessageWrapper>();

	private Set<String> loadedMessageIds = new HashSet<String>();

	private final MessageInputPresenter messageInputPresenter;

	@Inject
	MessageLogPresenter(final EventBus eventBus, final MyView view, final MessageInputPresenter messageInputPresenter, final GameStateProvider gameStateProvider) {
		super(eventBus, view);
		getView().setUiHandlers(this);
		this.messageInputPresenter = messageInputPresenter;
		gameStateProvider.setMessageLogPresenter(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(MessageInLimboEvent.getType(), this);
		addRegisteredHandler(RemoveMessageEvent.getType(), this);
		fetchGameStateMessages();
		fetchLatestMessage();
		Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

			@Override
			public boolean execute() {
				fetchLatestMessage();
				return true;
			}

		}, 2003);
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

	private void fetchGameStateMessages() {
		final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
		jsonp.requestObject(URL.encode(CAConstants.HOST + "/gamemessages"), gameStateCallback);

	}

	private boolean addMessage(final MessageWrapper message, final boolean inLimbo) {
		if (!inLimbo) {
			messageInputPresenter.markMessageArrived(message);
		}
		if (loadedMessageIds.add(message.getId())) {
			getView().addMessage(message, inLimbo);
			messages.add(message);
			if (message.isFromGameMaster()) {
				gameMasterMessages.add(message);
				return true;

			}
		}
		return false;

	}

	public String getGameId(int gamesAgo) {
		Collections.sort(gameMasterMessages);
		for (final MessageWrapper gameMasterMessage : gameMasterMessages) {
			final String gameId = gameMasterMessage.getNewGameId();
			if (gameId != null) {

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
			jsonp.setPredeterminedId("fm");
		}
		jsonp.requestObject(URL.encode(CAConstants.HOST + "/message" + (id == null ? "" : "?id=" + id)), fetchMessageCallback);

	}

	final SuccessCallback<JsonMessageCache> fetchMessageCallback = new SuccessCallback<JsonMessageCache>() {

		@Override
		public void onSuccess(final JsonMessageCache messageCache) {

			if (MessageLink.addMessageLink(messageCache)) {
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
			fireEvent(new GameStateUpdatedEvent());

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

}
