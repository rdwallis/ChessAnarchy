package com.wallissoftware.chessanarchy.server.messages;

import com.google.inject.servlet.ServletModule;

public class MessageModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/message").with(MessageServlet.class);
		serve("/processmessages").with(UpdateMessagesServlet.class);
	}

}
