package com.wallissoftware.chessanarchy.shared.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;

public class MoveTree {

	private final static char[][] rootBoard = new char[8][8];

	static {
		final String startPos = "RNBQKBNR";
		for (int i = 0; i < 8; i++) {
			rootBoard[i][0] = startPos.charAt(i);
			rootBoard[i][1] = 'P';
			for (int j = 2; j < 6; j++) {
				rootBoard[i][j] = 'x';
			}
			rootBoard[i][6] = 'p';
			rootBoard[i][7] = startPos.toLowerCase().charAt(i);
		}
	}
	private final static Logger logger = Logger.getLogger(MoveTree.class.getName());

	private final static MoveTree root;
	static {
		try {
			root = new MoveTree(null, null);
		} catch (final IllegalMoveException e) {
			throw new RuntimeException();
		}
	}

	private Boolean isCheck = null, isCheckMate = null;

	public static MoveTree get(final List<String> pgnMoves) throws IllegalMoveException {
		MoveTree node = root;
		for (final String move : pgnMoves) {
			node = node.getChild(move);
		}
		return node;
	}

	private MoveTree getChild(final String pgn) throws IllegalMoveException {
		for (final MoveTree child : getChildren()) {

			if (child.matchesPgn(pgn)) {
				return child;
			}
		}
		logger.info("Could not find move: " + pgn);
		logger.info("AvailableMoves = " + getLegalMoveMap());
		logBoard();
		throw new IllegalMoveException();
	}

	@SuppressWarnings("unused")
	private void logBoard() {
		if (LogConfiguration.loggingIsEnabled()) {
			final StringBuilder sb = new StringBuilder();
			sb.append("\n" + getFullPgn() + "\n");
			sb.append("\n" + toString() + "\n");
			final String line = "-----------------\n";
			sb.append(line);
			for (int rank = 7; rank >= 0; rank--) {
				sb.append("|");
				for (int file = 0; file < 8; file++) {
					sb.append((board[file][rank] == 'x' ? ' ' : board[file][rank]) + "|");

				}
				sb.append(" " + (rank + 1) + "\n");
				sb.append(line);
			}

			sb.append(" a b c d e f g h");
			logger.info(sb.toString());
		}

	}

	private boolean matchesPgn(final String pgn) {
		if (pgn.startsWith(this.pgn)) {
			return getPgn().equals(pgn);
		} else {
			return false;
		}

	}

	public String getPgn() {
		return pgn + (isCheck() ? (isCheckMate() ? "#" : "+") : "");
	}

	private final MoveTree parent;

	private final Move move;

	private String pgn;

	private Set<MoveTree> children;

	private final char[][] board;

	private final int depth;

	private MoveTree(final MoveTree parent, final Move move) throws IllegalMoveException {
		this.parent = parent;
		this.move = move;
		if (parent == null) {
			board = rootBoard;
			depth = 0;
			pgn = null;
		} else {
			depth = parent.getDepth() + 1;
			board = new char[8][8];
			for (int file = 0; file < 8; file++) {
				for (int rank = 0; rank < 8; rank++) {
					board[file][rank] = parent.getBoard()[file][rank];
				}
			}

			pgn = performMove();
			if (calculateIsCheck(true)) {
				throw new IllegalMoveException();
			}

		}
	}

	private String performMove() {
		String pgn = move.getEndSquare();

		char capture = board[move.getEndFile()][move.getEndRank()];
		board[move.getEndFile()][move.getEndRank()] = move.isPromotion() ? move.getPromotion() : board[move.getStartFile()][move.getStartRank()];
		final char movedPiece = Character.toUpperCase(board[move.getStartFile()][move.getStartRank()]);

		if (capture == 'x' && move.getStartFile() != move.getEndFile() && movedPiece == 'P') {
			//en passant
			capture = board[move.getEndFile()][move.getStartRank()];
			board[move.getEndFile()][move.getStartRank()] = 'x';

		}

		if (capture != 'x') {
			pgn = "x" + pgn;
			if (movedPiece == 'P') {
				pgn = move.getStartFileAsChar() + pgn;
			}
		}
		if (movedPiece != 'P') {
			pgn = movedPiece + pgn;
		}

		if (move.getFileDelta() > 1 && movedPiece == 'K') {
			//castling
			if (move.getEndFile() == 2) {
				//queenside
				pgn = "O-O-O";
				board[3][move.getStartRank()] = move.getStartRank() == 0 ? 'R' : 'r';
				board[0][move.getStartRank()] = 'x';
			} else if (move.getEndFile() == 6) {
				//king side
				pgn = "O-O";
				board[5][move.getStartRank()] = move.getStartRank() == 0 ? 'R' : 'r';
				board[7][move.getStartRank()] = 'x';
			}

		}

		if (move.isPromotion()) {
			pgn = pgn + "=" + Character.toUpperCase(move.getPromotion());
		}
		board[move.getStartFile()][move.getStartRank()] = 'x';

		return pgn;
	}

	public Set<MoveTree> getChildren() {
		if (children == null) {
			calculateChildren();
			checkAmbiguousChildren();
		}

		return children;
	}

	private void checkAmbiguousChildren() {
		final Map<String, List<MoveTree>> pgnMap = new HashMap<String, List<MoveTree>>();
		for (final MoveTree child : children) {
			if (!pgnMap.containsKey(child.pgn)) {
				pgnMap.put(child.pgn, new ArrayList<MoveTree>());
			}
			pgnMap.get(child.pgn).add(child);
		}

		for (final List<MoveTree> ambiguousGroup : pgnMap.values()) {
			if (ambiguousGroup.size() > 1) {
				for (int i = 0; i < ambiguousGroup.size(); i++) {
					for (int j = 0; j < ambiguousGroup.size(); j++) {
						if (j != i) {
							ambiguousGroup.get(i).refinePgn(ambiguousGroup.get(j).getMove());
						}
					}
				}
			}
		}

	}

	private void refinePgn(final Move other) {

		if (move.getStartFile() != other.getStartFile()) {
			pgn = pgn.substring(0, 1) + move.getStartFileAsChar() + pgn.substring(1);
		} else if (move.getStartRank() != other.getStartRank()) {
			if (Character.isUpperCase(pgn.charAt(0))) {
				pgn = pgn.substring(0, 1) + (move.getStartRank() + 1) + pgn.substring(1);
			} else {
				pgn = (move.getStartRank() + 1) + pgn;
			}
		}

	}

	@Override
	public String toString() {
		return "MoveNode [move=" + move + ", pgn=" + pgn + ", depth=" + depth + "]";
	}

	private String getFullPgn() {
		if (getMove() != null) {
			return getParent().getFullPgn() + (!isWhitesTurn() ? ((getDepth() / 2) + 1) + ". " : "") + pgn + " ";
		}
		return "";
	}

	private boolean isCheckMate() {
		if (!isCheck()) {
			return false;
		}
		if (isCheckMate != null) {
			return isCheckMate;
		}
		isCheckMate = calculateIsCheckMate();
		return isCheckMate;

	}

	private boolean calculateIsCheckMate() {
		return getChildren().isEmpty();

	}

	private boolean isCheck() {
		if (isCheck != null) {
			return isCheck;
		}
		isCheck = calculateIsCheck(false);
		return isCheck;
	}

	private boolean calculateIsCheck(final boolean swapSides) {
		for (int file = 0; file < 8; file++) {
			for (int rank = 0; rank < 8; rank++) {
				if (isKing(file, rank) && (swapSides ^ isMyPiece(file, rank))) {

					//check for knights
					for (int direction = 0; direction < 8; direction++) {
						int fileOffset = direction < 4 ? 1 : 2;
						int rankOffset = direction < 4 ? 2 : 1;
						fileOffset *= direction % 2 == 0 ? 1 : -1;
						rankOffset *= (direction / 2) % 2 == 0 ? 1 : -1;

						final int endFile = file + fileOffset;
						final int endRank = rank + rankOffset;

						if (isKnight(endFile, endRank) && (swapSides ^ isOpponentsPiece(endFile, endRank))) {

							return true;
						}
					}

					//check for pawns
					final int rankOffset = isWhitesTurn() ^ swapSides ? 1 : -1;
					if (isPawn(file - 1, rank + rankOffset) && (swapSides ^ isOpponentsPiece(file - 1, rank + rankOffset))) {

						return true;
					}
					if (isPawn(file + 1, rank + rankOffset) && (swapSides ^ isOpponentsPiece(file + 1, rank + rankOffset))) {

						return true;
					}

					for (int direction = 0; direction < 4; direction++) {
						for (int offset = 1;; offset++) {
							final int endFile = file + (direction < 2 ? 0 : direction % 2 == 0 ? offset : -offset);
							final int endRank = rank + (direction < 2 ? direction % 2 == 0 ? offset : -offset : 0);

							if (isQueen(endFile, endRank) || isRook(endFile, endRank) || (offset == 1 && isKing(endFile, endRank))) {
								if (swapSides ^ isOpponentsPiece(endFile, endRank)) {

									return true;
								}
							}

							if (!isEmptySquare(endFile, endRank)) {
								break;
							}

						}
					}

					for (int direction = 0; direction < 4; direction++) {
						for (int offset = 1;; offset++) {
							final int endFile = file + (direction % 2 == 0 ? offset : -offset);
							final int endRank = rank + (direction < 2 ? offset : -offset);
							if (isQueen(endFile, endRank) || isBishop(endFile, endRank) || (offset == 1 && isKing(endFile, endRank))) {
								if (swapSides ^ isOpponentsPiece(endFile, endRank)) {
									return true;
								}
							}
							if (!isEmptySquare(endFile, endRank)) {
								break;
							}

						}

					}
					return false;
				}
			}

		}
		logBoard();
		throw new RuntimeException("Could not find king");

	}

	private void calculateChildren() {
		children = new HashSet<MoveTree>();

		for (int file = 0; file < 8; file++) {
			for (int rank = 0; rank < 8; rank++) {

				if (isMyPiece(file, rank)) {
					switch (Character.toUpperCase(board[file][rank])) {
					case 'N':

						for (int direction = 0; direction < 8; direction++) {
							int fileOffset = direction < 4 ? 1 : 2;
							int rankOffset = direction < 4 ? 2 : 1;
							fileOffset *= direction % 2 == 0 ? 1 : -1;
							rankOffset *= (direction / 2) % 2 == 0 ? 1 : -1;

							final int endFile = file + fileOffset;
							final int endRank = rank + rankOffset;

							if (isValidSquare(endFile, endRank) && !isMyPiece(endFile, endRank)) {
								addChildMove(new Move(file, rank, endFile, endRank));
							}
						}
						break;
					case 'K':
						if (hasNeverMoved(file, rank)) {
							if (isEmptySquare(1, rank) && isEmptySquare(2, rank) && isEmptySquare(3, rank) && hasNeverMoved(0, rank)) {
								addChildMove(new Move(file, rank, 2, rank));
							}
							if (isEmptySquare(5, rank) && isEmptySquare(6, rank) && hasNeverMoved(7, rank)) {
								addChildMove(new Move(file, rank, 6, rank));
							}
						}
					case 'Q':

					case 'R':
						for (int direction = 0; direction < 4; direction++) {
							for (int offset = 1;; offset++) {
								final int endFile = file + (direction < 2 ? 0 : direction % 2 == 0 ? offset : -offset);
								final int endRank = rank + (direction < 2 ? direction % 2 == 0 ? offset : -offset : 0);
								if (isValidSquare(endFile, endRank) && !isMyPiece(endFile, endRank)) {
									addChildMove(new Move(file, rank, endFile, endRank));
								}
								if (!isEmptySquare(endFile, endRank) || isKing(file, rank)) {
									break;
								}

							}
						}
						if (Character.toUpperCase(board[file][rank]) == 'R') {
							break;
						}
					case 'B':
						for (int direction = 0; direction < 4; direction++) {
							for (int offset = 1;; offset++) {
								final int endFile = file + (direction % 2 == 0 ? offset : -offset);
								final int endRank = rank + (direction < 2 ? offset : -offset);
								if (isValidSquare(endFile, endRank) && !isMyPiece(endFile, endRank)) {
									addChildMove(new Move(file, rank, endFile, endRank));
								}
								if (!isEmptySquare(endFile, endRank) || isKing(file, rank)) {
									break;
								}

							}
						}
						break;
					case 'P':
						final int rankOffset = isWhitePiece(file, rank) ? 1 : -1;
						final int endRank = rank + rankOffset;
						if (isOpponentsPiece(file - 1, endRank)) {
							if (endRank == 0 || endRank == 7) {
								addChildMove(new Move(file, rank, file - 1, endRank, isWhitesTurn() ? 'Q' : 'q'));
								addChildMove(new Move(file, rank, file - 1, endRank, isWhitesTurn() ? 'R' : 'r'));
								addChildMove(new Move(file, rank, file - 1, endRank, isWhitesTurn() ? 'B' : 'b'));
								addChildMove(new Move(file, rank, file - 1, endRank, isWhitesTurn() ? 'N' : 'n'));
							} else {
								addChildMove(new Move(file, rank, file - 1, endRank));
							}
						}

						if (isOpponentsPiece(file + 1, endRank)) {

							if (endRank == 0 || endRank == 7) {
								addChildMove(new Move(file, rank, file + 1, endRank, isWhitesTurn() ? 'Q' : 'q'));
								addChildMove(new Move(file, rank, file + 1, endRank, isWhitesTurn() ? 'R' : 'r'));
								addChildMove(new Move(file, rank, file + 1, endRank, isWhitesTurn() ? 'B' : 'b'));
								addChildMove(new Move(file, rank, file + 1, endRank, isWhitesTurn() ? 'N' : 'n'));
							} else {
								addChildMove(new Move(file, rank, file + 1, endRank));
							}
						}

						if (isEmptySquare(file, endRank)) {
							if (endRank == 0 || endRank == 7) {
								addChildMove(new Move(file, rank, file, endRank, isWhitesTurn() ? 'Q' : 'q'));
								addChildMove(new Move(file, rank, file, endRank, isWhitesTurn() ? 'R' : 'r'));
								addChildMove(new Move(file, rank, file, endRank, isWhitesTurn() ? 'B' : 'b'));
								addChildMove(new Move(file, rank, file, endRank, isWhitesTurn() ? 'N' : 'n'));
							} else {
								addChildMove(new Move(file, rank, file, endRank));
								if ((isWhitesTurn() && rank == 1) || (!isWhitesTurn() && rank == 6)) {
									if (isEmptySquare(file, endRank + rankOffset)) {
										addChildMove(new Move(file, rank, file, endRank + rankOffset));
									}
								}
							}
						}

						//en passant
						if (!isRootNode() && getMove().getEndRank() == rank && getMove().getRankDelta() == 2 && ((isWhitesTurn() && rank == 4) || (!isWhitesTurn() && rank == 3))) {
							if (file - 1 >= 0) {
								if (isOpponentsPiece(file - 1, rank) && isPawn(file - 1, rank)) {
									if (getMove().getEndFile() == file - 1) {
										addChildMove(new Move(file, rank, file - 1, endRank));
									}
								}
							}
							if (file + 1 <= 7) {
								if (isOpponentsPiece(file + 1, rank) && isPawn(file + 1, rank)) {
									if (getMove().getEndFile() == file + 1) {
										addChildMove(new Move(file, rank, file + 1, endRank));

									}
								}
							}
						}

						break;
					}
				}
			}
		}

	}

	private void addChildMove(final Move move) {
		try {
			children.add(new MoveTree(this, move));
		} catch (final IllegalMoveException e) {
			//ignore
		}

	}

	private boolean hasNeverMoved(final int file, final int rank) {
		return hasNeverMoved(file, rank, true);
	}

	private boolean hasNeverMoved(final int file, final int rank, final boolean checkRootBoard) {
		if (getParent() == null) {
			return true;
		}
		if (checkRootBoard && rootBoard[file][rank] != getBoard()[file][rank]) {
			return false;
		}
		return getBoard()[file][rank] == getParent().getBoard()[file][rank] && getParent().hasNeverMoved(file, rank, false);
	}

	private boolean isMyPiece(final int file, final int rank) {
		return isValidSquare(file, rank) && !isEmptySquare(file, rank) && (isWhitesTurn() ^ !isWhitePiece(file, rank));
	}

	public boolean isEmptySquare(final int file, final int rank) {
		return isValidSquare(file, rank) && board[file][rank] == 'x';
	}

	private boolean isValidSquare(final int file, final int rank) {
		return file >= 0 && file < 8 && rank >= 0 && rank < 8;
	}

	private boolean isOpponentsPiece(final int file, final int rank) {
		return isValidSquare(file, rank) && !isEmptySquare(file, rank) && (isWhitesTurn() ^ isWhitePiece(file, rank));
	}

	public boolean isWhitePiece(final int file, final int rank) {
		return isValidSquare(file, rank) && Character.isUpperCase(board[file][rank]);
	}

	private boolean isWhitesTurn() {
		return depth % 2 == 0;
	}

	public char[][] getBoard() {
		return board;
	}

	public MoveTree getParent() {
		return parent;
	}

	public Move getMove() {
		return move;
	}

	private boolean isKnight(final int file, final int rank) {
		return isValidSquare(file, rank) && Character.toUpperCase(board[file][rank]) == 'N';
	}

	private boolean isKing(final int file, final int rank) {
		return isValidSquare(file, rank) && Character.toUpperCase(board[file][rank]) == 'K';
	}

	private boolean isPawn(final int file, final int rank) {
		return isValidSquare(file, rank) && Character.toUpperCase(board[file][rank]) == 'P';
	}

	private boolean isRook(final int file, final int rank) {
		return isValidSquare(file, rank) && Character.toUpperCase(board[file][rank]) == 'R';
	}

	private boolean isQueen(final int file, final int rank) {
		return isValidSquare(file, rank) && Character.toUpperCase(board[file][rank]) == 'Q';
	}

	private boolean isBishop(final int file, final int rank) {
		return isValidSquare(file, rank) && Character.toUpperCase(board[file][rank]) == 'B';
	}

	private int getDepth() {
		return depth;
	}

	public static MoveTree getRoot() {
		return root;
	}

	public Map<String, String> getLegalMoveMap() {
		final Map<String, String> result = new HashMap<String, String>();
		for (final MoveTree child : getChildren()) {
			result.put(child.getPgn(), child.getPgn());
			result.put(child.getMove().toString(), child.getPgn());
		}
		return result;

	}

	public Map<String, Move> getPgnMoveMap() {
		final Map<String, Move> result = new HashMap<String, Move>();
		for (final MoveTree child : getChildren()) {
			result.put(child.getPgn(), child.getMove());
			result.put(child.getMove().toString(), child.getMove());
		}
		return result;

	}

	public int getMovesUntilDraw() {
		return 50 - getTimeSinceLastPawnMoveOrCapture();
	}

	private int getTimeSinceLastPawnMoveOrCapture() {
		if (move == null || isPawnMoveOrCapture()) {
			return 0;
		} else {
			return getParent().getTimeSinceLastPawnMoveOrCapture() + 1;
		}
	}

	public boolean isDraw() {
		return depth > 50 && getMovesUntilDraw() <= 0;
	}

	private boolean isPawnMoveOrCapture() {
		return pgn.contains("x") || Character.isLowerCase(pgn.charAt(0));
	}

	public Color getCurrentPlayer() {
		return isWhitesTurn() ? Color.WHITE : Color.BLACK;
	}

	public MoveTree getChild(final Move move) throws IllegalMoveException {
		for (final MoveTree child : getChildren()) {

			if (child.getMove().equals(move)) {
				return child;
			}
		}
		throw new IllegalMoveException();
	}

	public boolean isRootNode() {
		return getMove() == null;
	}

	public boolean isMoveLegal(final Move move) {
		for (final MoveTree child : getChildren()) {
			if (child.getMove().matchesIgnoringPromotion(move)) {
				return true;
			}
		}
		return false;

	}

	public boolean isMovePromotion(final Move move) {
		for (final MoveTree child : getChildren()) {
			if (child.getMove().matchesIgnoringPromotion((move))) {
				return child.getMove().isPromotion();
			}
		}
		return false;
	}

	public boolean isGameFinished() {
		return isCheckMate() || isDraw();
	}
}
