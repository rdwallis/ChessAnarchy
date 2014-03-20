package com.wallissoftware.chessanarchy.client.game.gamestate.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.web.bindery.event.shared.EventBus;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.ResyncGameStateRequestEvent;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public final class GameState {

    private String id;
    private String whiteGovernment;
    private String blackGovernment;
    private long electionStart = 0;
    private List<String> moveList = new ArrayList<String>();
    private long lastMoveTime = 0;
    private long secondLastMoveTime = 0;

    private final static Logger logger = Logger.getLogger(GameState.class.getName());

    public GameState(final List<MessageWrapper> gameMessages, final EventBus eventBus) {
        for (final MessageWrapper message : gameMessages) {
            if (message.isFromGameMaster()) {
                if (message.getNewGameId() != null && SyncedTime.get() - message.getCreated() > 10000) {
                    if (!message.getNewGameId().equals(id)) {
                        this.id = message.getNewGameId();
                        moveList.clear();
                    }
                } else {
                    if (this.whiteGovernment == null) {
                        whiteGovernment = message.getWhiteGovernment();
                    }
                    if (this.blackGovernment == null) {
                        blackGovernment = message.getBlackGovernment();
                    }
                    if (message.getMove() != null) {

                        if (moveList.size() % 2 == 0 ^ message.getColor() == Color.BLACK) {
                            moveList.add(message.getMove());
                            secondLastMoveTime = lastMoveTime;
                            lastMoveTime = message.getCreated();
                        } else {
                            logger.info("GameState Out Of Sync");
                            eventBus.fireEvent(new ResyncGameStateRequestEvent());
                        }

                    }
                    if (message.getElectionStart() != null) {
                        electionStart = message.getElectionStart();
                    }

                }
            }
        }
        if (id == null) {
            id = "FAKE ID " + Math.random();
        }
    };

    public long getElectionStart() {
        return electionStart;
    }

    public boolean swapColors() {
        return id == null ? false : id.endsWith("T");
    }

    public SystemOfGovernment getWhiteGovernment() {
        return SystemOfGovernment.get(whiteGovernment);
    }

    public SystemOfGovernment getBlackGovernment() {
        return SystemOfGovernment.get(blackGovernment);
    }

    public List<String> getMoveList() {
        return moveList;
    }

    public String getId() {
        return id;
    }

    public long getLastMoveTime() {
        return Math.max(getElectionStart() + 30000, lastMoveTime);
    }

    public long getSecondLastMoveTime() {
        return Math.max(getElectionStart(), secondLastMoveTime);
    }

}
