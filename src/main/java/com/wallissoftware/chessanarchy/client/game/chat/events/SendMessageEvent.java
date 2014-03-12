package com.wallissoftware.chessanarchy.client.game.chat.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class SendMessageEvent extends GwtEvent<SendMessageEvent.SendMessageHandler> {

	public interface SendMessageHandler extends EventHandler {
		void onSendMessage(SendMessageEvent event);
	}

	public interface SendMessageHasHandlers extends HasHandlers {
		HandlerRegistration addSendMessageHandler(SendMessageHandler handler);
	}

	private static Type<SendMessageHandler> TYPE = new Type<SendMessageHandler>();

	public static Type<SendMessageHandler> getType() {
		return TYPE;
	}

	private final String message;
	private final boolean resendOnFail;

	public SendMessageEvent(final String message) {
		this(message, true);
	}

	public SendMessageEvent(final String message, final boolean resendOnFail) {
		this.message = message;
		this.resendOnFail = resendOnFail;
	}

	@Override
	protected void dispatch(final SendMessageHandler handler) {
		handler.onSendMessage(this);
	}

	@Override
	public Type<SendMessageHandler> getAssociatedType() {
		return TYPE;
	}

	public String getMessage() {
		return message;
	}

	public boolean resendOnFail() {
		return resendOnFail;
	}

}
