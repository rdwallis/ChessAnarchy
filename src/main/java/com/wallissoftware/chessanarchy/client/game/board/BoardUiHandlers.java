package com.wallissoftware.chessanarchy.client.game.board;

import com.gwtplatform.mvp.client.UiHandlers;
import com.wallissoftware.chessanarchy.shared.game.Square;

public interface BoardUiHandlers extends UiHandlers {

	boolean isMoveLegal(Square start, Square end);

	void makeMove(Square startDragSquare, Square endSquare);

	boolean canMove(Square square);
}
