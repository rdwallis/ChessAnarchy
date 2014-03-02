package com.wallissoftware.chessanarchy.client.game.chat.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public class MessageInLimboEvent extends GwtEvent<MessageInLimboEvent.MessageInLimboHandler> {

	public interface MessageInLimboHandler extends EventHandler {
		void onMessageInLimbo(MessageInLimboEvent event);
	}

	public interface MessageInLimboHasHandlers extends HasHandlers {
		HandlerRegistration addMessageInLimboHandler(MessageInLimboHandler handler);
	}

	private static Type<MessageInLimboHandler> TYPE = new Type<MessageInLimboHandler>();

	public static Type<MessageInLimboHandler> getType() {
		return TYPE;
	}

	private final MessageWrapper message;

	public MessageInLimboEvent(final MessageWrapper message) {
		this.message = message;
	}

	@Override
	protected void dispatch(final MessageInLimboHandler handler) {
		handler.onMessageInLimbo(this);
	}

	@Override
	public Type<MessageInLimboHandler> getAssociatedType() {
		return TYPE;
	}

	public MessageWrapper getMessage() {
		return message;
	}

}
