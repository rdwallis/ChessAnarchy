package com.wallissoftware.chessanarchy.server.governments;

import java.util.List;

public class Anarchy extends SystemOfGovernment {

	@Override
	public boolean isReady(final long timeOfLastMove, final List<MoveRequest> moveRequests) {
		return !moveRequests.isEmpty();
	}

	@Override
	public String getMove(final List<MoveRequest> moveRequest) {
		return moveRequest.get(0).getMove();
	}

}
