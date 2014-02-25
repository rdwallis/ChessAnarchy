package com.wallissoftware.chessanarchy.client.game.team;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.chat.messagelog.MessageLogPresenter;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent.GameStateUpdatedHandler;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.government.Government;
import com.wallissoftware.chessanarchy.shared.government.GovernmentInfo;

public class TeamPresenter extends PresenterWidget<TeamPresenter.MyView> implements TeamUiHandlers, GameStateUpdatedHandler {
	public interface MyView extends View, HasUiHandlers<TeamUiHandlers> {

		void setGovernmentInfo(GovernmentInfo governmentInfo);
	}

	private final GameStateProvider gameStateProvider;
	private Color color;
	private final MessageLogPresenter messageLogPresenter;

	@Inject
	TeamPresenter(final EventBus eventBus, final MyView view, final GameStateProvider gameStateProvider, final MessageLogPresenter messageLogPresenter) {
		super(eventBus, view);

		getView().setUiHandlers(this);
		this.gameStateProvider = gameStateProvider;
		this.messageLogPresenter = messageLogPresenter;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	@Override
	public void onGameStateUpdated(final GameStateUpdatedEvent event) {
		final Government government = color == Color.WHITE ? gameStateProvider.get().getWhiteGovernment() : gameStateProvider.get().getBlackGovernment();
		getView().setGovernmentInfo(government);

	}

}
