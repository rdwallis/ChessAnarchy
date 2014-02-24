package com.wallissoftware.chessanarchy.client.game.board;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.board.piece.PiecePresenter;
import com.wallissoftware.chessanarchy.client.game.board.promotion.PromotionPresenter;
import com.wallissoftware.chessanarchy.client.game.chat.events.ReceivedMessageCacheEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.ReceivedMessageCacheEvent.ReceivedMessageCacheHandler;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent;
import com.wallissoftware.chessanarchy.shared.game.Board;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;
import com.wallissoftware.chessanarchy.shared.game.pieces.Piece;
import com.wallissoftware.chessanarchy.shared.game.pieces.PieceMoveHandler;

public class BoardPresenter extends PresenterWidget<BoardPresenter.MyView> implements BoardUiHandlers, ReceivedMessageCacheHandler {
	public interface MyView extends View, HasUiHandlers<BoardUiHandlers> {

		void setPieceInSquare(IsWidget isWidget, Square square);

		void capture(IsWidget isWidget);

		void removeFromBoard(IsWidget piece);

		void makeGhostMove(final double startTime, Piece piece, Square end);

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
		addRegisteredHandler(ReceivedMessageCacheEvent.getType(), this);
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
		if (!board.isPartialMoveLegal(start, end)) {
			fireEvent(new SendMessageEvent(new Move(start, end).toString()));
			return false;
		}
		return true;

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
		fireEvent(new SendMessageEvent(board.doMove(move, true)));
	}

	@Override
	public void onReceivedMessageCache(final ReceivedMessageCacheEvent event) {
		final Map<String, Move> notationMap = board.getLegalMovesWithNotation();
		for (int i = 0; i < event.getMessageCache().getMessages().length(); i++) {
			final String message = event.getMessageCache().getMessages().get(i).getMessage().toLowerCase();
			final long created = event.getMessageCache().getMessages().get(i).getCreated();
			if (System.currentTimeMillis() - created < 4000) {
				if (message.length() == 5 && message.charAt(2) == '-') {
					final Square startSquare = Square.fromString(message.substring(0, 2));
					if (startSquare != null) {
						final Square endSquare = Square.fromString(message.substring(3, 5));
						if (endSquare != null) {
							makeGhostMove(created, new Move(startSquare, endSquare));
						}
					}
				} else if (notationMap.containsKey(message)) {
					makeGhostMove(created, notationMap.get(message));
				} else {
					//TODO maybe ghost guess at illegal moves???
				}
			}

		}

	}

	private void makeGhostMove(final double startTime, final Move move) {
		getView().makeGhostMove(startTime, board.getPieceAt(move.getStart()), move.getEnd());

	}

}
