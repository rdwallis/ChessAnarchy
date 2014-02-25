package com.wallissoftware.chessanarchy.client.game.chat.model;

import java.util.ArrayList;
import java.util.List;

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

	private native JsArray<Message> getNativeMessages() /*-{
		return this.messages;
	}-*/;

	public List<Message> getMessages() {
		final List<Message> result = new ArrayList<Message>();
		final JsArray<Message> jsArray = getNativeMessages();
		for (int i = 0; i < jsArray.length(); i++) {
			result.add(jsArray.get(i));
		}
		return result;
	}

	private static JsArray<MessageCache> nativeFromJson(final String json) {
		return JsonUtils.safeEval(json);
	}

	public static List<MessageCache> fromJson(final String json) {
		final List<MessageCache> result = new ArrayList<MessageCache>();
		final JsArray<MessageCache> jsArray = nativeFromJson(json);
		for (int i = 0; i < jsArray.length(); i++) {
			result.add(jsArray.get(i));
		}
		return result;
	}

	private final native String getNativeCreated() /*-{
		return this.created;
	}-*/;

	public long getCreated() {
		final String created = getNativeCreated();

		return Long.valueOf(created);
	}

}
