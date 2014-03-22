package com.wallissoftware.chessanarchy.client.game.board;

import com.google.gwt.user.client.ui.Widget;
import com.wallissoftware.chessanarchy.client.game.board.piece.PieceWidget;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;

public class GhostAnimation {

    private final static double MOVE_DURATION = 1500;
    private final static double FADE_DURATION = 800;

    private final PieceWidget piece;
    private double startTime;

    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    private final boolean capture;
    private char promotion;

    private final long syncDiff;

    public GhostAnimation(final double startTime, final PieceWidget piece, final int startX, final int startY, final int endX, final int endY, final char promotion) {
        this(false, startTime, piece, startX, startY, endX, endY, promotion);
    }

    public GhostAnimation(final boolean capture, final double startTime, final PieceWidget piece, final int startX, final int startY, final int endX, final int endY, final char promotion) {
        this.syncDiff = SyncedTime.getDiff();
        this.capture = capture;
        this.piece = piece;
        if (!capture) {
            piece.getElement().getStyle().setZIndex(2);
        } else {
            piece.getElement().getStyle().setZIndex(1);
        }
        this.startTime = Math.max(SyncedTime.get(), startTime);
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.promotion = promotion;
    }

    public Widget getWidget() {
        return piece;
    }

    public int getX(final long timestamp) {
        if (!isMovementStarted(timestamp)) {
            return startX;
        }
        if (isMovementComplete(timestamp)) {
            return endX;
        }
        return (int) easeInCubic(getSyncedTime(timestamp) - startTime, startX, endX, MOVE_DURATION);
    }

    private long getSyncedTime(final long timestamp) {
        return timestamp - syncDiff;
    }

    private boolean isMovementStarted(final long timestamp) {
        return getSyncedTime(timestamp) > startTime;
    }

    public int getY(final long timestamp) {
        if (!isMovementStarted(timestamp)) {
            return startY;
        }
        if (isMovementComplete(timestamp)) {
            return endY;
        }
        return (int) easeInCubic(getSyncedTime(timestamp) - startTime, startY, endY, MOVE_DURATION);
    }

    private double easeInCubic(double deltaTime, final double startValue, final double endValue, final double duration) {
        final double changeInValue = endValue - startValue;
        deltaTime /= duration;
        return changeInValue * deltaTime * deltaTime * deltaTime + startValue;
    }

    private double getMovePercent(final long timestamp) {
        final double delta = getSyncedTime(timestamp) - startTime;
        return Math.max(0, Math.min(delta / getMoveDuration(), 1));
    }

    public boolean isMovementComplete(final long timestamp) {
        return getMovePercent(timestamp) == 1 && doPromotion();
    }

    private boolean doPromotion() {
        if (isPromotion()) {
            piece.setKind(promotion);
        }
        return true;
    }

    private double getOpacity(final long timestamp) {
        if (!isMovementComplete(timestamp)) {
            return 0.5;
        }
        if (isFinished(timestamp)) {
            return 0;
        }

        return easeInCubic(getSyncedTime(timestamp) - (startTime + getMoveDuration()), 0.5, 0, FADE_DURATION);
    }

    public boolean isFinished(final long timestamp) {
        return (getSyncedTime(timestamp) - startTime) > getMoveDuration() + FADE_DURATION;
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

    public void updateOpacity(final long timestamp) {
        getWidget().getElement().getStyle().setOpacity(getOpacity(timestamp));

    }

    private boolean isPromotion() {
        return promotion != 'x';
    }

}
