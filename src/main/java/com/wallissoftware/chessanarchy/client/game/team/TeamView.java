package com.wallissoftware.chessanarchy.client.game.team;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.shared.CAConstants;
import com.wallissoftware.chessanarchy.shared.game.Color;

public class TeamView extends ViewWithUiHandlers<TeamUiHandlers> implements TeamPresenter.MyView {
	public interface Binder extends UiBinder<Widget, TeamView> {
	}

	@UiField UIObject teamView;

	@UiField Button joinTeamButton;

	@UiField Label joinTeamCountDown;

	@UiField HasText government, heading;;

	public interface MyStyle extends CssResource {
		String black();

		String white();
	}

	@UiField MyStyle style;

	private String governmentDescription;

	@Inject
	TeamView(final Binder binder) {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setColor(final Color color) {
		teamView.removeStyleName(color == Color.WHITE ? style.black() : style.white());
		teamView.addStyleName(color == Color.WHITE ? style.white() : style.black());
		final String teamName = Character.toUpperCase(color.name().charAt(0)) + color.name().substring(1) + " Team";
		heading.setText(teamName);

	}

	@UiHandler("joinTeamButton")
	void onJoinTeamButtonClick(final ClickEvent event) {
		getUiHandlers().joinTeam();
	}

	@Override
	public void setJoinCountDown(final Long joinTime) {
		joinTeamButton.setVisible(joinTime == null);
		if (joinTime != null) {

			final long timeSinceStart = SyncedTime.get() - joinTime;
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

	@Override
	public void setGovernmentName(final String name) {
		government.setText(name);

	}

	@Override
	public void setGovernmentDescription(final String description) {
		this.governmentDescription = description;

	}

	@UiHandler("government")
	void onGovernmentClick(final ClickEvent event) {

	}

}
