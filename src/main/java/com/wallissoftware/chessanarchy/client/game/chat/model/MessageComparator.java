package com.wallissoftware.chessanarchy.client.game.chat.model;

import java.util.Comparator;

import com.google.inject.Singleton;

@Singleton
public class MessageComparator implements Comparator<Message> {

	@Override
	public int compare(final Message o1, final Message o2) {
		if (o2.getCreated() < o1.getCreated()) {
			return -1;
		} else if (o2.getCreated() > o1.getCreated()) {
			return 1;
		}
		return 0;
	}

}
