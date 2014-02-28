package com.wallissoftware.chessanarchy.shared.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;
import com.wallissoftware.chessanarchy.shared.game.pieces.Bishop;
import com.wallissoftware.chessanarchy.shared.game.pieces.King;
import com.wallissoftware.chessanarchy.shared.game.pieces.Knight;
import com.wallissoftware.chessanarchy.shared.game.pieces.Pawn;
import com.wallissoftware.chessanarchy.shared.game.pieces.Piece;
import com.wallissoftware.chessanarchy.shared.game.pieces.Queen;
import com.wallissoftware.chessanarchy.shared.game.pieces.Rook;

public class Board {

	Piece[][] board;

	private List<Move> moveList;

	private King whiteKing;

	private King blackKing;

	private List<Piece> pieces = new ArrayList<Piece>();

	private Set<Move> lastCalculatedLegalMoves = new HashSet<Move>();

	private Piece lastMoved;

	private boolean requiresFullUndo = false;

	private final static Logger logger = Logger.getLogger(Board.class.getName());

	public Board(final List<String> moveList) throws IllegalMoveException {
		resetFromMoveList(moveList);
	}

	public void resetFromMoveList(final List<String> moveList) throws IllegalMoveException {
		reset(true);
		for (final String pgn : moveList) {
			doMove(pgn);
		}
	}

	private void doMove(final String pgn) throws IllegalMoveException {
		final Map<String, Move> notationMap = getLegalMovesWithNotation();
		if (notationMap.containsKey(pgn)) {
			doMove(notationMap.get(pgn), true, false);
		} else {
			logger.info(pgn + " is not contained in legal move list: " + notationMap.keySet());
			throw new IllegalMoveException();
		}

	}

	public Board() {
		reset(true);
	}

	public final String doMove(final Move move, final boolean recordMove, final boolean calcFullPgn) {
		final Square start = move.getStart();
		final Square end = move.getEnd();

		String pgn = end.toString();

		Piece capture = board[end.getRank()][end.getFile()];
		final Piece movedPiece = board[start.getRank()][start.getFile()];
		if (capture == null && start.getRank() != end.getRank() && movedPiece instanceof Pawn) {
			//en passant
			capture = board[end.getRank()][start.getFile()];
			board[end.getRank()][start.getFile()] = null;

		}

		if (capture != null) {
			pgn = "x" + pgn;
			requiresFullUndo = true;
		}

		if (calcFullPgn) {

			final List<Piece> otherPiecesOfSameType = getOtherPiecesOfSameTypeThatCanMoveToSquare(movedPiece, end);

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

			pgn = movedPiece.getPgnAbbreviation() + pgn;
		}

		if (Math.abs(end.getRank() - start.getRank()) > 1 && movedPiece instanceof King) {
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
			movingPiece = movedPiece;

		} else {
			pgn = pgn + "=" + movingPiece.getPgnAbbreviation();
			pieces.add(movingPiece);
			if (recordMove) {
				movedPiece.notfiyHandlersOfPosition();
			}
		}
		setLastMovedPiece(movingPiece);

		board[start.getRank()][start.getFile()] = null;

		board[end.getRank()][end.getFile()] = movingPiece;
		movingPiece.setPosition(end, recordMove);
		moveList.add(move);
		if (capture != null) {
			capture.capture(recordMove);
		}
		if (calcFullPgn) {
			if (isCheckMate()) {
				pgn = pgn + "#";
			} else if (isCheck()) {
				pgn = pgn + "+";
			}
		}

		return pgn;

	}

	private void setLastMovedPiece(final Piece movingPiece) {
		if (lastMoved != null) {
			lastMoved.setJustMoved(false);
		}
		this.lastMoved = movingPiece;
		if (lastMoved != null) {
			lastMoved.setJustMoved(true);
		}

	}

	private String getBoardAsText(final Square highlightSquare) {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n");
		final String line = "---------------------------------\n";
		sb.append(line);
		for (int file = 0; file < 8; file++) {
			for (int rank = 0; rank < 8; rank++) {
				final Square square = new Square(rank, file);
				sb.append(square.equals(highlightSquare) ? "[" : "|");
				final Piece p = board[rank][file];
				if (p == null) {
					sb.append("  ");
				} else {
					sb.append(p.getColor().name().charAt(0));
					sb.append(p.getPgnAbbreviation().isEmpty() ? "P" : p.getPgnAbbreviation());
				}
				sb.append(square.equals(highlightSquare) ? "]" : "|");

			}
			sb.append("\n");
			sb.append(line);
		}
		return sb.toString();
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
		return isCheck() && calculateLegalMoves().isEmpty();
	}

	private List<Piece> getOtherPiecesOfSameTypeThatCanMoveToSquare(final Piece piece, final Square square) {
		final List<Piece> result = new ArrayList<Piece>();
		if (piece instanceof King || piece instanceof Pawn) {
			return result;
		}
		for (final Move move : getLastCalculatedLegalMoves()) {
			if (move.getEnd().equals(square) && !move.getStart().equals(piece.getPosition())) {
				final Piece other = getPieceAt(move.getStart());

				if (other != null && other.getColor() == piece.getColor() && other.getPgnAbbreviation().equals(piece.getPgnAbbreviation())) {
					result.add(piece);
				}
			}
		}
		return result;
	}

	private void undoLastMove() {
		if (requiresFullUndo) {
			requiresFullUndo = false;
			final List<Move> moveList = this.moveList;
			reset(false);
			moveList.remove(moveList.size() - 1);
			for (final Move move : moveList) {
				doMove(move, false, false);
			}

		} else {
			final Move move = moveList.remove(moveList.size() - 1);
			final Square start = move.getStart();
			final Square end = move.getEnd();

			board[start.getRank()][start.getFile()] = lastMoved;
			board[end.getRank()][end.getFile()] = null;

			if (Math.abs(end.getRank() - start.getRank()) > 1 && lastMoved instanceof King) {
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

			lastMoved.setPosition(start, false, false);
			lastMoved.decrememntMoveCount();

			if (!moveList.isEmpty()) {
				setLastMovedPiece(getPieceAt(moveList.get(moveList.size() - 1).getEnd()));
			}
		}

	}

	private void reset(final boolean redraw) {
		logger.info("reseting board");
		moveList = new ArrayList<Move>();
		lastMoved = null;
		for (final Piece piece : getPieces()) {
			piece.recycle();
		}
		this.board = new Piece[8][8];
		final String startPos = "RNBQKBNR";

		for (int i = 0; i < 8; i++) {
			createPiece(new Square(i, 0), startPos.substring(i, i + 1), Color.WHITE, redraw);
			createPiece(new Square(i, 1), "", Color.WHITE, redraw);
			createPiece(new Square(i, 6), "", Color.BLACK, redraw);
			createPiece(new Square(i, 7), startPos.substring(i, i + 1), Color.BLACK, redraw);
		}
	}

	private void createPiece(final Square square, final String abbreviation, final Color color, final boolean redraw) {
		for (final Piece piece : getPieces()) {
			if (piece.canRecycle() && piece.getPgnAbbreviation().equals(abbreviation) && piece.getColor() == color) {
				setPieceAtSquare(piece, square, redraw);
				return;
			}
		}
		final Piece newPiece;
		if (abbreviation.equals("")) {
			newPiece = new Pawn(color);

		} else if (abbreviation.equals("Q")) {
			newPiece = new Queen(color);
		} else if (abbreviation.equals("R")) {
			newPiece = new Rook(color);
		} else if (abbreviation.equals("K")) {
			newPiece = new King(color);
			if (color == Color.WHITE) {
				whiteKing = (King) newPiece;
			} else {
				blackKing = (King) newPiece;
			}
		} else if (abbreviation.equals("N")) {
			newPiece = new Knight(color);
		} else {
			newPiece = new Bishop(color);
		}
		setPieceAtSquare(newPiece, square, redraw);
		pieces.add(newPiece);

	}

	private void setPieceAtSquare(final Piece piece, final Square square, final boolean fireEvents) {
		board[square.getRank()][square.getFile()] = piece;
		piece.setPosition(square, fireEvents);

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
		doMove(move, false, false);
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

	public List<Piece> getPieces() {
		return pieces;
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

	public Piece[][] getBoardArray() {
		return board;
	}

	public Map<String, Move> getLegalMovesWithNotation() {
		final Map<String, Move> result = new HashMap<String, Move>();
		for (final Move move : calculateLegalMoves()) {
			result.put(doMove(move, false, true), move);
			undoLastMove();
		}
		return result;
	}

	public Map<String, String> getAllLegalMovesWithNotation() {
		final Map<String, String> result = new HashMap<String, String>();
		for (final Move move : calculateLegalMoves()) {
			final String pgn = doMove(move, false, true);
			result.put(pgn, pgn);
			result.put(move.toString(), pgn);
			undoLastMove();
		}
		return result;

	}

}
