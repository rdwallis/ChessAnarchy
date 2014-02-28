package com.wallissoftware.chessanarchy.server.time;

import com.google.inject.servlet.ServletModule;

public class TimeModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/time").with(TimeServlet.class);
	}

}
