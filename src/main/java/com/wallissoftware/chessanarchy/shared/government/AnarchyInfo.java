package com.wallissoftware.chessanarchy.shared.government;

public class AnarchyInfo implements GovernmentInfo {

	@Override
	public String getName() {
		return "Anarchy";
	}

	@Override
	public String getDescription() {
		return "Under Anarchy the first legal move received by the server is played immediately.";
	}

	@Override
	public String getJoinMessage() {
		return "Like fire?  Become an Anarchist";
	}

	@Override
	public String getPlayerCountMessage(final int... playerCount) {
		return playerCount + (playerCount[0] == 1 ? " Anarchist" : " Anarchists");
	}

	@Override
	public String getUsingMessage() {
		return " have burned down their government. Anarchy prevails";
	}

}
