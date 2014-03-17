package com.wallissoftware.chessanarchy.client.game.board;

import com.google.gwt.user.client.ui.Widget;
import com.wallissoftware.chessanarchy.client.game.board.piece.PieceWidget;

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
    private final boolean capture;
    private PieceWidget promotion;

    public GhostAnimation(final double startTime, final Widget widget, final int startX, final int startY, final int endX, final int endY, final PieceWidget promotion) {
        this(false, false, startTime, widget, startX, startY, endX, endY, promotion);
    }

    public GhostAnimation(final boolean capture, final boolean fast, final double startTime, final Widget widget, final int startX, final int startY, final int endX, final int endY, final PieceWidget promotion) {
        this.capture = capture;
        this.fast = fast;
        this.widget = widget;
        this.startTime = startTime;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.promotion = promotion;
    }

    public Widget getWidget() {
        return widget;
    }

    public int getX(final double milli) {
        if (!isMovementStarted(milli)) {
            return startX;
        }
        if (isMovementComplete(milli)) {
            return endX;
        }
        return (int) easeInCubic(milli - startTime, startX, endX, MOVE_DURATION);
    }

    private boolean isMovementStarted(final double milli) {
        return milli < startTime;
    }

    public int getY(final double milli) {
        if (!isMovementStarted(milli)) {
            return startY;
        }
        if (isMovementComplete(milli)) {
            return endY;
        }
        return (int) easeInCubic(milli - startTime, startY, endY, MOVE_DURATION);
    }

    private double easeInCubic(double deltaTime, final double startValue, final double endValue, final double duration) {
        final double changeInValue = endValue - startValue;
        deltaTime /= duration;
        return changeInValue * deltaTime * deltaTime * deltaTime + startValue;
    }

    private double getMovePercent(final double milli) {
        final double delta = milli - startTime;
        return Math.max(0, Math.min(delta / getMoveDuration(), 1));
    }

    public boolean isMovementComplete(final double milli) {
        return getMovePercent(milli) == 1;
    }

    private double getOpacity(final double milli) {
        if (!isMovementComplete(milli)) {
            return 0.5;
        }
        if (isFinished(milli)) {
            return 0;
        }

        return easeInCubic(milli - (startTime + getMoveDuration()), 0.5, 0, FADE_DURATION);
    }

    public boolean isFinished(final double milli) {
        return (milli - startTime) > getMoveDuration() + FADE_DURATION;
    }

    private double getMoveDuration() {
        //return fast ? MOVE_DURATION / 2 : MOVE_DURATION;
        return MOVE_DURATION;
    }

    public void end() {
        startTime = 0;

    }

    public boolean isCapture() {
        return capture;
    }

    public void updateOpacity(final long milli) {
        getWidget().getElement().getStyle().setOpacity(getOpacity(milli));

    }

    public PieceWidget getPromotion() {
        return promotion;
    }

    public boolean isPromotion() {
        return promotion != null;
    }

}
