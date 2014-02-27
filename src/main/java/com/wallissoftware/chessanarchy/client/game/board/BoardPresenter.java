package com.wallissoftware.chessanarchy.client.game.board;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.Timer;
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
import com.wallissoftware.chessanarchy.client.game.chat.model.JsonMessage;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent.GameStateUpdatedHandler;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.client.user.UserChangedEvent;
import com.wallissoftware.chessanarchy.client.user.UserChangedEvent.UserChangedHandler;
import com.wallissoftware.chessanarchy.shared.game.Board;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;
import com.wallissoftware.chessanarchy.shared.game.pieces.Piece;
import com.wallissoftware.chessanarchy.shared.game.pieces.PieceMoveHandler;

public class BoardPresenter extends PresenterWidget<BoardPresenter.MyView> implements BoardUiHandlers, ReceivedMessageCacheHandler, GameStateUpdatedHandler, UserChangedHandler {
	public interface MyView extends View, HasUiHandlers<BoardUiHandlers> {

		void setPieceInSquare(IsWidget isWidget, Square square);

		void capture(IsWidget isWidget);

		void removeFromBoard(IsWidget piece);

		void makeGhostMove(final double startTime, Piece piece, Square end);

		void clearBoard();

	}

	private final Timer resetBoardTimer = new Timer() {

		@Override
		public void run() {
			try {
				if (!preventRedraw) {
					logger.info("Resetting Board:\n" + gameStateProvider.get().getMoveList());
					board.resetFromMoveList(gameStateProvider.get().getMoveList());
					if (shouldRedraw()) {
						drawBoard();
					}
				}
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	};

	private final Map<Piece, PiecePresenter> piecePresenterMap = new HashMap<Piece, PiecePresenter>();

	private final Provider<PiecePresenter> piecePresenterProvider;
	private final Board board;
	private final PromotionPresenter promotionPresenter;
	private final GameStateProvider gameStateProvider;
	private final static Logger logger = Logger.getLogger(BoardPresenter.class.getName());
	private Color lastUserColor = null;
	private boolean preventRedraw;
	private String lastGameId = "";

	private boolean shouldRedraw() {
		return User.get().getColor(true) != lastUserColor || lastGameId == null || lastGameId.equals(gameStateProvider.get().getId());
	}

	@Inject
	BoardPresenter(final EventBus eventBus, final MyView view, final Provider<PiecePresenter> piecePresenterProvider, final Board board, final PromotionPresenter promotionPresenter, final GameStateProvider gameStateProvider) {
		super(eventBus, view);
		this.gameStateProvider = gameStateProvider;
		this.promotionPresenter = promotionPresenter;
		this.piecePresenterProvider = piecePresenterProvider;
		this.board = board;
		getView().setUiHandlers(this);

	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(ReceivedMessageCacheEvent.getType(), this);
		addRegisteredHandler(GameStateUpdatedEvent.getType(), this);
		addRegisteredHandler(UserChangedEvent.getType(), this);
		drawBoard();

	}

	private void drawBoard() {
		lastUserColor = User.get().getColor(true);
		lastGameId = gameStateProvider.get().getId();
		for (final Piece piece : board.getPieces()) {
			final PiecePresenter piecePresenter = getPiecePresenter(piece);
			getView().setPieceInSquare(piecePresenter.getView(), piecePresenter.getPosition());
		}

	}

	private PiecePresenter getPiecePresenter(final Piece piece) {
		if (!piecePresenterMap.containsKey(piece)) {
			final PiecePresenter piecePresenter = piecePresenterProvider.get();
			piecePresenter.setPiece(piece);
			piece.addPieceMoveHandler(new PieceMoveHandler() {

				@Override
				public void afterMove() {
					logger.info("Redrawing Piece: " + piece);
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
			piecePresenterMap.put(piece, piecePresenter);
		}
		return piecePresenterMap.get(piece);
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
		fireEvent(new SendMessageEvent(board.doMove(move, true, true)));
	}

	@Override
	public void onReceivedMessageCache(final ReceivedMessageCacheEvent event) {
		try {
			final Map<String, Move> notationMap = board.getLegalMovesWithNotation();
			for (final JsonMessage msg : event.getMessageCache().getMessages()) {
				final String message = msg.getText();
				final long created = msg.getCreated();
				if (System.currentTimeMillis() - created < 4000 && board.getCurrentPlayer() == msg.getColor()) {
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
		} catch (final Exception e) {
			logger.info("Problem processing ghost moves");
		}

	}

	private void makeGhostMove(final double startTime, final Move move) {
		getView().makeGhostMove(startTime, gameStateProvider.getSyncedBoard().getPieceAt(move.getStart()), move.getEnd());

	}

	@Override
	public void onGameStateUpdated(final GameStateUpdatedEvent event) {
		resetBoard();
	}

	@Override
	public void onUserChanged(final UserChangedEvent event) {
		resetBoard();
	}

	private void resetBoard() {
		resetBoardTimer.schedule(100);

	}

	@Override
	public boolean canMove(final Square square) {
		try {
			if (User.get().getColor() == null) {
				return false;
			}
			Color userColor = User.get().getColor();
			if (userColor != null && gameStateProvider.get().swapColors()) {
				userColor = userColor.getOpposite();
			}
			if (board.getCurrentPlayer() != userColor) {
				return false;
			}
			if (board.getPieceAt(square) == null) {
				return false;
			}

			for (final Move move : board.getLegalMovesWithNotation().values()) {
				if (move.getStart().equals(square)) {
					return true;
				}
			}

			return false;
		} catch (final NullPointerException e) {
			return false;
		}

	}

	@Override
	public boolean swapBoard() {
		try {
			return gameStateProvider.get().swapColors();
		} catch (final NullPointerException e) {
			return false;
		}
	}

	@Override
	public void preventRedraw() {
		preventRedraw = true;

	}

	@Override
	public void allowRedraw() {
		preventRedraw = false;

	}

}
