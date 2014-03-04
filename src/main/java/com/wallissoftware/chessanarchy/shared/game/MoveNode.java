package com.wallissoftware.chessanarchy.shared.game;

import java.util.HashSet;
import java.util.Set;

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

	private final MoveNode parent;

	private final Move move;

	private String pgn;

	private Set<MoveNode> children;

	private final char[][] board;

	private final int depth;

	private boolean illegal = false;

	private MoveNode(final MoveNode parent, final Move move) {
		this.parent = parent;
		this.move = move;
		if (parent == null) {
			board = rootBoard;
			depth = 0;
			pgn = null;
		} else {
			depth = parent.getDepth() + 1;
			board = new char[8][8];
			for (int rank = 0; rank < 8; rank++) {
				for (int file = 0; file < 8; file++) {
					board[rank][file] = parent.getBoard()[rank][file];
				}
			}
			checkIfParentIsIllegal();
			if (!parent.isIllegal()) {
				pgn = performMove();
			}
		}
	}

	private boolean isIllegal() {
		return illegal;
	}

	private void checkIfParentIsIllegal() {
		final Square end = move.getEnd();
		final int rank = end.getRank();
		final int file = end.getFile();
		if (isOpponentsPiece(rank, file) && isKing(rank, file)) {
			getParent().markIllegal();
		}
		final Move parentsMove = getParent().getMove();
		final Square parentEnd = parentsMove.getEnd();
		if (parentEnd.getRank() == rank && isKing(parentEnd.getRank(), parentEnd.getFile())) {
			final Square parentStart = parentsMove.getStart();
			if (Math.abs(parentStart.getRank() - parentEnd.getRank()) > 1) {
				if (parentEnd.getRank() == 2 && file < 5 && file > 1) {
					getParent().markIllegal();
				}
				if (parentEnd.getRank() == 6 && file > 5 && file < 7) {
					getParent().markIllegal();
				}
			}
		}

	}

	private void markIllegal() {
		this.illegal = true;
		getParent().getChildren(0).remove(this);

	}

	private boolean isKing(final int rank, final int file) {
		return isValidSquare(rank, file) && Character.toUpperCase(board[rank][file]) == 'K';
	}

	private boolean isPawn(final int rank, final int file) {
		return isValidSquare(rank, file) && Character.toUpperCase(board[rank][file]) == 'P';
	}

	private int getDepth() {
		return depth;
	}

	private String performMove() {
		final Square start = move.getStart();
		final Square end = move.getEnd();

		String pgn = end.toString();

		char capture = board[end.getRank()][end.getFile()];
		board[end.getRank()][end.getFile()] = move.getPromote() == 'x' ? board[start.getRank()][start.getFile()] : move.getPromote();
		final char movedPiece = Character.toUpperCase(board[start.getRank()][start.getFile()]);

		if (capture == 'x' && start.getRank() != end.getRank() && movedPiece == 'P') {
			//en passant
			capture = board[end.getRank()][start.getFile()];
			board[end.getRank()][start.getFile()] = 'x';

		}

		if (capture != 'x') {
			pgn = "x" + pgn;
		}
		if (movedPiece == 'P') {
			pgn = start.toString().charAt(0) + pgn;
		} else {
			pgn = movedPiece + pgn;
		}

		if (Math.abs(end.getRank() - start.getRank()) > 1 && movedPiece == 'K') {
			//castling
			if (end.getRank() == 2) {
				//queenside
				pgn = "O-O-O";
				board[3][start.getFile()] = start.getFile() == 0 ? 'R' : 'r';
				board[0][start.getFile()] = 'x';
			} else if (end.getRank() == 6) {
				//king side
				pgn = "O-O";
				board[5][start.getFile()] = start.getFile() == 0 ? 'R' : 'r';
				board[7][start.getFile()] = 'x';
			}

		}

		if (move.getPromote() != 'x') {
			pgn = pgn + "=" + move.getPromote();
		}

		return pgn;
	}

	public Set<MoveNode> getChildren(final int depth) {
		if (children == null) {
			children = calculateChildren();
		}
		if (depth > 0) {
			for (final MoveNode child : children) {
				child.getChildren(depth - 1);
			}
		}
		return children;
	}

	private Set<MoveNode> calculateChildren() {
		final Set<MoveNode> result = new HashSet<MoveNode>();
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				if (this.illegal) {
					result.clear();
					return result;
				}
				if (isMyPiece(rank, file)) {
					switch (Character.toUpperCase(board[rank][file])) {
					case 'N':
						for (int direction = 0; direction < 8; direction++) {
							int rankOffset = direction < 4 ? 1 : 2;
							int fileOffset = direction < 4 ? 2 : 1;
							rankOffset *= direction % 2 == 0 ? 1 : -1;
							fileOffset *= (direction / 2) % 2 == 0 ? 1 : -1;

							final int endRank = rank + rankOffset;
							final int endFile = file + fileOffset;
							if (!isMyPiece(endRank, endFile)) {
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(endRank, endFile))));
							}
						}
					case 'K':
						if (hasNeverMoved(rank, file)) {
							if (isEmptySquare(1, file) && isEmptySquare(2, file) && isEmptySquare(3, file) && hasNeverMoved(0, file)) {
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(2, file))));
							}
							if (isEmptySquare(5, file) && isEmptySquare(6, file) && hasNeverMoved(7, file)) {
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(6, file))));
							}
						}
					case 'Q':
					case 'R':
						for (int direction = 0; direction < 4; direction++) {
							for (int offset = 1;; offset++) {
								final int endRank = rank + direction % 2 == 0 ? offset : 0;
								final int endFile = file + direction < 2 ? offset : 0;
								if (!isMyPiece(endRank, endFile)) {
									result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(endRank, endFile))));
								}
								if (!isEmptySquare(endRank, endFile) || Character.toUpperCase(board[rank][file]) == 'K') {
									break;
								}

							}
						}
						if (Character.toUpperCase(board[rank][file]) == 'R') {
							break;
						}
					case 'B':
						for (int direction = 0; direction < 4; direction++) {
							for (int offset = 1;; offset++) {
								final int endRank = rank + direction % 2 == 0 ? offset : -offset;
								final int endFile = file + direction < 2 ? offset : -offset;
								if (!isMyPiece(endRank, endFile)) {
									result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(endRank, endFile))));
								}
								if (!isEmptySquare(endRank, endFile) || Character.toUpperCase(board[rank][file]) == 'K') {
									break;
								}

							}
						}
					case 'P':
						final int fileOffset = isWhitePiece(rank, file) ? 1 : -1;
						final int endFile = file + fileOffset;
						if (isOpponentsPiece(rank - 1, endFile)) {
							if (endFile == 0 || endFile == 7) {
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank - 1, endFile), isWhitesTurn() ? 'Q' : 'q')));
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank - 1, endFile), isWhitesTurn() ? 'R' : 'r')));
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank - 1, endFile), isWhitesTurn() ? 'B' : 'b')));
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank - 1, endFile), isWhitesTurn() ? 'N' : 'n')));
							} else {
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank - 1, endFile))));
							}
						}

						if (isOpponentsPiece(rank + 1, endFile)) {

							if (endFile == 0 || endFile == 7) {
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank + 1, endFile), isWhitesTurn() ? 'Q' : 'q')));
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank + 1, endFile), isWhitesTurn() ? 'R' : 'r')));
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank + 1, endFile), isWhitesTurn() ? 'B' : 'b')));
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank + 1, endFile), isWhitesTurn() ? 'N' : 'n')));
							} else {
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank + 1, endFile))));
							}
						}

						if (isEmptySquare(rank, endFile)) {
							if (endFile == 0 || endFile == 7) {
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank, endFile), isWhitesTurn() ? 'Q' : 'q')));
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank, endFile), isWhitesTurn() ? 'R' : 'r')));
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank, endFile), isWhitesTurn() ? 'B' : 'b')));
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank, endFile), isWhitesTurn() ? 'N' : 'n')));
							} else {
								result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank, endFile))));
								if ((isWhitesTurn() && file == 1) || (!isWhitesTurn() && file == 6)) {
									if (isEmptySquare(rank, endFile + fileOffset)) {
										result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank, endFile + fileOffset))));
									}
								}
							}
						}

						//en passant
						if ((isWhitesTurn() && file == 4) || (!isWhitesTurn() && file == 3)) {
							if (rank - 1 >= 0) {
								if (isOpponentsPiece(rank - 1, file) && isPawn(rank - 1, file)) {
									if (getParent().getMove().getEnd() == Square.get(rank - 1, file) && Math.abs(getParent().move.getStart().getFile() - file) == 2) {
										result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank - 1, endFile))));
									}
								}
							}
							if (rank + 1 <= 7) {
								if (isOpponentsPiece(rank - 1, file) && isPawn(rank + 1, file)) {
									if (getParent().getMove().getEnd() == Square.get(rank - 1, file) && Math.abs(getParent().move.getStart().getFile() - file) == 2) {
										result.add(new MoveNode(this, new Move(Square.get(rank, file), Square.get(rank - 1, endFile))));
									}
								}
							}

						}

						break;
					}
				}
			}
		}
		return result;
	}

	private boolean hasNeverMoved(final int rank, final int file) {
		return hasNeverMoved(rank, file, true);
	}

	private boolean hasNeverMoved(final int rank, final int file, final boolean checkRootBoard) {
		if (getParent() == null) {
			return true;
		}
		if (checkRootBoard && rootBoard[rank][file] != getBoard()[rank][file]) {
			return false;
		}
		return getBoard()[rank][file] == getParent().getBoard()[rank][file] && getParent().hasNeverMoved(rank, file, false);
	}

	private boolean isMyPiece(final int rank, final int file) {
		return isValidSquare(rank, file) && !isEmptySquare(rank, file) && (isWhitesTurn() ^ !isWhitePiece(rank, file));
	}

	private boolean isEmptySquare(final int rank, final int file) {
		return isValidSquare(rank, file) && board[rank][file] == 'x';
	}

	private boolean isValidSquare(final int rank, final int file) {
		return rank > 0 && rank < 8 && file > 0 && file < 8;
	}

	private boolean isOpponentsPiece(final int rank, final int file) {
		return isValidSquare(rank, file) && !isEmptySquare(rank, file) && (isWhitesTurn() ^ isWhitePiece(rank, file));
	}

	private boolean isWhitePiece(final int rank, final int file) {
		return isValidSquare(rank, file) && Character.isUpperCase(board[rank][file]);
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

}
