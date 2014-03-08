package com.wallissoftware.chessanarchy.client.game.chat.messageinput;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.chat.events.MessageInLimboEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.RemoveMessageEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent.SendMessageHandler;
import com.wallissoftware.chessanarchy.client.game.chat.model.JsonMessage;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.shared.message.Message;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public class MessageInputPresenter extends PresenterWidget<MessageInputPresenter.MyView> implements MessageInputUiHandlers, SendMessageHandler {
	public interface MyView extends View, HasUiHandlers<MessageInputUiHandlers> {

	}

	private final Map<Long, Message> waitingMessages = new HashMap<Long, Message>();

	private final Logger logger = Logger.getLogger(MessageInputPresenter.class.getName());

	private final GameStateProvider gameStateProvider;

	@Inject
	MessageInputPresenter(final EventBus eventBus, final MyView view, final GameStateProvider gameStateProvider) {
		super(eventBus, view);
		this.gameStateProvider = gameStateProvider;
		getView().setUiHandlers(this);
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

			@Override
			public boolean execute() {
				resendOldMessages();
				return true;
			}

		}, 1019);
	}

	private void resendOldMessages() {
		final Iterator<Entry<Long, Message>> it = waitingMessages.entrySet().iterator();
		while (it.hasNext()) {
			final Entry<Long, Message> next = it.next();
			if (System.currentTimeMillis() - next.getKey() > 5000) {
				fireEvent(new RemoveMessageEvent(next.getValue().getId()));
				sendMessage(next.getValue().getText());
				logger.info("Resending message: " + next.getValue().getText());
				it.remove();

			}
		}

	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(SendMessageEvent.getType(), this);
	}

	@Override
	public void sendMessage(final String message) {
		fireEvent(new SendMessageEvent(message));
	}

	@Override
	public void onSendMessage(final SendMessageEvent event) {
		final String message = event.getMessage();
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
							final JSONObject result = JSONParser.parseStrict(response.getText()).isObject();
							if (result.containsKey("user")) {
								User.update(getEventBus(), result.get("user").isObject().getJavaScriptObject());
							}
							if (result.containsKey("message")) {
								final MessageWrapper msg = new MessageWrapper(JsonMessage.wrap(result.get("message").isObject().getJavaScriptObject()));
								if (gameStateProvider.getGameState().swapColors()) {
									msg.swapColor();
								}
								addToMessagesCheckQueue(msg);

							}

						} else {
						}

					}
				});
			} catch (final RequestException e) {
				// Couldn't connect to server
			}
		}

	}

	private void addToMessagesCheckQueue(final MessageWrapper message) {
		if (User.get().getUserId().equals(message.getUserId())) {
			logger.info("Adding message to waiting queue: " + message.getText());
			waitingMessages.put(System.currentTimeMillis(), message);
			fireEvent(new MessageInLimboEvent(message));
		}
	}

	public void markMessageArrived(final Message message) {
		if (User.get().getUserId().equals(message.getUserId())) {
			logger.info("Removing message from waiting queue: " + message.getText());
			final Iterator<Entry<Long, Message>> it = waitingMessages.entrySet().iterator();
			while (it.hasNext()) {
				if (message.getId().equals(it.next().getValue().getId())) {
					it.remove();
				}
			}
		}
	}

}
