package com.wallissoftware.chessanarchy.shared.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;

public class MoveNode {

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
	private final static Logger logger = Logger.getLogger(MoveNode.class.getName());

	private final static MoveNode root;
	static {
		try {
			root = new MoveNode(null, null);
		} catch (final IllegalMoveException e) {
			throw new RuntimeException();
		}
	}

	private Boolean isCheck = null, isCheckMate = null;

	public static char[][] getBoard(final List<String> pgnMoves) throws IllegalMoveException {
		MoveNode node = root;
		for (final String move : pgnMoves) {
			node = node.getChild(move);
		}
		return node.getBoard();
	}

	private MoveNode getChild(final String pgn) throws IllegalMoveException {
		for (final MoveNode child : getChildren()) {
			//logger.info("child: " + child.getPgn());
			if (child.getPgn().equals(pgn)) {
				return child;
			}
		}
		/*logger.info("Could not find child: " + pgn);
		final StringBuilder sb = new StringBuilder();
		sb.append("Available moves: ");
		for (final MoveNode child : getChildren()) {
			sb.append(child.getPgn()).append(" ");
		}
		logger.info(sb.toString());

		//logBoard();*/
		throw new IllegalMoveException();
	}

	private void logBoard() {
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

	private String getPgn() {
		return pgn + (isCheck() ? (isCheckMate() ? "#" : "+") : "");
	}

	private final MoveNode parent;

	private final Move move;

	private String pgn;

	private Set<MoveNode> children;

	private final char[][] board;

	private final int depth;

	private MoveNode(final MoveNode parent, final Move move) throws IllegalMoveException {
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

	/*private void checkIfParentIsIllegal() {
		if (parent.isIllegal()) {
			return;
		}
		final Square end = move.getEnd();
		final int file = end.getFile();
		final int rank = end.getRank();
		if (isMyPiece(file, rank) && isKing(file, rank)) {
			logger.info(move + " makes parent illegal 1");
			getParent().markIllegal();
		}
		final Move parentsMove = getParent().getMove();
		if (parentsMove != null) {
			final Square parentEnd = parentsMove.getEnd();
			if (parentEnd.getRank() == rank && isKing(parentEnd.getFile(), parentEnd.getRank())) {
				final Square parentStart = parentsMove.getStart();
				if (Math.abs(parentStart.getFile() - parentEnd.getFile()) > 1) {
					if (parentEnd.getFile() == 2 && file < 5 && file > 1) {
						logger.info(move + " makes parent illegal 2");
						getParent().markIllegal();
					}
					if (parentEnd.getFile() == 6 && file > 5 && file < 7) {
						getParent().markIllegal();
						logger.info(move + " makes parent illegal 3");
					}
				}
			}
		}

	}*/

	private String performMove() {
		final Square start = move.getStart();
		final Square end = move.getEnd();

		String pgn = end.toString();

		char capture = board[end.getFile()][end.getRank()];
		board[end.getFile()][end.getRank()] = move.getPromote() == 'x' ? board[start.getFile()][start.getRank()] : move.getPromote();
		final char movedPiece = Character.toUpperCase(board[start.getFile()][start.getRank()]);

		if (capture == 'x' && start.getFile() != end.getFile() && movedPiece == 'P') {
			//en passant
			capture = board[end.getFile()][start.getRank()];
			board[end.getFile()][start.getRank()] = 'x';

		}

		if (capture != 'x') {
			pgn = "x" + pgn;
			if (movedPiece == 'P') {
				pgn = start.toString().charAt(0) + pgn;
			}
		}
		if (movedPiece != 'P') {
			pgn = movedPiece + pgn;
		}

		if (Math.abs(end.getFile() - start.getFile()) > 1 && movedPiece == 'K') {
			//castling
			if (end.getFile() == 2) {
				//queenside
				pgn = "O-O-O";
				board[3][start.getRank()] = start.getRank() == 0 ? 'R' : 'r';
				board[0][start.getRank()] = 'x';
			} else if (end.getFile() == 6) {
				//king side
				pgn = "O-O";
				board[5][start.getRank()] = start.getRank() == 0 ? 'R' : 'r';
				board[7][start.getRank()] = 'x';
			}

		}

		if (move.getPromote() != 'x') {
			pgn = pgn + "=" + move.getPromote();
		}
		board[start.getFile()][start.getRank()] = 'x';

		return pgn;
	}

	public Set<MoveNode> getChildren() {
		if (children == null) {
			calculateChildren();
			checkAmbiguousChildren();
		}

		return children;
	}

	private void checkAmbiguousChildren() {
		final Map<String, List<MoveNode>> pgnMap = new HashMap<String, List<MoveNode>>();
		for (final MoveNode child : children) {
			if (!pgnMap.containsKey(child.getPgn())) {
				pgnMap.put(child.getPgn(), new ArrayList<MoveNode>());
			}
			pgnMap.get(child.getPgn()).add(child);
		}

		for (final List<MoveNode> ambiguousGroup : pgnMap.values()) {
			if (ambiguousGroup.size() > 1) {
				for (int i = 0; i < ambiguousGroup.size(); i++) {
					for (int j = 0; j < ambiguousGroup.size(); j++) {
						if (j != i) {
							ambiguousGroup.get(i).refinePgn(ambiguousGroup.get(j));
						}
					}
				}
			}
		}

	}

	private void refinePgn(final MoveNode other) {
		final Square myStart = getMove().getStart();
		final Square otherStart = other.getMove().getStart();

		if (myStart.getFile() != otherStart.getFile()) {
			if (Character.isUpperCase(pgn.charAt(0))) {
				pgn = pgn.substring(0, 1) + myStart.toString().charAt(0) + pgn.substring(1);
			} else {
				pgn = pgn + myStart.toString().charAt(0);
			}
		} else if (myStart.getRank() != otherStart.getRank()) {
			if (Character.isUpperCase(pgn.charAt(0))) {
				pgn = pgn.substring(0, 1) + myStart.toString().charAt(1) + pgn.substring(1);
			} else {
				pgn = pgn + myStart.toString().charAt(1);
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
		throw new RuntimeException("Could not find king");

	}

	private void calculateChildren() {
		children = new HashSet<MoveNode>();

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
								addChildMove(new Move(Square.get(file, rank), Square.get(endFile, endRank)));
							}
						}
						break;
					case 'K':
						if (hasNeverMoved(file, rank)) {
							if (isEmptySquare(1, rank) && isEmptySquare(2, rank) && isEmptySquare(3, rank) && hasNeverMoved(0, rank)) {
								addChildMove(new Move(Square.get(file, rank), Square.get(2, rank)));
							}
							if (isEmptySquare(5, rank) && isEmptySquare(6, rank) && hasNeverMoved(7, rank)) {
								addChildMove(new Move(Square.get(file, rank), Square.get(6, rank)));
							}
						}
					case 'Q':

					case 'R':
						for (int direction = 0; direction < 4; direction++) {
							for (int offset = 1;; offset++) {
								final int endFile = file + (direction < 2 ? 0 : direction % 2 == 0 ? offset : -offset);
								final int endRank = rank + (direction < 2 ? direction % 2 == 0 ? offset : -offset : 0);
								if (isValidSquare(endFile, endRank) && !isMyPiece(endFile, endRank)) {
									addChildMove(new Move(Square.get(file, rank), Square.get(endFile, endRank)));
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
									addChildMove(new Move(Square.get(file, rank), Square.get(endFile, endRank)));
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
								addChildMove(new Move(Square.get(file, rank), Square.get(file - 1, endRank), isWhitesTurn() ? 'Q' : 'q'));
								addChildMove(new Move(Square.get(file, rank), Square.get(file - 1, endRank), isWhitesTurn() ? 'R' : 'r'));
								addChildMove(new Move(Square.get(file, rank), Square.get(file - 1, endRank), isWhitesTurn() ? 'B' : 'b'));
								addChildMove(new Move(Square.get(file, rank), Square.get(file - 1, endRank), isWhitesTurn() ? 'N' : 'n'));
							} else {
								addChildMove(new Move(Square.get(file, rank), Square.get(file - 1, endRank)));
							}
						}

						if (isOpponentsPiece(file + 1, endRank)) {

							if (endRank == 0 || endRank == 7) {
								addChildMove(new Move(Square.get(file, rank), Square.get(file + 1, endRank), isWhitesTurn() ? 'Q' : 'q'));
								addChildMove(new Move(Square.get(file, rank), Square.get(file + 1, endRank), isWhitesTurn() ? 'R' : 'r'));
								addChildMove(new Move(Square.get(file, rank), Square.get(file + 1, endRank), isWhitesTurn() ? 'B' : 'b'));
								addChildMove(new Move(Square.get(file, rank), Square.get(file + 1, endRank), isWhitesTurn() ? 'N' : 'n'));
							} else {
								addChildMove(new Move(Square.get(file, rank), Square.get(file + 1, endRank)));
							}
						}

						if (isEmptySquare(file, endRank)) {
							if (endRank == 0 || endRank == 7) {
								addChildMove(new Move(Square.get(file, rank), Square.get(file, endRank), isWhitesTurn() ? 'Q' : 'q'));
								addChildMove(new Move(Square.get(file, rank), Square.get(file, endRank), isWhitesTurn() ? 'R' : 'r'));
								addChildMove(new Move(Square.get(file, rank), Square.get(file, endRank), isWhitesTurn() ? 'B' : 'b'));
								addChildMove(new Move(Square.get(file, rank), Square.get(file, endRank), isWhitesTurn() ? 'N' : 'n'));
							} else {
								addChildMove(new Move(Square.get(file, rank), Square.get(file, endRank)));
								if ((isWhitesTurn() && rank == 1) || (!isWhitesTurn() && rank == 6)) {
									if (isEmptySquare(file, endRank + rankOffset)) {
										addChildMove(new Move(Square.get(file, rank), Square.get(file, endRank + rankOffset)));
									}
								}
							}
						}

						//en passant
						if ((isWhitesTurn() && rank == 4) || (!isWhitesTurn() && rank == 3)) {

							if (file - 1 >= 0) {
								if (isOpponentsPiece(file - 1, rank) && isPawn(file - 1, rank)) {

									if (getMove().getEnd() == Square.get(file - 1, rank) && Math.abs(getMove().getStart().getRank() - getMove().getEnd().getRank()) == 2) {
										addChildMove(new Move(Square.get(file, rank), Square.get(file - 1, endRank)));

									}
								}
							}
							if (file + 1 <= 7) {
								if (isOpponentsPiece(file + 1, rank) && isPawn(file + 1, rank)) {

									if (getMove().getEnd() == Square.get(file + 1, rank) && Math.abs(getMove().getStart().getRank() - getMove().getEnd().getRank()) == 2) {
										addChildMove(new Move(Square.get(file, rank), Square.get(file + 1, endRank)));

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
			children.add(new MoveNode(this, move));
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

	private boolean isEmptySquare(final int file, final int rank) {
		return isValidSquare(file, rank) && board[file][rank] == 'x';
	}

	private boolean isValidSquare(final int file, final int rank) {
		return file >= 0 && file < 8 && rank >= 0 && rank < 8;
	}

	private boolean isOpponentsPiece(final int file, final int rank) {
		return isValidSquare(file, rank) && !isEmptySquare(file, rank) && (isWhitesTurn() ^ isWhitePiece(file, rank));
	}

	private boolean isWhitePiece(final int file, final int rank) {
		return isValidSquare(file, rank) && Character.isUpperCase(board[file][rank]);
	}

	private boolean isWhitesTurn() {
		return depth % 2 == 0;
	}

	private char[][] getBoard() {
		return board;
	}

	private MoveNode getParent() {
		return parent;
	}

	private Move getMove() {
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
}
