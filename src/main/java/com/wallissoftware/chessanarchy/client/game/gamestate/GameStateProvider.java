package com.wallissoftware.chessanarchy.client.game.gamestate;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.wallissoftware.chessanarchy.client.game.chat.messagelog.MessageLogPresenter;
import com.wallissoftware.chessanarchy.client.game.gamestate.model.GameState;
import com.wallissoftware.chessanarchy.shared.game.MoveTree;
import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

@Singleton
public class GameStateProvider implements Provider<GameState> {

	private final MessageLogPresenter messageLogPresenter;

	private List<MessageWrapper> gameMasterMessages;

	private GameState gameState;

	@Inject
	GameStateProvider(final MessageLogPresenter messageLogPresenter) {

		this.messageLogPresenter = messageLogPresenter;

	}

	@Override
	public GameState get() {
		final List<MessageWrapper> currentGameMasterMessages = messageLogPresenter.getCurrentGameMasterMessages();
		if (gameMasterMessages == null || gameMasterMessages.size() != currentGameMasterMessages.size()) {
			gameMasterMessages = currentGameMasterMessages;
			gameState = new GameState(gameMasterMessages);
		}
		return gameState;
	}

	public MoveTree getSyncedMoveTree() {
		try {
			return MoveTree.get(get().getMoveList());
		} catch (final IllegalMoveException e) {
			return MoveTree.getRoot();
		}
	}

}
