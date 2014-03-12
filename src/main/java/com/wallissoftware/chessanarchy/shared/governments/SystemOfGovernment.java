package com.wallissoftware.chessanarchy.shared.governments;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.message.MessageWrapper;

public abstract class SystemOfGovernment implements GovernmentInfo {

	private static final Map<String, SystemOfGovernment> registeredGovernments = new HashMap<String, SystemOfGovernment>();

	private static void registerGovernment(final SystemOfGovernment government) {
		if (registeredGovernments.containsKey(government.getName())) {
			throw new RuntimeException("You've already registered a government called " + government.getName() + ".");
		}
		registeredGovernments.put(government.getName(), government);
	}

	static {
		registerGovernment(new Anarchy());
		registerGovernment(new Democracy());
		registerGovernment(new Hipsterism());
	}

	public boolean isReady(final String extraInfo, final long timeOfLastMove, final List<MoveRequest> moveRequests) {
		return System.currentTimeMillis() - timeOfLastMove > 30000 && !moveRequests.isEmpty();
	}

	public abstract MoveResult getMove(final String extraInfo, List<MoveRequest> moveRequests);

	public static SystemOfGovernment get(final String governmentName) {
		return registeredGovernments.get(governmentName);
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

}
