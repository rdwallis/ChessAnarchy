package com.wallissoftware.chessanarchy.client.game.team;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
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

    @UiField Label joinTeamCountDown, government, heading, timeUntilMove;

    @UiField Image governmentHelpIcon, governmentImage;

    public interface MyStyle extends CssResource {
        String black();

        String white();

        String hide();
    }

    @UiField MyStyle style;

    private Color color = Color.WHITE;

    @Inject
    TeamView(final Binder binder) {
        initWidget(binder.createAndBindUi(this));
    }

    @Override
    public void setColor(final Color color) {
        this.color = color;
        teamView.removeStyleName(color == Color.WHITE ? style.black() : style.white());
        teamView.addStyleName(color == Color.WHITE ? style.white() : style.black());
        heading.setText(getTeamName());
        joinTeamButton.setText("Join the " + getTeamName());
        governmentHelpIcon.setResource(color == Color.WHITE ? HelpIcons.INSTANCE.help_white() : HelpIcons.INSTANCE.help_black());

    }

    @UiHandler("joinTeamButton")
    void onJoinTeamButtonClick(final ClickEvent event) {
        getUiHandlers().joinTeam();
    }

    @Override
    public void setJoinCountDown(final Long joinTime) {

        if (joinTime != null) {
            joinTeamButton.addStyleName(style.hide());
            joinTeamCountDown.removeStyleName(style.hide());
            final long timeSinceStart = SyncedTime.get() - joinTime;
            if (timeSinceStart < CAConstants.JOIN_TEAM_WAIT) {
                joinTeamCountDown.setText("Joining in " + (CAConstants.JOIN_TEAM_WAIT - timeSinceStart) / 1000 + " seconds.");
                timeUntilMove.setVisible(false);
            } else {
                joinTeamCountDown.setText("You're on the " + getTeamName());
            }
        } else {
            joinTeamCountDown.addStyleName(style.hide());
            joinTeamButton.removeStyleName(style.hide());
        }

    }

    private String getTeamName() {
        return color.getTitleCase() + " Team";
    }

    @Override
    public void setGovernmentName(final String name) {
        government.setText(name);
        governmentHelpIcon.removeStyleName(style.hide());

    }

    @UiHandler({ "governmentHelp", "governmentImage" })
    void onGovernmentClick(final ClickEvent event) {
        getUiHandlers().showGovernmentDescription();
    }

    @Override
    public void setGovernmentIcon(final String governmentIcon) {
        governmentImage.setUrl(governmentIcon);

    }

    @Override
    public Set<IsWidget> getAutoHidePartners() {
        final Set<IsWidget> result = new HashSet<IsWidget>();
        result.add(government);
        result.add(governmentImage);
        return result;
    }

    @Override
    public void setTimeUntilMove(final long timeUntilMove) {
        this.timeUntilMove.setText(timeUntilMove + "s");
        this.timeUntilMove.setVisible(timeUntilMove > 0);

    }

}
