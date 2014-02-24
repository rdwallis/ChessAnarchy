package com.wallissoftware.chessanarchy.server.governments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wallissoftware.chessanarchy.shared.government.Government;

public abstract class SystemOfGovernment {
	private final static Map<Government, SystemOfGovernment> systemOfGovernments = new HashMap<Government, SystemOfGovernment>();

	static {
		systemOfGovernments.put(Government.ANARCHY, new Anarchy());

	}

	public boolean isReady(final long timeOfLastMove, final List<MoveRequest> moveRequests) {
		return System.currentTimeMillis() - timeOfLastMove > 30000 && !moveRequests.isEmpty();
	}

	public abstract String getMove(List<MoveRequest> moveRequest);

	public static SystemOfGovernment get(final Government government) {
		return systemOfGovernments.get(government);
	}

}
