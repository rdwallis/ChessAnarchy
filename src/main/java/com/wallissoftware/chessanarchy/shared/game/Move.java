package com.wallissoftware.chessanarchy.shared.game;

import com.wallissoftware.chessanarchy.shared.dto.Dto;
import com.wallissoftware.chessanarchy.shared.game.pieces.Piece;

public class Move implements Dto {

	private static final long serialVersionUID = 1L;

	private Square start;
	private Square end;
	private Piece promote;

	public Move(final Square start, final Square end) {
		this(start, end, null);
	}

	public Move(final Square start, final Square end, final Piece promote) {
		this.start = start;
		this.end = end;
		this.promote = promote;
	}

	public Square getStart() {
		return start;
	}

	public Square getEnd() {
		return end;
	}

	public Piece getPromote() {
		return promote;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((promote == null) ? 0 : promote.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (promote == null) {
			if (other.promote != null)
				return false;
		} else if (!promote.equals(other.promote))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return start + "-" + end;
	}

	public boolean matchesWithoutPromotion(final Move move) {
		return start.equals(move.start) && end.equals(move.end);
	}

}
