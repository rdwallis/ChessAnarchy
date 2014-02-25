package com.wallissoftware.chessanarchy.client.game.chat.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.wallissoftware.chessanarchy.shared.game.Color;

public final class Message extends JavaScriptObject {

	protected Message() {
	};

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getUserId() /*-{
		return this.userId;
	}-*/;

	public final native String getMessage() /*-{
		return this.message;
	}-*/;

	private final native String getNativeCreated() /*-{
		return this.created;
	}-*/;

	private final native String getNativeColor() /*-{
		return this.color;
	}-*/;

	private Color getColor() {
		final String color = getNativeColor();
		if (color == null) {
			return null;
		}
		return Color.valueOf(color);
	}

	public long getCreated() {
		final String created = getNativeCreated();

		return Long.valueOf(created);
	}

	public static Message fromJson(final String json) {
		return JsonUtils.safeEval(json);
	}

	public boolean isNickChange() {
		return getMessage().toLowerCase().startsWith("/nick");

	}

	public boolean is3rdPerson() {
		return getMessage().toLowerCase().startsWith("/me");

	}

	public String getFormattedMessage() {
		if (isNickChange()) {
			String name = getMessage().substring(5);
			name = name.replace(" ", "");
			if (name.length() > 20) {
				name = name.substring(0, 20);
			}
			return getName() + " changed their nick to " + name;
		} else if (is3rdPerson()) {
			return getName() + " " + getMessage().substring(3);
		} else {
			return getMessage();
		}

	}

	public boolean isFromGameMaster() {
		return getUserId().equals("Game Master");
	}

	public String getNewGameId() {
		if (isFromGameMaster() && getMessage().startsWith("STARTING NEW GAME: ")) {
			return getMessage().replace("STARTING NEW GAME: ", "");
		}
		return null;
	}
}
