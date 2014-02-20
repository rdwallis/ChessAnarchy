package com.wallissoftware.chessanarchy.shared.game;

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

	@Override
	public String toString() {
		return ((char) (rank + 97)) + "" + (file + 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + file;
		result = prime * result + rank;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Square other = (Square) obj;
		if (file != other.file)
			return false;
		if (rank != other.rank)
			return false;
		return true;
	}

}
