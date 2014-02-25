package com.wallissoftware.chessanarchy.shared.government;

public enum Government implements GovernmentInfo {
	ANARCHY(new AnarchyInfo());

	private final GovernmentInfo governmentInfo;

	Government(final GovernmentInfo governmentInfo) {
		this.governmentInfo = governmentInfo;
	}

	@Override
	public String getName() {
		return governmentInfo.getName();
	}

	@Override
	public String getDescription() {
		return governmentInfo.getDescription();
	}

	@Override
	public String getJoinMessage() {
		return governmentInfo.getJoinMessage();
	}

	@Override
	public String getPlayerCountMessage(final int... playerCount) {
		return governmentInfo.getPlayerCountMessage(playerCount);
	}

	@Override
	public String getUsingMessage() {
		return governmentInfo.getUsingMessage();
	}

}
