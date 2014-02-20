package com.wallissoftware.client.game.board;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.wallissoftware.client.game.board.piece.PieceModule;

public class BoardModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new PieceModule());
		bindPresenterWidget(BoardPresenter.class, BoardPresenter.MyView.class, BoardView.class);
	}
}
