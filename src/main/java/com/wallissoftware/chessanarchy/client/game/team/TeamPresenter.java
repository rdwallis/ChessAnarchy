package com.wallissoftware.chessanarchy.client.game.team;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.wallissoftware.chessanarchy.client.game.chat.events.SendMessageEvent;
import com.wallissoftware.chessanarchy.client.game.chat.messagelog.MessageLogPresenter;
import com.wallissoftware.chessanarchy.client.game.gamestate.GameStateProvider;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.shared.game.Color;
import com.wallissoftware.chessanarchy.shared.governments.SystemOfGovernment;

public class TeamPresenter extends PresenterWidget<TeamPresenter.MyView> implements TeamUiHandlers {
	public interface MyView extends View, HasUiHandlers<TeamUiHandlers> {

		void setPlayerCountMessage(String playerCount);

		void setJoinButtonText(String joinMessage);

		void setIsUsingText(String usingMessage);

		void setColor(Color color);

		void setJoinCountDown(Long joinTime);

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
			getView().setColor(color);
			update();
		}
	}

	private Color getColor() {
		if (this.color == null) {
			return null;
		}
		try {
			if (gameStateProvider.get().swapColors()) {
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
			final SystemOfGovernment government = getColor() == Color.WHITE ? gameStateProvider.get().getWhiteGovernment() : gameStateProvider.get().getBlackGovernment();

			getView().setIsUsingText(government.getUsingMessage());
			getView().setJoinButtonText(government.getJoinMessage());
			getView().setPlayerCountMessage(government.getPlayerCount(getColor(), messageLogPresenter.getMessagesForGames(messageLogPresenter.getGameId(0), messageLogPresenter.getGameId(1))));

			getView().setJoinCountDown(User.get().getColorJoinTime(getColor()));

		} catch (final NullPointerException e) {

		}

	}

	@Override
	public void joinTeam() {
		fireEvent(new SendMessageEvent("/team " + getColor().name()));
		User.get().joinTeam(getEventBus(), getColor());
	}

}
