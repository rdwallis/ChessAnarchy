package com.wallissoftware.chessanarchy.client.game.chat.messageinput;

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

public class MessageInputPresenter extends PresenterWidget<MessageInputPresenter.MyView> implements MessageInputUiHandlers {
	public interface MyView extends View, HasUiHandlers<MessageInputUiHandlers> {
	}

	@Inject
	MessageInputPresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
		getView().setUiHandlers(this);
	}

	@Override
	public void sendMessage(final String message) {
		if (!message.isEmpty()) {
			final RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT, URL.encode("/message"));

			try {
				builder.sendRequest(message, new RequestCallback() {
					@Override
					public void onError(final Request request, final Throwable exception) {
					}

					@Override
					public void onResponseReceived(final Request request, final Response response) {
						if (200 == response.getStatusCode()) {
						} else {
						}

					}
				});
			} catch (final RequestException e) {
				// Couldn't connect to server
			}
		}
	}
}
