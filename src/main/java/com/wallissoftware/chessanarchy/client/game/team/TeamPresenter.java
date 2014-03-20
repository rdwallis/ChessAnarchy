package com.wallissoftware.chessanarchy.client.game.team;

import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.game.team.governmentdescription.GovernmentDescriptionPresenter;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.shared.CAConstants;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.GovernmentInfo;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;

public class TeamPresenter extends PresenterWidget<TeamPresenter.MyView> implements TeamUiHandlers {
    public interface MyView extends View, HasUiHandlers<TeamUiHandlers> {

        void setColor(Color color);

        void setJoinCountDown(Long joinTime);

        void setGovernmentName(String name);

        void setGovernmentIcon(String governmentIcon);

        Set<IsWidget> getAutoHidePartners();

        void setTimeUntilMove(long timeUntilMove);

    }

    private final GameStateProvider gameStateProvider;
    private Color color;
    private final GovernmentDescriptionPresenter governmentDescriptionPresenter;

    @Inject
    TeamPresenter(final EventBus eventBus, final MyView view, final GameStateProvider gameStateProvider, final Provider<GovernmentDescriptionPresenter> governmentDescriptionPresenterProvider) {
        super(eventBus, view);

        getView().setUiHandlers(this);
        this.gameStateProvider = gameStateProvider;
        this.governmentDescriptionPresenter = governmentDescriptionPresenterProvider.get();
        for (final IsWidget autoHidePartner : getView().getAutoHidePartners()) {
            governmentDescriptionPresenter.addAutoHidePartner(autoHidePartner);
        }

        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

            @Override
            public boolean execute() {
                update();
                return true;
            }
        }, 200);
    }

    public void setColor(final Color color) {
        this.color = color;
        if (color != null) {
            getView().setColor(getColor());
            getView().setTimeUntilMove(0);
            update();
        }
    }

    private Color getColor() {
        if (this.color == null) {
            return null;
        }
        try {
            if (gameStateProvider.getGameState().swapColors()) {
                return color.getOpposite();
            } else {
                return color;
            }
        } catch (final NullPointerException e) {
            return color;
        }
    }

    private void update() {
        if (getGovernment() != null) {
            getView().setGovernmentName(getGovernment().getName());
            getView().setGovernmentIcon(getColor() == Color.WHITE ? getGovernment().getWhiteIconUrl() : getGovernment().getBlackIconUrl());
            if (!getGovernment().getName().equals("Anarchy") && (gameStateProvider.getMoveTree().isWhitesTurn() ^ getColor() == Color.BLACK)) {
                final long timeSinceMove = Math.max(0, ((SyncedTime.get() - CAConstants.SYNC_DELAY) - gameStateProvider.getLastMoveTime()) / 1000);
                getView().setTimeUntilMove(20 - timeSinceMove);
            } else if (!getGovernment().getName().equals("Anarchy")) {
                final long timeSinceMove = Math.max(0, ((SyncedTime.get() - CAConstants.SYNC_DELAY) - gameStateProvider.getSecondLastMoveTime()) / 1000);
                getView().setTimeUntilMove(20 - timeSinceMove);
            } else {
                getView().setTimeUntilMove(0);
            }
        }
        getView().setJoinCountDown(User.get().getColorJoinTime(color));

    }

    @Override
    public void joinTeam() {
        fireEvent(new SendMessageEvent("/team " + getColor().name()));
        User.get().joinTeam(getEventBus(), getColor());
    }

    @Override
    public void showGovernmentDescription() {
        governmentDescriptionPresenter.setGovernment(getGovernment());
        governmentDescriptionPresenter.showRelativeTo(getView());

    }

    private GovernmentInfo getGovernment() {
        try {
            return getColor() == Color.WHITE ? gameStateProvider.getGameState().getWhiteGovernment() : gameStateProvider.getGameState().getBlackGovernment();

        } catch (final NullPointerException e) {
            return SystemOfGovernment.get("Anarchy");
        }

    }

}
