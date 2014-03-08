package com.wallissoftware.chessanarchy.client.game.board.promotion;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.wallissoftware.chessanarchy.client.game.board.BoardPresenter;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;

public class PromotionPresenter extends PresenterWidget<PromotionPresenter.MyView> implements PromotionUiHandlers {
	public interface MyView extends PopupView, HasUiHandlers<PromotionUiHandlers> {

		void setColor(Color color);
	}

	private final GameStateProvider gameStateProvider;
	private Move partialMove;
	private BoardPresenter boardPresenter;

	@Inject
	PromotionPresenter(final EventBus eventBus, final MyView view, final GameStateProvider gameStateProvider) {
		super(eventBus, view);
		this.gameStateProvider = gameStateProvider;

		getView().setUiHandlers(this);

	}

	public void setBoardPresenter(final BoardPresenter boardPresenter) {
		this.boardPresenter = boardPresenter;
	}

	public void setPartialMove(final Move move) {
		getView().setColor(getColor());
		partialMove = move;

	}

	private Color getColor() {
		return gameStateProvider.getMoveTree().getCurrentPlayer();
	}

	@Override
	public void cancel() {
	}

	@Override
	public void promoteToRook() {
		makeMove(new Move(partialMove, getColor() == Color.WHITE ? 'R' : 'r'));

	}

	@Override
	public void promoteToBishop() {
		makeMove(new Move(partialMove, getColor() == Color.WHITE ? 'B' : 'b'));

	}

	@Override
	public void promoteToKnight() {
		makeMove(new Move(partialMove, getColor() == Color.WHITE ? 'N' : 'n'));

	}

	@Override
	public void promoteToQueen() {
		makeMove(new Move(partialMove, getColor() == Color.WHITE ? 'Q' : 'q'));
	}

	private void makeMove(final Move move) {
		boardPresenter.makeMove(move);
	}

}
