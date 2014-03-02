package com.wallissoftware.chessanarchy.client.game.gamestate.model;

import java.util.ArrayList;
import java.util.List;

import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public final class GameState {

	private String id;
	private String whiteGovernment;
	private String blackGovernment;
	private List<String> moveList = new ArrayList<String>();

	//private final static Logger logger = Logger.getLogger(GameState.class.getName());

	public GameState(final List<MessageWrapper> gameMessages) {
		for (final MessageWrapper message : gameMessages) {
			if (message.isFromGameMaster()) {
				if (this.id == null) {
					final String id = message.getNewGameId();
					if (id != null) {
						this.id = id;
					}
				} else {
					if (this.whiteGovernment == null) {
						whiteGovernment = message.getWhiteGovernment();
					}
					if (this.blackGovernment == null) {
						blackGovernment = message.getBlackGovernment();
					}
					if (message.getMove() != null) {
						moveList.add(message.getMove());
					}

				}
			}
		}
		if (id == null) {
			id = "FAKE ID " + Math.random();
		}
	};

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

}
