package com.wallissoftware.chessanarchy.server.mainpage;

import com.google.inject.servlet.ServletModule;

public class MainPageModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/").with(MainPage.class);
	}

}
