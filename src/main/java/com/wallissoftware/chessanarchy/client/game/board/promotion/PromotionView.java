package com.wallissoftware.chessanarchy.client.game.board.promotion;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.wallissoftware.chessanarchy.client.game.board.piece.images.PieceSprites;
import com.wallissoftware.chessanarchy.shared.game.Color;

public class PromotionView extends PopupViewWithUiHandlers<PromotionUiHandlers> implements PromotionPresenter.MyView {
	public interface Binder extends UiBinder<PopupPanel, PromotionView> {
	}

	private PieceSprites sprites;

	@UiField PushButton queenButton, rookButton, knightButton, bishopButton;

	@Inject
	PromotionView(final Binder uiBinder, final EventBus eventBus, final PieceSprites sprites) {
		super(eventBus);

		initWidget(uiBinder.createAndBindUi(this));
		this.sprites = sprites;
	}

	@Override
	public void setColor(final Color color) {
		if (color == Color.WHITE) {
			queenButton.getUpFace().setImage(new Image(sprites.whiteQueen()));
			rookButton.getUpFace().setImage(new Image(sprites.whiteRook()));
			bishopButton.getUpFace().setImage(new Image(sprites.whiteBishop()));
			knightButton.getUpFace().setImage(new Image(sprites.whiteKnight()));
		} else {
			queenButton.getUpFace().setImage(new Image(sprites.blackQueen()));
			rookButton.getUpFace().setImage(new Image(sprites.blackRook()));
			bishopButton.getUpFace().setImage(new Image(sprites.blackBishop()));
			knightButton.getUpFace().setImage(new Image(sprites.blackKnight()));
		}
	}

	@UiHandler("queenButton")
	void onQueenButtonClick(final ClickEvent event) {
		hide();
		getUiHandlers().promoteToQueen();
	}

	@UiHandler("knightButton")
	void onKnightButtonClick(final ClickEvent event) {
		hide();
		getUiHandlers().promoteToKnight();
	}

	@UiHandler("bishopButton")
	void onBishopButtonClick(final ClickEvent event) {
		hide();
		getUiHandlers().promoteToBishop();
	}

	@UiHandler("rookButton")
	void onRookButtonClick(final ClickEvent event) {
		hide();
		getUiHandlers().promoteToRook();
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		hide();
		getUiHandlers().cancel();

	}
}
