package com.wallissoftware.chessanarchy.client.game.chat.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.message.Message;

public final class JsonMessage extends JavaScriptObject implements Message {

	protected JsonMessage() {
	};

	@Override
	public final native String getName() /*-{
		return this.name;
	}-*/;

	@Override
	public final native String getUserId() /*-{
		return this.userId;
	}-*/;

	@Override
	public final native String getText() /*-{
		return this.message;
	}-*/;

	private final native String getNativeCreated() /*-{
		return this.created;
	}-*/;

	private final native String getNativeColor() /*-{
		return this.color;
	}-*/;

	@Override
	public Color getColor() {
		final String color = getNativeColor();
		if (color == null) {
			return null;
		}
		return Color.valueOf(color);
	}

	@Override
	public long getCreated() {
		final String created = getNativeCreated();

		return Long.valueOf(created);
	}

	public static JsonMessage fromJson(final String json) {
		return JsonUtils.safeEval(json);
	}

	public static native JsonMessage wrap(final JavaScriptObject object) /*-{
		return object;
	}-*/;

	@Override
	public final native String getId() /*-{
		return this.id;
	}-*/;

	public static JsArray<JsonMessage> aryFromJson(final String json) {
		return JsonUtils.safeEval(json);
	};

}
