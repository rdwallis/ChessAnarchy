/**
 * Copyright 2012 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this rank except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wallissoftware.chessanarchy.client.game;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.wallissoftware.chessanarchy.client.game.board.BoardPresenter;
import com.wallissoftware.chessanarchy.client.game.chat.ChatPresenter;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent.GameStateUpdatedHandler;
import com.wallissoftware.chessanarchy.client.game.team.TeamPresenter;
import com.wallissoftware.chessanarchy.client.place.NameTokens;
import com.wallissoftware.chessanarchy.client.user.User;
import com.wallissoftware.chessanarchy.client.user.UserChangedEvent;
import com.wallissoftware.chessanarchy.client.user.UserChangedEvent.UserChangedHandler;

public class GamePresenter extends Presenter<GamePresenter.MyView, GamePresenter.MyProxy> implements UserChangedHandler, GameStateUpdatedHandler {
	public interface MyView extends View {
	}

	public static final Object BOARD_SLOT = new Object(), CHAT_SLOT = new Object(), TOP_TEAM_SLOT = new Object(), BOTTOM_TEAM_SLOT = new Object();

	@ProxyStandard
	@NameToken(NameTokens.game)
	public interface MyProxy extends ProxyPlace<GamePresenter> {
	}

	private final BoardPresenter boardPresenter;
	private final ChatPresenter chatPresenter;
	private final TeamPresenter topTeamPresenter;
	private final TeamPresenter bottomTeamPresenter;

	@Inject
	GamePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final BoardPresenter boardPresenter, final ChatPresenter chatPresenter, final Provider<TeamPresenter> teamPresenterProvider) {
		super(eventBus, view, proxy, RevealType.Root);
		this.boardPresenter = boardPresenter;
		this.chatPresenter = chatPresenter;
		this.topTeamPresenter = teamPresenterProvider.get();
		this.bottomTeamPresenter = teamPresenterProvider.get();
		update();

	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(BOARD_SLOT, boardPresenter);
		setInSlot(CHAT_SLOT, chatPresenter);
		setInSlot(BOTTOM_TEAM_SLOT, bottomTeamPresenter);
		setInSlot(TOP_TEAM_SLOT, topTeamPresenter);
		addRegisteredHandler(UserChangedEvent.getType(), this);
		addRegisteredHandler(GameStateUpdatedEvent.getType(), this);
	}

	@Override
	public void onUserChanged(final UserChangedEvent event) {
		update();

	}

	private void update() {
		if (User.get().getColor(true) != null) {
			bottomTeamPresenter.setColor(User.get().getColor(true));
			topTeamPresenter.setColor(User.get().getColor(true).getOpposite());
		} else {
			Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

				@Override
				public boolean execute() {
					update();
					return false;
				}

			}, 50);

		}

	}

	@Override
	public void onGameStateUpdated(final GameStateUpdatedEvent event) {
		update();

	}

}
