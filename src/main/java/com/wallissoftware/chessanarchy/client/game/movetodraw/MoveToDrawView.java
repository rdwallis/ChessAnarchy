package com.wallissoftware.chessanarchy.client.game.movetodraw;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;

public class MoveToDrawView extends ViewImpl implements MoveToDrawPresenter.MyView {
    public interface Binder extends UiBinder<HTMLPanel, MoveToDrawView> {
    }

    @UiField HasText movesUntilDraw;

    @Inject
    MoveToDrawView(final Binder binder) {
        initWidget(binder.createAndBindUi(this));
    }

    @Override
    public void setMovesUntilDraw(final int movesUntilDraw) {
        this.movesUntilDraw.setText("Draw in " + movesUntilDraw + " moves");

    }
}
