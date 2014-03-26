package com.wallissoftware.chessanarchy.client.user;

import com.google.gwt.core.client.JavaScriptObject;
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

    private final native double getBlackJoinTime() /*-{
		return this.black || -1;
    }-*/;

    private final native double getWhiteJoinTime() /*-{
		return this.white || -1;
    }-*/;

    public final native String getSessionId() /*-{
		return this.sessionId;
    }-*/;

    public native static User get() /*-{
		return $wnd.chessAnarchy.user;
    }-*/;

    private static native void setUser(JavaScriptObject usr) /*-{
		$wnd.chessAnarchy.user = usr;
    }-*/;

    public static void update(final EventBus eventBus, final JavaScriptObject user) {
        setUser(user);
        eventBus.fireEvent(new UserChangedEvent());
    }

    public double getColorJoinTime(final Color color) {
        if (color == null) {
            return -1;
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
        final double teamWaitTime = ignoreTeamWait ? -100000 : CAConstants.JOIN_TEAM_WAIT;
        if (getWhiteJoinTime() != -1 && SyncedTime.get() - getWhiteJoinTime() > teamWaitTime) {
            return Color.WHITE;
        }
        if (getBlackJoinTime() != -1 && SyncedTime.get() - getBlackJoinTime() > teamWaitTime) {
            return Color.BLACK;
        }
        return Color.WHITE;
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
