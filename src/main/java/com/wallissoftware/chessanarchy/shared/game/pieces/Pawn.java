package com.wallissoftware.chessanarchy.shared.game.pieces;

import java.util.HashSet;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;

public class Pawn extends Piece {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private Pawn() {
	};

	public Pawn(final Color color) {
		super(color);
	}

	@Override
	public Set<Move> getLegalMoves(final Piece[][] board) {
		final Set<Move> legalMoves = new HashSet<Move>();
		final Square pos = getPosition();

		final int fileOffset = getColor() == Color.WHITE ? 1 : -1;
		final int file = pos.getFile() + fileOffset;
		if ((0 > file) || (file > 7)) {
			return legalMoves;
		}
		if (pos.getRank() - 1 >= 0 && board[pos.getRank() - 1][file] != null && board[pos.getRank() - 1][file].getColor() != getColor()) {
			if (file == 0 || file == 7) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() - 1, file), new Queen(this)));
				legalMoves.add(new Move(pos, new Square(pos.getRank() - 1, file), new Rook(this)));
				legalMoves.add(new Move(pos, new Square(pos.getRank() - 1, file), new Bishop(this)));
				legalMoves.add(new Move(pos, new Square(pos.getRank() - 1, file), new Knight(this)));
			} else {
				legalMoves.add(new Move(pos, new Square(pos.getRank() - 1, file)));
			}
		}

		if (pos.getRank() + 1 < 8 && board[pos.getRank() + 1][file] != null && board[pos.getRank() + 1][file].getColor() != getColor()) {

			if (file == 0 || file == 7) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() + 1, file), new Queen(this)));
				legalMoves.add(new Move(pos, new Square(pos.getRank() + 1, file), new Rook(this)));
				legalMoves.add(new Move(pos, new Square(pos.getRank() + 1, file), new Bishop(this)));
				legalMoves.add(new Move(pos, new Square(pos.getRank() + 1, file), new Knight(this)));
			} else {
				legalMoves.add(new Move(pos, new Square(pos.getRank() + 1, file)));
			}
		}

		if (board[pos.getRank()][file] == null) {
			if (file == 0 || file == 7) {
				legalMoves.add(new Move(pos, new Square(pos.getRank(), file), new Queen(getColor())));
				legalMoves.add(new Move(pos, new Square(pos.getRank(), file), new Rook(getColor())));
				legalMoves.add(new Move(pos, new Square(pos.getRank(), file), new Bishop(getColor())));
				legalMoves.add(new Move(pos, new Square(pos.getRank(), file), new Knight(getColor())));
			} else {
				legalMoves.add(new Move(pos, new Square(pos.getRank(), file)));
			}
		}

		if (getMoveCount() == 0) {
			if (0 <= file + fileOffset && file + fileOffset <= 7) {
				if (board[pos.getRank()][file + fileOffset] == null) {
					legalMoves.add(new Move(pos, new Square(pos.getRank(), file + fileOffset)));
				}
			}

		}

		//en passant
		if ((getColor() == Color.WHITE && pos.getFile() == 4) || (getColor() == Color.BLACK && pos.getFile() == 3)) {
			if (pos.getRank() - 1 >= 0 && board[pos.getRank() - 1][pos.getFile()] != null && board[pos.getRank() - 1][pos.getFile()].getColor() != getColor() && board[pos.getRank() - 1][pos.getFile()].getMoveCount() == 1 && board[pos.getRank() - 1][pos.getFile()] instanceof Pawn) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() - 1, file)));
			}
			if (pos.getRank() + 1 >= 0 && board[pos.getRank() + 1][pos.getFile()] != null && board[pos.getRank() + 1][pos.getFile()].getColor() != getColor() && board[pos.getRank() + 1][pos.getFile()].getMoveCount() == 1 && board[pos.getRank() + 1][pos.getFile()] instanceof Pawn) {
				legalMoves.add(new Move(pos, new Square(pos.getRank() + 1, file)));
			}
		}

		return legalMoves;
	}

	void setPromotedTo(final Piece piece) {
		this.promotedTo = piece;

	}

	private Piece promotedTo;

	@Override
	public Piece getPromotedTo() {
		return promotedTo;
	}

}