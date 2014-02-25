package com.wallissoftware.chessanarchy.client.game.team;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.chessanarchy.shared.government.GovernmentInfo;

public class TeamView extends ViewWithUiHandlers<TeamUiHandlers> implements TeamPresenter.MyView {
	public interface Binder extends UiBinder<HTMLPanel, TeamView> {
	}

	@Inject
	TeamView(final Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setGovernmentInfo(final GovernmentInfo governmentInfo) {
		// TODO Auto-generated method stub

	}
}
