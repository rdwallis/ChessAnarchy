package com.wallissoftware.chessanarchy.shared.governments;

import java.util.List;

import com.wallissoftware.chessanarchy.shared.CAConstants;

public class Anarchy extends SystemOfGovernment {

    public Anarchy() {
        addCountingVoteMessage("The ${color} team's government has been set on fire.");
        addCountingVoteMessage("The ${color} team is revolting.");
        addCountingVoteMessage("Unidentified troops have moved to take control of ${color} team's seaports.");
        addCountingVoteMessage("An unidentified boeing 777 has crashed into ${color} team's parlimentary buildings.");
        addMoveMessage("In the chaos ${color} team moves to ${move}");
        addInsult("The ${color} team is not a part of the system because the system didn't want them.");
    }

    @Override
    public boolean isReady(final String extraInfo, final long timeOfLastMove, final List<MoveRequest> moveRequests) {
        return !moveRequests.isEmpty() && System.currentTimeMillis() - timeOfLastMove > 4000;
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
