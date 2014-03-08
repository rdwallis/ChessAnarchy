package com.wallissoftware.chessanarchy.client.game.chat.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class RemoveMessageEvent extends GwtEvent<RemoveMessageEvent.RemoveMessageHandler> {

	public interface RemoveMessageHandler extends EventHandler {
		void onRemoveMessage(RemoveMessageEvent event);
	}

	public interface RemoveMessageHasHandlers extends HasHandlers {
		HandlerRegistration addRemoveMessageHandler(RemoveMessageHandler handler);
	}

	private static Type<RemoveMessageHandler> TYPE = new Type<RemoveMessageHandler>();

	public static Type<RemoveMessageHandler> getType() {
		return TYPE;
	}

	private final String messageId;

	public RemoveMessageEvent(final String messageId) {
		this.messageId = messageId;
	}

	@Override
	protected void dispatch(final RemoveMessageHandler handler) {
		handler.onRemoveMessage(this);
	}

	@Override
	public Type<RemoveMessageHandler> getAssociatedType() {
		return TYPE;
	}

	public String getMessageId() {
		return messageId;
	}

}
