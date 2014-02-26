package com.wallissoftware.chessanarchy.client.user;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class UserChangedEvent extends GwtEvent<UserChangedEvent.UserChangedHandler> {

	public interface UserChangedHandler extends EventHandler {
		void onUserChanged(UserChangedEvent event);
	}

	public interface UserChangedHasHandlers extends HasHandlers {
		HandlerRegistration addUserChangedHandler(UserChangedHandler handler);
	}

	private static Type<UserChangedHandler> TYPE = new Type<UserChangedHandler>();

	public static Type<UserChangedHandler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(final UserChangedHandler handler) {
		handler.onUserChanged(this);
	}

	@Override
	public Type<UserChangedHandler> getAssociatedType() {
		return TYPE;
	}

}
