package com.wallissoftware.chessanarchy.client.game.gamestate.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class ResyncGameStateRequestEvent extends GwtEvent<ResyncGameStateRequestEvent.ResyncGameStateRequestHandler> {

    public interface ResyncGameStateRequestHandler extends EventHandler {
        void onResyncGameStateRequest(ResyncGameStateRequestEvent event);
    }

    public interface ResyncGameStateRequestHasHandlers extends HasHandlers {
        HandlerRegistration addResyncGameStateRequestHandler(ResyncGameStateRequestHandler handler);
    }

    private static Type<ResyncGameStateRequestHandler> TYPE = new Type<ResyncGameStateRequestHandler>();

    public static Type<ResyncGameStateRequestHandler> getType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final ResyncGameStateRequestHandler handler) {
        handler.onResyncGameStateRequest(this);
    }

    @Override
    public Type<ResyncGameStateRequestHandler> getAssociatedType() {
        return TYPE;
    }

}
