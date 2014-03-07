package com.wallissoftware.chessanarchy.client.game.board;

import com.gwtplatform.mvp.client.UiHandlers;
import com.wallissoftware.chessanarchy.shared.game.Move;

public interface BoardUiHandlers extends UiHandlers {

	boolean isMoveLegal(Move move);

	void makeMove(Move move);

	boolean canMove(int startFile, int startRank);

	boolean swapBoard();

	void preventRedraw();

	void allowRedraw();
}
