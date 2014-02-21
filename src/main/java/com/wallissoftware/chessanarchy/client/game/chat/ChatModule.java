package com.wallissoftware.chessanarchy.client.game.chat;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.wallissoftware.chessanarchy.client.game.chat.messageinput.MessageInputModule;
import com.wallissoftware.chessanarchy.client.game.chat.messagelog.MessageLogModule;

public class ChatModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new MessageLogModule());
		install(new MessageInputModule());
		bindSingletonPresenterWidget(ChatPresenter.class, ChatPresenter.MyView.class, ChatView.class);
	}
}
