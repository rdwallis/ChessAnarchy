package com.wallissoftware.chessanarchy.client.game.chat.messagelog;

import java.util.HashMap;
import java.util.Map;

import com.wallissoftware.chessanarchy.client.game.chat.model.JsonMessageCache;

public class MessageLink {
	private final String id;

	private final String previousId;

	private final long created;

	private static MessageLink head;

	private static Map<String, MessageLink> mapById = new HashMap<String, MessageLink>();

	private static Map<String, MessageLink> mapByPreviousId = new HashMap<String, MessageLink>();

	private MessageLink(final long created, final String id, final String previousId) {
		this.created = created;
		this.id = id;
		this.previousId = previousId;
	}

	public boolean hasLoadedPrevious() {
		return previousId == null || mapById.containsKey(previousId);
	}

	public static boolean addMessageLink(final JsonMessageCache messageCache) {
		final String id = messageCache.getId();
		if (mapById.containsKey(id)) {
			return false;
		}
		final String previousId = messageCache.getPreviousId();
		final long created = messageCache.getCreated();

		final MessageLink newMessage = createMessageLink(created, id, previousId);
		if (head == null || head.getId().equals(previousId) || head.getCreated() < created) {
			head = newMessage;
		}
		return true;

	}

	private static MessageLink createMessageLink(final long created, final String id, final String previousId) {
		final MessageLink result = new MessageLink(created, id, previousId);
		mapById.put(id, result);
		if (previousId != null) {
			mapByPreviousId.put(previousId, result);
		}
		return result;
	}

	private long getCreated() {
		return created;
	}

	public static MessageLink getTail() {
		MessageLink index = head;
		while (index != null && index.hasLoadedPrevious()) {
			index = index.getPreviousLink();
		}
		return index;
	}

	private MessageLink getPreviousLink() {
		return mapById.get(previousId);
	}

	public String getId() {
		return id;
	}

	public String getPreviousId() {
		return previousId;
	}

}
