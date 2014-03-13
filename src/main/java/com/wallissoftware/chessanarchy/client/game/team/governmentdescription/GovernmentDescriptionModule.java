package com.wallissoftware.chessanarchy.client.game.team.governmentdescription;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

public class GovernmentDescriptionModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenterWidget(GovernmentDescriptionPresenter.class, GovernmentDescriptionPresenter.MyView.class, GovernmentDescriptionView.class);
	}
}
