package com.wallissoftware.chessanarchy.client.game.chat.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class GameMasterMessageEvent extends GwtEvent<GameMasterMessageEvent.GameMasterMessageHandler> {

	public interface GameMasterMessageHandler extends EventHandler {
		void onGameMasterMessage(GameMasterMessageEvent event);
	}

	public interface GameMasterMessageHasHandlers extends HasHandlers {
		HandlerRegistration addGameMasterMessageHandler(GameMasterMessageHandler handler);
	}

	private static Type<GameMasterMessageHandler> TYPE = new Type<GameMasterMessageHandler>();

	public static Type<GameMasterMessageHandler> getType() {
		return TYPE;
	}

	private final String message;

	public GameMasterMessageEvent(final String message) {
		this.message = message;
	}

	@Override
	protected void dispatch(final GameMasterMessageHandler handler) {
		handler.onGameMasterMessage(this);
	}

	@Override
	public Type<GameMasterMessageHandler> getAssociatedType() {
		return TYPE;
	}

	public String getMessage() {
		return message;
	}

}
