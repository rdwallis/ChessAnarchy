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
		for (int rankOffset = -1; rankOffset <= 1; rankOffset++) {
			final int rank = pos.getRank() + rankOffset;
			if (0 > rank || rank > 7) {
				continue;
			}
			for (int fileOffset = -1; fileOffset <= 1; fileOffset++) {
				final int file = pos.getFile() + fileOffset;
				if (0 > file || file > 7) {
					continue;
				}
				if (board[rank][file] == null || board[rank][file].getColor() != getColor()) {
					legalMoves.add(new Move(getPosition(), new Square(rank, file)));
				}
			}
		}
		//castling
		if (getMoveCount() == 0) {
			final Piece leftRook = board[0][pos.getFile()];
			if (leftRook != null && leftRook.getMoveCount() == 0 && leftRook.getColor() == getColor()) {
				boolean blocked = false;
				for (int rank = 1; rank < pos.getRank(); rank++) {
					if (board[rank][pos.getFile()] != null) {
						blocked = true;
						break;
					}
				}
				if (!blocked) {
					legalMoves.add(new Move(pos, new Square(2, pos.getFile())));
				}
			}

			final Piece rightRook = board[7][pos.getFile()];
			if (rightRook != null && rightRook.getMoveCount() == 0 && rightRook.getColor() == getColor()) {
				boolean blocked = false;
				for (int rank = pos.getRank() + 1; rank < 7; rank++) {
					if (board[rank][pos.getFile()] != null) {
						blocked = true;
						break;
					}
				}
				if (!blocked) {
					legalMoves.add(new Move(pos, new Square(6, pos.getFile())));
				}
			}

		}
		return legalMoves;
	}

	@Override
	public String getPgnAbbreviation() {
		return "K";
	}

}
