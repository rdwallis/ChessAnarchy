package com.wallissoftware.chessanarchy.client.game.gamestate;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wallissoftware.chessanarchy.client.game.chat.messagelog.MessageLogPresenter;
import com.wallissoftware.chessanarchy.client.game.gamestate.model.GameState;
import com.wallissoftware.chessanarchy.shared.game.MoveTree;
import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

@Singleton
public class GameStateProvider {

	private final MessageLogPresenter messageLogPresenter;

	private List<MessageWrapper> gameMasterMessages;

	private GameState gameState;

	@Inject
	GameStateProvider(final MessageLogPresenter messageLogPresenter) {

		this.messageLogPresenter = messageLogPresenter;

	}

	public GameState getGameState() {
		final List<MessageWrapper> currentGameMasterMessages = messageLogPresenter.getCurrentGameMasterMessages();
		if (gameMasterMessages == null || gameMasterMessages.size() != currentGameMasterMessages.size()) {
			gameMasterMessages = currentGameMasterMessages;
			gameState = new GameState(gameMasterMessages);
		}
		return gameState;
	}

	public MoveTree getMoveTree() {
		try {
			return MoveTree.get(getGameState().getMoveList());
		} catch (final IllegalMoveException e) {
			return MoveTree.getRoot();
		}
	}

	public MoveTree getParentMoveTree() {
		if (getMoveTree().isRootNode()) {
			return getMoveTree();
		} else {
			return getMoveTree().getParent();
		}
	}

}
