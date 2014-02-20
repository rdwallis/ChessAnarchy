package com.wallissoftware.chessanarchy.client.game.board.promotion;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class PromotionModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindSingletonPresenterWidget(PromotionPresenter.class, PromotionPresenter.MyView.class, PromotionView.class);
	}
}
