package com.wallissoftware.chessanarchy.client.game.movetodraw;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent.GameStateUpdatedHandler;

public class MoveToDrawPresenter extends PresenterWidget<MoveToDrawPresenter.MyView> implements GameStateUpdatedHandler {
    public interface MyView extends View {

        void setMovesUntilDraw(int movesUntilDraw);
    }

    private final GameStateProvider gameStateProvider;

    @Inject
    MoveToDrawPresenter(final EventBus eventBus, final MyView view, final GameStateProvider gameStateProvider) {
        super(eventBus, view);
        this.gameStateProvider = gameStateProvider;
    }

    @Override
    public void onGameStateUpdated(final GameStateUpdatedEvent event) {
        getView().setMovesUntilDraw(gameStateProvider.getMoveTree().getMovesUntilDraw());
    }

    @Override
    protected void onBind() {
        super.onBind();
        addRegisteredHandler(GameStateUpdatedEvent.getType(), this);
    }

}
