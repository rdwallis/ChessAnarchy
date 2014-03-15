package com.wallissoftware.chessanarchy.client.game.gamestate.model;

import java.util.ArrayList;
import java.util.List;

import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public final class GameState {

	private String id;
	private String whiteGovernment;
	private String blackGovernment;
	private long electionStart = 0;
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
						if (moveList.size() % 2 == 0 ^ message.getColor() == Color.BLACK) {
							moveList.add(message.getMove());
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

}
