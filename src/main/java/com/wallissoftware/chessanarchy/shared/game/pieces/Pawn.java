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

		final int rankOffset = getColor() == Color.WHITE ? 1 : -1;
		final int rank = pos.getRank() + rankOffset;
		if ((0 > rank) || (rank > 7)) {
			return legalMoves;
		}
		if (pos.getFile() - 1 >= 0 && board[pos.getFile() - 1][rank] != null && board[pos.getFile() - 1][rank].getColor() != getColor()) {
			if (rank == 0 || rank == 7) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() - 1, rank), 'Q'));
				legalMoves.add(new Move(pos, Square.get(pos.getFile() - 1, rank), 'R'));
				legalMoves.add(new Move(pos, Square.get(pos.getFile() - 1, rank), 'B'));
				legalMoves.add(new Move(pos, Square.get(pos.getFile() - 1, rank), 'N'));
			} else {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() - 1, rank)));
			}
		}

		if (pos.getFile() + 1 < 8 && board[pos.getFile() + 1][rank] != null && board[pos.getFile() + 1][rank].getColor() != getColor()) {

			if (rank == 0 || rank == 7) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() + 1, rank), 'Q'));
				legalMoves.add(new Move(pos, Square.get(pos.getFile() + 1, rank), 'R'));
				legalMoves.add(new Move(pos, Square.get(pos.getFile() + 1, rank), 'B'));
				legalMoves.add(new Move(pos, Square.get(pos.getFile() + 1, rank), 'N'));
			} else {
				legalMoves.add(new Move(pos, Square.get(pos.getFile() + 1, rank)));
			}
		}

		if (board[pos.getFile()][rank] == null) {
			if (rank == 0 || rank == 7) {
				legalMoves.add(new Move(pos, Square.get(pos.getFile(), rank), 'Q'));
				legalMoves.add(new Move(pos, Square.get(pos.getFile(), rank), 'R'));
				legalMoves.add(new Move(pos, Square.get(pos.getFile(), rank), 'B'));
				legalMoves.add(new Move(pos, Square.get(pos.getFile(), rank), 'N'));
			} else {
				legalMoves.add(new Move(pos, Square.get(pos.getFile(), rank)));
				if (getMoveCount() == 0) {
					if (0 <= rank + rankOffset && rank + rankOffset <= 7) {
						if (board[pos.getFile()][rank + rankOffset] == null) {
							legalMoves.add(new Move(pos, Square.get(pos.getFile(), rank + rankOffset)));
						}
					}

				}
			}
		}

		//en passant
		if ((getColor() == Color.WHITE && pos.getRank() == 4) || (getColor() == Color.BLACK && pos.getRank() == 3)) {
			if (pos.getFile() - 1 >= 0) {
				final Piece other = board[pos.getFile() - 1][pos.getRank()];
				if (other != null && other.justMoved() && other.getColor() != getColor() && other.getMoveCount() == 1 && other instanceof Pawn) {
					legalMoves.add(new Move(pos, Square.get(pos.getFile() - 1, rank)));
				}
			}
			if (pos.getFile() + 1 <= 7) {
				final Piece other = board[pos.getFile() + 1][pos.getRank()];
				if (other != null && other.justMoved() && other.getColor() != getColor() && other.getMoveCount() == 1 && other instanceof Pawn) {
					legalMoves.add(new Move(pos, Square.get(pos.getFile() + 1, rank)));
				}
			}

		}

		return legalMoves;
	}

	@Override
	public void setPromotedTo(final Piece piece) {
		this.promotedTo = piece;

	}

	private Piece promotedTo;

	@Override
	public Piece getPromotedTo() {
		return promotedTo;
	}

	@Override
	public char getPgnAbbreviation() {
		return 'P';
	}

	@Override
	public void recycle() {
		super.recycle();
		promotedTo = null;
	}

}
