package com.wallissoftware.chessanarchy.shared.game.pieces;

import java.util.HashSet;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;

public class Knight extends Piece {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private Knight() {
	};

	public Knight(final Color color) {
		super(color);
	}

	public Knight(final Pawn promotedFrom) {
		super(promotedFrom);
	}

	@Override
	public Set<Move> getLegalMoves(final Piece[][] board) {
		final Set<Move> legalMoves = new HashSet<Move>();

		Move move = getRelativePositionMove(board, 1, 2);
		if (move != null) {
			legalMoves.add(move);
		}

		move = getRelativePositionMove(board, -1, 2);
		if (move != null) {
			legalMoves.add(move);
		}

		move = getRelativePositionMove(board, 1, -2);
		if (move != null) {
			legalMoves.add(move);
		}

		move = getRelativePositionMove(board, -1, -2);
		if (move != null) {
			legalMoves.add(move);
		}

		move = getRelativePositionMove(board, 2, 1);
		if (move != null) {
			legalMoves.add(move);
		}

		move = getRelativePositionMove(board, -2, 1);
		if (move != null) {
			legalMoves.add(move);
		}

		move = getRelativePositionMove(board, 2, -1);
		if (move != null) {
			legalMoves.add(move);
		}

		move = getRelativePositionMove(board, -2, -1);
		if (move != null) {
			legalMoves.add(move);
		}

		return legalMoves;
	}

	private Move getRelativePositionMove(final Piece[][] board, final int fileOffset, final int rankOffset) {
		final int file = getPosition().getFile() + fileOffset;
		final int rank = getPosition().getRank() + rankOffset;
		if (0 > file || file > 7) {
			return null;
		}
		if (0 > rank || rank > 7) {
			return null;
		}

		if (board[file][rank] == null || board[file][rank].getColor() != getColor()) {
			return new Move(getPosition(), Square.get(file, rank));
		} else {
			return null;
		}
	}

	@Override
	public char getPgnAbbreviation() {
		return 'N';
	}

}
