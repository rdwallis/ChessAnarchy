package com.wallissoftware.chessanarchy.client.game.board;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.client.user.UserChangedEvent;
import com.wallissoftware.chessanarchy.client.user.UserChangedEvent.UserChangedHandler;
import com.wallissoftware.chessanarchy.shared.game.Board;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.PieceCreationHandler;
import com.wallissoftware.chessanarchy.shared.game.Square;
import com.wallissoftware.chessanarchy.shared.game.pieces.Piece;

public class BoardPresenter extends PresenterWidget<BoardPresenter.MyView> implements BoardUiHandlers, ReceivedMessageCacheHandler, GameStateUpdatedHandler, UserChangedHandler, PieceCreationHandler {
	public interface MyView extends View, HasUiHandlers<BoardUiHandlers> {

		void setPieceInSquare(IsWidget isWidget, Square square, Move lastMove);

		void capture(PiecePresenter piecePresenter, Move lastMove);

		void removeFromBoard(IsWidget piece);

		void makeGhostMove(final double startTime, Piece piece, Square end);

		void highlightMove(Move move);

		void resetGridLabels();

	}

	private final Timer resetBoardTimer = new Timer() {

		@Override
		public void run() {
			try {
				if (!preventRedraw) {
					board.resetFromMoveList(gameStateProvider.get().getMoveList());
					drawBoard();
				}
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	};

	private final Set<PiecePresenter> piecePresenters = new HashSet<PiecePresenter>();

	private final Provider<PiecePresenter> piecePresenterProvider;
	private final Board board;
	private final PromotionPresenter promotionPresenter;
	private final GameStateProvider gameStateProvider;
	private final static Logger logger = Logger.getLogger(BoardPresenter.class.getName());
	private boolean preventRedraw;

	int piecePresenterCount = 0;

	@Inject
	BoardPresenter(final EventBus eventBus, final MyView view, final Provider<PiecePresenter> piecePresenterProvider, final Board board, final PromotionPresenter promotionPresenter, final GameStateProvider gameStateProvider) {
		super(eventBus, view);
		this.gameStateProvider = gameStateProvider;
		this.promotionPresenter = promotionPresenter;
		this.piecePresenterProvider = piecePresenterProvider;
		this.board = board;
		board.setPieceCreationHandler(this);
		getView().setUiHandlers(this);

	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(ReceivedMessageCacheEvent.getType(), this);
		addRegisteredHandler(GameStateUpdatedEvent.getType(), this);
		addRegisteredHandler(UserChangedEvent.getType(), this);

	}

	private void drawBoard() {
		getView().resetGridLabels();
		getView().highlightMove(board.getLastMove());
		logger.info("Number of pieces: " + board.getPieces().size());
		for (final PiecePresenter piecePresenter : piecePresenters) {
			if (piecePresenter.getPiece().isCaptured()) {
				getView().capture(piecePresenter, board.getLastMove());
			} else {
				getView().setPieceInSquare(piecePresenter, piecePresenter.getPiece().getPosition(), board.getLastMove());
			}

		}

	}

	@Override
	public boolean isMoveLegal(final Square start, final Square end) {
		if (!board.isPartialMoveLegal(start, end)) {
			if (!start.equals(end)) {
				fireEvent(new SendMessageEvent(new Move(start, end).toString()));
			}
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
		drawBoard();
	}

	@Override
	public void onReceivedMessageCache(final ReceivedMessageCacheEvent event) {
		try {
			final Board syncedBoard = gameStateProvider.getSyncedBoard();
			final Map<String, Move> notationMap = syncedBoard.getLegalMovesWithNotation();
			for (final JsonMessage msg : event.getMessageCache().getMessages()) {
				final String message = msg.getText();
				final long created = msg.getCreated();
				if (SyncedTime.get() - created < 4000 && syncedBoard.getCurrentPlayer() == msg.getColor()) {
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
		resetBoardTimer.schedule(10);
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
			/*if (board.getCurrentPlayer() != userColor) {
				return false;
			}*/
			if (board.getPieceAt(square) == null) {
				return false;
			}
			return true;
			/*for (final Move move : board.getLegalMovesWithNotation().values()) {
				if (move.getStart().equals(square)) {
					return true;
				}
			}

			return false;*/
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

	@Override
	public void onPieceCreated(final Piece piece) {
		final PiecePresenter piecePresenter = piecePresenterProvider.get();
		piecePresenter.setPiece(piece);
		piecePresenters.add(piecePresenter);

	}

}
