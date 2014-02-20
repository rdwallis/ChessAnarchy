package com.wallissoftware.shared.game.pieces;

import java.util.Set;

import com.wallissoftware.shared.dto.Dto;
import com.wallissoftware.shared.game.Color;
import com.wallissoftware.shared.game.Move;
import com.wallissoftware.shared.game.Square;

public abstract class Piece implements Dto {
	private static final long serialVersionUID = 1L;

	private Color color;
	private Square position;
	private int moveCount;

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
	}

	public Color getColor() {
		return color;
	}

	public Square getPosition() {
		return position;
	}

	public void setPosition(final Square position) {
		setPosition(position, true);

	}

	protected int getMoveCount() {
		return moveCount;
	}

	public abstract Set<Move> getLegalMoves(Piece[][] board);

	public void setPosition(final Square position, final boolean countMove) {
		if (countMove && this.position != null) {
			moveCount += 1;
		}
		this.position = position;

	}

	public void decrememntMoveCount() {
		moveCount -= 1;

	}

}
