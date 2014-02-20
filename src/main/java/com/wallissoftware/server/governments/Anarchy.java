package com.wallissoftware.server.governments;

import java.util.List;

import com.wallissoftware.shared.game.Move;

public class Anarchy extends SystemOfGovernment {

	@Override
	Move getMove(final List<MoveRequest> moveRequest) {
		return moveRequest.get(0).getMove();
	}

	@Override
	public boolean isReady(final List<MoveRequest> moveRequests) {
		return !moveRequests.isEmpty();
	}

}
