package com.wallissoftware.chessanarchy.shared.game.pieces;

import java.util.HashSet;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.dto.Dto;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;

public abstract class Piece implements Dto {
	private static final long serialVersionUID = 1L;

	private Color color;
	private Square position;
	private int moveCount;
	private boolean captured = false;

	private Set<PieceMoveHandler> pieceMoveHandlers = new HashSet<PieceMoveHandler>();

	Piece() {
	};

	private Piece(final Color color, final int moveCount) {
		this.color = color;
		this.moveCount = moveCount;
	}

	public Piece(final Color color) {
		this(color, 0);
	}

	Piece(final Pawn promotedFrom) {
		this(promotedFrom.getColor(), promotedFrom.getMoveCount());
		setPosition(promotedFrom.getPosition(), false);
		promotedFrom.setPromotedTo(this);
	}

	public Piece getPromotedTo() {
		return null;
	}

	public void addPieceMoveHandler(final PieceMoveHandler pieceMoveHandler) {
		pieceMoveHandlers.add(pieceMoveHandler);
	}

	public Color getColor() {
		return color;
	}

	public Square getPosition() {
		return position;
	}

	public void setPosition(final Square position, final boolean fireEvents) {
		setPosition(position, fireEvents, true);

	}

	public void capture() {
		captured = true;
		notfiyHandlersOfPosition();
	}

	public boolean isCaptured() {
		return captured;
	}

	protected int getMoveCount() {
		return moveCount;
	}

	public abstract Set<Move> getLegalMoves(Piece[][] board);

	public void setPosition(final Square position, final boolean fireEvents, final boolean countMove) {
		if (countMove && this.position != null) {
			moveCount += 1;
		}
		this.position = position;
		if (fireEvents) {
			notfiyHandlersOfPosition();
		}

	}

	public void notfiyHandlersOfPosition() {

		for (final PieceMoveHandler handler : pieceMoveHandlers) {
			handler.afterMove();
		}

	}

	public void decrememntMoveCount() {
		moveCount -= 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + moveCount;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Piece other = (Piece) obj;
		if (color != other.color)
			return false;
		if (moveCount != other.moveCount)
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return color + " on " + position;
	}

}
