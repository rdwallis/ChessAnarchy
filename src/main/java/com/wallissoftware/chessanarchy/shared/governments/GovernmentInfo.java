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
	 * @return the url for the government when the player is black. Should be
	 *         120x120px see src/main/webapp/images/ for existing examples.
	 */
	String getBlackIconUrl();

	/**
	 * @return the url for the government when the player is white. Should be
	 *         120x120px see src/main/webapp/images/ for existing examples.
	 */
	String getWhiteIconUrl();

	String getMovePreamble();

	String getCountingVotesMessage();

}
