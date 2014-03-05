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

package com.wallissoftware.chessanarchy.server.guice;

import com.googlecode.objectify.ObjectifyService;
import com.gwtplatform.dispatch.rpc.server.guice.HandlerModule;
import com.wallissoftware.chessanarchy.server.dispatch.DispatchHandlersModule;
import com.wallissoftware.chessanarchy.server.gamestate.GameState;
import com.wallissoftware.chessanarchy.server.gamestate.GameStateModule;
import com.wallissoftware.chessanarchy.server.mainpage.MainPageModule;
import com.wallissoftware.chessanarchy.server.messages.MessageCache;
import com.wallissoftware.chessanarchy.server.messages.MessageModule;
import com.wallissoftware.chessanarchy.server.time.TimeModule;
import com.wallissoftware.chessanarchy.server.user.UserModule;
import com.wallissoftware.chessanarchy.server.wipe.WipeModule;

public class ServerModule extends HandlerModule {
	@Override
	protected void configureHandlers() {
		registerEntities();
		install(new DispatchHandlersModule());
		install(new MessageModule());
		install(new MainPageModule());
		install(new GameStateModule());
		install(new TimeModule());
		install(new WipeModule());
		install(new UserModule());

	}

	private void registerEntities() {
		ObjectifyService.factory().register(MessageCache.class);
		ObjectifyService.factory().register(GameState.class);

	}
}
