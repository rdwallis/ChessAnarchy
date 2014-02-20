package com.wallissoftware.chessanarchy.client.game.board.piece;

import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.wallissoftware.chessanarchy.client.game.board.piece.images.PieceSprites;
import com.wallissoftware.chessanarchy.client.game.board.promotion.PromotionModule;

public class PieceModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new PromotionModule());
		bind(PieceSprites.class).in(Singleton.class);
		bindPresenterWidget(PiecePresenter.class, PiecePresenter.MyView.class, PieceViewImpl.class);
	}
}
