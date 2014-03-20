package com.wallissoftware.chessanarchy.server.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.google.gson.GsonBuilder;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Serialize;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.MoveTree;
import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;
import com.wallissoftware.chessanarchy.shared.governments.MoveRequest;
import com.wallissoftware.chessanarchy.shared.governments.MoveResult;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;

@Entity
public class GameState {

    //private static final Logger logger = Logger.getLogger(GameState.class.getName());

    @Id private Long id;
    private long lastUpdated;
    @Index private long creationTime;

    private boolean swapColors;

    private String whiteGovernment = null;
    private String blackGovernment = null;

    private List<String> moveList = new ArrayList<String>();

    private List<Long> moveTimes = new ArrayList<Long>();

    private Set<MoveRequest> moveRequests = new HashSet<MoveRequest>();

    private String whiteExtraInfo, blackExtraInfo;

    @Serialize private List<Map<String, String>> messages = new ArrayList<Map<String, String>>();
    private static long lastServerMessage = -1;

    @SuppressWarnings("unused")
    private GameState() {
    };

    public GameState(final boolean swapColors) {
        this.creationTime = System.currentTimeMillis() + 10000;
        this.swapColors = swapColors;
    }

    public boolean isGovernmentElected() {
        return whiteGovernment != null && blackGovernment != null;
    }

    @OnSave
    void markLastUpdated() {
        lastUpdated = System.currentTimeMillis();
    }

    public void addMove(final String move) {
        moveList.add(move);
        moveRequests.clear();
        try {
            MoveTree.get(moveList);
            moveTimes.add(System.currentTimeMillis());
            addMessage("m" + move, getCurrentPlayer().getOpposite());
            if (isFinished()) {
                String message = "GAME OVER: ";
                if (getLastMove().endsWith("#")) {
                    message += getCurrentPlayer().getOpposite() + " WINS";
                } else {
                    message += "DRAW";
                }
                addMessage(message, null);
            }
        } catch (final IllegalMoveException e) {
            moveList.remove(moveList.size() - 1);
            addMessage("IllegalMoveException for move: " + move, null);
        }

    }

    private String getLastMove() {
        if (moveList.isEmpty()) {
            return "";
        }
        return moveList.get(moveList.size() - 1);
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public boolean isFinished() {
        try {
            return (getLastMove().endsWith("#")) || (moveList.size() > 50 && MoveTree.get(moveList).isDraw());
        } catch (final IllegalMoveException e) {
            return true;
        }
    }

    public String getJson() {
        final Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("id", id + "");
        jsonMap.put("created", creationTime + "");
        jsonMap.put("swapColors", swapColors);
        jsonMap.put("whiteGovernment", whiteGovernment);
        jsonMap.put("blackGovernment", blackGovernment);
        jsonMap.put("moveList", moveList);
        jsonMap.put("legalMoves", getLegalMoveMap());
        return new GsonBuilder().disableHtmlEscaping().create().toJson(jsonMap);
    }

    public Map<String, String> getLegalMoveMap() {
        try {
            return MoveTree.get(moveList).getLegalMoveMap();

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

        if (isGovernmentElected()) {
            if (getSystemOfGovernemnt().isReady(getExtraInfo(), getTimeOfLastMove(), moveRequestList)) {
                final MoveResult moveResult = getSystemOfGovernemnt().getMove(getExtraInfo(), moveRequestList);
                addMove(moveResult.getMove());
                setExtraInfo(moveResult.getExtraInfo());
            }
        } else if (isElectionComplete()) {

            final Map<String, Integer> whiteVotes = new HashMap<String, Integer>();
            final Map<String, Integer> blackVotes = new HashMap<String, Integer>();
            for (final MoveRequest moveRequest : SystemOfGovernment.stripMultipleVotesAndSort(moveRequestList, true)) {
                if (moveRequest.getMove() != null) {
                    final String vote = moveRequest.getMove().toLowerCase();

                    final Map<String, Integer> voteMap = moveRequest.getColor() == Color.WHITE ? whiteVotes : blackVotes;

                    if (!voteMap.containsKey(vote)) {
                        voteMap.put(vote, 0);
                    }
                    voteMap.put(vote, voteMap.get(vote) + 1);

                }
            }

            Entry<String, Integer> whiteEntry = null;
            for (final Entry<String, Integer> entry : whiteVotes.entrySet()) {
                if (whiteEntry == null || whiteEntry.getValue() < entry.getValue()) {
                    whiteEntry = entry;
                }
            }
            setWhiteSystemOfGovernment(whiteEntry == null ? "anarchy" : whiteEntry.getKey());

            addMessage("WHITE USES " + getWhiteSystemOfGovernment(), null);

            Entry<String, Integer> blackEntry = null;
            for (final Entry<String, Integer> entry : blackVotes.entrySet()) {
                if (blackEntry == null || blackEntry.getValue() < entry.getValue()) {
                    blackEntry = entry;
                }
            }
            setBlackSystemOfGovernment(blackEntry == null ? "anarchy" : blackEntry.getKey());
            addMessage("BLACK USES " + getBlackSystemOfGovernment(), null);
            moveRequests.clear();
        }

    }

    private boolean isElectionComplete() {
        return isElectionStarted() && System.currentTimeMillis() - creationTime > 60000;
    }

    public boolean isElectionStarted() {
        return System.currentTimeMillis() - creationTime > 30000;
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
        return SystemOfGovernment.get(getCurrentPlayer() == Color.WHITE ? getWhiteSystemOfGovernment() : getBlackSystemOfGovernment());

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
        return swapColors ? blackGovernment : whiteGovernment;
    }

    private void setWhiteSystemOfGovernment(final String governmentName) {
        if (swapColors) {
            blackGovernment = governmentName;
        } else {
            whiteGovernment = governmentName;
        }
    }

    private void setBlackSystemOfGovernment(final String governmentName) {
        if (swapColors) {
            whiteGovernment = governmentName;
        } else {
            blackGovernment = governmentName;
        }
    }

    private void addMessage(final String message, final Color color) {
        lastServerMessage = Math.max(creationTime + 1, Math.max(lastServerMessage + 1, System.currentTimeMillis()));
        addMessage(lastServerMessage, message, color);

    }

    public Set<Map<String, String>> getLastMessages(final int count) {
        final HashSet<Map<String, String>> result = new HashSet<Map<String, String>>(messages.subList(Math.max(0, messages.size() - count), messages.size()));
        if (getId() != null && result.size() < count) {
            result.add(getMessage("Start" + getId(), creationTime, "STARTING GAME: " + getId() + (swapColors() ? "T" : "F"), null));
        }

        return result;
    }

    public Set<Map<String, String>> getAllMessages() {
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

    public String getPgn() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moveList.size(); i++) {
            if (i % 2 == 0) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append("\n");
                sb.append((i / 2) + 1).append(".");
            }
            sb.append(" ").append(moveList.get(i));
        }
        return sb.toString();

    }

}
