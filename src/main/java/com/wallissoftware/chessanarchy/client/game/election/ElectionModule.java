package com.wallissoftware.chessanarchy.client.game.election;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.wallissoftware.chessanarchy.client.game.election.vote.VoteModule;

public class ElectionModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new VoteModule());
        bindSingletonPresenterWidget(ElectionPresenter.class, ElectionPresenter.MyView.class, ElectionView.class);
	}
}
