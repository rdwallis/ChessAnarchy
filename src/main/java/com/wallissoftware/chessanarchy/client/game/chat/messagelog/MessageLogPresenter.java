package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.wallissoftware.chessanarchy.client.game.chat.events.GameMasterMessageEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.ReceivedMessageCacheEvent;
import com.wallissoftware.chessanarchy.client.game.chat.model.JsonMessageCache;
import com.wallissoftware.chessanarchy.shared.message.Message;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public class MessageLogPresenter extends PresenterWidget<MessageLogPresenter.MyView> implements MessageLogUiHandlers {
	public interface MyView extends View, HasUiHandlers<MessageLogUiHandlers> {

		void addMessage(MessageWrapper message);

		boolean isScrollBarShowing();
	}

	private final RequestBuilder fullRequestBuilder;

	private Set<String> loadedMessageCacheIds = new HashSet<String>();

	private List<MessageWrapper> messages = new ArrayList<MessageWrapper>();

	private List<MessageWrapper> gameMasterMessages = new ArrayList<MessageWrapper>();

	private long earliestMessageCacheCreationTime = -1;
	private String earliestMessageCacheId = null;

	@Inject
	MessageLogPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
		getView().setUiHandlers(this);
		fullRequestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode("/message"));
	}

	@Override
	protected void onBind() {
		super.onBind();
		fetchLatestMessages();
		Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

			@Override
			public boolean execute() {
				fetchLatestMessages();
				return true;
			}

		}, 1019);
	}

	private void fetchLatestMessages() {

		try {
			fullRequestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(final Request request, final Throwable exception) {
				}

				@Override
				public void onResponseReceived(final Request request, final Response response) {
					if (200 == response.getStatusCode()) {
						processMessages("[" + response.getText() + "]", false);
					} else {
					}

				}

			});
		} catch (final RequestException e) {
			// Couldn't connect to server
		}

	}

	private void processMessages(final String messagesJson, final boolean toGameStart) {
		final List<JsonMessageCache> messageCacheList = JsonMessageCache.fromJson(messagesJson);
		for (final JsonMessageCache messageCache : messageCacheList) {
			if (loadedMessageCacheIds.add(messageCache.getId())) {
				for (final Message message : messageCache.getMessages()) {
					addMessage(new MessageWrapper(message));

				}
				if (!toGameStart) {
					if (messageCache.getPreviousId() != null && !loadedMessageCacheIds.contains(messageCache.getPreviousId())) {
						fetchMessage(messageCache.getPreviousId(), true);
					}
				}
				if (earliestMessageCacheCreationTime == -1 || messageCache.getCreated() < earliestMessageCacheCreationTime) {
					earliestMessageCacheId = messageCache.getPreviousId();
					earliestMessageCacheCreationTime = messageCache.getCreated();
				}
				fireEvent(new ReceivedMessageCacheEvent(messageCache));
			}

		}
		if (!getView().isScrollBarShowing()) {
			prependEarlierMessages();
		}

	}

	private void addMessage(final MessageWrapper message) {
		getView().addMessage(message);
		messages.add(message);
		if (message.isFromGameMaster()) {
			gameMasterMessages.add(message);
			if (System.currentTimeMillis() - message.getCreated() < 10000) {
				fireEvent(new GameMasterMessageEvent(message.getText()));
			}
		}

	}

	public String getGameId(int gamesAgo) {
		Collections.sort(gameMasterMessages);
		for (final MessageWrapper gameMasterMessage : gameMasterMessages) {
			final String gameId = gameMasterMessage.getNewGameId();
			if (gameId != null) {
				gamesAgo -= 1;
				if (gamesAgo == 0) {
					return gameId;
				}
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

	private void fetchMessage(final String id, final boolean toGameStart) {
		final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode("/message?id=" + id + (toGameStart ? "&tgs" : "")));
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(final Request request, final Throwable exception) {
				}

				@Override
				public void onResponseReceived(final Request request, final Response response) {
					if (200 == response.getStatusCode()) {
						processMessages(response.getText(), toGameStart);
					} else {
					}

				}

			});
		} catch (final RequestException e) {
			// Couldn't connect to server
		}

	}

	@Override
	public void prependEarlierMessages() {
		if (earliestMessageCacheId != null) {
			fetchMessage(earliestMessageCacheId, true);
		}

	}

}
