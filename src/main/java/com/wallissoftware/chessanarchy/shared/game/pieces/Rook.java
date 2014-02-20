package com.wallissoftware.chessanarchy.shared.game.pieces;

import java.util.HashSet;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;

public class Rook extends Piece {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private Rook() {
	};

	public Rook(final Color color) {
		super(color);
	}

	public Rook(final Pawn promotedFrom) {
		super(promotedFrom);
	}

	@Override
	public Set<Move> getLegalMoves(final Piece[][] board) {
		final Set<Move> legalMoves = new HashSet<Move>();
		final Square pos = getPosition();
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
