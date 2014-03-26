package com.wallissoftware.chessanarchy.shared.message;

import java.io.Serializable;

import com.wallissoftware.chessanarchy.shared.game.Color;

public class MessageImpl implements Message, Serializable {

    private static final long serialVersionUID = 1L;

    private final String name, userId, text, id;

    private Color color;

    private final double created;

    public MessageImpl(final String name, final String userId, final String text, final String id, final Color color, final double created) {
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
    public double getCreated() {
        return created;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        long temp;
        temp = Double.doubleToLongBits(created);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
        final MessageImpl other = (MessageImpl) obj;
        if (color != other.color)
            return false;
        if (Double.doubleToLongBits(created) != Double.doubleToLongBits(other.created))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }

    @Override
    public void swapColors() {
        if (color != null) {
            color = color.getOpposite();
        }

    }

}
