package com.wallissoftware.chessanarchy.client.game.board.piece;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.shared.game.pieces.Piece;

public class PiecePresenter extends PresenterWidget<PiecePresenter.MyView> implements PieceUiHandlers {
	public interface MyView extends View, HasUiHandlers<PieceUiHandlers> {

		void setPiece(final Piece piece);
	}

	private Piece piece;

	@Inject
	PiecePresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
		getView().setUiHandlers(this);
	}

	public void setPiece(final Piece piece) {
		this.piece = piece;
		getView().setPiece(piece);
	}

	public Piece getPiece() {
		return piece;
	}

}
