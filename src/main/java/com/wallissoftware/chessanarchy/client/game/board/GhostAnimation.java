package com.wallissoftware.chessanarchy.client.game.board;

import com.google.gwt.user.client.ui.Widget;

public class GhostAnimation {

	private final static double MOVE_DURATION = 2000;
	private final static double FADE_DURATION = 1000;

	private final Widget widget;
	private double startTime;

	private final int startX;
	private final int startY;
	private final int endX;
	private final int endY;
	private final boolean fast;

	public GhostAnimation(final double startTime, final Widget widget, final int startX, final int startY, final int endX, final int endY) {
		this(false, startTime, widget, startX, startY, endX, endY);
	}

	public GhostAnimation(final boolean fast, final double startTime, final Widget widget, final int startX, final int startY, final int endX, final int endY) {
		this.fast = fast;
		this.widget = widget;
		this.startTime = startTime;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	public Widget getWidget() {
		return widget;
	}

	public int getX(final double milli) {
		final int distance = endX - startX;
		return (int) (distance * getMovePercent(milli)) + startX;
	}

	public int getY(final double milli) {
		final int distance = endY - startY;
		return (int) (distance * getMovePercent(milli)) + startY;
	}

	private double getMovePercent(final double milli) {
		final double delta = milli - startTime;
		return Math.max(0, Math.min(delta / getMoveDuration(), 1));
	}

	public boolean isMovementComplete(final double milli) {
		return getMovePercent(milli) == 1;
	}

	public double getOpacity(final double milli) {
		if (!isMovementComplete(milli)) {
			return 0.5;
		}

		final double delta = (milli - startTime) - getMoveDuration();
		final double percent = Math.min(delta / FADE_DURATION, 1);
		return 0.5 - (percent / 2.0);
	}

	public boolean isFinished(final double milli) {
		return getOpacity(milli) == 0;
	}

	private double getMoveDuration() {
		return fast ? MOVE_DURATION / 2 : MOVE_DURATION;
	}

	public void end() {
		startTime = 0;

	}

}
