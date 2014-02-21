package com.wallissoftware.chessanarchy.client.game.chat.messageinput;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class MessageInputModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(MessageInputPresenter.class, MessageInputPresenter.MyView.class, MessageInputView.class);
	}
}
