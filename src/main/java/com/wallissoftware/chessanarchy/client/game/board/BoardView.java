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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.chessanarchy.client.game.board.piece.PieceWidget;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.game.Move;

public class BoardView extends ViewWithUiHandlers<BoardUiHandlers> implements BoardPresenter.MyView, DragHandler {
	public interface Binder extends UiBinder<Widget, BoardView> {
	}

	public interface MyStyle extends CssResource {
		String gridLabel();

		String darkSquare();

		String lightSquare();

		String highlight();

	}

	@UiField AbsolutePanel dropSurface;

	@UiField MyStyle style;
	@UiField LayoutPanel layoutPanel, boardBackground;

	private final GridConstrainedDropController dropController;
	private final PickupDragController dragController;

	private final List<Widget> fileLabels = new ArrayList<Widget>();
	private final List<Widget> rankLabels = new ArrayList<Widget>();

	private final Set<GhostAnimation> ghostAnimations = new HashSet<GhostAnimation>();
	private final Set<GhostAnimation> animations = new HashSet<GhostAnimation>();

	private SimplePanel[][] squares = new SimplePanel[8][8];

	private Set<SimplePanel> highlightedSquares = new HashSet<SimplePanel>();

	private int startDragFile, startDragRank, endDragFile, endDragRank;

	private Color lastDrawOrientation;

	private PieceWidget[][] board = new PieceWidget[8][8];

	@Inject
	BoardView(final Binder binder) {
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
				dropSurface.remove(ghostAnimation.getWidget());
			} else {
				ghostAnimation.getWidget().getElement().getStyle().setOpacity(ghostAnimation.getOpacity(milli));
				dropSurface.setWidgetPosition(ghostAnimation.getWidget(), ghostAnimation.getX(milli), ghostAnimation.getY(milli));
			}
		}

		final Iterator<GhostAnimation> it1 = animations.iterator();
		while (it1.hasNext()) {
			final GhostAnimation animation = it1.next();
			dropSurface.setWidgetPosition(animation.getWidget(), animation.getX(milli), animation.getY(milli));
			if (animation.isMovementComplete(milli)) {
				it1.remove();
			}
		}

	}

	private void drawSquares() {
		for (int file = 0; file < 8; file++) {
			for (int rank = 0; rank < 8; rank++) {
				final boolean dark = ((file + rank) % 2 == 1);
				final SimplePanel square = new SimplePanel();
				squares[file][rank] = square;
				square.addStyleName(dark ? style.darkSquare() : style.lightSquare());
				boardBackground.insert(square, 0);
				boardBackground.setWidgetLeftWidth(square, file * 50, Unit.PX, 50, Unit.PX);
				boardBackground.setWidgetTopHeight(square, rank * 50, Unit.PX, 50, Unit.PX);
			}
		}

	}

	private void resetGridLabels() {
		if (lastDrawOrientation != getOrientation()) {
			this.lastDrawOrientation = getOrientation();
			if (fileLabels.isEmpty()) {
				for (int i = 0; i < 8; i++) {
					final Label fileLabel = new Label("" + ((char) (i + 97)));
					fileLabel.addStyleName(style.gridLabel());
					fileLabels.add(fileLabel);
					layoutPanel.insert(fileLabel, 0);
					layoutPanel.setWidgetBottomHeight(fileLabel, 5, Unit.PX, 26, Unit.PX);

					final Label rankLabel = new Label("" + (i + 1));
					rankLabel.addStyleName(style.gridLabel());
					rankLabels.add(rankLabel);
					layoutPanel.insert(rankLabel, 0);
					layoutPanel.setWidgetRightWidth(rankLabel, 10, Unit.PX, 16, Unit.PX);
				}
			}

			for (int i = 0; i < 8; i++) {
				final Widget fileLabel = getOrientation() == Color.WHITE ? fileLabels.get(i) : fileLabels.get(7 - i);
				final Widget rankLabel = getOrientation() == Color.WHITE ? rankLabels.get(7 - i) : rankLabels.get(i);
				layoutPanel.setWidgetLeftWidth(fileLabel, (i * 50) + 30, Unit.PX, 16, Unit.PX);
				layoutPanel.setWidgetTopHeight(rankLabel, (i * 50) + 26, Unit.PX, 16, Unit.PX);
			}

			for (int rank = 0; rank < 8; rank++) {
				for (int file = 0; file < 8; file++) {
					if (board[file][rank] != null) {
						movePieceTo(board[file][rank], file, rank);
					}
				}
			}
		}

	}

	private void movePieceTo(final PieceWidget pieceWidget, final int file, final int rank) {
		int x = file * 50;
		int y = 350 - (rank * 50);
		if (getOrientation() == Color.BLACK) {
			x = 350 - x;
			y = 350 - y;
		}

		dropSurface.setWidgetPosition(pieceWidget, x, y);

	}

	@Override
	public void drawBoard(final char[][] board, final Move move) {
		resetGridLabels();
		highlightMove(move);
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				setPieceInSquare(board[file][rank], file, rank);

			}
		}
		animateMove(move);

	}

	private void animateMove(final Move move) {
		if (move == null) {
			return;
		}
		for (final GhostAnimation animation : animations) {
			animation.end();
		}
		final PieceWidget piece = board[move.getStartFile()][move.getStartRank()];
		if (piece != null) {
			createAnimation(false, SyncedTime.get(), piece, move);

		}

	}

	private void setPieceInSquare(final char kind, final int file, final int rank) {
		final PieceWidget existingPiece = board[file][rank];
		if (kind == 'x') {
			if (existingPiece != null) {
				dragController.makeNotDraggable(existingPiece);
				dropSurface.remove(existingPiece);
				board[file][rank] = null;
			}
		} else {

			if (existingPiece != null && existingPiece.getKind() != kind) {
				dragController.makeNotDraggable(existingPiece);
				dropSurface.remove(existingPiece);

			}

			if (existingPiece == null || existingPiece.getKind() != kind) {
				int x = file * 50;
				int y = 350 - (rank * 50);
				if (getOrientation() == Color.BLACK) {
					x = 350 - x;
					y = 350 - y;
				}
				final PieceWidget pieceWidget = new PieceWidget(kind);

				dropSurface.add(pieceWidget, x, y);
				dragController.makeDraggable(pieceWidget);
				board[file][rank] = pieceWidget;
			}

		}

	}

	private Color getOrientation() {
		try {
			return getUiHandlers().swapBoard() ? User.get().getColor(true).getOpposite() : User.get().getColor(true);
		} catch (final NullPointerException e) {
			return Color.WHITE;
		}

	}

	@Override
	public void onDragEnd(final DragEndEvent event) {
		getUiHandlers().allowRedraw();
		if (event.getContext().vetoException == null) {

			getUiHandlers().makeMove(new Move(startDragFile, startDragRank, endDragFile, endDragRank));
		}

	}

	@Override
	public void onDragStart(final DragStartEvent event) {
		this.startDragFile = getFileOfMouseCoordinate(event.getContext().mouseX);
		this.startDragRank = getRankOfMouseCoordinate(event.getContext().mouseY);

	}

	@Override
	public void onPreviewDragEnd(final DragEndEvent event) throws VetoDragException {
		this.endDragFile = getFileOfMouseCoordinate(event.getContext().mouseX);
		this.endDragRank = getRankOfMouseCoordinate(event.getContext().mouseY);
		if (!getUiHandlers().isMoveLegal(new Move(startDragFile, startDragRank, endDragFile, endDragRank))) {
			throw new VetoDragException();
		}

	}

	@Override
	public void onPreviewDragStart(final DragStartEvent event) throws VetoDragException {
		if (!getUiHandlers().canMove(getFileOfMouseCoordinate(event.getContext().mouseX), getRankOfMouseCoordinate(event.getContext().mouseY))) {
			throw new VetoDragException();
		} else {
			getUiHandlers().preventRedraw();
		}

	}

	private int getFileOfMouseCoordinate(final int mouseX) {
		final int relativeX = mouseX - dropSurface.getAbsoluteLeft();
		int file = relativeX / 50;
		file = Math.max(0, Math.min(7, file));
		if (getOrientation() == Color.BLACK) {
			file = 7 - file;
		}
		return file;
	}

	private int getRankOfMouseCoordinate(final int mouseY) {
		final int relativeY = mouseY - dropSurface.getAbsoluteTop();
		int rank = 7 - relativeY / 50;
		rank = Math.max(0, Math.min(7, rank));
		if (getOrientation() == Color.BLACK) {
			rank = 7 - rank;
		}
		return rank;
	}

	@Override
	public void makeGhostMove(final long startTime, final char[][] board, final Move move) {
		if (ghostAnimations.size() > 40 || move == null) {
			return;
		}

		final char kind = board[move.getStartFile()][move.getStartRank()];
		if (kind != 'x') {
			createAnimation(true, startTime, new PieceWidget(kind), move);
		}

	}

	private void createAnimation(final boolean ghost, final long startTime, final PieceWidget piece, final Move move) {
		int x = move.getStartFile() * 50;
		int y = 350 - (move.getStartRank() * 50);
		if (getOrientation() == Color.BLACK) {
			x = 350 - x;
			y = 350 - y;
		}

		int x1 = move.getEndFile() * 50;
		int y1 = 350 - (move.getEndRank() * 50);
		if (getOrientation() == Color.BLACK) {
			x1 = 350 - x1;
			y1 = 350 - y1;
		}
		if (ghost) {
			dropSurface.add(piece, x, y);
			ghostAnimations.add(new GhostAnimation(startTime + 3000, piece, x, y, x1, y1));
		} else {
			animations.add(new GhostAnimation(true, startTime, piece, x, y, x1, y1));
		}

	}

	private void highlightMove(final Move move) {
		for (final SimplePanel square : highlightedSquares) {
			square.removeStyleName(style.highlight());
		}
		highlightedSquares.clear();
		if (move != null) {
			highlightSquare(move.getStartFile(), move.getStartRank());
			highlightSquare(move.getEndFile(), move.getEndRank());
		}

	}

	private void highlightSquare(int file, int rank) {
		if (getOrientation() == Color.WHITE) {
			rank = 7 - rank;
		} else {
			file = 7 - file;
		}
		squares[file][rank].addStyleName(style.highlight());
		highlightedSquares.add(squares[file][rank]);

	}

}
