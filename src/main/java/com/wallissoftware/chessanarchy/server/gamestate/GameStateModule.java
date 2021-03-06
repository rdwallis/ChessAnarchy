package com.wallissoftware.chessanarchy.server.gamestate;

import com.google.inject.servlet.ServletModule;

public class GameStateModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/gamemessages").with(GameStateMessageServlet.class);
		serve("/gamestate").with(GameStateServlet.class);
		serve("/pgn").with(PgnServlet.class);
	}

}
