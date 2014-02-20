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

		//right
		for (int rank = pos.getRank() + 1; rank < 8; rank++) {

			if (board[rank][pos.getFile()] == null) {
				legalMoves.add(new Move(pos, new Square(rank, pos.getFile())));
			} else if (board[rank][pos.getFile()].getColor() != getColor()) {
				legalMoves.add(new Move(pos, new Square(rank, pos.getFile())));
				break;
			} else {
				break;
			}
		}
		//left
		for (int rank = pos.getRank() - 1; rank >= 0; rank--) {
			if (board[rank][pos.getFile()] == null) {
				legalMoves.add(new Move(pos, new Square(rank, pos.getFile())));
			} else if (board[rank][pos.getFile()].getColor() != getColor()) {
				legalMoves.add(new Move(pos, new Square(rank, pos.getFile())));
				break;
			} else {
				break;
			}
		}

		//up
		for (int file = pos.getFile() - 1; file >= 0; file--) {
			if (board[pos.getRank()][file] == null) {
				legalMoves.add(new Move(pos, new Square(pos.getRank(), file)));
			} else if (board[pos.getRank()][file].getColor() != getColor()) {
				legalMoves.add(new Move(pos, new Square(pos.getRank(), file)));
				break;
			} else {
				break;
			}
		}

		//down
		for (int file = pos.getFile() + 1; file < 8; file++) {
			if (board[pos.getRank()][file] == null) {
				legalMoves.add(new Move(pos, new Square(pos.getRank(), file)));
			} else if (board[pos.getRank()][file].getColor() != getColor()) {
				legalMoves.add(new Move(pos, new Square(pos.getRank(), file)));
				break;
			} else {
				break;
			}
		}

		return legalMoves;
	}

}
