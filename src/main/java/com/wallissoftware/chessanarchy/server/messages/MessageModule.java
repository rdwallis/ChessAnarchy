package com.wallissoftware.chessanarchy.server.messages;

import com.google.inject.servlet.ServletModule;

public class MessageModule extends ServletModule {

	public final static String SEND_MESSAGE_URL = "/send";
	public final static String MESSAGE_URL = "/message";

	@Override
	protected void configureServlets() {
		serve("/send*").with(SendMessageServlet.class);
		serve("/message*").with(GetMessageServlet.class);
		serve("/admin/processmessages").with(UpdateMessagesServlet.class);
	}

}
