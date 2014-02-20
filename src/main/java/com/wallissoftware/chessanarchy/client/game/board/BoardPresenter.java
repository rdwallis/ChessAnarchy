package com.wallissoftware.chessanarchy.client.game.board;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.board.piece.PiecePresenter;
import com.wallissoftware.chessanarchy.client.game.board.promotion.PromotionPresenter;
import com.wallissoftware.chessanarchy.shared.game.Board;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;
import com.wallissoftware.chessanarchy.shared.game.pieces.Piece;
import com.wallissoftware.chessanarchy.shared.game.pieces.PieceMoveHandler;

public class BoardPresenter extends PresenterWidget<BoardPresenter.MyView> implements BoardUiHandlers {
	public interface MyView extends View, HasUiHandlers<BoardUiHandlers> {

		void setPieceInSquare(IsWidget isWidget, Square square);

		void capture(IsWidget isWidget);

		void removeFromBoard(IsWidget piece);

	}

	private final Provider<PiecePresenter> piecePresenterProvider;
	private final Board board;
	private final PromotionPresenter promotionPresenter;

	@Inject
	BoardPresenter(final EventBus eventBus, final MyView view, final Provider<PiecePresenter> piecePresenterProvider, final Board board, final PromotionPresenter promotionPresenter) {
		super(eventBus, view);
		this.promotionPresenter = promotionPresenter;
		this.piecePresenterProvider = piecePresenterProvider;
		this.board = board;
		getView().setUiHandlers(this);

	}

	@Override
	protected void onBind() {
		super.onBind();

		drawBoard();

	}

	private void drawBoard() {
		for (final Piece piece : board.getPieces()) {
			final PiecePresenter piecePresenter = getPiecePresenter(piece);
			getView().setPieceInSquare(piecePresenter.getView(), piecePresenter.getPosition());
		}

	}

	private PiecePresenter getPiecePresenter(final Piece piece) {
		final PiecePresenter piecePresenter = piecePresenterProvider.get();
		piecePresenter.setPiece(piece);
		piece.addPieceMoveHandler(new PieceMoveHandler() {

			@Override
			public void afterMove() {
				if (piece.getPromotedTo() != null) {
					getView().removeFromBoard(piecePresenter.getView());
					getView().setPieceInSquare(getPiecePresenter(piece.getPromotedTo()), piece.getPromotedTo().getPosition());
				} else if (piece.isCaptured()) {
					getView().capture(piecePresenter.getView());
				} else {
					getView().setPieceInSquare(piecePresenter.getView(), piece.getPosition());
				}

			}
		});
		return piecePresenter;
	}

	@Override
	public boolean isMoveLegal(final Square start, final Square end) {
		return board.isPartialMoveLegal(start, end);

	}

	@Override
	public void makeMove(final Square start, final Square end) {
		if (board.isMovePromotion(start, end)) {
			promotionPresenter.setPartialMove(start, end);
			addToPopupSlot(promotionPresenter);
		} else {
			makeMove(new Move(start, end));
		}
	}

	private void makeMove(final Move move) {
		board.doMove(move, true);
	}

}
