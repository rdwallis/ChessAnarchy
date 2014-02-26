package com.wallissoftware.chessanarchy.client.game.chat.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;

public final class JsonMessageCache extends JavaScriptObject {

	protected JsonMessageCache() {
	};

	public native String getPreviousId() /*-{
		return this.previous;
	}-*/;

	public native String getId() /*-{
		return this.id;
	}-*/;

	private native JsArray<JsonMessage> getNativeMessages() /*-{
		return this.messages;
	}-*/;

	public List<JsonMessage> getMessages() {
		final List<JsonMessage> result = new ArrayList<JsonMessage>();
		final JsArray<JsonMessage> jsArray = getNativeMessages();
		for (int i = 0; i < jsArray.length(); i++) {
			result.add(jsArray.get(i));
		}
		return result;
	}

	private static JsArray<JsonMessageCache> nativeFromJson(final String json) {
		return JsonUtils.safeEval(json);
	}

	public static List<JsonMessageCache> fromJson(final String json) {
		final List<JsonMessageCache> result = new ArrayList<JsonMessageCache>();
		final JsArray<JsonMessageCache> jsArray = nativeFromJson(json);
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
