package com.wallissoftware.client.game.board;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.client.game.board.piece.PiecePresenter;
import com.wallissoftware.shared.game.Square;

public class BoardPresenter extends PresenterWidget<BoardPresenter.MyView> implements BoardUiHandlers {
	public interface MyView extends View, HasUiHandlers<BoardUiHandlers> {

		void setPieceInSquare(IsWidget piece, Square square);
	}

	private final Provider<PiecePresenter> piecePresenterProvider;

	@Inject
	BoardPresenter(final EventBus eventBus, final MyView view, final Provider<PiecePresenter> piecePresenterProvider) {
		super(eventBus, view);
		this.piecePresenterProvider = piecePresenterProvider;
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		getView().setPieceInSquare(piecePresenterProvider.get().getView(), new Square(1, 1));
	}

}
