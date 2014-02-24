package com.wallissoftware.chessanarchy.server.gamestate;

import com.google.inject.servlet.ServletModule;

public class GameStateModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/gamestate").with(GameStateServlet.class);
	}

}
