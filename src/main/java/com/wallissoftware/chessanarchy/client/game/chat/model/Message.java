package com.wallissoftware.chessanarchy.client.game.chat.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;

public final class Message extends JavaScriptObject {

	protected Message() {
	};

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getMessage() /*-{
		return this.message;
	}-*/;

	public final native double getCreated() /*-{
		return this.created;
	}-*/;

	public static Message fromJson(final String json) {
		return JsonUtils.safeEval(json);
	}

}
