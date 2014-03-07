package com.wallissoftware.chessanarchy.shared.game;

import com.wallissoftware.chessanarchy.shared.dto.Dto;

public class Move implements Dto {

	private static final long serialVersionUID = 1L;

	private final int startFile, startRank, endFile, endRank;
	private final char promotion;

	public Move(final int startFile, final int startRank, final int endFile, final int endRank) {
		this(startFile, startRank, endFile, endRank, 'x');
	}

	public Move(final int startFile, final int startRank, final int endFile, final int endRank, final char promotion) {
		super();
		this.startFile = startFile;
		this.startRank = startRank;
		this.endFile = endFile;
		this.endRank = endRank;
		this.promotion = promotion;
	}

	public Move(final Move partialMove, final char promotion) {
		this(partialMove.getStartFile(), partialMove.getStartRank(), partialMove.getEndFile(), partialMove.getEndRank(), promotion);
	}

	@Override
	public String toString() {
		return getStartSquare() + getEndSquare() + (isPromotion() ? getPromotion() : "");
	}

	public boolean isPromotion() {
		return getPromotion() != 'x';
	}

	public String getStartSquare() {
		return new StringBuilder().append(getStartFileAsChar()).append(getStartRank() + 1).toString();
	}

	public char getStartFileAsChar() {
		return (char) (startFile + 97);
	}

	public char getEndFileAsChar() {
		return (char) (endFile + 97);
	}

	public String getEndSquare() {
		return new StringBuilder().append(getEndFileAsChar()).append(getEndRank() + 1).toString();
	}

	public int getStartFile() {
		return startFile;
	}

	public int getStartRank() {
		return startRank;
	}

	public int getEndFile() {
		return endFile;
	}

	public int getEndRank() {
		return endRank;
	}

	public char getPromotion() {
		return promotion;
	}

	public int getFileDelta() {
		return Math.abs(endFile - startFile);
	}

	public int getRankDelta() {
		return Math.abs(endRank - startRank);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endFile;
		result = prime * result + endRank;
		result = prime * result + promotion;
		result = prime * result + startFile;
		result = prime * result + startRank;
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
		final Move other = (Move) obj;
		if (endFile != other.endFile)
			return false;
		if (endRank != other.endRank)
			return false;
		if (promotion != other.promotion)
			return false;
		if (startFile != other.startFile)
			return false;
		if (startRank != other.startRank)
			return false;
		return true;
	}

	public int getDelta() {
		return getFileDelta() + getRankDelta();
	}

	public boolean matchesIgnoringPromotion(final Move other) {
		return getStartRank() == other.getStartRank() && getStartFile() == other.getStartFile() && getEndRank() == other.getEndRank() && getEndFile() == other.getEndFile();
	}

}
