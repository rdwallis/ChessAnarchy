package com.wallissoftware.chessanarchy.client.game.board;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.wallissoftware.chessanarchy.client.game.board.promotion.PromotionModule;

public class BoardModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new PromotionModule());
		bindSingletonPresenterWidget(BoardPresenter.class, BoardPresenter.MyView.class, BoardView.class);
	}
}
