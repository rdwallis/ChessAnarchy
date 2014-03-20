package com.wallissoftware.chessanarchy.client.game.election.vote;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class VoteModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        bindPresenterWidget(VotePresenter.class, VotePresenter.MyView.class, VoteView.class);
    }
}
