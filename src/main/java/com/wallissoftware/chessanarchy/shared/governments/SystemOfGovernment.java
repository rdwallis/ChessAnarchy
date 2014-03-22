package com.wallissoftware.chessanarchy.shared.governments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public abstract class SystemOfGovernment implements GovernmentInfo {

    private static final Map<String, SystemOfGovernment> registeredGovernments = new HashMap<String, SystemOfGovernment>();

    private static void registerGovernment(final SystemOfGovernment government) {
        if (registeredGovernments.containsKey(government.getName().toLowerCase())) {
            throw new RuntimeException("You've already registered a government called " + government.getName() + ".");
        }
        registeredGovernments.put(government.getName().toLowerCase(), government);
    }

    static {
        registerGovernment(new Anarchy());
        registerGovernment(new Democracy());
        registerGovernment(new Hipsterism());
    }

    private final List<String> moveMessages = new ArrayList<String>();
    private final List<String> countingVoteMessages = new ArrayList<String>();
    private final List<String> insults = new ArrayList<String>();

    protected void addMoveMessage(final String message) {
        moveMessages.add(message);
    }

    protected void addCountingVoteMessage(final String message) {
        countingVoteMessages.add(message);
    }

    protected void addInsult(final String message) {
        insults.add(message);
    }

    private String getRandomMessage(final long seed, final List<String> messages, final Color color) {
        final String result = messages.get(new Random(seed).nextInt(messages.size()));
        if (color != null) {
            return result.replace("${color}", color.name().toLowerCase());
        }
        return result;
    }

    public boolean isReady(final String extraInfo, final long timeOfLastMove, final List<MoveRequest> moveRequests) {
        return System.currentTimeMillis() - timeOfLastMove > 5000 && !moveRequests.isEmpty();
    }

    protected abstract MoveResult calculateMove(final String extraInfo, List<MoveRequest> moveRequests);

    public MoveResult getMove(final String extraInfo, final List<MoveRequest> moveRequests) {
        return calculateMove(extraInfo, stripMultipleVotesAndSort(moveRequests, isLastVoteOfPlayerPreferred()));
    }

    abstract boolean isLastVoteOfPlayerPreferred();

    public static List<MoveRequest> stripMultipleVotesAndSort(final List<MoveRequest> moveRequests, final boolean preferLastVoteOfPlayer) {
        Collections.sort(moveRequests);
        if (preferLastVoteOfPlayer) {
            Collections.reverse(moveRequests);
        }
        final Set<String> playerIds = new HashSet<String>();
        final Iterator<MoveRequest> it = moveRequests.iterator();
        while (it.hasNext()) {
            if (!playerIds.add(it.next().getPlayerId())) {
                it.remove();
            }
        }
        if (preferLastVoteOfPlayer) {
            Collections.reverse(moveRequests);
        }
        return moveRequests;

    }

    public static SystemOfGovernment get(final String governmentName) {
        if (governmentName == null) {
            return null;
        }
        return registeredGovernments.get(governmentName.toLowerCase());
    }

    public String getPlayerCount(final Color color, final List<MessageWrapper> messages) {

        final Set<String> countedPlayers = new HashSet<String>();
        int playerCount = 0;
        for (final MessageWrapper message : messages) {
            if (message.getColor() == color && countedPlayers.add(message.getUserId())) {
                playerCount += 1;
            }
        }
        return playerCount + " players";
    }

    public static boolean isSystemOfGovernment(final String governmentName) {
        return registeredGovernments.containsKey(governmentName.toLowerCase());
    }

    public static List<SystemOfGovernment> getAll() {
        return new ArrayList<SystemOfGovernment>(registeredGovernments.values());
    }

    @Override
    public String getShortDescription() {
        return getDescription();
    }

    @Override
    public String getMoveMessage(final long seed, final Color color, final String move) {
        return getRandomMessage(seed, moveMessages, color).replace("${move}", move);
    }

    @Override
    public String getInsult(final long seed, final Color color) {
        return getRandomMessage(seed, insults, color);
    }

    @Override
    public String getCountingVotesMessage(final long seed, final Color color) {
        return getRandomMessage(seed, countingVoteMessages, color);
    }

}
