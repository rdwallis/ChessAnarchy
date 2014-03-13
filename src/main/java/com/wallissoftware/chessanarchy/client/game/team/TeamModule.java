package com.wallissoftware.chessanarchy.client.game.team;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.wallissoftware.chessanarchy.client.game.team.governmentdescription.GovernmentDescriptionModule;

public class TeamModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new GovernmentDescriptionModule());
		bindPresenterWidget(TeamPresenter.class, TeamPresenter.MyView.class, TeamView.class);
	}
}
