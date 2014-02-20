package com.wallissoftware.chessanarchy.client.game.board.promotion;

import com.gwtplatform.mvp.client.UiHandlers;

public interface PromotionUiHandlers extends UiHandlers {

	void cancel();

	void promoteToRook();

	void promoteToBishop();

	void promoteToKnight();

	void promoteToQueen();
}
