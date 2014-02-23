package com.wallissoftware.chessanarchy.client.game.chat.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

public final class MessageCache extends JavaScriptObject {

	protected MessageCache() {
	};

	public native String getPreviousId() /*-{
		return this.previous;
	}-*/;

	public native String getId() /*-{
		return this.id;
	}-*/;

	public native JsArray<Message> getMessages() /*-{
		return this.messages;
	}-*/;

	public static MessageCache fromJson(final String json) {
		return JsonUtils.safeEval(json);
	}

	private final native String getNativeCreated() /*-{
		return this.created;
	}-*/;

	public long getCreated() {
		final String created = getNativeCreated();

		return Long.valueOf(created);
	}

}
