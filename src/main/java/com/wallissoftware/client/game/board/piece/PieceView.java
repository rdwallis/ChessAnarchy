package com.wallissoftware.client.game.board.piece;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class PieceView extends ViewWithUiHandlers<PieceUiHandlers> implements PiecePresenter.MyView {
	public interface Binder extends UiBinder<Widget, PieceView> {
	}

	@Inject
	PieceView(final Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}
}
