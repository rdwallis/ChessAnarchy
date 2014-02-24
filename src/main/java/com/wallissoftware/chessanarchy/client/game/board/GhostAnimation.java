package com.wallissoftware.chessanarchy.client.game.board;

import com.google.gwt.user.client.ui.Image;

public class GhostAnimation {

	private final static double MOVE_DURATION = 2000;
	private final static double FADE_DURATION = 1000;

	private final Image image;
	private final double startTime;

	private final int startX;
	private final int startY;
	private final int endX;
	private final int endY;

	public GhostAnimation(final double startTime, final Image image, final int startX, final int startY, final int endX, final int endY) {
		super();
		this.image = image;
		this.startTime = startTime;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;

		image.getElement().getStyle().setOpacity(0.5);
	}

	public Image getImage() {
		return image;
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
		return Math.max(0, Math.min(delta / MOVE_DURATION, 1));
	}

	public boolean isMovementComplete(final double milli) {
		return getMovePercent(milli) == 1;
	}

	public double getOpacity(final double milli) {
		if (!isMovementComplete(milli)) {
			return 0.5;
		}

		final double delta = (milli - startTime) - MOVE_DURATION;
		final double percent = Math.min(delta / FADE_DURATION, 1);
		return 0.5 - (percent / 2.0);
	}

	public boolean isFinished(final double milli) {
		return getOpacity(milli) == 0;
	}

}
