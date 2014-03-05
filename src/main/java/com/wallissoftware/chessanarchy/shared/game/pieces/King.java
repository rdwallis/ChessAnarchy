package com.wallissoftware.chessanarchy.shared.game.pieces;

import java.util.HashSet;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;

public class King extends Piece {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private King() {
	};

	public King(final Color color) {
		super(color);
	}

	@Override
	public Set<Move> getLegalMoves(final Piece[][] board) {
		final Set<Move> legalMoves = new HashSet<Move>();
		final Square pos = getPosition();
		for (int fileOffset = -1; fileOffset <= 1; fileOffset++) {
			final int file = pos.getFile() + fileOffset;
			if (0 > file || file > 7) {
				continue;
			}
			for (int rankOffset = -1; rankOffset <= 1; rankOffset++) {
				final int rank = pos.getRank() + rankOffset;
				if (0 > rank || rank > 7) {
					continue;
				}
				if (board[file][rank] == null || board[file][rank].getColor() != getColor()) {
					legalMoves.add(new Move(getPosition(), Square.get(file, rank)));
				}
			}
		}
		//castling
		if (getMoveCount() == 0) {
			final Piece leftRook = board[0][pos.getRank()];
			if (leftRook != null && leftRook.getMoveCount() == 0 && leftRook.getColor() == getColor()) {
				boolean blocked = false;
				for (int file = 1; file < pos.getFile(); file++) {
					if (board[file][pos.getRank()] != null) {
						blocked = true;
						break;
					}
				}
				if (!blocked) {
					legalMoves.add(new Move(pos, Square.get(2, pos.getRank())));
				}
			}

			final Piece rightRook = board[7][pos.getRank()];
			if (rightRook != null && rightRook.getMoveCount() == 0 && rightRook.getColor() == getColor()) {
				boolean blocked = false;
				for (int file = pos.getFile() + 1; file < 7; file++) {
					if (board[file][pos.getRank()] != null) {
						blocked = true;
						break;
					}
				}
				if (!blocked) {
					legalMoves.add(new Move(pos, Square.get(6, pos.getRank())));
				}
			}

		}
		return legalMoves;
	}

	@Override
	public char getPgnAbbreviation() {
		return 'K';
	}

}
