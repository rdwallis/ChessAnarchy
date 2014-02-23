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

	private Set<Move> lastCalculatedLegalMoves = new HashSet<Move>();

	public Board() {
		reset();
	}

	public final String doMove(final Move move, final boolean recordMove) {
		final Square start = move.getStart();
		final Square end = move.getEnd();

		String pgn = end.toString();

		this.lastCapture = board[end.getRank()][end.getFile()];
		if (lastCapture != null) {
			pgn = "x" + pgn;
		}

		this.lastMovedPiece = board[start.getRank()][start.getFile()];

		if (recordMove) {
			final List<Piece> otherPiecesOfSameType = getOtherPiecesOfSameTypeThatCanMoveToSquare(lastMovedPiece, end);

			boolean fileIsUnique = true;
			boolean rankIsUnique = true;
			for (final Piece piece : otherPiecesOfSameType) {
				final Square otherPosition = piece.getPosition();
				final Square position = move.getStart();
				if (otherPosition.getRank() == position.getRank()) {
					rankIsUnique = false;
				}
				if (otherPosition.getFile() == position.getFile()) {
					fileIsUnique = false;
				}
			}

			if (!fileIsUnique) {
				pgn = start.toString().charAt(1) + pgn;
			}

			if (!rankIsUnique) {
				pgn = start.toString().charAt(0) + pgn;
			}

			pgn = lastMovedPiece.getPgnAbbreviation() + pgn;
		}

		if (lastCapture == null && start.getRank() != end.getRank() && lastMovedPiece instanceof Pawn) {
			//en passant
			lastCapture = board[end.getRank()][start.getFile()];
			board[end.getRank()][start.getFile()] = null;

		}

		if (Math.abs(end.getRank() - start.getRank()) > 1 && lastMovedPiece instanceof King) {
			//castling
			if (end.getRank() == 2) {
				//queenside
				pgn = "O-O-O";
				final Piece castle = board[0][start.getFile()];
				board[3][start.getFile()] = castle;
				castle.setPosition(new Square(3, start.getFile()), recordMove, false);
			} else if (end.getRank() == 6) {
				//king side
				pgn = "O-O";
				final Piece castle = board[7][start.getFile()];
				board[5][start.getFile()] = castle;
				castle.setPosition(new Square(5, start.getFile()), recordMove, false);
			}

		}

		Piece movingPiece = move.getPromote();
		if (movingPiece == null) {
			movingPiece = lastMovedPiece;

		} else if (recordMove) {
			pgn = pgn + "=" + movingPiece.getPgnAbbreviation();
			lastMovedPiece.notfiyHandlersOfPosition();
		}
		board[start.getRank()][start.getFile()] = null;

		board[end.getRank()][end.getFile()] = movingPiece;
		movingPiece.setPosition(end, recordMove);
		moveList.add(move);
		if (lastCapture != null) {
			capturedPieces.add(lastCapture);
			if (recordMove) {
				lastCapture.capture();
			}
		}
		if (recordMove) {
			if (isCheckMate()) {
				pgn = pgn + "#";
			} else if (isCheck()) {
				pgn = pgn + "+";
			}
		}
		return pgn;

	}

	private boolean isCheck() {
		final King king = getCurrentPlayer() == Color.WHITE ? whiteKing : blackKing;
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				if (board[rank][file] != null && board[rank][file].getColor() != getCurrentPlayer()) {
					for (final Move move : board[rank][file].getLegalMoves(board)) {
						if (move.getEnd().equals(king.getPosition())) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private boolean isCheckMate() {
		return calculateLegalMoves().isEmpty();
	}

	private List<Piece> getOtherPiecesOfSameTypeThatCanMoveToSquare(final Piece piece, final Square square) {
		final List<Piece> result = new ArrayList<Piece>();
		if (piece instanceof King || piece instanceof Pawn) {
			return result;
		}
		for (final Move move : getLastCalculatedLegalMoves()) {
			if (move.getEnd().equals(square) && !move.getStart().equals(piece.getPosition())) {
				final Piece other = getPieceAt(move.getStart());
				if (other.getColor() == piece.getColor() && other.getPgnAbbreviation().equals(piece.getPgnAbbreviation())) {
					result.add(piece);
				}
			}
		}
		return result;
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
				castle.setPosition(new Square(0, start.getFile()), false, false);
			} else if (end.getRank() == 6) {
				final Piece castle = board[5][start.getFile()];
				board[7][start.getFile()] = castle;
				castle.setPosition(new Square(7, start.getFile()), false, false);
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

	private Set<Move> calculateLegalMoves() {
		return calculateLegalMoves(false);
	}

	private Set<Move> calculateLegalMoves(final boolean ignoreCheck) {
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
			lastCalculatedLegalMoves = legalMoves;
		}

		return legalMoves;
	}

	private Set<Move> getLastCalculatedLegalMoves() {
		return lastCalculatedLegalMoves;
	}

	private boolean moveWillLeaveCurrentPlacyerInCheck(final Move move) {
		final Set<Square> protectedSquares = new HashSet<Square>();
		if (getPieceAt(move.getStart()) instanceof King) {
			if (Math.abs(move.getEnd().getRank() - move.getStart().getRank()) > 1) {
				//castling
				final int file = move.getStart().getFile();
				if (move.getEnd().getRank() == 2) {
					protectedSquares.add(move.getStart());
					protectedSquares.add(new Square(3, file));
					protectedSquares.add(new Square(4, file));
				} else if (move.getEnd().getRank() == 6) {
					protectedSquares.add(new Square(5, file));
					protectedSquares.add(move.getStart());
				}
			}
		}
		doMove(move, false);
		final King currentKing = getCurrentPlayer() != Color.WHITE ? whiteKing : blackKing;
		protectedSquares.add(currentKing.getPosition());
		for (final Move opponentMove : calculateLegalMoves(true)) {
			for (final Square pSquare : protectedSquares) {
				if (opponentMove.getEnd().equals(pSquare)) {
					undoLastMove();
					return true;
				}
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
		for (final Move legalMove : calculateLegalMoves()) {
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
