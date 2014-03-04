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

	private Move lastMove;

	private boolean requiresFullUndo = false;

	private final static Logger logger = Logger.getLogger(Board.class.getName());

	private PieceCreationHandler pieceCreationHandler;

	private List<Move> moveListAtTimeOfLastCalculation;

	public Board(final List<String> moveList) throws IllegalMoveException {
		//reset(true);
		resetFromMoveList(moveList);
	}

	public void resetFromMoveList(final List<String> moveList) throws IllegalMoveException {
		if (!moveList.equals(this.moveList)) {
			if (!moveList.isEmpty() && moveList.subList(0, moveList.size() - 1).equals(this.moveList)) {
				doMove(moveList.get(moveList.size() - 1));
			} else {
				reset(true);
				for (final String pgn : moveList) {
					doMove(pgn);
				}
			}

		}
	}

	private void doMove(final String pgn) throws IllegalMoveException {
		final Move move = pgnToMove(pgn);
		if (move != null) {
			doMove(move, true, false);
		} else {
			logger.info(pgn + " is not contained in legal move list: " + getAllLegalMovesWithNotation().keySet());
			throw new IllegalMoveException();
		}

	}

	public Board() {
		reset(true);
	}

	public final String doMove(final Move move, final boolean recordMove, final boolean calcFullPgn) {
		try {
			if (recordMove) {
				lastMove = move;
			}
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
					if (!otherPosition.equals(start)) {
						if (otherPosition.getRank() == start.getRank()) {
							rankIsUnique = false;
						} else {

							fileIsUnique = false;
						}

					}
				}

				if (!rankIsUnique) {

					pgn = start.toString().charAt(1) + pgn;

				}

				if (!fileIsUnique || (capture != null && movedPiece instanceof Pawn)) {

					pgn = start.toString().charAt(0) + pgn;

				}

			}
			if (movedPiece.getPgnAbbreviation() != 'P') {
				pgn = movedPiece.getPgnAbbreviation() + pgn;
			}

			if (Math.abs(end.getRank() - start.getRank()) > 1 && movedPiece instanceof King) {
				//castling
				if (end.getRank() == 2) {
					//queenside
					pgn = "O-O-O";
					final Piece castle = board[0][start.getFile()];
					board[3][start.getFile()] = castle;
					castle.setPosition(Square.get(3, start.getFile()), recordMove, false);
					board[0][start.getFile()] = null;
				} else if (end.getRank() == 6) {
					//king side
					pgn = "O-O";
					final Piece castle = board[7][start.getFile()];
					board[5][start.getFile()] = castle;
					castle.setPosition(Square.get(5, start.getFile()), recordMove, false);
					board[7][start.getFile()] = null;
				}

			}

			Piece movingPiece;
			if (move.getPromote() != 'x') {
				pgn = pgn + "=" + move.getPromote();
				movingPiece = createPiece(end, move.getPromote(), movedPiece.getColor(), true);
				movedPiece.setPromotedTo(movingPiece);
			} else {
				movingPiece = movedPiece;
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
		} catch (final NullPointerException e) {

			logger.warning("doMove: " + move);
			logger.warning("moveList: " + moveList);
			logger.warning(getBoardAsText(null));
			throw e;
		}
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
		for (int file = 7; file >= 0; file--) {
			for (int rank = 0; rank < 8; rank++) {
				final Square square = Square.get(rank, file);
				sb.append(square.equals(highlightSquare) ? "[" : "|");
				final Piece p = board[rank][file];
				if (p == null) {
					sb.append("  ");
				} else {
					sb.append(p.getColor().name().charAt(0));
					sb.append(p.getPgnAbbreviation());
				}
				sb.append(square.equals(highlightSquare) ? "]" : "|");

			}
			sb.append(" ").append(file + 1);
			sb.append("\n");
			sb.append(line);
		}
		sb.append(" a   b   c   d   e   f   g   h");
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
		return isCheck() && calculateLegalMoves(false, false).isEmpty();
	}

	private List<Piece> getOtherPiecesOfSameTypeThatCanMoveToSquare(final Piece piece, final Square square) {
		final List<Piece> result = new ArrayList<Piece>();
		if (piece instanceof King || piece instanceof Pawn) {
			return result;
		}
		for (final Move move : getLastCalculatedLegalMoves()) {
			if (move.getEnd().equals(square)) {
				final Piece other = getPieceAt(move.getStart());

				if (other != null && other != piece && other.getColor() == piece.getColor() && other.getPgnAbbreviation() == piece.getPgnAbbreviation()) {
					//logger.info("found other piece:" + piece + " = " + other);
					result.add(other);
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
					castle.setPosition(Square.get(0, start.getFile()), false, false);
				} else if (end.getRank() == 6) {
					final Piece castle = board[5][start.getFile()];
					board[7][start.getFile()] = castle;
					castle.setPosition(Square.get(7, start.getFile()), false, false);
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
		//logger.info("reseting board");
		moveList = new ArrayList<Move>();
		lastMoved = null;
		for (final Piece piece : getPieces()) {
			piece.recycle();
		}
		this.board = new Piece[8][8];
		final String startPos = "RNBQKBNR";

		for (int i = 0; i < 8; i++) {
			createPiece(Square.get(i, 0), startPos.charAt(i), Color.WHITE, redraw);
			createPiece(Square.get(i, 1), 'P', Color.WHITE, redraw);
			createPiece(Square.get(i, 6), 'P', Color.BLACK, redraw);
			createPiece(Square.get(i, 7), startPos.charAt(i), Color.BLACK, redraw);
		}
	}

	private Piece createPiece(final Square square, final char abbreviation, final Color color, final boolean redraw) {
		for (final Piece piece : getPieces()) {
			if (piece.canRecycle() && piece.getPgnAbbreviation() == abbreviation && piece.getColor() == color) {
				setPieceAtSquare(piece, square, redraw);
				return piece;
			}
		}
		final Piece newPiece;
		if (abbreviation == 'P') {
			newPiece = new Pawn(color);

		} else if (abbreviation == 'Q') {
			newPiece = new Queen(color);
		} else if (abbreviation == 'R') {
			newPiece = new Rook(color);
		} else if (abbreviation == 'K') {
			newPiece = new King(color);
			if (color == Color.WHITE) {
				whiteKing = (King) newPiece;
			} else {
				blackKing = (King) newPiece;
			}
		} else if (abbreviation == 'N') {
			newPiece = new Knight(color);
		} else {
			newPiece = new Bishop(color);
		}
		setPieceAtSquare(newPiece, square, redraw);
		pieces.add(newPiece);
		if (pieceCreationHandler != null) {
			pieceCreationHandler.onPieceCreated(newPiece);
		}
		return newPiece;

	}

	public void setPieceCreationHandler(final PieceCreationHandler handler) {
		this.pieceCreationHandler = handler;
		for (final Piece piece : getPieces()) {
			pieceCreationHandler.onPieceCreated(piece);
		}
	}

	private void setPieceAtSquare(final Piece piece, final Square square, final boolean fireEvents) {
		board[square.getRank()][square.getFile()] = piece;
		piece.setPosition(square, fireEvents);

	}

	private Set<Move> calculateLegalMoves() {
		return calculateLegalMoves(false, true);
	}

	private Set<Move> calculateLegalMoves(final boolean ignoreCheck, final boolean saveResults) {
		if (!ignoreCheck && moveListAtTimeOfLastCalculation != null && moveListAtTimeOfLastCalculation.equals(this.moveList)) {
			return lastCalculatedLegalMoves;
		}
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
			if (saveResults) {
				lastCalculatedLegalMoves = legalMoves;
				moveListAtTimeOfLastCalculation = new ArrayList<Move>(moveList);
			}
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
					protectedSquares.add(Square.get(3, file));
					protectedSquares.add(Square.get(4, file));
				} else if (move.getEnd().getRank() == 6) {
					protectedSquares.add(Square.get(5, file));
					protectedSquares.add(move.getStart());
				}
			}
		}
		doMove(move, false, false);
		final King currentKing = getCurrentPlayer() != Color.WHITE ? whiteKing : blackKing;
		protectedSquares.add(currentKing.getPosition());
		for (final Move opponentMove : calculateLegalMoves(true, false)) {
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
				if (move.getPromote() != 'x' && move.matchesWithoutPromotion(new Move(start, end))) {
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

	public Move pgnToMove(final String pgn) {

		for (final Move move : calculateLegalMoves()) {
			final String pMove = doMove(move, false, true);
			undoLastMove();
			if (pMove.equals(pgn)) {
				return move;
			}
		}
		return null;
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

	public void printBoard() {
		logger.info(getBoardAsText(null));

	}

	public Move getLastMove() {
		return lastMove;
	}

	public Map<String, Move> getPgnMoveMap() {
		final Map<String, Move> result = new HashMap<String, Move>();
		for (final Move move : calculateLegalMoves()) {
			final String pgn = doMove(move, false, true);
			result.put(pgn, move);
			undoLastMove();
		}
		return result;
	}

}
