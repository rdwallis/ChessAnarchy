package com.wallissoftware.chessanarchy.client.game.chat.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.wallissoftware.chessanarchy.client.game.chat.model.MessageCache;

public class ReceivedMessageCacheEvent extends GwtEvent<ReceivedMessageCacheEvent.ReceivedMessageCacheHandler> {

	public interface ReceivedMessageCacheHandler extends EventHandler {
		void onReceivedMessageCache(ReceivedMessageCacheEvent event);
	}

	public interface ReceivedMessageCacheHasHandlers extends HasHandlers {
		HandlerRegistration addReceivedMessageCacheHandler(ReceivedMessageCacheHandler handler);
	}

	private static Type<ReceivedMessageCacheHandler> TYPE = new Type<ReceivedMessageCacheHandler>();

	public static Type<ReceivedMessageCacheHandler> getType() {
		return TYPE;
	}

	private final MessageCache messageCache;

	public ReceivedMessageCacheEvent(final MessageCache message) {
		this.messageCache = message;
	}

	@Override
	protected void dispatch(final ReceivedMessageCacheHandler handler) {
		handler.onReceivedMessageCache(this);
	}

	@Override
	public Type<ReceivedMessageCacheHandler> getAssociatedType() {
		return TYPE;
	}

	public MessageCache getMessageCache() {
		return messageCache;
	}

}
