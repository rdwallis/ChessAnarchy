package com.wallissoftware.chessanarchy.client.game.election.vote;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.GovernmentInfo;

public class VotePresenter extends PresenterWidget<VotePresenter.MyView> implements VoteUiHandlers {
    public interface MyView extends View, HasUiHandlers<VoteUiHandlers> {
        void setName(String name);

        void setDescription(String description);

        void setImageUrl(String imageUrl);
    }

    private String name;

    @Inject
    VotePresenter(final EventBus eventBus, final MyView view) {
        super(eventBus, view);
        getView().setUiHandlers(this);
    }

    public void setGovernmentInfo(final GovernmentInfo government, final Color color) {
        this.name = government.getName();
        getView().setName(government.getName());
        getView().setDescription(government.getDescription());
        getView().setImageUrl(color == Color.WHITE ? government.getWhiteIconUrl() : government.getBlackIconUrl());
    }

    @Override
    public void doVote() {
        fireEvent(new SendMessageEvent(name));
    }

}
