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

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class GameView extends ViewWithUiHandlers<GameUiHandlers> implements GamePresenter.MyView {
	public interface Binder extends UiBinder<Widget, GameView> {
	}

	@UiField HasOneWidget boardPanel, chatPanel, topTeamPanel, bottomTeamPanel, pgnPanel;

	@Inject
	GameView(final Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (slot == GamePresenter.TOP_TEAM_SLOT) {
			if (RootPanel.get("chessAnarchyOpponentTeam") != null) {
				RootPanel.get("chessAnarchyOpponentTeam").clear(true);
				RootPanel.get("chessAnarchyOpponentTeam").add(content);
			} else {
				topTeamPanel.setWidget(content);
			}
		} else if (slot == GamePresenter.BOTTOM_TEAM_SLOT) {
			if (RootPanel.get("chessAnarchyMyTeam") != null) {
				RootPanel.get("chessAnarchyMyTeam").clear(true);
				RootPanel.get("chessAnarchyMyTeam").add(content);
			} else {
				bottomTeamPanel.setWidget(content);
			}
		} else if (slot == GamePresenter.BOARD_SLOT) {
			if (RootPanel.get("chessAnarchyBoard") != null) {
				RootPanel.get("chessAnarchyBoard").clear(true);
				RootPanel.get("chessAnarchyBoard").add(content);
			} else {
				boardPanel.setWidget(content);
			}
		} else if (slot == GamePresenter.PGN_SLOT) {
			if (RootPanel.get("chessAnarchyNotation") != null) {
				RootPanel.get("chessAnarchyNotation").clear(true);
				RootPanel.get("chessAnarchyNotation").add(content);
			} else {
				pgnPanel.setWidget(content);
			}
		} else if (slot == GamePresenter.CHAT_SLOT) {
			if (RootPanel.get("chessAnarchyChat") != null) {
				RootPanel.get("chessAnarchyChat").clear(true);
				RootPanel.get("chessAnarchyChat").add(content);
			} else {
				chatPanel.setWidget(content);
			}
		} else {
			super.setInSlot(slot, content);
		}
	}

	@UiHandler("embedButton")
	void onEmbedButtonClick(final ClickEvent event) {
		getUiHandlers().showEmbedInstructions();
	}

}
