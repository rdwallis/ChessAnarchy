package com.wallissoftware.chessanarchy.client.game.chat.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.wallissoftware.chessanarchy.client.user.User;

public final class SendMessageResponse extends JavaScriptObject {

	protected SendMessageResponse() {
	};

	public native User getUser()/*-{
		return this.user;
	}-*/;

	public native JsonMessage getMessage() /*-{
		return this.message;
	}-*/;
}
