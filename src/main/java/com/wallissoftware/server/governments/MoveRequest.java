package com.wallissoftware.server.governments;

import com.wallissoftware.shared.game.Move;

public class MoveRequest {

	private final long creationTime;

	private final String playerId;

	private final Move move;

	public MoveRequest(final String playerId, final Move move) {
		this.creationTime = System.currentTimeMillis();
		this.playerId = playerId;
		this.move = move;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public String getPlayerId() {
		return playerId;
	}

	public Move getMove() {
		return move;
	}

}
