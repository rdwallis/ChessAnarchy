package com.wallissoftware.chessanarchy.client.game.board;

import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
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
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;

public class BoardPresenter extends PresenterWidget<BoardPresenter.MyView> implements BoardUiHandlers, ReceivedMessageCacheHandler, GameStateUpdatedHandler, UserChangedHandler {
	public interface MyView extends View, HasUiHandlers<BoardUiHandlers> {

		void makeGhostMove(long startTime, char[][] board, Move move);

		void drawBoard(char[][] board, Move move);

		void animateCapture(Move move, boolean immediate);

	}

	private final PromotionPresenter promotionPresenter;
	private final GameStateProvider gameStateProvider;
	private final static Logger logger = Logger.getLogger(BoardPresenter.class.getName());
	private boolean preventRedraw;

	int piecePresenterCount = 0;

	private Color lastUserColor = null;

	@Inject
	BoardPresenter(final EventBus eventBus, final MyView view, final PromotionPresenter promotionPresenter, final GameStateProvider gameStateProvider) {
		super(eventBus, view);
		this.gameStateProvider = gameStateProvider;
		this.promotionPresenter = promotionPresenter;
		promotionPresenter.setBoardPresenter(this);
		getView().setUiHandlers(this);

	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(ReceivedMessageCacheEvent.getType(), this);
		addRegisteredHandler(GameStateUpdatedEvent.getType(), this);
		addRegisteredHandler(UserChangedEvent.getType(), this);
	}

	private void updateBoard() {
		if (!preventRedraw) {
			getView().drawBoard(gameStateProvider.getParentMoveTree().getBoard(), gameStateProvider.getMoveTree().getMove());
		} else {
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

				@Override
				public boolean execute() {
					updateBoard();
					return false;
				}

			}, 100);
		}

	}

	@Override
	public boolean isMoveLegal(final Move move) {

		if (!gameStateProvider.getMoveTree().isMoveLegal(move)) {
			if (move.getDelta() > 0) {
				fireEvent(new SendMessageEvent(move.toString()));
			}
			return false;
		}
		return true;

	}

	@Override
	public void makeMove(final Move move) {
		getView().animateCapture(move, true);
		if (!move.isPromotion() && gameStateProvider.getMoveTree().isMovePromotion(move)) {
			promotionPresenter.setPartialMove(move);
			addToPopupSlot(promotionPresenter);
		} else {
			fireEvent(new SendMessageEvent(gameStateProvider.getMoveTree().getLegalMoveMap().get(move.toString())));
		}
	}

	@Override
	public void onReceivedMessageCache(final ReceivedMessageCacheEvent event) {
		try {
			final Map<String, Move> pgnMoveMap = gameStateProvider.getMoveTree().getPgnMoveMap();
			;
			for (final JsonMessage msg : event.getMessageCache().getMessages()) {
				final String message = msg.getText();
				final long created = msg.getCreated();
				if (SyncedTime.get() - created < 4000) {
					if (pgnMoveMap.containsKey(message)) {
						makeGhostMove(created, pgnMoveMap.get(message));
					} else if (message.length() == 4) {
						try {
							makeGhostMove(created, Move.fromString(message));
						} catch (final IllegalMoveException e) {
							logger.info("Couldn't make move from string");
						}
					}

				}

			}
		} catch (final Exception e) {
			logger.info("Problem processing ghost moves");
		}

	}

	private void makeGhostMove(final long startTime, final Move move) {
		getView().makeGhostMove(startTime, gameStateProvider.getMoveTree().getBoard(), move);

	}

	@Override
	public void onGameStateUpdated(final GameStateUpdatedEvent event) {
		updateBoard();
	}

	@Override
	public void onUserChanged(final UserChangedEvent event) {
		if (User.get().getColor(true) != lastUserColor) {
			lastUserColor = User.get().getColor(true);
			updateBoard();
		}
	}

	@Override
	public boolean canMove(final int file, final int rank) {
		try {
			Color userColor = User.get().getColor();
			if (userColor == null) {
				return false;
			}

			if (gameStateProvider.getGameState().swapColors()) {
				userColor = userColor.getOpposite();
			}

			return !gameStateProvider.getMoveTree().isEmptySquare(file, rank) && (userColor == Color.BLACK ^ gameStateProvider.getMoveTree().isWhitePiece(file, rank));
		} catch (final NullPointerException e) {
			return false;
		}

	}

	@Override
	public boolean swapBoard() {
		try {
			return gameStateProvider.getGameState().swapColors();
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
