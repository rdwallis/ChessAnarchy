package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class MessageLogModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(MessageLogPresenter.class, MessageLogPresenter.MyView.class, MessageLogView.class);
	}
}
