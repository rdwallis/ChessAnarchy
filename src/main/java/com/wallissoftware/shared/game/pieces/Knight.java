package com.wallissoftware.shared.game.pieces;

import java.util.HashSet;
import java.util.Set;

import com.wallissoftware.shared.game.Color;
import com.wallissoftware.shared.game.Move;
import com.wallissoftware.shared.game.Square;

public class Knight extends Piece {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private Knight() {
	};

	public Knight(final Color color) {
		super(color);
	}

	Knight(final Pawn promotedFrom) {
		super(promotedFrom);
	}

	@Override
	public Set<Move> getLegalMoves(final Piece[][] board) {
		final Set<Move> legalMoves = new HashSet<Move>();

		final Move move = getRelativePositionMove(board, 1, 2);
		if (move != null) {
			legalMoves.add(move);
		}
		return legalMoves;
	}

	private Move getRelativePositionMove(final Piece[][] board, final int rankOffset, final int fileOffset) {
		final int rank = getPosition().getRank() + rankOffset;
		final int file = getPosition().getFile() + fileOffset;
		if (0 > rank || rank > 7) {
			return null;
		}
		if (0 > file || file > 7) {
			return null;
		}

		if (board[rank][file] == null || board[rank][file].getColor() != getColor()) {
			return new Move(getPosition(), new Square(rank, file));
		} else {
			return null;
		}
	}

}
