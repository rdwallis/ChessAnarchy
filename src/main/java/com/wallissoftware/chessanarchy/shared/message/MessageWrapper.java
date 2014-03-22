package com.wallissoftware.chessanarchy.shared.message;

import java.util.HashSet;
import java.util.Set;

import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.GovernmentInfo;

public class MessageWrapper implements Message, Comparable<MessageWrapper> {

    private final Message message;
    private boolean swapColor = false;
    private boolean valid = true;
    private boolean validated = false;

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
        return isValid() && getUserId().equals("Game Master");
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
                return getColor().getTitleCase() + " makes move: " + getMove();
            }
            return getMove();
        }
        if (isFromGameMaster()) {
            if (getNewGameId() != null) {
                return "Starting new game in 30 seconds.";
            }
            if (getText().startsWith("BLACK USES ")) {
                return getText().replace("BLACK USES", "Black chooses");
            }
            if (getText().startsWith("WHITE USES ")) {
                return getText().replace("WHITE USES", "White chooses");
            }
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

    public Set<MessageWrapper> getFakeMessages(final GovernmentInfo whiteGovernment, final GovernmentInfo blackGovernment) {

        if (getNewGameId() != null) {
            final Message electionStartMessage = new MessageImpl(getName(), getUserId(), "CHOOSE YOUR GOVERNMENT", getId() + "esm", getColor(), getCreated() + 30000);
            final Set<MessageWrapper> result = new HashSet<MessageWrapper>();
            result.add(new MessageWrapper(electionStartMessage));
            return result;
        }
        if (whiteGovernment != null && blackGovernment != null) {
            if (getWinner() != null) {
                final Message insultMessage = new MessageImpl(getName(), getUserId(), getWinner().getOpposite() == Color.WHITE ? whiteGovernment.getInsult(getCreated() + 500, getWinner().getOpposite()) : blackGovernment.getInsult(getCreated() + 500, getWinner().getOpposite()),
                        getId() + "insult", getColor(), getCreated() + 500);
                final Set<MessageWrapper> result = new HashSet<MessageWrapper>();
                result.add(new MessageWrapper(insultMessage));
                return result;
            }
            if (getMove() != null) {
                final Set<MessageWrapper> result = new HashSet<MessageWrapper>();
                final GovernmentInfo gov = getColor() == Color.WHITE ? whiteGovernment : blackGovernment;
                result.add(new MessageWrapper(new MessageImpl(getName(), getUserId(), gov.getCountingVotesMessage(getCreated() - 1000, getColor()), getId() + "cvm", getColor(), getCreated() - 1000)));
                result.add(new MessageWrapper(new MessageImpl(getName(), getUserId(), gov.getMoveMessage(getCreated(), getColor(), getMove()), getId() + "move", getColor(), getCreated())));
                return result;
            }
        }
        return null;
    }

    private Color getWinner() {
        if (isFromGameMaster() && getText().startsWith("GAME OVER: ")) {
            final String[] split = getText().split(" ");
            return Color.valueOf(split[2]);
        }
        return null;
    }

    public Long getElectionStart() {
        return isFromGameMaster() && getText().startsWith("CHOOSE YOUR GOVERNMENT") ? getCreated() : null;
    }

    private boolean isValid() {
        return valid || validated;
    }

    public void setValid(final boolean valid) {
        this.valid = valid;
        if (valid) {
            validated = true;
        }

    }

}
