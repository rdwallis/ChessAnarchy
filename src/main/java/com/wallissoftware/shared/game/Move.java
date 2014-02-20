package com.wallissoftware.shared.game;

import com.wallissoftware.shared.dto.Dto;
import com.wallissoftware.shared.game.pieces.Piece;

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

}
