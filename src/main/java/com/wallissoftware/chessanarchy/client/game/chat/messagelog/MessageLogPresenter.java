package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import java.util.HashSet;
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
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.chat.model.Message;
import com.wallissoftware.chessanarchy.client.game.chat.model.MessageCache;

public class MessageLogPresenter extends PresenterWidget<MessageLogPresenter.MyView> {
	public interface MyView extends View {

		void addMessage(Message message);

		boolean isScrollBarShowing();
	}

	private final RequestBuilder fullRequestBuilder;

	private Set<String> loadedIds = new HashSet<String>();

	@Inject
	MessageLogPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
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
						processMessages(response.getText());
					} else {
					}

				}

			});
		} catch (final RequestException e) {
			// Couldn't connect to server
		}

	}

	private void processMessages(final String messagesJson) {
		final MessageCache messageCache = MessageCache.fromJson(messagesJson);
		if (loadedIds.add(messageCache.getId())) {
			for (int i = 0; i < messageCache.getMessages().length(); i++) {
				getView().addMessage(messageCache.getMessages().get(i));
			}
		}
		if (messageCache.getPreviousId() != null) {
			if (!loadedIds.contains(messageCache.getPreviousId()) && !getView().isScrollBarShowing()) {
				fetchMessages(messageCache.getPreviousId());
			}
		}

	}

	private void fetchMessages(final String previousId) {
		final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode("/message?id=" + previousId));
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(final Request request, final Throwable exception) {
				}

				@Override
				public void onResponseReceived(final Request request, final Response response) {
					if (200 == response.getStatusCode()) {
						processMessages(response.getText());
					} else {
					}

				}

			});
		} catch (final RequestException e) {
			// Couldn't connect to server
		}
	}

}
