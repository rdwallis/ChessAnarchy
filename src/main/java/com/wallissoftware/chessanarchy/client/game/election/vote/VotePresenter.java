package com.wallissoftware.chessanarchy.client.game.election.vote;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.chat.messagelog.MessageLogPresenter;
import com.wallissoftware.chessanarchy.client.game.election.ElectionUiHandlers;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.GovernmentInfo;
import com.wallissoftware.chessanarchy.shared.message.Message;

public class VotePresenter extends PresenterWidget<VotePresenter.MyView> implements HasUiHandlers<ElectionUiHandlers> {
    public interface MyView extends View, HasUiHandlers<ElectionUiHandlers> {
        void setName(String name);

        void setDescription(String description);

        void setImageUrl(String imageUrl);

        void setHasVoted(boolean equals, Color playerColor);

        void setVoteTotal(int voteTotal);
    }

    private GovernmentInfo government;
    private Color playerColor;
    private final MessageLogPresenter messageLogPresenter;

    @Inject
    VotePresenter(final EventBus eventBus, final MyView view, final MessageLogPresenter messageLogPresenter) {
        super(eventBus, view);
        this.messageLogPresenter = messageLogPresenter;

    }

    public void setGovernmentInfo(final GovernmentInfo government) {
        this.government = government;
        redraw();
    }

    public void setPlayerColor(final Color playerColor) {
        this.playerColor = playerColor;
        redraw();
    }

    private void redraw() {
        if (government != null && playerColor != null) {
            getView().setName(government.getName());
            getView().setDescription(government.getShortDescription());
            getView().setImageUrl(playerColor == Color.WHITE ? government.getWhiteIconUrl() : government.getBlackIconUrl());
            getView().setHasVoted(false, playerColor);
        }
    }

    @Override
    public void setUiHandlers(final ElectionUiHandlers uiHandlers) {
        getView().setUiHandlers(uiHandlers);

    }

    public void informOfVote(final String name) {
        getView().setHasVoted(name.equals(government.getName()), playerColor);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

            @Override
            public boolean execute() {
                getView().setVoteTotal(getVoteTotal());
                return isVisible();
            }

        }, 500);
    }

    private int getVoteTotal() {
        int count = 0;
        for (final Message message : messageLogPresenter.getCurrentGameMessages()) {
            if (message.getColor() == playerColor && message.getText().equals(government.getName())) {
                count += 1;
            }
        }
        return count;
    }

}
