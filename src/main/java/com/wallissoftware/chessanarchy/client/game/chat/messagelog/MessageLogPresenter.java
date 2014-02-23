package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.wallissoftware.chessanarchy.client.game.chat.model.Message;
import com.wallissoftware.chessanarchy.client.game.chat.model.MessageCache;

public class MessageLogPresenter extends PresenterWidget<MessageLogPresenter.MyView> implements MessageLogUiHandlers {
	public interface MyView extends View, HasUiHandlers<MessageLogUiHandlers> {

		void addMessage(Message message);

		boolean isScrollBarShowing();
	}

	private final RequestBuilder fullRequestBuilder;

	private List<MessageLink> messageLinks = new ArrayList<MessageLink>();

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
						processMessages(response.getText(), 0);
					} else {
					}

				}

			});
		} catch (final RequestException e) {
			// Couldn't connect to server
		}

	}

	private void processMessages(final String messagesJson, final int ancestorCount) {
		final MessageCache messageCache = MessageCache.fromJson(messagesJson);
		final MessageLink link = new MessageLink(messageCache);
		if (!messageLinks.contains(link)) {
			messageLinks.add(link);
			for (int i = 0; i < messageCache.getMessages().length(); i++) {
				getView().addMessage(messageCache.getMessages().get(i));
			}
		}
		if (ancestorCount > 0 || !getView().isScrollBarShowing() || Math.abs(messageCache.getCreated() - System.currentTimeMillis()) < 10000) {
			fetchMessage(messageCache.getPreviousId(), ancestorCount - 1);
		}

	}

	private void fetchMessage(final String id, final int ancestorCount) {
		if (!messageHasBeenLoaded(id)) {
			final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode("/message?id=" + id));
			try {
				requestBuilder.sendRequest(null, new RequestCallback() {
					@Override
					public void onError(final Request request, final Throwable exception) {
					}

					@Override
					public void onResponseReceived(final Request request, final Response response) {
						if (200 == response.getStatusCode()) {
							processMessages(response.getText(), ancestorCount);
						} else {
						}

					}

				});
			} catch (final RequestException e) {
				// Couldn't connect to server
			}
		}
	}

	private boolean messageHasBeenLoaded(final String id) {
		if (id == null) {
			return true;
		}
		for (final MessageLink messageLink : messageLinks) {
			if (messageLink.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	private String getLatestLinkNotLoaded() {
		Collections.sort(messageLinks);
		if (!messageLinks.isEmpty()) {
			String previousId = messageLinks.get(0).getPreviousId();
			String lastNotLoaded = previousId;
			for (final MessageLink messageLink : messageLinks) {
				if (previousId != null && !previousId.equals(messageLink.getPreviousId())) {
					lastNotLoaded = messageLink.getPreviousId();
				}
				previousId = messageLink.getPreviousId();
			}
			return lastNotLoaded;
		}
		return null;
	}

	@Override
	public void prependEarlierMessages() {
		fetchMessage(getLatestLinkNotLoaded(), 10);

	}

}
