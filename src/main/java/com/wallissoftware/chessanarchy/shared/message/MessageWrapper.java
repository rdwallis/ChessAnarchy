package com.wallissoftware.chessanarchy.shared.message;

import com.wallissoftware.chessanarchy.shared.game.Color;

public class MessageWrapper implements Message, Comparable<MessageWrapper> {

	private final Message message;
	private boolean swapColor = false;

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
		if (swapColor) {
			if (message.getColor() != null) {
				return message.getColor().getOpposite();
			}
		}
		return message.getColor();

	}

	public boolean isFromGameMaster() {
		return getUserId().equals("Game Master");
	}

	public String getNewGameId() {
		if (isFromGameMaster() && getText().startsWith("STARTING GAME: ")) {
			return getText().replace("STARTING GAME: ", "");
		}
		return null;
	}

	public String getFormattedMessage() {
		if (getMove() != null) {
			if (getColor() != null) {
				return getColor() + " makes move: " + getMove();
			}
			return getMove();
		}
		if (isNickChange()) {
			String name = getText().substring(5);
			name = name.replace(" ", "");
			if (name.length() > 20) {
				name = name.substring(0, 20);
			}
			return getName() + " changed their nick to " + name;
		} else if (is3rdPerson()) {
			return getName() + " " + getText().substring(3);
		} else if (isTeamChange()) {
			String team = getText().substring(5);
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
		return getText().toLowerCase().startsWith("/nick");

	}

	public boolean is3rdPerson() {
		return getText().toLowerCase().startsWith("/me");

	}

	public boolean isTeamChange() {
		return getText().toLowerCase().startsWith("/team");
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

	public String getBlackGovernment() {
		return getText().startsWith("BLACK USES ") ? getText().replace("BLACK USES ", "") : null;

	}

	public String getWhiteGovernment() {
		return getText().startsWith("WHITE USES ") ? getText().replace("WHITE USES ", "") : null;

	}

	public String getMove() {
		return isFromGameMaster() && getText().startsWith("m") ? getText().substring(1) : null;
	}

	@Override
	public String toString() {
		return getCreated() + ": <" + getName() + "> : " + getText();
	}

	@Override
	public String getId() {
		return message.getId();
	}

	public void swapColor() {
		this.swapColor = true;

	}

}
