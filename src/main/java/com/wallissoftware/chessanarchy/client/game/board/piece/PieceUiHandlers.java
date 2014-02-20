package com.wallissoftware.chessanarchy.client.game.board.piece;

import com.gwtplatform.mvp.client.UiHandlers;
import com.wallissoftware.chessanarchy.shared.game.Square;

public interface PieceUiHandlers extends UiHandlers {

	Square getPosition();
}
