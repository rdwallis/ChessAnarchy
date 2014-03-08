package com.wallissoftware.chessanarchy.client.game.board.piece;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.wallissoftware.chessanarchy.client.game.board.piece.images.PieceSprites;

public class PieceWidget extends Image {

	private char kind;

	public PieceWidget(final char kind) {
		super(getImageResource(kind));
		this.kind = kind;
	}

	public char getKind() {
		return kind;
	}

	private static ImageResource getImageResource(final char c) {
		switch (c) {
		case 'k':
			return PieceSprites.INSTANCE.blackKing();
		case 'q':
			return PieceSprites.INSTANCE.blackQueen();
		case 'r':
			return PieceSprites.INSTANCE.blackRook();
		case 'b':
			return PieceSprites.INSTANCE.blackBishop();
		case 'n':
			return PieceSprites.INSTANCE.blackKnight();
		case 'p':
			return PieceSprites.INSTANCE.blackPawn();

		case 'K':
			return PieceSprites.INSTANCE.whiteKing();
		case 'Q':
			return PieceSprites.INSTANCE.whiteQueen();
		case 'R':
			return PieceSprites.INSTANCE.whiteRook();
		case 'B':
			return PieceSprites.INSTANCE.whiteBishop();
		case 'N':
			return PieceSprites.INSTANCE.whiteKnight();
		case 'P':
			return PieceSprites.INSTANCE.whitePawn();

		}
		throw new RuntimeException(c + " is not a valid piece");
	}

	public void setKind(final char kind) {
		this.kind = kind;
		this.setResource(getImageResource(kind));
	}

	public boolean isKing() {
		return Character.toUpperCase(getKind()) == 'K';
	}

	public boolean isPawn() {
		return Character.toUpperCase(getKind()) == 'P';
	}

}
