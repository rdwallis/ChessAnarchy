package com.wallissoftware.chessanarchy.server.governments;

import com.googlecode.objectify.annotation.Embed;
import com.wallissoftware.chessanarchy.shared.game.Color;

@Embed
public class MoveRequest implements Comparable<MoveRequest> {

	private long creationTime;

	private String playerId;

	private String move;

	private Color color;

	@SuppressWarnings("unused")
	private MoveRequest() {
	};

	public MoveRequest(final Color color, final String playerId, final String move) {
		this.color = color;
		this.creationTime = System.currentTimeMillis();
		this.playerId = playerId;
		this.move = move;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public String getPlayerId() {
		return playerId;
	}

	public String getMove() {
		return move;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (creationTime ^ (creationTime >>> 32));
		result = prime * result + ((move == null) ? 0 : move.hashCode());
		result = prime * result + ((playerId == null) ? 0 : playerId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MoveRequest other = (MoveRequest) obj;
		if (creationTime != other.creationTime)
			return false;
		if (move == null) {
			if (other.move != null)
				return false;
		} else if (!move.equals(other.move))
			return false;
		if (playerId == null) {
			if (other.playerId != null)
				return false;
		} else if (!playerId.equals(other.playerId))
			return false;
		return true;
	}

	@Override
	public int compareTo(final MoveRequest o) {
		return Long.compare(creationTime, o.creationTime);
	}

	public Color getColor() {
		return color;
	}

}
