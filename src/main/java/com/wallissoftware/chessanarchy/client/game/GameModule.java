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

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.wallissoftware.chessanarchy.client.game.board.BoardModule;

public class GameModule extends AbstractPresenterModule {
	@Override
	protected void configure() {

		install(new BoardModule());
		bindPresenter(GamePresenter.class, GamePresenter.MyView.class, GameView.class, GamePresenter.MyProxy.class);
	}
}