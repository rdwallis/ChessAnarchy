package com.wallissoftware.chessanarchy.server.gamestate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.wallissoftware.chessanarchy.shared.game.Board;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;
import com.wallissoftware.chessanarchy.shared.governments.MoveRequest;
import com.wallissoftware.chessanarchy.shared.governments.MoveResult;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;

@Entity
@Cache
public class GameState {

	@Id private Long id;
	private long lastUpdated;
	@Index private long creationTime;

	private boolean swapColors;

	private String whiteGovernment = "Anarchy";
	private String blackGovernment = "Anarchy";

	private List<String> moveList = new ArrayList<String>();

	private List<Long> moveTimes = new ArrayList<Long>();

	private Set<MoveRequest> moveRequests = new HashSet<MoveRequest>();

	private String whiteExtraInfo, blackExtraInfo;

	@SuppressWarnings("unused")
	private GameState() {
	};

	public GameState(final boolean swapColors) {
		this.creationTime = System.currentTimeMillis();
		this.swapColors = swapColors;
	}

	@OnSave
	void markLastUpdated() {
		lastUpdated = System.currentTimeMillis();
	}

	public void addMove(final String move) {
		moveList.add(move);
		moveRequests.clear();
		try {
			new Board(moveList);
			moveTimes.add(System.currentTimeMillis());

		} catch (final IllegalMoveException e) {
			moveList.remove(moveList.size() - 1);
		}

	}

	public long getLastUpdated() {
		return lastUpdated;
	}

	public Long getId() {
		return id;
	}

	public boolean isComplete() {
		return !moveList.isEmpty() && moveList.get(moveList.size() - 1).endsWith("#");
	}

	public String getJson() {
		final Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("id", id + "");
		jsonMap.put("created", creationTime + "");
		jsonMap.put("swapColors", swapColors);
		jsonMap.put("whiteGovernment", whiteGovernment);
		jsonMap.put("blackGovernment", blackGovernment);
		jsonMap.put("moveList", moveList);
		return new Gson().toJson(jsonMap);

	}

	public Map<String, String> getLegalMoves() {
		try {
			final Board board = new Board(moveList);
			return board.getAllLegalMovesWithNotation();

		} catch (final IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public void addMoveRequest(final MoveRequest moveRequest) {
		moveRequests.add(moveRequest);

	}

	public String processMoveRequests() {
		final List<MoveRequest> moveRequestList = new ArrayList<MoveRequest>(moveRequests);
		Collections.sort(moveRequestList);
		if (getSystemOfGovernemnt().isReady(getExtraInfo(), getTimeOfLastMove(), moveRequestList)) {
			final MoveResult moveResult = getSystemOfGovernemnt().getMove(getExtraInfo(), moveRequestList);
			addMove(moveResult.getMove());
			setExtraInfo(moveResult.getExtraInfo());
			if (!moveList.isEmpty()) {
				return moveList.get(moveList.size() - 1);
			}
		}
		return null;

	}

	private void setExtraInfo(final String extraInfo) {
		if (getCurrentPlayer() == Color.WHITE) {
			whiteExtraInfo = extraInfo;
		} else {
			blackExtraInfo = extraInfo;
		}
	}

	private String getExtraInfo() {
		return getCurrentPlayer() == Color.WHITE ? whiteExtraInfo : blackExtraInfo;
	}

	private long getTimeOfLastMove() {
		if (moveTimes.isEmpty()) {
			return creationTime;
		} else {
			return moveTimes.get(moveTimes.size() - 1);
		}

	}

	private SystemOfGovernment getSystemOfGovernemnt() {
		return SystemOfGovernment.get(getCurrentPlayer() == Color.WHITE ? whiteGovernment : blackGovernment);

	}

	public boolean swapColors() {
		return swapColors;
	}

	public Color getCurrentPlayer() {
		return moveList.size() % 2 == 0 ? Color.WHITE : Color.BLACK;
	}
}
