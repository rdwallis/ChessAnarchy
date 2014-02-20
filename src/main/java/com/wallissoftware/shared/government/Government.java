package com.wallissoftware.shared.government;

public enum Government {
	ANARCHY("Anarchy", "Under anarchy the first legal move recieved by server will be played immediately");

	private final String description;
	private final String name;

	Government(final String name, final String description) {
		this.name = name;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}
}
