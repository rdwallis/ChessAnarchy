package com.wallissoftware.chessanarchy.shared.game;

public class Square {

	private final static Square[][] squares = new Square[8][8];

	static {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				squares[rank][file] = new Square(rank, file);
			}
		}

	}

	private final int rank;
	private final int file;

	private Square(final int rank, final int file) {
		this.rank = rank;
		this.file = file;
	}

	public static Square get(final int rank, final int file) {
		return squares[rank][file];
	}

	public int getFile() {
		return file;
	}

	public int getRank() {
		return rank;
	}

	@Override
	public String toString() {
		return ((char) (rank + 97)) + "" + (file + 1);
	}

	public static Square get(final String square) {
		final int rank = square.toLowerCase().charAt(0) - 97;
		final int file = square.charAt(1) - 49;
		return Square.get(rank, file);
	}

}
