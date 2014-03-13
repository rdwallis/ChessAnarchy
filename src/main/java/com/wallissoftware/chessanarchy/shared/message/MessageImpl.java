package com.wallissoftware.chessanarchy.shared.message;

import com.wallissoftware.chessanarchy.shared.game.Color;

public class MessageImpl implements Message {

	private final String name, userId, text, id;

	private final Color color;

	private final long created;

	public MessageImpl(final String name, final String userId, final String text, final String id, final Color color, final long created) {
		super();
		this.name = name;
		this.userId = userId;
		this.text = text;
		this.id = id;
		this.color = color;
		this.created = created;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public long getCreated() {
		return created;
	}

	@Override
	public String getId() {
		return id;
	}

}
