package com.wallissoftware.client.game.board;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.shared.game.Color;
import com.wallissoftware.shared.game.Square;

public class BoardView extends ViewWithUiHandlers<BoardUiHandlers> implements BoardPresenter.MyView {
	public interface Binder extends UiBinder<Widget, BoardView> {
	}

	public interface MyStyle extends CssResource {
		String gridLabel();
	}

	@UiField(provided = true) Grid boardBackground = new Grid(8, 8);

	@UiField AbsolutePanel dropSurface;

	@UiField MyStyle style;
	@UiField LayoutPanel layoutPanel;

	private final List<Widget> rankLabels = new ArrayList<Widget>();
	private final List<Widget> fileLabels = new ArrayList<Widget>();

	@Inject
	BoardView(final Binder binder) {
		initWidget(binder.createAndBindUi(this));

		resetGridLabels();
	}

	private void resetGridLabels() {
		if (rankLabels.isEmpty()) {
			for (int i = 0; i < 8; i++) {
				final Label rankLabel = new Label("" + ((char) (i + 97)));
				rankLabel.addStyleName(style.gridLabel());
				rankLabels.add(rankLabel);
				layoutPanel.add(rankLabel);
				layoutPanel.setWidgetBottomHeight(rankLabel, 5, Unit.PX, 26, Unit.PX);

				final Label fileLabel = new Label("" + (i + 1));
				fileLabel.addStyleName(style.gridLabel());
				fileLabels.add(fileLabel);
				layoutPanel.add(fileLabel);
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
	}

	private Color getOrientation() {
		return Color.WHITE;
	}
}
