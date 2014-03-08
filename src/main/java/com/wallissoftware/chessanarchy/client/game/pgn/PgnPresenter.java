package com.wallissoftware.chessanarchy.client.game.pgn;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent.GameStateUpdatedHandler;

public class PgnPresenter extends PresenterWidget<PgnPresenter.MyView> implements PgnUiHandlers, GameStateUpdatedHandler {
	public interface MyView extends View, HasUiHandlers<PgnUiHandlers> {

		void addMove(String pgn);

		void clearMoves();
	}

	private final GameStateProvider gameStateProvider;

	private List<String> moveList = new ArrayList<String>();

	@Inject
	PgnPresenter(final EventBus eventBus, final MyView view, final GameStateProvider gameStateProvider) {
		super(eventBus, view);
		this.gameStateProvider = gameStateProvider;
		getView().setUiHandlers(this);

	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(GameStateUpdatedEvent.getType(), this);
	}

	@Override
	public void onGameStateUpdated(final GameStateUpdatedEvent event) {
		setMoveList(gameStateProvider.getGameState().getMoveList());

	}

	private void setMoveList(final List<String> moveList) {
		if (!moveList.equals(this.moveList)) {
			if (moveList.subList(0, moveList.size() - 1).equals(this.moveList)) {
				getView().addMove(moveList.get(moveList.size() - 1));
			} else {
				getView().clearMoves();
				this.moveList = moveList;
				for (final String move : moveList) {
					getView().addMove(move);
				}
			}
		}

	}

}
