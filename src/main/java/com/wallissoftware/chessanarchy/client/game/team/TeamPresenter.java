package com.wallissoftware.chessanarchy.client.game.team;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;

public class TeamPresenter extends PresenterWidget<TeamPresenter.MyView> implements TeamUiHandlers {
	public interface MyView extends View, HasUiHandlers<TeamUiHandlers> {

		void setColor(Color color);

		void setJoinCountDown(Long joinTime);

		void setGovernmentName(String name);

		void setGovernmentDescription(String description);

	}

	private final GameStateProvider gameStateProvider;
	private Color color;

	@Inject
	TeamPresenter(final EventBus eventBus, final MyView view, final GameStateProvider gameStateProvider) {
		super(eventBus, view);

		getView().setUiHandlers(this);
		this.gameStateProvider = gameStateProvider;
		Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

			@Override
			public boolean execute() {
				update();
				return true;
			}
		}, 1000);
	}

	public void setColor(final Color color) {
		this.color = color;
		if (color != null) {
			getView().setColor(getColor());
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
		try {
			final SystemOfGovernment government = getColor() == Color.WHITE ? gameStateProvider.getGameState().getWhiteGovernment() : gameStateProvider.getGameState().getBlackGovernment();

			getView().setGovernmentName(government.getName());
			getView().setGovernmentDescription(government.getDescription());
			getView().setJoinCountDown(User.get().getColorJoinTime(color));

		} catch (final NullPointerException e) {

		}

	}

	@Override
	public void joinTeam() {
		fireEvent(new SendMessageEvent("/team " + getColor().name()));
		User.get().joinTeam(getEventBus(), getColor());
	}

}
