package com.wallissoftware.chessanarchy.shared.message;

import com.wallissoftware.chessanarchy.shared.game.Color;

public class MessageWrapper implements Message, Comparable<MessageWrapper> {

	private final Message message;

	public MessageWrapper(final Message message) {
		this.message = message;
	}

	@Override
	public String getName() {
		return message.getName();
	}

	@Override
	public String getUserId() {
		return message.getUserId();
	}

	@Override
	public String getText() {
		return message.getText();
	}

	@Override
	public Color getColor() {
		return message.getColor();
	}

	public boolean isFromGameMaster() {
		return getUserId().equals("Game Master");
	}

	public String getNewGameId() {
		if (isFromGameMaster() && getText().startsWith("STARTING NEW GAME: ")) {
			return getText().replace("STARTING NEW GAME: ", "");
		}
		return null;
	}

	public String getFormattedMessage() {
		if (isNickChange()) {
			String name = getText().substring(6);
			name = name.replace(" ", "");
			if (name.length() > 20) {
				name = name.substring(0, 20);
			}
			return getName() + " changed their nick to " + name;
		} else if (is3rdPerson()) {
			return getName() + " " + getText().substring(4);
		} else if (isTeamChange()) {
			String team = getText().substring(6);
			team = team.replace(" ", "");
			if (team.toLowerCase().equals("white") || team.toLowerCase().equals("black")) {
				return getName() + " joins the " + team.toLowerCase() + " team.";
			}
			return "";
		} else {
			return getText();
		}

	}

	public boolean isNickChange() {
		return getText().toLowerCase().startsWith("\\/nick");

	}

	public boolean is3rdPerson() {
		return getText().toLowerCase().startsWith("\\/me");

	}

	public boolean isTeamChange() {
		return getText().toLowerCase().startsWith("\\/team");
	}

	@Override
	public long getCreated() {
		return message.getCreated();
	}

	@Override
	public int compareTo(final MessageWrapper o) {
		if (o.getCreated() < getCreated()) {
			return -1;
		} else if (o.getCreated() > getCreated()) {
			return 1;
		}
		return 0;
	}
}
