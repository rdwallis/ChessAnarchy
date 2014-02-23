package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import com.wallissoftware.chessanarchy.client.game.chat.model.MessageCache;

public class MessageLink implements Comparable<MessageLink> {

	private final String previousId;
	private final String id;
	private long created;

	public MessageLink(final MessageCache cache) {
		this.previousId = cache.getPreviousId();
		this.id = cache.getId();
		this.created = cache.getCreated();
	}

	@Override
	public int compareTo(final MessageLink o) {
		if (created > o.created) {
			return 1;
		} else if (created < o.created) {
			return -1;
		} else {
			return 0;
		}
	}

	public String getId() {
		return id;
	}

	public String getPreviousId() {
		return previousId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (created ^ (created >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((previousId == null) ? 0 : previousId.hashCode());
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
		final MessageLink other = (MessageLink) obj;
		if (created != other.created)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (previousId == null) {
			if (other.previousId != null)
				return false;
		} else if (!previousId.equals(other.previousId))
			return false;
		return true;
	}

}
