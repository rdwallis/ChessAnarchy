package com.wallissoftware.chessanarchy.client.game.embedinstructions;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class EmbedInstructionsModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(EmbedInstructionsPresenter.class, EmbedInstructionsPresenter.MyView.class, EmbedInstructionsView.class);
	}
}
