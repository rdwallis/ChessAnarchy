package com.wallissoftware.chessanarchy.server.messages;

import com.google.inject.servlet.ServletModule;

public class MessageModule extends ServletModule {

    @Override
    protected void configureServlets() {
        serve("/send*").with(SendMessageServlet.class);
        serve("/getm*").with(GetMessageServlet.class);
        serve("/admin/processmessages").with(UpdateMessagesServlet.class);
    }

}
