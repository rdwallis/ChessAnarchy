package com.wallissoftware.chessanarchy.client.game.team;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class TeamModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenterWidget(TeamPresenter.class, TeamPresenter.MyView.class, TeamView.class);
	}
}
