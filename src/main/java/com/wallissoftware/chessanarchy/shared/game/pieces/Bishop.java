package com.wallissoftware.chessanarchy.shared.game.pieces;

import java.util.HashSet;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;

public class Bishop extends Piece {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private Bishop() {
	};

	public Bishop(final Color color) {
		super(color);
	}

	public Bishop(final Pawn promotedFrom) {
		super(promotedFrom);
	}

	@Override
	public Set<Move> getLegalMoves(final Piece[][] board) {
		final Set<Move> legalMoves = new HashSet<Move>();
		final Square pos = getPosition();
		//rightdown
		for (int offset = 1; pos.getRank() + offset < 8 && pos.getFile() + offset < 8; offset++) {
			if (board[pos.getRank() + offset][pos.getFile() + offset] == null) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() + offset, pos.getFile() + offset)));
			} else if (board[pos.getRank() + offset][pos.getFile() + offset].getColor() != getColor()) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() + offset, pos.getFile() + offset)));
				break;
			} else {
				break;
			}
		}
		//rightup
		for (int offset = 1; pos.getRank() + offset < 8 && pos.getFile() - offset >= 0; offset++) {
			if (board[pos.getRank() + offset][pos.getFile() - offset] == null) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() + offset, pos.getFile() - offset)));
			} else if (board[pos.getRank() + offset][pos.getFile() - offset].getColor() != getColor()) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() + offset, pos.getFile() - offset)));
				break;
			} else {
				break;
			}
		}

		//leftup
		for (int offset = 1; pos.getRank() - offset >= 0 && pos.getFile() - offset >= 0; offset++) {
			if (board[pos.getRank() - offset][pos.getFile() - offset] == null) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() - offset, pos.getFile() - offset)));
			} else if (board[pos.getRank() - offset][pos.getFile() - offset].getColor() != getColor()) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() - offset, pos.getFile() - offset)));
				break;
			} else {
				break;
			}
		}
		//leftdown
		for (int offset = 1; pos.getRank() - offset >= 0 && pos.getFile() + offset < 8; offset++) {
			if (board[pos.getRank() - offset][pos.getFile() + offset] == null) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() - offset, pos.getFile() + offset)));
			} else if (board[pos.getRank() - offset][pos.getFile() + offset].getColor() != getColor()) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() - offset, pos.getFile() + offset)));
				break;
			} else {
				break;
			}
		}

		return legalMoves;
	}

	@Override
	public String getPgnAbbreviation() {
		return "B";
	}

}
