package com.wallissoftware.chessanarchy.server.wipe;

import com.google.inject.servlet.ServletModule;

public class WipeModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/admin/wipe").with(WipeServlet.class);
	}

}
