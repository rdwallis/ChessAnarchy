package com.wallissoftware.chessanarchy.server.governments;

import java.util.List;

import com.wallissoftware.chessanarchy.shared.game.Move;

public abstract class SystemOfGovernment {

	private long startTime;

	public final void start() {
		this.startTime = System.currentTimeMillis();
	}

	public boolean isReady(final List<MoveRequest> moveRequests) {
		return System.currentTimeMillis() - startTime > 30000 && !moveRequests.isEmpty();
	}

	abstract Move getMove(List<MoveRequest> moveRequest);

}
