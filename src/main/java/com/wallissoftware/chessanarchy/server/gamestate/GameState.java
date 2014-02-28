package com.wallissoftware.chessanarchy.server.gamestate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Serialize;
import com.wallissoftware.chessanarchy.shared.game.Board;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;
import com.wallissoftware.chessanarchy.shared.governments.MoveRequest;
import com.wallissoftware.chessanarchy.shared.governments.MoveResult;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;

@Entity
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

	@Serialize private Set<Map<String, String>> messages = new HashSet<Map<String, String>>();
	private static long lastServerMessage = -1;

	@SuppressWarnings("unused")
	private GameState() {
	};

	public GameState(final boolean swapColors) {
		this.creationTime = System.currentTimeMillis();
		this.swapColors = swapColors;

		addMessage("WHITE USES " + getWhiteSystemOfGovernment(), null);
		addMessage("BLACK USES " + getBlackSystemOfGovernment(), null);
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
			addMessage("m" + move, getCurrentPlayer().getOpposite());

		} catch (final IllegalMoveException e) {
			//moveList.remove(moveList.size() - 1);
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

	public void processMoveRequests() {
		final List<MoveRequest> moveRequestList = new ArrayList<MoveRequest>(moveRequests);
		Collections.sort(moveRequestList);
		if (getSystemOfGovernemnt().isReady(getExtraInfo(), getTimeOfLastMove(), moveRequestList)) {
			final MoveResult moveResult = getSystemOfGovernemnt().getMove(getExtraInfo(), moveRequestList);
			addMove(moveResult.getMove());
			setExtraInfo(moveResult.getExtraInfo());
		}

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

	public String getBlackSystemOfGovernment() {
		return swapColors ? whiteGovernment : blackGovernment;
	}

	public String getWhiteSystemOfGovernment() {
		return swapColors ? whiteGovernment : blackGovernment;
	}

	private void addMessage(final String message, final Color color) {
		lastServerMessage = Math.max(creationTime + 1, Math.max(lastServerMessage + 1, System.currentTimeMillis()));
		addMessage(lastServerMessage, message, color);

	}

	public Set<Map<String, String>> getMessages() {
		final HashSet<Map<String, String>> result = new HashSet<Map<String, String>>(messages);
		if (getId() != null) {
			result.add(getMessage("Start" + getId(), creationTime, "STARTING GAME: " + getId() + (swapColors() ? "T" : "F"), null));
		}

		return result;
	}

	private void addMessage(final long creationTime, final String message, final Color color) {

		messages.add(getMessage(UUID.randomUUID().toString(), creationTime, message, color));

	}

	private Map<String, String> getMessage(final String id, final long creationTime, final String message, final Color color) {
		final Map<String, String> serverMessageMap = new HashMap<String, String>();
		serverMessageMap.put("userId", "Game Master");
		serverMessageMap.put("name", "Game Master");
		serverMessageMap.put("id", id);
		serverMessageMap.put("message", message);
		serverMessageMap.put("created", creationTime + "");
		if (color != null) {
			serverMessageMap.put("color", color.name());
		}
		return serverMessageMap;
	}
}
