package com.wallissoftware.chessanarchy.shared.game.pieces;

import java.util.HashSet;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;

public class Queen extends Piece {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private Queen() {
	};

	public Queen(final Color color) {
		super(color);
	}

	public Queen(final Pawn promotedFrom) {
		super(promotedFrom);
	}

	@Override
	public Set<Move> getLegalMoves(final Piece[][] board) {
		final Set<Move> legalMoves = new HashSet<Move>();
		final Square pos = getPosition();
		//rightdown
		for (int offset = 1; pos.getFile() + offset < 8 && pos.getRank() + offset < 8; offset++) {
			if (board[pos.getFile() + offset][pos.getRank() + offset] == null) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() + offset, pos.getRank() + offset)));
			} else if (board[pos.getFile() + offset][pos.getRank() + offset].getColor() != getColor()) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() + offset, pos.getRank() + offset)));
				break;
			} else {
				break;
			}
		}
		//rightup
		for (int offset = 1; pos.getFile() + offset < 8 && pos.getRank() - offset >= 0; offset++) {
			if (board[pos.getFile() + offset][pos.getRank() - offset] == null) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() + offset, pos.getRank() - offset)));
			} else if (board[pos.getFile() + offset][pos.getRank() - offset].getColor() != getColor()) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() + offset, pos.getRank() - offset)));
				break;
			} else {
				break;
			}
		}

		//leftup
		for (int offset = 1; pos.getFile() - offset >= 0 && pos.getRank() - offset >= 0; offset++) {
			if (board[pos.getFile() - offset][pos.getRank() - offset] == null) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() - offset, pos.getRank() - offset)));
			} else if (board[pos.getFile() - offset][pos.getRank() - offset].getColor() != getColor()) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() - offset, pos.getRank() - offset)));
				break;
			} else {
				break;
			}
		}
		//leftdown
		for (int offset = 1; pos.getFile() - offset >= 0 && pos.getRank() + offset < 8; offset++) {
			if (board[pos.getFile() - offset][pos.getRank() + offset] == null) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() - offset, pos.getRank() + offset)));
			} else if (board[pos.getFile() - offset][pos.getRank() + offset].getColor() != getColor()) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() - offset, pos.getRank() + offset)));
				break;
			} else {
				break;
			}
		}

		//right
		for (int file = pos.getFile() + 1; file < 8; file++) {

			if (board[file][pos.getRank()] == null) {
				legalMoves.add(new Move(pos, Square.get(file, pos.getRank())));
			} else if (board[file][pos.getRank()].getColor() != getColor()) {
				legalMoves.add(new Move(pos, Square.get(file, pos.getRank())));
				break;
			} else {
				break;
			}
		}
		//left
		for (int file = pos.getFile() - 1; file >= 0; file--) {
			if (board[file][pos.getRank()] == null) {
				legalMoves.add(new Move(pos, Square.get(file, pos.getRank())));
			} else if (board[file][pos.getRank()].getColor() != getColor()) {
				legalMoves.add(new Move(pos, Square.get(file, pos.getRank())));
				break;
			} else {
				break;
			}
		}

		//up
		for (int rank = pos.getRank() - 1; rank >= 0; rank--) {
			if (board[pos.getFile()][rank] == null) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile(), rank)));
			} else if (board[pos.getFile()][rank].getColor() != getColor()) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile(), rank)));
				break;
			} else {
				break;
			}
		}

		//down
		for (int rank = pos.getRank() + 1; rank < 8; rank++) {
			if (board[pos.getFile()][rank] == null) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile(), rank)));
			} else if (board[pos.getFile()][rank].getColor() != getColor()) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile(), rank)));
				break;
			} else {
				break;
			}
		}

		return legalMoves;
	}

	@Override
	public char getPgnAbbreviation() {
		return 'Q';

	}

}
