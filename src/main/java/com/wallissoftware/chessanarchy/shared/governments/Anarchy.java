package com.wallissoftware.chessanarchy.shared.governments;

import java.util.List;

import com.wallissoftware.chessanarchy.shared.CAConstants;

public class Anarchy extends SystemOfGovernment {

	@Override
	public boolean isReady(final String extraInfo, final long timeOfLastMove, final List<MoveRequest> moveRequests) {
		return !moveRequests.isEmpty();
	}

	@Override
	public MoveResult calculateMove(final String extraInfo, final List<MoveRequest> moveRequests) {
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

	@Override
	public String getBlackIconUrl() {
		return CAConstants.HOST + "/images/anarchy_black.png";
	}

	@Override
	public String getWhiteIconUrl() {
		return CAConstants.HOST + "/images/anarchy_white.png";
	}

	@Override
	boolean isLastVoteOfPlayerPreferred() {
		return false;
	}

}
