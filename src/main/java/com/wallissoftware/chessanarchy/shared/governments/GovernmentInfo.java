package com.wallissoftware.chessanarchy.shared.governments;

public interface GovernmentInfo {

	/**
	 * @return the name of the government<br>
	 *         eg. Anarchy, Democracy etc..
	 */
	String getName();

	/**
	 * @return A description of how the government works.<br>
	 *         eg. Under Anarchy the first legal move received by the server is
	 *         immediately played.
	 */
	String getDescription();

	/**
	 * @return the text of the join team button. <br>
	 *         eg. Become an Anarchist
	 */
	String getJoinMessage();

	/**
	 * @return a message describing the system of government a team is using.
	 *         Should make sense if you add black or white before the string. <br>
	 * 
	 *         eg. Democracy.getUsingMessage() returns
	 *         "has formed a democratic government.";
	 * 
	 * 
	 */
	String getUsingMessage();
}
