package com.wallissoftware.chessanarchy.server.user;

import com.google.inject.servlet.ServletModule;

public class UserModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/user").with(UserServlet.class);
	}

}