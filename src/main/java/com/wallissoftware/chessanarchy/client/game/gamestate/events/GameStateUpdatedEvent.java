package com.wallissoftware.chessanarchy.client.game.gamestate.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class GameStateUpdatedEvent extends GwtEvent<GameStateUpdatedEvent.GameStateUpdatedHandler> {

	public interface GameStateUpdatedHandler extends EventHandler {
		void onGameStateUpdated(GameStateUpdatedEvent event);
	}

	public interface GameStateUpdatedHasHandlers extends HasHandlers {
		HandlerRegistration addGameStateUpdatedHandler(GameStateUpdatedHandler handler);
	}

	private static Type<GameStateUpdatedHandler> TYPE = new Type<GameStateUpdatedHandler>();

	public static Type<GameStateUpdatedHandler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final GameStateUpdatedHandler handler) {
		handler.onGameStateUpdated(this);
	}

	@Override
	public Type<GameStateUpdatedHandler> getAssociatedType() {
		return TYPE;
	}

}
