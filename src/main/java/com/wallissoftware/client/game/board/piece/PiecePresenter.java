package com.wallissoftware.client.game.board.piece;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.shared.game.pieces.Piece;

public class PiecePresenter extends PresenterWidget<PiecePresenter.MyView> implements PieceUiHandlers {
	public interface MyView extends View, HasUiHandlers<PieceUiHandlers> {
	}

	private Piece piece;

	@Inject
	PiecePresenter(final EventBus eventBus, final MyView view) {
		super(eventBus, view);
		getView().setUiHandlers(this);
	}

	public void setPiece(final Piece piece) {
		this.piece = piece;
	}

}
