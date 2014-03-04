package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.chat.events.MessageInLimboEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.MessageInLimboEvent.MessageInLimboHandler;
import com.wallissoftware.chessanarchy.client.game.chat.events.ReceivedMessageCacheEvent;
import com.wallissoftware.chessanarchy.client.game.chat.messageinput.MessageInputPresenter;
import com.wallissoftware.chessanarchy.client.game.chat.model.JsonMessage;
import com.wallissoftware.chessanarchy.client.game.chat.model.JsonMessageCache;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.shared.message.Message;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public class MessageLogPresenter extends PresenterWidget<MessageLogPresenter.MyView> implements MessageLogUiHandlers, MessageInLimboHandler {
	public interface MyView extends View, HasUiHandlers<MessageLogUiHandlers> {

		void addMessage(MessageWrapper message);

		boolean isScrollBarShowing();
	}

	private List<MessageWrapper> messages = new ArrayList<MessageWrapper>();

	private List<MessageWrapper> gameMasterMessages = new ArrayList<MessageWrapper>();

	private Set<String> loadedMessageIds = new HashSet<String>();

	private final MessageInputPresenter messageInputPresenter;

	@Inject
	MessageLogPresenter(final EventBus eventBus, final MyView view, final MessageInputPresenter messageInputPresenter) {
		super(eventBus, view);
		getView().setUiHandlers(this);
		this.messageInputPresenter = messageInputPresenter;
	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(MessageInLimboEvent.getType(), this);
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

	private void fetchGameStateMessages() {
		final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode("/gamemessages"));
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(final Request request, final Throwable exception) {
				}

				@Override
				public void onResponseReceived(final Request request, final Response response) {
					if (200 == response.getStatusCode()) {
						processRawMessages(response.getText());
					} else {
					}

				}

				private void processRawMessages(final String json) {
					final JsArray<JsonMessage> msgAry = JsonMessage.aryFromJson(json);
					for (int i = 0; i < msgAry.length(); i++) {
						addMessage(new MessageWrapper(msgAry.get(i)), false);
					}
					fireEvent(new GameStateUpdatedEvent());

				}

			});
		} catch (final RequestException e) {
			// Couldn't connect to server
		}

	}

	private boolean addMessage(final MessageWrapper message, final boolean inLimbo) {
		if (!inLimbo) {
			messageInputPresenter.markMessageArrived(message);
		}
		if (loadedMessageIds.add(message.getId())) {
			getView().addMessage(message);
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
		fetchMessage(null, 1);
	}

	private void fetchMessage(final String id, final int messageCount) {
		final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode("/message" + (id == null ? "" : "?id=" + id)));
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(final Request request, final Throwable exception) {
				}

				@Override
				public void onResponseReceived(final Request request, final Response response) {
					if (200 == response.getStatusCode()) {
						processMessages("[" + response.getText() + "]", messageCount);
					} else {
					}

				}

				private void processMessages(final String messagesJson, final int messageCount) {
					final List<JsonMessageCache> messageCacheList = JsonMessageCache.fromJson(messagesJson);
					boolean gameStateUpdated = false;
					for (final JsonMessageCache messageCache : messageCacheList) {
						if (MessageLink.addMessageLink(messageCache)) {
							for (final Message message : messageCache.getMessages()) {
								gameStateUpdated = addMessage(new MessageWrapper(message), false) || gameStateUpdated;

							}
							if (messageCount >= 0) {
								if (messageCache.getPreviousId() != null) {
									fetchMessage(messageCache.getPreviousId(), messageCount - 1);
								}
							}
							if (Math.abs(SyncedTime.get() - messageCache.getCreated()) < 10000) {
								fireEvent(new ReceivedMessageCacheEvent(messageCache));
							}
						}

					}
					if (gameStateUpdated) {
						fireEvent(new GameStateUpdatedEvent());
					}
					if (!getView().isScrollBarShowing()) {
						prependEarlierMessages();
					}

				}

			});
		} catch (final RequestException e) {
			// Couldn't connect to server
		}

	}

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

}
