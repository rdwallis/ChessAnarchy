package com.wallissoftware.chessanarchy.shared.governments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wallissoftware.chessanarchy.shared.CAConstants;

public class Hipsterism extends SystemOfGovernment {

	@Override
	public MoveResult calculateMove(final String extraInfo, final List<MoveRequest> moveRequests) {
		final Map<String, Integer> moveVotes = new HashMap<String, Integer>();
		for (final MoveRequest moveRequest : moveRequests) {
			if (!moveVotes.containsKey(moveRequest.getMove())) {
				moveVotes.put(moveRequest.getMove(), 0);
			}
			moveVotes.put(moveRequest.getMove(), moveVotes.get(moveRequest.getMove()));
		}

		Entry<String, Integer> leastPopularMove = null;
		for (final Entry<String, Integer> entry : moveVotes.entrySet()) {
			if (leastPopularMove == null || entry.getValue() < leastPopularMove.getValue()) {
				leastPopularMove = entry;
				if (leastPopularMove.getValue() == 1) {
					break;
				}

			}
		}
		return new MoveResult(extraInfo, leastPopularMove.getKey());
	}

	@Override
	public String getName() {
		return "Hipsterism";
	}

	@Override
	public String getDescription() {
		return "Hipsters liked your move before it became popular. Under Hipsterism the votes are counted after 30 seconds and then the least popular move is made.";
	}

	@Override
	public String getBlackIconUrl() {
		return CAConstants.HOST + "/images/hipsterism_black.png";
	}

	@Override
	public String getWhiteIconUrl() {
		return CAConstants.HOST + "/images/hipsterism_white.png";
	}

	@Override
	boolean isLastVoteOfPlayerPreferred() {
		return true;
	}

	@Override
	public String getMovePreamble() {
		return "color Team's least popular move was ";
	}

}
