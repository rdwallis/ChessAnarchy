package com.wallissoftware.chessanarchy.client.game.pgn;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class PgnView extends ViewWithUiHandlers<PgnUiHandlers> implements PgnPresenter.MyView {
    public interface Binder extends UiBinder<Widget, PgnView> {
    }

    int moveCount = 0;

    @UiField FlexTable flexTable;

    @Inject
    PgnView(final Binder binder) {
        initWidget(binder.createAndBindUi(this));
    }

    @Override
    public void addMove(final String pgn) {

        final int row = moveCount / 2;
        if (moveCount % 2 == 0) {
            flexTable.setText(row, 0, (row + 1) + ".");
            flexTable.setText(row, 1, pgn);
        } else {
            flexTable.setText(row, 2, pgn);
        }

        moveCount += 1;

    }

    @Override
    public void clearMoves() {
        moveCount = 0;
        flexTable.clear();
    }
}
