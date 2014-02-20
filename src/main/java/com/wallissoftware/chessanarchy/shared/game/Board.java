package com.wallissoftware.chessanarchy.shared.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.game.pieces.Bishop;
import com.wallissoftware.chessanarchy.shared.game.pieces.King;
import com.wallissoftware.chessanarchy.shared.game.pieces.Knight;
import com.wallissoftware.chessanarchy.shared.game.pieces.Pawn;
import com.wallissoftware.chessanarchy.shared.game.pieces.Piece;
import com.wallissoftware.chessanarchy.shared.game.pieces.Queen;
import com.wallissoftware.chessanarchy.shared.game.pieces.Rook;

public class Board {

	Piece[][] board;

	private final List<Move> moveList = new ArrayList<Move>();

	private King whiteKing;

	private King blackKing;

	private Piece lastCapture;

	private Piece lastMovedPiece;

	private Set<Piece> capturedPieces = new HashSet<Piece>();

	public Board() {
		reset();
	}

	public final void doMove(final Move move, final boolean fireEvents) {
		final Square start = move.getStart();
		final Square end = move.getEnd();

		this.lastCapture = board[end.getRank()][end.getFile()];
		this.lastMovedPiece = board[start.getRank()][start.getFile()];
		if (lastCapture == null && start.getRank() != end.getRank() && lastMovedPiece instanceof Pawn) {
			//en passant
			lastCapture = board[end.getRank()][start.getFile()];
			board[end.getRank()][start.getFile()] = null;

		}

		if (Math.abs(end.getRank() - start.getRank()) > 1 && lastMovedPiece instanceof King) {
			//castling
			if (end.getRank() == 2) {
				final Piece castle = board[0][start.getFile()];
				board[3][start.getFile()] = castle;
				castle.setPosition(new Square(3, start.getFile()), fireEvents);
			} else if (end.getRank() == 6) {
				final Piece castle = board[7][start.getFile()];
				board[5][start.getFile()] = castle;
				castle.setPosition(new Square(5, start.getFile()), fireEvents);
			}

		}

		Piece movingPiece = move.getPromote();
		if (movingPiece == null) {
			movingPiece = lastMovedPiece;

		} else if (fireEvents) {
			lastMovedPiece.notfiyHandlersOfPosition();
		}
		board[start.getRank()][start.getFile()] = null;

		board[end.getRank()][end.getFile()] = movingPiece;
		movingPiece.setPosition(end, fireEvents);
		moveList.add(move);
		if (lastCapture != null) {
			capturedPieces.add(lastCapture);
			if (fireEvents) {
				lastCapture.capture();
			}
		}

	}

	private void undoLastMove() {
		final Move move = moveList.remove(moveList.size() - 1);
		final Square start = move.getStart();
		final Square end = move.getEnd();

		board[start.getRank()][start.getFile()] = lastMovedPiece;
		board[end.getRank()][end.getFile()] = null;
		if (lastCapture != null) {
			capturedPieces.remove(lastCapture);
			board[lastCapture.getPosition().getRank()][lastCapture.getPosition().getFile()] = lastCapture;
		}

		if (Math.abs(end.getRank() - start.getRank()) > 1 && lastMovedPiece instanceof King) {
			//castling
			if (end.getRank() == 1) {
				final Piece castle = board[2][start.getFile()];
				board[0][start.getFile()] = castle;
				castle.setPosition(new Square(0, start.getFile()), false);
			} else if (end.getRank() == 6) {
				final Piece castle = board[5][start.getFile()];
				board[7][start.getFile()] = castle;
				castle.setPosition(new Square(7, start.getFile()), false);
			}
		}

		lastMovedPiece.setPosition(start, false, false);
		lastMovedPiece.decrememntMoveCount();

	}

	private void reset() {
		capturedPieces.clear();
		this.board = new Piece[8][8];
		board[0][0] = new Rook(Color.WHITE);
		board[1][0] = new Knight(Color.WHITE);
		board[2][0] = new Bishop(Color.WHITE);
		board[3][0] = new Queen(Color.WHITE);
		this.whiteKing = new King(Color.WHITE);
		board[4][0] = whiteKing;
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
		board[3][7] = new Queen(Color.BLACK);
		this.blackKing = new King(Color.BLACK);
		board[4][7] = blackKing;
		board[5][7] = new Bishop(Color.BLACK);
		board[6][7] = new Knight(Color.BLACK);
		board[7][7] = new Rook(Color.BLACK);
		resetPiecePositions();
	}

	private void resetPiecePositions() {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				if (board[rank][file] != null) {
					board[rank][file].setPosition(new Square(rank, file), true);
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
		doMove(move, false);
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

	public Color getCurrentPlayer() {
		return moveList.size() % 2 == 0 ? Color.WHITE : Color.BLACK;
	}

	public Set<Piece> getPieces() {
		final Set<Piece> allPieces = new HashSet<Piece>();
		allPieces.addAll(capturedPieces);
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				if (board[rank][file] != null) {
					allPieces.add(board[rank][file]);
				}
			}
		}
		return allPieces;
	}

	public boolean isMovePromotion(final Square start, final Square end) {

		if (getPieceAt(start) instanceof Pawn) {
			for (final Move move : getPieceAt(start).getLegalMoves(board)) {
				if (move.getPromote() != null && move.matchesWithoutPromotion(new Move(start, end))) {
					return true;
				}
			}
		}
		return false;

	}

	public boolean isPartialMoveLegal(final Square start, final Square end) {
		final Move partialMove = new Move(start, end);
		for (final Move legalMove : getLegalMoves(false)) {
			if (legalMove.matchesWithoutPromotion(partialMove)) {
				return true;
			}
		}
		return false;

	}

	public Piece getPieceAt(final Square square) {
		return board[square.getRank()][square.getFile()];
	}

}
