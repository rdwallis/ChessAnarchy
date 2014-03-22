package com.wallissoftware.chessanarchy.client.game.election;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent;
import com.wallissoftware.chessanarchy.client.game.election.vote.VotePresenter;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.time.SyncedTime;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.GovernmentInfo;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;

public class ElectionPresenter extends PresenterWidget<ElectionPresenter.MyView> implements ElectionUiHandlers {
    public interface MyView extends PopupView {

        void setColor(Color color);

        void setCountDown(long seconds);
    }

    private final Set<VotePresenter> votePresenters = new HashSet<VotePresenter>();

    private final GameStateProvider gameStateProvider;

    final static Object VOTE_PRESENTER_SLOT = new Object();

    @Inject
    ElectionPresenter(final EventBus eventBus, final MyView view, final Provider<VotePresenter> votePresenterProvider, final GameStateProvider gameStateProvider) {
        super(eventBus, view);
        this.gameStateProvider = gameStateProvider;
        for (final GovernmentInfo government : SystemOfGovernment.getAll()) {
            final VotePresenter votePresenter = votePresenterProvider.get();
            votePresenter.setGovernmentInfo(government);
            votePresenter.setUiHandlers(this);
            votePresenters.add(votePresenter);

        }
    }

    @Override
    protected void onBind() {
        super.onBind();
        for (final VotePresenter votePresenter : votePresenters) {
            addToSlot(VOTE_PRESENTER_SLOT, votePresenter);
        }
    }

    @Override
    public void doVote(final String name) {
        fireEvent(new SendMessageEvent(name));
        for (final VotePresenter votePresenter : votePresenters) {
            votePresenter.informOfVote(name);
        }

    }

    @Override
    protected void onReveal() {
        super.onReveal();
        Color color = User.get().getColor(true);
        if (gameStateProvider.getGameState().swapColors()) {
            color = color.getOpposite();
        }
        getView().setColor(color);
        for (final VotePresenter votePresenter : votePresenters) {
            votePresenter.setPlayerColor(color);
        }
        Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

            @Override
            public boolean execute() {
                final long electionStart = gameStateProvider.getGameState().getElectionStart();
                if (electionStart > 10000) {
                    final long electionTime = SyncedTime.get() - electionStart;
                    final long secondsRemaining = Math.max(0, (30000 - electionTime) / 1000);
                    getView().setCountDown(secondsRemaining);
                    if (secondsRemaining <= 0) {
                        getView().hide();
                    }

                    return secondsRemaining > 0;
                }
                return true;
            }
        }, 1000);

    }
}
