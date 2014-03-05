package com.wallissoftware.chessanarchy.shared.game;

public class Square {

	private final static Square[][] squares = new Square[8][8];

	static {
		for (int file = 0; file < 8; file++) {
			for (int rank = 0; rank < 8; rank++) {
				squares[file][rank] = new Square(file, rank);
			}
		}

	}

	private final int file;
	private final int rank;

	private Square(final int file, final int rank) {
		this.file = file;
		this.rank = rank;
	}

	public static Square get(final int file, final int rank) {
		return squares[file][rank];
	}

	public int getRank() {
		return rank;
	}

	public int getFile() {
		return file;
	}

	@Override
	public String toString() {
		return ((char) (file + 97)) + "" + (rank + 1);
	}

	public static Square get(final String square) {
		final int file = square.toLowerCase().charAt(0) - 97;
		final int rank = square.charAt(1) - 49;
		return Square.get(file, rank);
	}

}
