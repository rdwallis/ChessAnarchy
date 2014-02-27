package com.wallissoftware.chessanarchy.client.game.team;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.chessanarchy.shared.CAConstants;
import com.wallissoftware.chessanarchy.shared.game.Color;

public class TeamView extends ViewWithUiHandlers<TeamUiHandlers> implements TeamPresenter.MyView {
	public interface Binder extends UiBinder<HTMLPanel, TeamView> {
	}

	@UiField HasText playerCount, color, isUsing;
	@UiField Button joinTeamButton;

	@UiField Label joinTeamCountDown;

	@Inject
	TeamView(final Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setPlayerCountMessage(final String playerCount) {
		this.playerCount.setText(playerCount);

	}

	@Override
	public void setJoinButtonText(final String joinMessage) {
		this.joinTeamButton.setText(joinMessage);

	}

	@Override
	public void setIsUsingText(final String usingMessage) {
		this.isUsing.setText(usingMessage);

	}

	@Override
	public void setColor(final Color color) {
		this.color.setText(color.name());

	}

	@UiHandler("joinTeamButton")
	void onJoinTeamButtonClick(final ClickEvent event) {
		getUiHandlers().joinTeam();
	}

	@Override
	public void setJoinCountDown(final Long joinTime) {

		joinTeamButton.setVisible(joinTime == null);
		if (joinTime != null) {

			final long timeSinceStart = System.currentTimeMillis() - joinTime;
			if (timeSinceStart < CAConstants.JOIN_TEAM_WAIT) {
				joinTeamCountDown.setText("Joining in " + (CAConstants.JOIN_TEAM_WAIT - timeSinceStart) / 1000 + " seconds.");
				joinTeamCountDown.setVisible(true);
			} else {
				joinTeamCountDown.setVisible(false);
			}
		} else {
			joinTeamCountDown.setVisible(false);
		}

	}
}
