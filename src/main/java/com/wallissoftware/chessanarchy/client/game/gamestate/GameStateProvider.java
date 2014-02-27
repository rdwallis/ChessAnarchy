package com.wallissoftware.chessanarchy.client.game.gamestate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.wallissoftware.chessanarchy.client.game.chat.messagelog.MessageLogPresenter;
import com.wallissoftware.chessanarchy.client.game.gamestate.model.GameState;
import com.wallissoftware.chessanarchy.shared.game.Board;
import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;

@Singleton
public class GameStateProvider implements Provider<GameState> {

	private final MessageLogPresenter messageLogPresenter;

	private final Board syncedBoard = new Board();

	@Inject
	GameStateProvider(final MessageLogPresenter messageLogPresenter) {

		this.messageLogPresenter = messageLogPresenter;

	}

	@Override
	public GameState get() {
		return new GameState(messageLogPresenter.getCurrentGameMessages());
	}

	public Board getSyncedBoard() {
		try {
			syncedBoard.resetFromMoveList(get().getMoveList());
			return syncedBoard;
		} catch (final IllegalMoveException e) {
			return new Board();
		}
	}

}
