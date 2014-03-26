package com.wallissoftware.chessanarchy.shared.message;

import com.wallissoftware.chessanarchy.shared.game.Color;

public interface Message {

    String getName();

    String getUserId();

    String getText();

    Color getColor();

    double getCreated();

    String getId();

    void swapColors();

}
