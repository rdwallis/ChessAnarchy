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

}
