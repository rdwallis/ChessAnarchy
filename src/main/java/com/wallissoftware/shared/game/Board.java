package com.wallissoftware.shared.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.wallissoftware.shared.game.pieces.Bishop;
import com.wallissoftware.shared.game.pieces.King;
import com.wallissoftware.shared.game.pieces.Knight;
import com.wallissoftware.shared.game.pieces.Pawn;
import com.wallissoftware.shared.game.pieces.Piece;
import com.wallissoftware.shared.game.pieces.Queen;
import com.wallissoftware.shared.game.pieces.Rook;

public class Board {

	Piece[][] board;

	private final List<Move> moveList = new ArrayList<Move>();

	private King whiteKing;

	private King blackKing;

	private Piece lastCapture;

	private Piece lastMovedPiece;

	public Board() {
		reset();
	}

	private final void doMove(final Move move) {
		final Square start = move.getStart();
		final Square end = move.getEnd();

		this.lastCapture = board[end.getRank()][end.getFile()];
		this.lastMovedPiece = board[start.getRank()][start.getFile()];
		final Piece movingPiece = move.getPromote() == null ? lastMovedPiece : move.getPromote();
		board[start.getRank()][start.getFile()] = null;

		board[end.getRank()][end.getFile()] = movingPiece;
		movingPiece.setPosition(end);
		moveList.add(move);

	}

	private void undoLastMove() {
		final Move move = moveList.remove(moveList.size() - 1);
		final Square start = move.getStart();
		final Square end = move.getEnd();

		board[start.getRank()][start.getFile()] = lastMovedPiece;
		board[end.getRank()][end.getFile()] = lastCapture;

		if (lastCapture != null) {
			lastCapture.setPosition(end, false);
		}

		lastMovedPiece.setPosition(start, false);
		lastMovedPiece.decrememntMoveCount();

	}

	private void reset() {
		this.board = new Piece[8][8];
		board[0][0] = new Rook(Color.WHITE);
		board[1][0] = new Knight(Color.WHITE);
		board[2][0] = new Bishop(Color.WHITE);
		this.whiteKing = new King(Color.WHITE);
		board[3][0] = whiteKing;
		board[4][0] = new Queen(Color.WHITE);
		board[5][0] = new Bishop(Color.WHITE);
		board[6][0] = new Knight(Color.WHITE);
		board[7][0] = new Rook(Color.WHITE);

		for (int i = 0; i < 8; i++) {
			board[i][1] = new Pawn(Color.WHITE);
			board[i][6] = new Pawn(Color.BLACK);
		}

		board[0][7] = new Rook(Color.BLACK);
		board[1][7] = new Knight(Color.BLACK);
		board[2][7] = new Bishop(Color.BLACK);
		this.blackKing = new King(Color.BLACK);
		board[3][7] = blackKing;
		board[4][7] = new Queen(Color.BLACK);
		board[5][7] = new Bishop(Color.BLACK);
		board[6][7] = new Knight(Color.BLACK);
		board[7][7] = new Rook(Color.BLACK);
		resetPiecePositions();
	}

	private void resetPiecePositions() {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				if (board[rank][file] != null) {
					board[rank][file].setPosition(new Square(rank, file));
				}
			}
		}

	}

	private Set<Move> getLegalMoves(final boolean ignoreCheck) {
		final Set<Move> legalMoves = new HashSet<Move>();
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				if (board[rank][file] != null && board[rank][file].getColor() == getCurrentPlayer()) {
					legalMoves.addAll(board[rank][file].getLegalMoves(board));
				}
			}
		}
		if (!ignoreCheck) {
			final Iterator<Move> it = legalMoves.iterator();
			while (it.hasNext()) {
				if (moveWillLeaveCurrentPlacyerInCheck(it.next())) {
					it.remove();
				}
			}
		}

		return legalMoves;
	}

	private boolean moveWillLeaveCurrentPlacyerInCheck(final Move move) {
		doMove(move);
		final King currentKing = getCurrentPlayer() != Color.WHITE ? whiteKing : blackKing;
		for (final Move opponentMove : getLegalMoves(true)) {
			if (opponentMove.getEnd().equals(currentKing.getPosition())) {
				undoLastMove();
				return true;
			}
		}
		undoLastMove();
		return false;

	}

	private Color getCurrentPlayer() {
		return moveList.size() % 2 == 0 ? Color.BLACK : Color.WHITE;
	}
}
