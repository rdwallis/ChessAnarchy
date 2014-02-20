package com.wallissoftware.client.game.board.piece;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class PieceModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenterWidget(PiecePresenter.class, PiecePresenter.MyView.class, PieceView.class);
	}
}
