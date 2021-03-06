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

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.wallissoftware.chessanarchy.client.game.board.BoardModule;
import com.wallissoftware.chessanarchy.client.game.chat.ChatModule;
import com.wallissoftware.chessanarchy.client.game.election.ElectionModule;
import com.wallissoftware.chessanarchy.client.game.embedinstructions.EmbedInstructionsModule;
import com.wallissoftware.chessanarchy.client.game.pgn.PgnModule;
import com.wallissoftware.chessanarchy.client.game.team.TeamModule;
import com.wallissoftware.chessanarchy.client.game.movetodraw.MoveToDrawModule;

public class GameModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new MoveToDrawModule());
        install(new ElectionModule());
        install(new EmbedInstructionsModule());
        install(new PgnModule());
        install(new TeamModule());
        install(new ChatModule());
        install(new BoardModule());
        bindPresenter(GamePresenter.class, GamePresenter.MyView.class, GameView.class, GamePresenter.MyProxy.class);
    }
}
