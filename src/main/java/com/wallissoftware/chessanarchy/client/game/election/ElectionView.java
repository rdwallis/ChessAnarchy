package com.wallissoftware.chessanarchy.client.game.election;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewImpl;
import com.wallissoftware.chessanarchy.shared.game.Color;

public class ElectionView extends PopupViewImpl implements ElectionPresenter.MyView {
    public interface Binder extends UiBinder<PopupPanel, ElectionView> {
    }

    public interface MyStyle extends CssResource {
        String black();

        String white();

    }

    @UiField UIObject mainPanel;

    @UiField MyStyle style;

    @UiField HorizontalPanel votePanel;

    @UiField HasText teamLabel, countDown;

    @Inject
    ElectionView(final Binder uiBinder, final EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));

    }

    @Override
    public void addToSlot(final Object slot, final IsWidget content) {
        if (slot == ElectionPresenter.VOTE_PRESENTER_SLOT) {
            votePanel.add(content);
        } else {
            super.addToSlot(slot, content);
        }
    }

    @Override
    public void setColor(final Color color) {
        mainPanel.addStyleName(color == Color.WHITE ? style.white() : style.black());
        mainPanel.removeStyleName(color == Color.WHITE ? style.black() : style.white());
        teamLabel.setText("You're on the " + color.getTitleCase() + " Team");
    }

    @Override
    public void setCountDown(final double seconds) {
        countDown.setText("A new game begins in " + Math.floor(seconds) + " seconds");
    }

    @UiHandler("closeButton")
    void onCloseButtonClick(final ClickEvent event) {
        hide();
    }

}
