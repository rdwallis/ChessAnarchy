package com.wallissoftware.chessanarchy.shared.governments;

public class MoveResult {

	private final String move;

	private final String extraInfo;

	public MoveResult(final String extraInfo, final String move) {
		this.move = move;
		this.extraInfo = extraInfo;
	}

	public String getMove() {
		return move;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

}
