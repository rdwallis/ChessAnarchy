package com.wallissoftware.chessanarchy.client.game.board.promotion;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent;
import com.wallissoftware.chessanarchy.shared.game.Board;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;
import com.wallissoftware.chessanarchy.shared.game.Square;
import com.wallissoftware.chessanarchy.shared.game.pieces.Bishop;
import com.wallissoftware.chessanarchy.shared.game.pieces.Knight;
import com.wallissoftware.chessanarchy.shared.game.pieces.Pawn;
import com.wallissoftware.chessanarchy.shared.game.pieces.Queen;
import com.wallissoftware.chessanarchy.shared.game.pieces.Rook;

public class PromotionPresenter extends PresenterWidget<PromotionPresenter.MyView> implements PromotionUiHandlers {
	public interface MyView extends PopupView, HasUiHandlers<PromotionUiHandlers> {

		void setColor(Color color);
	}

	private Square start;
	private Square end;
	private final Board board;

	@Inject
	PromotionPresenter(final EventBus eventBus, final MyView view, final Board board) {
		super(eventBus, view);
		this.board = board;
		getView().setUiHandlers(this);

	}

	public void setPartialMove(final Square start, final Square end) {
		getView().setColor(board.getCurrentPlayer());
		this.start = start;
		this.end = end;

	}

	@Override
	public void cancel() {
		getPromotedPawn().setPosition(getPromotedPawn().getPosition(), true, false);
	}

	@Override
	public void promoteToRook() {
		makeMove(new Move(start, end, new Rook(getPromotedPawn())));

	}

	private Pawn getPromotedPawn() {
		return (Pawn) board.getPieceAt(start);
	}

	@Override
	public void promoteToBishop() {
		makeMove(new Move(start, end, new Bishop(getPromotedPawn())));

	}

	@Override
	public void promoteToKnight() {
		makeMove(new Move(start, end, new Knight(getPromotedPawn())));

	}

	@Override
	public void promoteToQueen() {
		makeMove(new Move(start, end, new Queen(getPromotedPawn())));

	}

	private void makeMove(final Move move) {
		fireEvent(new SendMessageEvent(board.doMove(move, true, true)));
	}

}
