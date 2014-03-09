package com.wallissoftware.chessanarchy.server.session;

import com.google.inject.servlet.ServletModule;

public class SessionModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/cookiesEnabled").with(CookiesEnabledServlet.class);
	}

}
