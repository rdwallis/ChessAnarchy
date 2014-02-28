package com.wallissoftware.chessanarchy.client.user;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.web.bindery.event.shared.EventBus;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.shared.CAConstants;
import com.wallissoftware.chessanarchy.shared.game.Color;

public final class User extends JavaScriptObject {

	protected User() {
	};

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getUserId() /*-{
		return this.userId;
	}-*/;

	private final native String getNativeBlackJoinTime() /*-{
		return this.black;
	}-*/;

	private final native String getNativeWhiteJoinTime() /*-{
		return this.white;
	}-*/;

	private Long getBlackJoinTime() {
		final String joinTime = getNativeBlackJoinTime();
		if (joinTime == null) {
			return null;
		} else {
			return Long.valueOf(joinTime);
		}

	}

	private Long getWhiteJoinTime() {
		final String joinTime = getNativeWhiteJoinTime();
		if (joinTime == null) {
			return null;
		} else {
			return Long.valueOf(joinTime);
		}

	}

	public native static User get() /*-{
		return $wnd.user;
	}-*/;

	private static native void setUser(JavaScriptObject user) /*-{
		$wnd.user = user;
	}-*/;

	public static void update(final EventBus eventBus, final String json) {
		if (json != null && json.length() > 10) {
			setUser(JsonUtils.safeEval(json));
			eventBus.fireEvent(new UserChangedEvent());
		}

	}

	public Long getColorJoinTime(final Color color) {
		if (color == null) {
			return null;
		}

		if (color == Color.WHITE) {
			return getWhiteJoinTime();
		} else {
			return getBlackJoinTime();
		}
	}

	public Color getColor() {
		return getColor(false);
	}

	public Color getColor(final boolean ignoreTeamWait) {
		final long teamWaitTime = ignoreTeamWait ? -1000 : CAConstants.JOIN_TEAM_WAIT;
		if (getWhiteJoinTime() != null && SyncedTime.get() - getWhiteJoinTime() > teamWaitTime) {
			return Color.WHITE;
		}
		if (getBlackJoinTime() != null && SyncedTime.get() - getBlackJoinTime() > teamWaitTime) {
			return Color.BLACK;
		}
		return null;
	}

	public void joinTeam(final EventBus eventBus, final Color color) {
		if (color == Color.WHITE) {
			setWhiteColorJoinTime(SyncedTime.get() + "");
		} else {
			setBlackColorJoinTime(SyncedTime.get() + "");
		}
		eventBus.fireEvent(new UserChangedEvent());

	}

	private native void setWhiteColorJoinTime(String joinTime) /*-{
		this.white = joinTime;

	}-*/;

	private native void setBlackColorJoinTime(String joinTime) /*-{
		this.black = joinTime;
	}-*/;
}
