package com.wallissoftware.chessanarchy.client.game.pgn;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class PgnModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(PgnPresenter.class, PgnPresenter.MyView.class, PgnView.class);
	}
}
