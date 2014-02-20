package com.wallissoftware.chessanarchy.client.game.board;

import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.wallissoftware.chessanarchy.client.game.board.piece.PieceModule;
import com.wallissoftware.chessanarchy.shared.game.Board;

public class BoardModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new PieceModule());
		bind(Board.class).in(Singleton.class);
		bindPresenterWidget(BoardPresenter.class, BoardPresenter.MyView.class, BoardView.class);
	}
}
