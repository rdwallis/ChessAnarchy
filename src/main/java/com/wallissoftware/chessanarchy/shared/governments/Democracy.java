package com.wallissoftware.chessanarchy.shared.governments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wallissoftware.chessanarchy.shared.CAConstants;

public class Democracy extends SystemOfGovernment {

    public Democracy() {
        addCountingVoteMessage("The ${color} team's votes are being counted.");
        addMoveMessage("The people have spoken, ${color} team moves to ${move}");
        addInsult("The ${color} team proves Vox Populi Vox Moranus.  The voice of the people is the voice of a moron.");
    }

    @Override
    public MoveResult calculateMove(final String extraInfo, final List<MoveRequest> moveRequests) {
        final Map<String, Integer> moveVotes = new HashMap<String, Integer>();
        for (final MoveRequest moveRequest : moveRequests) {
            if (!moveVotes.containsKey(moveRequest.getMove())) {
                moveVotes.put(moveRequest.getMove(), 0);
            }
            moveVotes.put(moveRequest.getMove(), moveVotes.get(moveRequest.getMove()) + 1);
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
        return "Under democracy votes are counted after 20 seconds and the most popular move is played.";
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
