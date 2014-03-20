package com.wallissoftware.chessanarchy.client.game.gamestate;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.wallissoftware.chessanarchy.client.game.chat.messagelog.MessageLogPresenter;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.ResyncGameStateRequestEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.model.GameState;
import com.wallissoftware.chessanarchy.shared.game.MoveTree;
import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

@Singleton
public class GameStateProvider {

    private MessageLogPresenter messageLogPresenter;

    private List<MessageWrapper> gameMasterMessages;

    private GameState gameState;

    private final EventBus eventBus;

    @Inject
    GameStateProvider(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setMessageLogPresenter(final MessageLogPresenter messageLogPresenter) {
        this.messageLogPresenter = messageLogPresenter;
    }

    public GameState getGameState() {
        if (messageLogPresenter == null) {
            return new GameState(new ArrayList<MessageWrapper>(), eventBus);
        }
        final List<MessageWrapper> currentGameMasterMessages = messageLogPresenter.getCurrentGameMasterMessages();
        if (gameMasterMessages == null || gameMasterMessages.size() != currentGameMasterMessages.size()) {
            gameMasterMessages = currentGameMasterMessages;
            gameState = new GameState(gameMasterMessages, eventBus);
        }
        return gameState;
    }

    public MoveTree getMoveTree() {
        try {
            return MoveTree.get(getGameState().getMoveList());
        } catch (final IllegalMoveException e) {
            eventBus.fireEvent(new ResyncGameStateRequestEvent());
            return MoveTree.getRoot();
        }
    }

    public MoveTree getParentMoveTree() {
        if (getMoveTree().getParent() == null) {
            return getMoveTree();
        } else {
            return getMoveTree().getParent();
        }
    }

    public long getLastMoveTime() {
        return getGameState().getLastMoveTime();
    }

    public long getSecondLastMoveTime() {
        return getGameState().getSecondLastMoveTime();
    }

}
