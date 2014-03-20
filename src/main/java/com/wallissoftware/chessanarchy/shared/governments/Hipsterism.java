package com.wallissoftware.chessanarchy.shared.governments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wallissoftware.chessanarchy.shared.CAConstants;

public class Hipsterism extends SystemOfGovernment {

    public Hipsterism() {
        addCountingVoteMessage("The ${color} team's votes are being counted.");
        addMoveMessage("The ${color} team, totally likes ${move} because, you know, it hasn't sold out.  It's authentic you know like a piece of art, not like that commercial crap everyone else is into.");
        addInsult("Perhaps if the ${color} team put some lenses in their glasses they would see how stupid they were.");
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
        return "Hipsters liked your move before it became popular. Under Hipsterism the votes are counted after 20 seconds and then the least popular move is made.";
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
    public String getShortDescription() {
        return "Under Hipsterism the votes are counted after 20 seconds and then the least popular move is made.";
    }

}
