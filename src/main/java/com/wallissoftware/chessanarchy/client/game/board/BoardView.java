package com.wallissoftware.chessanarchy.client.game.board;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.GridConstrainedDropController;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Square;

public class BoardView extends ViewWithUiHandlers<BoardUiHandlers> implements BoardPresenter.MyView, DragHandler {
	public interface Binder extends UiBinder<Widget, BoardView> {
	}

	public interface MyStyle extends CssResource {
		String gridLabel();

		String darkSquare();

		String lightSquare();
	}

	@UiField AbsolutePanel dropSurface;

	@UiField MyStyle style;
	@UiField LayoutPanel layoutPanel, boardBackground;

	private final GridConstrainedDropController dropController;
	private final PickupDragController dragController;

	private final List<Widget> rankLabels = new ArrayList<Widget>();
	private final List<Widget> fileLabels = new ArrayList<Widget>();

	private Square startDragSquare;

	@Inject
	BoardView(final Binder binder) {
		initWidget(binder.createAndBindUi(this));
		dropController = new GridConstrainedDropController(dropSurface, 50, 50);

		dragController = new PickupDragController(dropSurface, true);
		dragController.setBehaviorConstrainedToBoundaryPanel(true);
		dragController.setBehaviorMultipleSelection(false);
		dragController.registerDropController(dropController);
		dragController.addDragHandler(this);

		drawSquares();
		resetGridLabels();

	}

	private void drawSquares() {
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				final boolean dark = ((rank + file) % 2 == 1);
				final SimplePanel square = new SimplePanel();
				square.addStyleName(dark ? style.darkSquare() : style.lightSquare());
				boardBackground.insert(square, 0);
				boardBackground.setWidgetLeftWidth(square, rank * 50, Unit.PX, 50, Unit.PX);
				boardBackground.setWidgetTopHeight(square, file * 50, Unit.PX, 50, Unit.PX);
			}
		}

	}

	private void resetGridLabels() {
		if (rankLabels.isEmpty()) {
			for (int i = 0; i < 8; i++) {
				final Label rankLabel = new Label("" + ((char) (i + 97)));
				rankLabel.addStyleName(style.gridLabel());
				rankLabels.add(rankLabel);
				layoutPanel.insert(rankLabel, 0);
				layoutPanel.setWidgetBottomHeight(rankLabel, 5, Unit.PX, 26, Unit.PX);

				final Label fileLabel = new Label("" + (i + 1));
				fileLabel.addStyleName(style.gridLabel());
				fileLabels.add(fileLabel);
				layoutPanel.insert(fileLabel, 0);
				layoutPanel.setWidgetRightWidth(fileLabel, 10, Unit.PX, 16, Unit.PX);
			}
		}

		for (int i = 0; i < 8; i++) {
			final Widget rankLabel = getOrientation() == Color.WHITE ? rankLabels.get(i) : rankLabels.get(7 - i);
			final Widget fileLabel = getOrientation() == Color.WHITE ? fileLabels.get(7 - i) : fileLabels.get(i);
			layoutPanel.setWidgetLeftWidth(rankLabel, (i * 50) + 30, Unit.PX, 16, Unit.PX);
			layoutPanel.setWidgetTopHeight(fileLabel, (i * 50) + 26, Unit.PX, 16, Unit.PX);
		}

	}

	@Override
	public void setPieceInSquare(final IsWidget piece, final Square square) {
		int x = square.getRank() * 50;
		int y = 350 - (square.getFile() * 50);
		if (getOrientation() == Color.BLACK) {
			x = 350 - x;
			y = 350 - y;
		}

		dropSurface.add(piece, x, y);
		dragController.makeDraggable(piece.asWidget());

	}

	private Color getOrientation() {
		return Color.WHITE;
	}

	@Override
	public void onDragEnd(final DragEndEvent event) {
		if (event.getContext().vetoException == null) {
			final Square endSquare = getSquareAtMouseCoordinate(event.getContext().mouseX, event.getContext().mouseY);
			getUiHandlers().makeMove(startDragSquare, endSquare);
		}

	}

	@Override
	public void onDragStart(final DragStartEvent event) {
		this.startDragSquare = getSquareAtMouseCoordinate(event.getContext().mouseX, event.getContext().mouseY);

	}

	@Override
	public void onPreviewDragEnd(final DragEndEvent event) throws VetoDragException {
		final Square endSquare = getSquareAtMouseCoordinate(event.getContext().mouseX, event.getContext().mouseY);
		if (!getUiHandlers().isMoveLegal(startDragSquare, endSquare)) {
			throw new VetoDragException();
		}

	}

	@Override
	public void onPreviewDragStart(final DragStartEvent event) throws VetoDragException {
		// TODO Auto-generated method stub

	}

	private Square getSquareAtMouseCoordinate(final int mouseX, final int mouseY) {
		final int relativeX = mouseX - dropSurface.getAbsoluteLeft();
		final int relativeY = mouseY - dropSurface.getAbsoluteTop();

		int rank = relativeX / 50;
		int file = 7 - relativeY / 50;

		rank = Math.max(0, Math.min(7, rank));
		file = Math.max(0, Math.min(7, file));

		if (getOrientation() == Color.BLACK) {
			rank = 7 - rank;
			file = 7 - file;
		}
		return new Square(rank, file);

	}

	@Override
	public void capture(final IsWidget piece) {
		piece.asWidget().removeFromParent();

	}

	@Override
	public void removeFromBoard(final IsWidget piece) {
		piece.asWidget().removeFromParent();

	}
}
