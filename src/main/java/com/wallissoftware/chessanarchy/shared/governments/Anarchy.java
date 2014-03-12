package com.wallissoftware.chessanarchy.shared.governments;

import java.util.List;

public class Anarchy extends SystemOfGovernment {

	@Override
	public boolean isReady(final String extraInfo, final long timeOfLastMove, final List<MoveRequest> moveRequests) {
		return !moveRequests.isEmpty();
	}

	@Override
	public MoveResult getMove(final String extraInfo, final List<MoveRequest> moveRequests) {
		return new MoveResult(null, moveRequests.get(0).getMove());
	}

	@Override
	public String getName() {
		return "Anarchy";
	}

	@Override
	public String getDescription() {
		return "Under Anarchy the first legal move received by the server is played immediately.";
	}

}
