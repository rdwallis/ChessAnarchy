package com.wallissoftware.chessanarchy.client.game.chat.messageinput;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.dispatch.JsessionUrlEncoder;
import com.wallissoftware.chessanarchy.client.dispatch.SuccessCallback;
import com.wallissoftware.chessanarchy.client.game.chat.events.MessageInLimboEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.RemoveMessageEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent.SendMessageHandler;
import com.wallissoftware.chessanarchy.client.game.chat.model.SendMessageResponse;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.shared.CAConstants;
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
				sendMessage(next.getValue().getText(), false);
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
	public void sendMessage(final String message, final boolean resendOnFail) {
		fireEvent(new SendMessageEvent(message, resendOnFail));
	}

	@Override
	public void onSendMessage(final SendMessageEvent event) {
		final String message = event.getMessage();
		if (!message.isEmpty()) {
			final JsonpRequestBuilder jsonp = new JsonpRequestBuilder();
			jsonp.requestObject(JsessionUrlEncoder.encode(CAConstants.HOST + "/send?msg=" + message), new SuccessCallback<SendMessageResponse>() {

				@Override
				public void onSuccess(final SendMessageResponse response) {
					if (response.getUser() != null) {
						User.update(getEventBus(), response.getUser());
					}
					if (response.getMessage() != null) {
						final MessageWrapper msg = new MessageWrapper(response.getMessage());
						if (gameStateProvider.getGameState().swapColors()) {
							msg.swapColor();
						}
						if (event.resendOnFail()) {
							addToMessagesCheckQueue(msg);
						}
					}

				}
			});

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
