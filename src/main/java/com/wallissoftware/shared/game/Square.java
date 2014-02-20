package com.wallissoftware.shared.game;

public class Square {

	private final int rank;
	private final int file;

	public Square(final int rank, final int file) {
		assert (rank <= 7) && (rank >= 0) : "rank cannot be: " + rank;
		assert (file <= 7) && (file >= 0) : "file cannot be: " + file;
		this.rank = rank;
		this.file = file;
	}

	public int getFile() {
		return file;
	}

	public int getRank() {
		return rank;
	}

}
