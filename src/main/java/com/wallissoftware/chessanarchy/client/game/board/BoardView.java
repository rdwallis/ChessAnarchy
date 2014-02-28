package com.wallissoftware.chessanarchy.client.game.board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.GridConstrainedDropController;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.chessanarchy.client.game.board.piece.images.PieceSprites;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Square;
import com.wallissoftware.chessanarchy.shared.game.pieces.Bishop;
import com.wallissoftware.chessanarchy.shared.game.pieces.King;
import com.wallissoftware.chessanarchy.shared.game.pieces.Knight;
import com.wallissoftware.chessanarchy.shared.game.pieces.Pawn;
import com.wallissoftware.chessanarchy.shared.game.pieces.Piece;
import com.wallissoftware.chessanarchy.shared.game.pieces.Queen;
import com.wallissoftware.chessanarchy.shared.game.pieces.Rook;

public class BoardView extends ViewWithUiHandlers<BoardUiHandlers> implements BoardPresenter.MyView, DragHandler {
	public interface Binder extends UiBinder<Widget, BoardView> {
	}

	public interface MyStyle extends CssResource {
		String gridLabel();

		String darkSquare();

		String lightSquare();

	}

	private final PieceSprites sprites;

	@UiField AbsolutePanel dropSurface;

	@UiField MyStyle style;
	@UiField LayoutPanel layoutPanel, boardBackground;

	private final GridConstrainedDropController dropController;
	private final PickupDragController dragController;

	private final List<Widget> rankLabels = new ArrayList<Widget>();
	private final List<Widget> fileLabels = new ArrayList<Widget>();

	private final Set<GhostAnimation> ghostAnimations = new HashSet<GhostAnimation>();

	private Square startDragSquare;

	@Inject
	BoardView(final Binder binder, final PieceSprites sprites) {
		this.sprites = sprites;
		initWidget(binder.createAndBindUi(this));
		dropController = new GridConstrainedDropController(dropSurface, 50, 50);

		dragController = new PickupDragController(dropSurface, true);
		dragController.setBehaviorConstrainedToBoundaryPanel(true);
		dragController.setBehaviorMultipleSelection(false);
		dragController.registerDropController(dropController);
		dragController.addDragHandler(this);

	}

	@Override
	public void setUiHandlers(final BoardUiHandlers uiHandlers) {
		super.setUiHandlers(uiHandlers);
		drawSquares();
		resetGridLabels();
		startGhostAnimations();
	}

	private void startGhostAnimations() {
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

			@Override
			public boolean execute() {
				updateGhostAnimations();
				return true;
			}

		}, 100);

	}

	private void updateGhostAnimations() {
		final long milli = SyncedTime.get();
		final Iterator<GhostAnimation> it = ghostAnimations.iterator();
		while (it.hasNext()) {
			final GhostAnimation ghostAnimation = it.next();
			if (ghostAnimation.isFinished(milli)) {
				it.remove();
				dropSurface.remove(ghostAnimation.getImage());
			} else {
				ghostAnimation.getImage().getElement().getStyle().setOpacity(ghostAnimation.getOpacity(milli));
				dropSurface.setWidgetPosition(ghostAnimation.getImage(), ghostAnimation.getX(milli), ghostAnimation.getY(milli));
			}
		}

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
		try {
			return User.get().getColor(true) == null ? null : getUiHandlers().swapBoard() ? User.get().getColor(true).getOpposite() : User.get().getColor(true);
		} catch (final NullPointerException e) {
			return Color.WHITE;
		}

	}

	@Override
	public void onDragEnd(final DragEndEvent event) {
		getUiHandlers().allowRedraw();
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
		if (!getUiHandlers().canMove(getSquareAtMouseCoordinate(event.getContext().mouseX, event.getContext().mouseY))) {
			throw new VetoDragException();
		} else {
			getUiHandlers().preventRedraw();
		}

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

	@Override
	public void makeGhostMove(final double startTime, final Piece piece, final Square end) {
		if (ghostAnimations.size() > 20 || piece == null) {
			return;
		}
		final Image image = new Image();
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

		final Square start = piece.getPosition();
		int x = start.getRank() * 50;
		int y = 350 - (start.getFile() * 50);
		if (getOrientation() == Color.BLACK) {
			x = 350 - x;
			y = 350 - y;
		}

		int x1 = end.getRank() * 50;
		int y1 = 350 - (end.getFile() * 50);
		if (getOrientation() == Color.BLACK) {
			x1 = 350 - x1;
			y1 = 350 - y1;
		}

		dropSurface.add(image, x, y);
		ghostAnimations.add(new GhostAnimation(startTime + 1000, image, x, y, x1, y1));

	}

	@Override
	public void clearBoard() {
		dropSurface.clear();

	}

}
