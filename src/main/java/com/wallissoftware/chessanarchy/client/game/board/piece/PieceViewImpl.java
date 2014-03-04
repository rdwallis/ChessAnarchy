package com.wallissoftware.chessanarchy.client.game.board.piece;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.chessanarchy.client.game.board.piece.images.PieceSprites;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.pieces.Bishop;
import com.wallissoftware.chessanarchy.shared.game.pieces.King;
import com.wallissoftware.chessanarchy.shared.game.pieces.Knight;
import com.wallissoftware.chessanarchy.shared.game.pieces.Pawn;
import com.wallissoftware.chessanarchy.shared.game.pieces.Piece;
import com.wallissoftware.chessanarchy.shared.game.pieces.Queen;
import com.wallissoftware.chessanarchy.shared.game.pieces.Rook;

public class PieceViewImpl extends ViewWithUiHandlers<PieceUiHandlers> implements PiecePresenter.MyView {
	public interface Binder extends UiBinder<Widget, PieceViewImpl> {
	}

	@UiField Image image;
	private final PieceSprites sprites;

	@Inject
	PieceViewImpl(final Binder binder, final PieceSprites sprites) {
		initWidget(binder.createAndBindUi(this));
		this.sprites = sprites;
	}

	@Override
	public void setPiece(final Piece piece) {
		if (piece instanceof Pawn) {
			if (piece.getColor() == Color.WHITE) {
				image.setResource(sprites.whitePawn());
			} else {
				image.setResource(sprites.blackPawn());
			}
		} else if (piece instanceof Rook) {
			if (piece.getColor() == Color.WHITE) {
				image.setResource(sprites.whiteRook());
			} else {
				image.setResource(sprites.blackRook());
			}
		} else if (piece instanceof Knight) {
			if (piece.getColor() == Color.WHITE) {
				image.setResource(sprites.whiteKnight());
			} else {
				image.setResource(sprites.blackKnight());
			}
		} else if (piece instanceof Bishop) {
			if (piece.getColor() == Color.WHITE) {
				image.setResource(sprites.whiteBishop());
			} else {
				image.setResource(sprites.blackBishop());
			}
		} else if (piece instanceof King) {
			if (piece.getColor() == Color.WHITE) {
				image.setResource(sprites.whiteKing());
			} else {
				image.setResource(sprites.blackKing());
			}
		} else if (piece instanceof Queen) {
			if (piece.getColor() == Color.WHITE) {
				image.setResource(sprites.whiteQueen());
			} else {
				image.setResource(sprites.blackQueen());
			}
		}
	}
}
