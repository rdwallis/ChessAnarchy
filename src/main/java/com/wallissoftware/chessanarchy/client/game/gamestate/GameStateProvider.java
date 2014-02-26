package com.wallissoftware.chessanarchy.client.game.gamestate;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.wallissoftware.chessanarchy.client.game.chat.events.GameMasterMessageEvent;
import com.wallissoftware.chessanarchy.client.game.chat.events.GameMasterMessageEvent.GameMasterMessageHandler;
import com.wallissoftware.chessanarchy.client.game.gamestate.events.GameStateUpdatedEvent;
import com.wallissoftware.chessanarchy.client.game.gamestate.model.GameState;
import com.wallissoftware.chessanarchy.shared.game.Board;
import com.wallissoftware.chessanarchy.shared.game.exceptions.IllegalMoveException;

@Singleton
public class GameStateProvider implements Provider<GameState>, GameMasterMessageHandler {

	private EventBus eventBus;

	private final RequestBuilder requestBuilder;

	private GameState gameState = null;

	private String fetchedJson;

	private final Board syncedBoard = new Board();

	@Inject
	GameStateProvider(final EventBus eventBus) {
		this.eventBus = eventBus;
		requestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode("/gamestate"));
		Scheduler.get().scheduleFixedPeriod(new RepeatingCommand() {

			@Override
			public boolean execute() {
				fetchLatestGameState();
				return true;
			}

		}, 10000);
		fetchLatestGameState();
	}

	private void fetchLatestGameState() {
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onError(final Request request, final Throwable exception) {
				}

				@Override
				public void onResponseReceived(final Request request, final Response response) {
					if (200 == response.getStatusCode()) {
						processJson(response.getText());
					} else {
					}

				}

			});
		} catch (final RequestException e) {
			// Couldn't connect to server
		}

	}

	private void processJson(final String json) {
		if (fetchedJson == null || !json.equals(fetchedJson)) {
			fetchedJson = json;
			gameState = GameState.fromJson(json);
			try {
				syncedBoard.resetFromMoveList(gameState.getMoveList());
			} catch (final IllegalMoveException e) {

			}
			eventBus.fireEvent(new GameStateUpdatedEvent());
		}
	}

	@Override
	public GameState get() {
		if (gameState == null) {
			throw new NullPointerException();
		}
		return gameState;
	}

	@Override
	public void onGameMasterMessage(final GameMasterMessageEvent event) {
		fetchLatestGameState();

	}

	public Board getSyncedBoard() {
		return syncedBoard;
	}

}
