package com.wallissoftware.chessanarchy.shared.governments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wallissoftware.chessanarchy.shared.CAConstants;

public class Democracy extends SystemOfGovernment {

	@Override
	public MoveResult calculateMove(final String extraInfo, final List<MoveRequest> moveRequests) {
		final Map<String, Integer> moveVotes = new HashMap<String, Integer>();
		for (final MoveRequest moveRequest : moveRequests) {
			if (!moveVotes.containsKey(moveRequest.getMove())) {
				moveVotes.put(moveRequest.getMove(), 0);
			}
			moveVotes.put(moveRequest.getMove(), moveVotes.get(moveRequest.getMove()));
		}

		Entry<String, Integer> mostPopularMove = null;
		for (final Entry<String, Integer> entry : moveVotes.entrySet()) {
			if (mostPopularMove == null || entry.getValue() > mostPopularMove.getValue()) {
				mostPopularMove = entry;

			}
		}
		return new MoveResult(extraInfo, mostPopularMove.getKey());
	}

	@Override
	public String getName() {
		return "Democracy";
	}

	@Override
	public String getDescription() {
		return "Under democracy votes are counted after 30 seconds and the most popular move is played.";
	}

	@Override
	public String getBlackIconUrl() {
		return CAConstants.HOST + "/images/democracy_black.png";
	}

	@Override
	public String getWhiteIconUrl() {
		return CAConstants.HOST + "/images/democracy_white.png";
	}

	@Override
	boolean isLastVoteOfPlayerPreferred() {
		return true;
	}

}
