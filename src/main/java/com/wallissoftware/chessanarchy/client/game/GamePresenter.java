/**
 * Copyright 2012 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
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

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.wallissoftware.chessanarchy.client.game.board.BoardPresenter;
import com.wallissoftware.chessanarchy.client.place.NameTokens;

public class GamePresenter extends Presenter<GamePresenter.MyView, GamePresenter.MyProxy> {
	public interface MyView extends View {
	}

	public static final Object BOARD_SLOT = new Object();

	@ProxyStandard
	@NameToken(NameTokens.game)
	public interface MyProxy extends ProxyPlace<GamePresenter> {
	}

	private final BoardPresenter boardPresenter;

	@Inject
	GamePresenter(final EventBus eventBus, final MyView view, final MyProxy proxy, final BoardPresenter boardPresenter) {
		super(eventBus, view, proxy, RevealType.Root);
		this.boardPresenter = boardPresenter;

	}

	@Override
	protected void onBind() {
		super.onBind();
		setInSlot(BOARD_SLOT, boardPresenter);
	}

}
