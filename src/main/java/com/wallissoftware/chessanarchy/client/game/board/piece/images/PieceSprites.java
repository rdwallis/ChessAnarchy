package com.wallissoftware.chessanarchy.client.game.board.piece.images;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PieceSprites extends ClientBundle {

	public static PieceSprites INSTANCE = GWT.create(PieceSprites.class);

	ImageResource blackBishop();

	ImageResource blackKing();

	ImageResource blackKnight();

	ImageResource blackPawn();

	ImageResource blackQueen();

	ImageResource blackRook();

	ImageResource whiteBishop();

	ImageResource whiteKing();

	ImageResource whiteKnight();

	ImageResource whitePawn();

	ImageResource whiteQueen();

	ImageResource whiteRook();

}
