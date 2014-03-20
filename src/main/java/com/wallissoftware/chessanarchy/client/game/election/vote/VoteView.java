package com.wallissoftware.chessanarchy.client.game.election.vote;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.chessanarchy.client.game.election.ElectionUiHandlers;
import com.wallissoftware.chessanarchy.shared.game.Color;

public class VoteView extends ViewWithUiHandlers<ElectionUiHandlers> implements VotePresenter.MyView {
    public interface Binder extends UiBinder<Widget, VoteView> {
    }

    public interface MyStyle extends CssResource {
        String hasVoted();

        String black();

        String white();
    }

    @UiField HasText name, description, voteCount;

    @UiField UIObject verticalPanel;

    @UiField Image image;

    @UiField MyStyle style;

    private boolean hasVoted = false;

    @Inject
    VoteView(final Binder binder) {
        initWidget(binder.createAndBindUi(this));
    }

    @Override
    public void setName(final String name) {
        this.name.setText(name);
    }

    @Override
    public void setDescription(final String description) {
        this.description.setText(description);
    }

    @Override
    public void setImageUrl(final String imageUrl) {
        image.setUrl(imageUrl);
    }

    @UiHandler("button")
    void onButtonClick(final ClickEvent event) {
        if (!hasVoted) {
            getUiHandlers().doVote(this.name.getText());
        }
    }

    @Override
    public void setHasVoted(final boolean hasVoted, final Color playerColor) {
        this.hasVoted = hasVoted;
        if (hasVoted) {
            verticalPanel.addStyleName("gwt-PushButton-down");
            asWidget().addStyleName(style.hasVoted());
            asWidget().removeStyleName(style.black());
            asWidget().removeStyleName(style.white());
            verticalPanel.removeStyleName("gwt-PushButton-up");
        } else {
            verticalPanel.addStyleName("gwt-PushButton-up");
            asWidget().addStyleName(playerColor == Color.WHITE ? style.white() : style.black());
            asWidget().removeStyleName(style.hasVoted());
            verticalPanel.removeStyleName("gwt-PushButton-down");
        }
    }

    @Override
    public void setVoteTotal(final int voteTotal) {
        voteCount.setText(voteTotal + " Votes");

    }
}
